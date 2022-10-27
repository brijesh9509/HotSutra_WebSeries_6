package app.hotsutra.live;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import app.hotsutra.live.adapters.PackageAdapter;
import app.hotsutra.live.bottomshit.PaymentBottomShitDialog;
import app.hotsutra.live.database.DatabaseHelper;
import app.hotsutra.live.network.apis.PackageApi;
import app.hotsutra.live.network.apis.PaymentApi;
import app.hotsutra.live.network.apis.SubscriptionApi;
import app.hotsutra.live.network.model.ActiveStatus;
import app.hotsutra.live.network.model.AllPackage;
import app.hotsutra.live.network.model.Package;
import app.hotsutra.live.network.model.config.PaymentConfig;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import app.hotsutra.live.R;
import app.hotsutra.live.network.RetrofitClient;
import app.hotsutra.live.utils.PreferenceUtils;
import app.hotsutra.live.utils.ApiResources;
import app.hotsutra.live.utils.RtlUtils;
import app.hotsutra.live.utils.ToastMsg;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.hotsutra.live.utils.Constants;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PurchasePlanActivity extends AppCompatActivity
        implements PackageAdapter.OnItemClickListener,
        PaymentBottomShitDialog.OnBottomShitClickListener {

    private static final String TAG = PurchasePlanActivity.class.getSimpleName();
    private static final int PAYPAL_REQUEST_CODE = 100;
    private TextView noTv;
    private ProgressBar progressBar;
    private ImageView closeIv;
    private RecyclerView packageRv;
    private String currency = "";


    private static final PayPalConfiguration config = new PayPalConfiguration()
            .environment(getPaypalStatus())
            .clientId(ApiResources.PAYPAL_CLIENT_ID);


    private Package packageItem;


    private static String getPaypalStatus() {
        if (AppConfig.PAYPAL_ACCOUNT_LIVE) {
            return PayPalConfiguration.ENVIRONMENT_PRODUCTION;
        }
        return PayPalConfiguration.ENVIRONMENT_SANDBOX;
    }

    private BillingClient billingClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RtlUtils.setScreenDirection(this);
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("push", MODE_PRIVATE);
        boolean isDark = sharedPreferences.getBoolean("dark", false);

        if (isDark) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppThemeLight);
        }

        setContentView(R.layout.activity_purchase_plan);

        Log.e(TAG, "onCreate: payPal client id: " + ApiResources.PAYPAL_CLIENT_ID);

        initView();

        // ---------- start paypal service ----------
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        // getting currency symbol
        PaymentConfig config = new DatabaseHelper(PurchasePlanActivity.this).getConfigurationData().getPaymentConfig();
        currency = config.getCurrencySymbol();
        String exchangeRate = config.getExchangeRate();
        packageRv.setHasFixedSize(true);
        packageRv.setLayoutManager(new LinearLayoutManager(this));

        getPurchasePlanInfo();

        closeIv.setOnClickListener(view -> finish());

        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(
                        (billingResult, list) -> {
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {

                                for (Purchase purchase : list) {
                                    verifySubPurchase(purchase);
                                }
                            }

                        }
                ).build();

        //start the connection after initializing the billing client
        //establishConnection();
    }


    private void getPurchasePlanInfo() {
        String userId = PreferenceUtils.getUserId(this);
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        final PackageApi packageApi = retrofit.create(PackageApi.class);
        Call<AllPackage> call = packageApi.getAllPackage(AppConfig.API_KEY,
                BuildConfig.VERSION_CODE,userId, Constants.getDeviceId(this));
        call.enqueue(new Callback<AllPackage>() {
            @Override
            public void onResponse(@NonNull Call<AllPackage> call, @NonNull Response<AllPackage> response) {
                AllPackage allPackage = response.body();
                if (response.code() == 200) {
                    if (allPackage != null) {
                        if (allPackage.getPackage().size() > 0) {
                            noTv.setVisibility(View.GONE);
                            PackageAdapter adapter = new PackageAdapter(
                                    PurchasePlanActivity.this, allPackage.getPackage(), currency);
                            adapter.setItemClickListener(PurchasePlanActivity.this);
                            packageRv.setAdapter(adapter);
                        } else {
                            noTv.setVisibility(View.VISIBLE);
                        }
                    }
                } else if (response.code() == 412) {
                    try {
                        if (response.errorBody() != null) {
                            ApiResources.openLoginScreen(response.errorBody().string(),
                                    PurchasePlanActivity.this);
                            finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(PurchasePlanActivity.this,
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<AllPackage> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        String paymentDetails = confirmation.toJSONObject().toString(4);
                        completePayment(paymentDetails);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    new ToastMsg(this).toastIconError("Cancel");
                }
            }

        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            new ToastMsg(this).toastIconError("Invalid");
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    private void completePayment(String paymentDetails) {
        try {
            JSONObject jsonObject = new JSONObject(paymentDetails);
            String payId = jsonObject.getString("id");
            sendDataToServer(payId, "Paypal");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendDataToServer(String payId, String paymentMethod) {
        final String userId = PreferenceUtils.getUserId(PurchasePlanActivity.this);

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        PaymentApi paymentApi = retrofit.create(PaymentApi.class);
        Call<ResponseBody> call = paymentApi.savePayment(AppConfig.API_KEY,
                packageItem.getPlanId(), userId, packageItem.getPrice(),
                payId, paymentMethod, BuildConfig.VERSION_CODE, Constants.getDeviceId(this));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.code() == 200) {

                    updateActiveStatus(userId);

                } else if (response.code() == 412) {
                    try {
                        if (response.errorBody() != null) {
                            ApiResources.openLoginScreen(response.errorBody().string(),
                                    PurchasePlanActivity.this);
                            finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(PurchasePlanActivity.this,
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }else {
                    new ToastMsg(PurchasePlanActivity.this).toastIconError(getString(R.string.something_went_wrong));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                new ToastMsg(PurchasePlanActivity.this).toastIconError(getString(R.string.something_went_wrong));
                t.printStackTrace();
                Log.e("PAYMENT", "error: " + t.getLocalizedMessage());
            }

        });

    }

    private void updateActiveStatus(String userId) {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SubscriptionApi subscriptionApi = retrofit.create(SubscriptionApi.class);
        Call<ActiveStatus> call = subscriptionApi.getActiveStatus(AppConfig.API_KEY, userId,
                BuildConfig.VERSION_CODE, Constants.getDeviceId(this));
        call.enqueue(new Callback<ActiveStatus>() {
            @Override
            public void onResponse(@NonNull Call<ActiveStatus> call, @NonNull Response<ActiveStatus> response) {
                if (response.code() == 200) {
                    ActiveStatus activiStatus = response.body();
                    saveActiveStatus(activiStatus);
                }else if (response.code() == 412) {
                    try {
                        if (response.errorBody() != null) {
                            ApiResources.openLoginScreen(response.errorBody().string(),
                                    PurchasePlanActivity.this);
                            finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(PurchasePlanActivity.this,
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    new ToastMsg(PurchasePlanActivity.this).toastIconError("Payment info not save to the own server. something went wrong.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ActiveStatus> call, @NonNull Throwable t) {
                new ToastMsg(PurchasePlanActivity.this).toastIconError(t.getMessage());
                t.printStackTrace();
            }
        });

    }

    private void saveActiveStatus(ActiveStatus activeStatus) {
        DatabaseHelper db = new DatabaseHelper(PurchasePlanActivity.this);
        if (db.getActiveStatusCount() > 1) {
            db.deleteAllActiveStatusData();
        }
        if (db.getActiveStatusCount() == 0) {
            db.insertActiveStatusData(activeStatus);
        } else {
            db.updateActiveStatus(activeStatus, 1);
        }
        new ToastMsg(PurchasePlanActivity.this).toastIconSuccess(getResources().getString(R.string.payment_success));

        Intent intent = new Intent(PurchasePlanActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    private void processPaypalPayment(Package packageItem) {
        String[] paypalAcceptedList = getResources().getStringArray(R.array.paypal_currency_list);
        if (Arrays.asList(paypalAcceptedList).contains(ApiResources.CURRENCY)) {
            PayPalPayment payPalPayment = new PayPalPayment((new BigDecimal(String.valueOf(packageItem.getPrice()))),
                    ApiResources.CURRENCY,
                    "Payment for Package",
                    PayPalPayment.PAYMENT_INTENT_SALE);

            Log.e("Payment", "currency: " + ApiResources.CURRENCY);
            Intent intent = new Intent(this, PaymentActivity.class);
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
            startActivityForResult(intent, PAYPAL_REQUEST_CODE);
        } else {
            PaymentConfig paymentConfig = new DatabaseHelper(PurchasePlanActivity.this).getConfigurationData().getPaymentConfig();
            double exchangeRate = Double.parseDouble(paymentConfig.getExchangeRate());
            double price = Double.parseDouble(packageItem.getPrice());
            double priceInUSD = (double) price / exchangeRate;
            PayPalPayment payPalPayment = new PayPalPayment((new BigDecimal(String.valueOf(priceInUSD))),
                    "USD",
                    "Payment for Package",
                    PayPalPayment.PAYMENT_INTENT_SALE);
            Intent intent = new Intent(this, PaymentActivity.class);
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
            startActivityForResult(intent, PAYPAL_REQUEST_CODE);
        }
    }

    private void initView() {

        noTv = findViewById(R.id.no_tv);
        progressBar = findViewById(R.id.progress_bar);
        packageRv = findViewById(R.id.pacakge_rv);
        closeIv = findViewById(R.id.close_iv);
    }

    @Override
    public void onItemClick(Package pac) {
        packageItem = pac;

        boolean isInAppPurchase = !pac.getProductId().isEmpty();

        PaymentBottomShitDialog paymentBottomShitDialog = new PaymentBottomShitDialog(isInAppPurchase);
        paymentBottomShitDialog.show(getSupportFragmentManager(), "PaymentBottomShitDialog");
    }

    @Override
    public void onBottomShitClick(String paymentMethodName) {
        if (paymentMethodName.equals(PaymentBottomShitDialog.PAYPAL)) {
            processPaypalPayment(packageItem);

        } else if (paymentMethodName.equals(PaymentBottomShitDialog.STRIP)) {
            Intent intent = new Intent(PurchasePlanActivity.this, StripePaymentActivity.class);
            intent.putExtra("package", packageItem);
            intent.putExtra("currency", currency);
            startActivity(intent);

        } else if (paymentMethodName.equalsIgnoreCase(PaymentBottomShitDialog.RAZOR_PAY)) {
            Intent intent = new Intent(PurchasePlanActivity.this, RazorPayActivity.class);
            intent.putExtra("package", packageItem);
            intent.putExtra("currency", currency);
            startActivity(intent);
        } else if (paymentMethodName.equalsIgnoreCase(PaymentBottomShitDialog.OFFLINE_PAY)) {
            //show an alert dialog
            showOfflinePaymentDialog();
        } else if (paymentMethodName.equalsIgnoreCase(PaymentBottomShitDialog.GOOGLE_PAY)) {
            //show an alert dialog
            //showOfflinePaymentDialog();
            establishConnection(packageItem.getProductId());
            // establishConnection("30days");
        }
    }

    private void showOfflinePaymentDialog() {
        DatabaseHelper helper = new DatabaseHelper(this);
        PaymentConfig paymentConfig = helper.getConfigurationData().getPaymentConfig();
        new MaterialAlertDialogBuilder(this)
                .setTitle(paymentConfig.getOfflinePaymentTitle())
                .setMessage(paymentConfig.getOfflinePaymentInstruction())
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss()).show();

    }


    void establishConnection(String productId) {

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    showProducts(productId);
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                establishConnection(productId);
            }
        });
    }

    void showProducts(String productId) {

        List<String> skuList = new ArrayList<>();
        /*skuList.add("test_500");
        skuList.add("test_250");
        skuList.add("30days");
        skuList.add("test_r_100");*/
        skuList.add(productId);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);
        billingClient.querySkuDetailsAsync(params.build(),
                (billingResult, skuDetailsList) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                        // Process the result.
                        Log.e("skuDetailsList", skuDetailsList.toString());
                        for (SkuDetails skuDetails : skuDetailsList) {
                            if (skuDetails.getSku().equals(productId)) {
                                //Now update the UI
                                launchPurchaseFlow(skuDetails);
                            }
                        }
                    }
                });
    }

    void launchPurchaseFlow(SkuDetails skuDetails) {
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build();
        billingClient.launchBillingFlow(PurchasePlanActivity.this, billingFlowParams);
    }

    void verifySubPurchase(Purchase purchases) {
        Log.e("purchases", purchases.toString());

        AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams
                .newBuilder()
                .setPurchaseToken(purchases.getPurchaseToken())
                .build();

        billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                //Toast.makeText(SubscriptionActivity.this, "Item Consumed", Toast.LENGTH_SHORT).show();
                // Handle the success of the consume operation.
                //user prefs to set premium
                //Toast.makeText(PurchasePlanActivity.this, "You are a premium user now", Toast.LENGTH_SHORT).show();
                //updateUser();

                //Setting premium to 1
                // 1 - premium
                //0 - no premium
            }
        });


        /*Log.e(TAG, "Purchase Token: " + purchases.getPurchaseToken());
        Log.e(TAG, "Purchase Time: " + purchases.getPurchaseTime());
        Log.e(TAG, "Purchase OrderID: " + purchases.getOrderId());*/

        sendDataToServer(purchases.getOrderId(), "inApp");

    }


    protected void onResume() {
        super.onResume();

        billingClient.queryPurchasesAsync(
                BillingClient.SkuType.SUBS,
                (billingResult, list) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        for (Purchase purchase : list) {

                            Log.e("purchase", purchase.toString());
                            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
                                verifySubPurchase(purchase);
                            }
                        }
                    }
                }
        );

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, PayPalService.class));
        if (billingClient != null) {
            billingClient.endConnection();
        }
    }
}

