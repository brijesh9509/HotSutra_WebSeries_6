package app.hotsutra.live;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.instamojo.android.Instamojo;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import app.hotsutra.live.database.DatabaseHelper;
import app.hotsutra.live.network.RetrofitClient;
import app.hotsutra.live.network.apis.PaymentApi;
import app.hotsutra.live.network.apis.SubscriptionApi;
import app.hotsutra.live.network.model.ActiveStatus;
import app.hotsutra.live.network.model.InstaMojo2Response;
import app.hotsutra.live.network.model.Package;
import app.hotsutra.live.network.model.PaytmResponse;
import app.hotsutra.live.network.model.PhonepeResponse;
import app.hotsutra.live.network.model.config.PaymentConfig;
import app.hotsutra.live.utils.ApiResources;
import app.hotsutra.live.utils.Constants;
import app.hotsutra.live.utils.MyAppClass;
import app.hotsutra.live.utils.PreferenceUtils;
import app.hotsutra.live.utils.RtlUtils;
import app.hotsutra.live.utils.ToastMsg;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PaymentOptionsActivity extends AppCompatActivity
        implements
        Instamojo.InstamojoPaymentCallback {

    private static final String TAG = PaymentOptionsActivity.class.getSimpleName();
    private static final int PAYPAL_REQUEST_CODE = 100;
    private static final int PAYTM_REQUEST_CODE = 100;
    private ProgressBar progressBar;
    private ImageView closeIv;
    private String currency = "";
    private boolean isInAppPurchase = false;

    private static final int B2B_PG_REQUEST_CODE = 777;

    /*private static final PayPalConfiguration config = new PayPalConfiguration()
            .environment(getPaypalStatus())
            .clientId(ApiResources.PAYPAL_CLIENT_ID);*/

    private Package packageItem;

    /*private static String getPaypalStatus() {
        if (AppConfig.PAYPAL_ACCOUNT_LIVE) {
            return PayPalConfiguration.ENVIRONMENT_PRODUCTION;
        }
        return PayPalConfiguration.ENVIRONMENT_SANDBOX;
    }*/

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

        setContentView(R.layout.activity_payment_options);

        //Log.e(TAG, "onCreate: payPal client id: " + ApiResources.PAYPAL_CLIENT_ID);

        packageItem = (Package) getIntent().getSerializableExtra("package");
        isInAppPurchase = getIntent().getBooleanExtra("isInAppPurchase", false);

        initView();

        // ---------- start paypal service ----------
        /*Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);*/


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


        Log.e("AAAAA===", getInstalledUPIApps().toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PAYPAL_REQUEST_CODE) {
            /*if (resultCode == RESULT_OK) {
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
            }*/

        } /*else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            new ToastMsg(this).toastIconError("Invalid");
        } */ else if (requestCode == PAYTM_REQUEST_CODE && data != null) {
            Toast.makeText(this, data.getStringExtra("nativeSdkForMerchantMessage")
                    + data.getStringExtra("response"), Toast.LENGTH_SHORT).show();
        } else if (requestCode == B2B_PG_REQUEST_CODE) {

            if (data != null) {
                /*Log.e("22222222", data.toString());
                Log.e("222222221", data.getStringExtra("txnId")+"");
                Log.e("222222222", data.getStringExtra("Status")+"-kk");
                Log.e("222222223", data.getStringExtra("txnRef")+"-kk");
                Log.e("222222224", data.getStringExtra("responseCode")+"-kk");*/

                if (data.getStringExtra("Status") != null) {
                    if (data.getStringExtra("Status").equalsIgnoreCase("success")) {
                        sendDataToServer(data.getStringExtra("txnId"), "PhonePe");
                    }
                }
            }
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
        progressBar.setVisibility(View.VISIBLE);
        final String userId = PreferenceUtils.getUserId(PaymentOptionsActivity.this);

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        PaymentApi paymentApi = retrofit.create(PaymentApi.class);
        Call<ResponseBody> call = paymentApi.savePayment(MyAppClass.API_KEY,
                packageItem.getPlanId(), userId, packageItem.getPrice(),
                payId, paymentMethod, BuildConfig.VERSION_CODE, Constants.getDeviceId(this));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                progressBar.setVisibility(View.GONE);
                if (response.code() == 200) {

                    updateActiveStatus(userId);

                } else if (response.code() == 412) {
                    try {
                        if (response.errorBody() != null) {
                            ApiResources.openLoginScreen(response.errorBody().string(),
                                    PaymentOptionsActivity.this);
                            finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(PaymentOptionsActivity.this,
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    new ToastMsg(PaymentOptionsActivity.this).toastIconError(getString(R.string.something_went_wrong));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                new ToastMsg(PaymentOptionsActivity.this).toastIconError(getString(R.string.something_went_wrong));
                t.printStackTrace();
                Log.e("PAYMENT", "error: " + t.getLocalizedMessage());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void updateActiveStatus(String userId) {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SubscriptionApi subscriptionApi = retrofit.create(SubscriptionApi.class);
        Call<ActiveStatus> call = subscriptionApi.getActiveStatus(MyAppClass.API_KEY, userId,
                BuildConfig.VERSION_CODE, Constants.getDeviceId(this));
        call.enqueue(new Callback<ActiveStatus>() {
            @Override
            public void onResponse(@NonNull Call<ActiveStatus> call, @NonNull Response<ActiveStatus> response) {
                if (response.code() == 200) {
                    ActiveStatus activiStatus = response.body();
                    saveActiveStatus(activiStatus);
                } else if (response.code() == 412) {
                    try {
                        if (response.errorBody() != null) {
                            ApiResources.openLoginScreen(response.errorBody().string(),
                                    PaymentOptionsActivity.this);
                            finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(PaymentOptionsActivity.this,
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    new ToastMsg(PaymentOptionsActivity.this).toastIconError("Payment info not save to the own server. something went wrong.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ActiveStatus> call, @NonNull Throwable t) {
                new ToastMsg(PaymentOptionsActivity.this).toastIconError(t.getMessage());
                t.printStackTrace();
            }
        });

    }

    private void getPaytmData(String productId) {
        progressBar.setVisibility(View.VISIBLE);
        final String userId = PreferenceUtils.getUserId(PaymentOptionsActivity.this);

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        PaymentApi paymentApi = retrofit.create(PaymentApi.class);
        Call<PaytmResponse> call = paymentApi.getPaytmToken(MyAppClass.API_KEY,
                productId, userId, BuildConfig.VERSION_CODE, Constants.getDeviceId(this));

        call.enqueue(new Callback<PaytmResponse>() {
            @Override
            public void onResponse(@NonNull Call<PaytmResponse> call, @NonNull Response<PaytmResponse> response) {
                if (response.code() == 200) {

                    if (response.body() != null) {
                        processPaytmTransaction(response.body().getOrderId(), response.body().getMid(),
                                response.body().getToken(), response.body().getAmount(), response.body().getCallBackUrl());
                    }

                } else {
                    new ToastMsg(PaymentOptionsActivity.this).toastIconError(getString(R.string.something_went_wrong));
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<PaytmResponse> call, @NonNull Throwable t) {
                new ToastMsg(PaymentOptionsActivity.this).toastIconError(getString(R.string.something_went_wrong));
                t.printStackTrace();
                Log.e("PAYMENT", "error: " + t.getLocalizedMessage());
                progressBar.setVisibility(View.GONE);
            }

        });
    }

    private void getInstamojoData(String productId) {
        progressBar.setVisibility(View.VISIBLE);
        final String userId = PreferenceUtils.getUserId(PaymentOptionsActivity.this);

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        PaymentApi paymentApi = retrofit.create(PaymentApi.class);
        Call<InstaMojo2Response> call = paymentApi.getIntaMojoToken(MyAppClass.API_KEY,
                productId, userId, BuildConfig.VERSION_CODE, Constants.getDeviceId(this));

        call.enqueue(new Callback<InstaMojo2Response>() {
            @Override
            public void onResponse(@NonNull Call<InstaMojo2Response> call, @NonNull Response<InstaMojo2Response> response) {

                if (response.code() == 200) {

                    /*Intent intent = new Intent(PaymentOptionsActivity.this, InstamojoWebActivity.class);
                    intent.putExtra("url", response.body().getLongUrl());
                    someActivityResultLauncher.launch(intent);*/

                    if (response.body() != null) {
                        initiateSDKPayment(response.body().getOrderId());
                    }

                    /*ApiContext context = ApiContext.create(response.body().getClientId(),
                            response.body().getClientSecret(), ApiContext.Mode.TEST);
                    Instamojo api = new InstamojoImpl(context);

                    PaymentOrder order = new PaymentOrder();
                    order.setName("John Smith");
                    order.setEmail("jsk143fams@gmail.com");
                    order.setPhone("9723248900");
                    order.setCurrency("INR");
                    order.setAmount(Double.parseDouble(response.body().getPaymentRequest().getAmount()));
                    order.setDescription(response.body().getPaymentRequest().getPurpose());
                    order.setRedirectUrl(response.body().getPaymentRequest().getRedirectUrl());
                    //order.setWebhookUrl(response.body().getPaymentRequest().getWebhook());
                    order.setTransactionId(response.body().getPaymentRequest().getId());

                    try {
                        PaymentOrderResponse paymentOrderResponse = api.createPaymentOrder(order);
                        System.out.println(paymentOrderResponse.getPaymentOrder().getStatus());

                    } catch (HTTPException e) {
                        System.out.println(e.getStatusCode());
                        System.out.println(e.getMessage());
                        System.out.println(e.getJsonPayload());

                    } catch (ConnectionException e) {
                        System.out.println(e.getMessage());
                    }
*/
                } else {
                    new ToastMsg(PaymentOptionsActivity.this).toastIconError(getString(R.string.something_went_wrong));
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<InstaMojo2Response> call, @NonNull Throwable t) {
                new ToastMsg(PaymentOptionsActivity.this).toastIconError(getString(R.string.something_went_wrong));
                t.printStackTrace();
                Log.e("PAYMENT", "error: " + t.getLocalizedMessage());
                progressBar.setVisibility(View.GONE);
            }

        });
    }

    private void getPhonePeData(String productId, String appPackageName) {
        progressBar.setVisibility(View.VISIBLE);
        final String userId = PreferenceUtils.getUserId(PaymentOptionsActivity.this);

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        PaymentApi paymentApi = retrofit.create(PaymentApi.class);
        Call<PhonepeResponse> call = paymentApi.getPhonePeToken(MyAppClass.API_KEY,
                productId, userId, BuildConfig.VERSION_CODE, Constants.getDeviceId(this));

        call.enqueue(new Callback<PhonepeResponse>() {
            @Override
            public void onResponse(@NonNull Call<PhonepeResponse> call, @NonNull Response<PhonepeResponse> response) {

                if (response.code() == 200) {

                    if (response.body() != null) {
                        if (response.body().getData().getInstrumentResponse().getIntentUrl() != null) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(response.body().getData().getInstrumentResponse().getIntentUrl()));
                            intent.setPackage(appPackageName);
                            startActivityForResult(intent, B2B_PG_REQUEST_CODE);
                        }
                    }

                } else {
                    new ToastMsg(PaymentOptionsActivity.this).toastIconError(getString(R.string.something_went_wrong));
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<PhonepeResponse> call, @NonNull Throwable t) {
                new ToastMsg(PaymentOptionsActivity.this).toastIconError(getString(R.string.something_went_wrong));
                t.printStackTrace();
                Log.e("PAYMENT", "error: " + t.getLocalizedMessage());
                progressBar.setVisibility(View.GONE);
            }

        });
    }

    private void saveActiveStatus(ActiveStatus activeStatus) {
        DatabaseHelper db = new DatabaseHelper(PaymentOptionsActivity.this);
        if (db.getActiveStatusCount() > 1) {
            db.deleteAllActiveStatusData();
        }
        if (db.getActiveStatusCount() == 0) {
            db.insertActiveStatusData(activeStatus);
        } else {
            db.updateActiveStatus(activeStatus, 1);
        }
        new ToastMsg(PaymentOptionsActivity.this).toastIconSuccess(getResources().getString(R.string.payment_success));

        Intent intent = new Intent(PaymentOptionsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /*private void processPaypalPayment(Package packageItem) {
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
            double priceInUSD = price / exchangeRate;
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
*/
    private void initView() {
        progressBar = findViewById(R.id.progress_bar);
        closeIv = findViewById(R.id.close_iv);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        PaymentConfig config = databaseHelper.getConfigurationData().getPaymentConfig();
        CardView paypalBt, stripBt, razorpayBt, offlineBtn, googlePlay_btn,
                paytm_btn, instamojo_btn, phonePeBtn, gPayBtn, paytmUpiBtn,
                bhimBtn, amazonBtn;
        paypalBt = findViewById(R.id.paypal_btn);
        stripBt = findViewById(R.id.stripe_btn);
        razorpayBt = findViewById(R.id.razorpay_btn);
        offlineBtn = findViewById(R.id.offline_btn);
        googlePlay_btn = findViewById(R.id.googlePlay_btn);
        instamojo_btn = findViewById(R.id.instamojo_btn);
        phonePeBtn = findViewById(R.id.phonePeBtn);
        paytm_btn = findViewById(R.id.paytm_btn);
        gPayBtn = findViewById(R.id.gPayBtn);
        paytmUpiBtn = findViewById(R.id.paytmUpiBtn);
        bhimBtn = findViewById(R.id.bhimBtn);
        amazonBtn = findViewById(R.id.amazonBtn);

        TextView txtPackageDays = findViewById(R.id.txtPackageDays);
        TextView txtPackageRupees = findViewById(R.id.txtPackageRupees);

        LinearLayout llTollFree = findViewById(R.id.llTollFree);

        currency = config.getCurrencySymbol();

        txtPackageDays.setText(packageItem.getName());
        txtPackageRupees.setText(currency.concat(" ").concat(packageItem.getPrice()));

        TextView txtTollFree = findViewById(R.id.txtTollFree);
        txtTollFree.setPaintFlags(txtTollFree.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        if (config.getInstamojoIsProduction()) {
            Instamojo.getInstance().initialize(PaymentOptionsActivity.this, Instamojo.Environment.PRODUCTION);
        } else {
            Instamojo.getInstance().initialize(PaymentOptionsActivity.this, Instamojo.Environment.TEST);
        }

        /*if (!config.getPaypalEnable()) {
            paypalBt.setVisibility(View.GONE);
            space.setVisibility(View.GONE);
        }*/
        paypalBt.setVisibility(View.GONE);

        if (!config.getStripeEnable()) {
            stripBt.setVisibility(View.GONE);
        }
        if (!config.getRazorpayEnable()) {
            razorpayBt.setVisibility(View.GONE);
        }
        if (!config.isOfflinePaymentEnable()) {
            offlineBtn.setVisibility(View.GONE);
        }

        if (!config.getPaytmEnable()) {
            paytm_btn.setVisibility(View.GONE);
        }

        if (!isInAppPurchase) {
            googlePlay_btn.setVisibility(View.GONE);
        }

        if (!config.getInstamojoEnable()) {
            instamojo_btn.setVisibility(View.GONE);
        }
        //config.setPhonepe_enable(false);
        //config.setPhonepe_is_production(false);

        if (isUPIAppInstalled("com.phonepe.app") && config.getPhonepe_enable()) {
            phonePeBtn.setVisibility(View.VISIBLE);
        }else {
            phonePeBtn.setVisibility(View.GONE);
        }

        if (isUPIAppInstalled("com.google.android.apps.nbu.paisa.user")
                && config.getPhonepe_enable() && config.getPhonepe_is_production()) {
            gPayBtn.setVisibility(View.VISIBLE);
        }else {
            gPayBtn.setVisibility(View.GONE);
        }

        if (isUPIAppInstalled("net.one97.paytm")
                && config.getPhonepe_enable() && config.getPhonepe_is_production()) {
            paytmUpiBtn.setVisibility(View.VISIBLE);
        }else {
            paytmUpiBtn.setVisibility(View.GONE);
        }

        if (isUPIAppInstalled("in.org.npci.upiapp")
                && config.getPhonepe_enable() && config.getPhonepe_is_production()) {
            bhimBtn.setVisibility(View.VISIBLE);
        }else {
            bhimBtn.setVisibility(View.GONE);
        }

        if (isUPIAppInstalled("in.amazon.mShop.android.shopping")
                && config.getPhonepe_enable() && config.getPhonepe_is_production()) {
            amazonBtn.setVisibility(View.VISIBLE);
        }else {
            amazonBtn.setVisibility(View.GONE);
        }

        paypalBt.setOnClickListener(view1 -> {
            //processPaypalPayment(packageItem);
        });

        stripBt.setOnClickListener(view1 -> {
            Intent intent = new Intent(PaymentOptionsActivity.this, StripePaymentActivity.class);
            intent.putExtra("package", packageItem);
            intent.putExtra("currency", currency);
            startActivity(intent);
        });

        razorpayBt.setOnClickListener(view1 -> {
            Intent intent = new Intent(PaymentOptionsActivity.this, RazorPayActivity.class);
            intent.putExtra("package", packageItem);
            intent.putExtra("currency", currency);
            startActivity(intent);
        });

        offlineBtn.setOnClickListener(view1 -> {
            showOfflinePaymentDialog();
        });

        googlePlay_btn.setOnClickListener(view1 -> {
            establishConnection();
        });

        paytm_btn.setOnClickListener(view1 -> {
            getPaytmData(packageItem.getPlanId());
        });

        instamojo_btn.setOnClickListener(view1 -> {
            getInstamojoData(packageItem.getPlanId());
        });

        phonePeBtn.setOnClickListener(view1 -> {
            if (config.getPhonepe_is_production()) {
                getPhonePeData(packageItem.getPlanId(), "com.phonepe.app"); //LIVE
            } else {
                getPhonePeData(packageItem.getPlanId(), "com.phonepe.simulator"); //TEST
            }
        });

        gPayBtn.setOnClickListener(view1 -> {
            getPhonePeData(packageItem.getPlanId(), "com.google.android.apps.nbu.paisa.user");
        });

        paytmUpiBtn.setOnClickListener(view1 -> {
            getPhonePeData(packageItem.getPlanId(), "net.one97.paytm");
        });

        bhimBtn.setOnClickListener(view1 -> {
            getPhonePeData(packageItem.getPlanId(), "in.org.npci.upiapp");
        });

        amazonBtn.setOnClickListener(view1 -> {
            getPhonePeData(packageItem.getPlanId(), "in.amazon.mShop.android.shopping");
        });

        llTollFree.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(AppConfig.TOLL_WEB_URL));
            startActivity(i);
        });
    }

    private void showOfflinePaymentDialog() {
        DatabaseHelper helper = new DatabaseHelper(this);
        PaymentConfig paymentConfig = helper.getConfigurationData().getPaymentConfig();
        new MaterialAlertDialogBuilder(this)
                .setTitle(paymentConfig.getOfflinePaymentTitle())
                .setMessage(paymentConfig.getOfflinePaymentInstruction())
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss()).show();

    }

    void establishConnection() {

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    //showProducts(productId);
                    GetSubPurchasesNEWINAPP();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                establishConnection();
            }
        });
    }

    protected void onResume() {
        super.onResume();

        /*billingClient.queryPurchasesAsync(
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
        );*/

    }

    void processPaytmTransaction(String orderID, String mID, String txnToken,
                                 String amount, String ccl) {
        try {
            //String host = "https://securegw-stage.paytm.in/"; //TEST
            String host = "https://securegw.paytm.in/";  // LIVE

            PaytmOrder paytmOrder = new PaytmOrder(orderID, mID, txnToken, amount, ccl);
            TransactionManager transactionManager = new TransactionManager(paytmOrder,
                    new PaytmPaymentTransactionCallback() {

                        @Override
                        public void onTransactionResponse(Bundle bundle) {
                            if (bundle != null) {
                                JSONObject json = new JSONObject();
                                Set<String> keys = bundle.keySet();
                                for (String key : keys) {
                                    try {
                                        // json.put(key, bundle.get(key)); see edit below
                                        json.put(key, JSONObject.wrap(bundle.get(key)));
                                    } catch (JSONException e) {
                                        //Handle exception here
                                    }
                                }
                                Log.e("response", json.toString());
                                Log.e("TXNID", bundle.get("TXNID").toString());

                                if (bundle.get("STATUS").toString().equalsIgnoreCase("TXN_SUCCESS")) {
                                    sendDataToServer(bundle.get("TXNID").toString(), "Paytm");
                                }
                            }
                        }

                        @Override
                        public void networkNotAvailable() {
                            Log.e("networkNotAvailable", "network");
                        }

                        @Override
                        public void onErrorProceed(String s) {
                            Log.e("onErrorProceed", s);
                        }

                        @Override
                        public void clientAuthenticationFailed(String s) {
                            Log.e("AuthenticationFailed", s);
                        }

                        @Override
                        public void someUIErrorOccurred(String s) {
                            Log.e("someUIErrorOccurred", s);
                        }

                        @Override
                        public void onErrorLoadingWebPage(int i, String s, String s1) {
                            Log.e("onErrorLoadingWebPage", s);
                        }

                        @Override
                        public void onBackPressedCancelTransaction() {
                            Log.e("onBackPressed", "back");
                        }

                        @Override
                        public void onTransactionCancel(String s, Bundle bundle) {
                            if (bundle != null) {
                                JSONObject json = new JSONObject();
                                Set<String> keys = bundle.keySet();
                                for (String key : keys) {
                                    try {
                                        // json.put(key, bundle.get(key)); see edit below
                                        json.put(key, JSONObject.wrap(bundle.get(key)));
                                    } catch (JSONException e) {
                                        //Handle exception here
                                    }
                                }
                                Log.e("Cancel", json.toString());
                            }
                        }
                    });

            transactionManager.setShowPaymentUrl(host + "theia/api/v1/showPaymentPage");
            transactionManager.setAppInvokeEnabled(false);
            transactionManager.startTransaction(this, PAYTM_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //stopService(new Intent(this, PayPalService.class));
        if (billingClient != null) {
            billingClient.endConnection();
        }
    }

    private void initiateSDKPayment(String orderID) {
        Instamojo.getInstance().initiatePayment(this, orderID, this);
    }

    @Override
    public void onInstamojoPaymentComplete(String orderID, String transactionID, String paymentID, String paymentStatus) {
        Log.d(TAG, "Payment complete");
        Log.e("aaaaaaaaa=======", "Payment complete. Order ID: " + orderID + ", Transaction ID: " + transactionID
                + ", Payment ID:" + paymentID + ", Status: " + paymentStatus);
        /*showToast("Payment complete. Order ID: " + orderID + ", Transaction ID: " + transactionID
                + ", Payment ID:" + paymentID + ", Status: " + paymentStatus);*/

        // sendDataToServer(paymentID,"instamojo");

        updateActiveStatus();
    }

    @Override
    public void onPaymentCancelled() {
        Log.d(TAG, "Payment cancelled");
        //showToast("Payment cancelled by user");

        updateActiveStatus();
    }

    @Override
    public void onInitiatePaymentFailure(String errorMessage) {
        Log.d(TAG, "Initiate payment failed");
        //showToast("Initiating payment failed. Error: " + errorMessage);

        updateActiveStatus();
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Here, no request code
                    updateActiveStatus();
                }
            });

    private void updateActiveStatus() {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SubscriptionApi subscriptionApi = retrofit.create(SubscriptionApi.class);

        Call<ActiveStatus> call = subscriptionApi.getActiveStatus(MyAppClass.API_KEY,
                PreferenceUtils.getUserId(PaymentOptionsActivity.this),
                BuildConfig.VERSION_CODE, Constants.getDeviceId(this));
        call.enqueue(new Callback<ActiveStatus>() {
            @Override
            public void onResponse(@NonNull Call<ActiveStatus> call, @NonNull Response<ActiveStatus> response) {
                if (response.code() == 200) {


                    if (response.body() != null && response.body().getStatus().equalsIgnoreCase("active")) {
                        ActiveStatus activeStatus = response.body();
                        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                        db.deleteAllActiveStatusData();
                        db.insertActiveStatusData(activeStatus);

                        progressBar.setVisibility(View.GONE);
                        new ToastMsg(PaymentOptionsActivity.this).toastIconSuccess(getResources().getString(R.string.payment_success));
                        Intent intent = new Intent(PaymentOptionsActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                } else if (response.code() == 412) {
                    try {
                        if (response.errorBody() != null) {
                            ApiResources.openLoginScreen(response.errorBody().string(),
                                    PaymentOptionsActivity.this);
                            finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(PaymentOptionsActivity.this,
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ActiveStatus> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });

    }

    private void showToast(final String message) {
        runOnUiThread(() -> Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show());
    }

    private ArrayList<String> getInstalledUPIApps() {
        ArrayList<String> upiList = new ArrayList<>();
        Uri uri = Uri.parse(String.format("%s://%s", "upi", "pay"));
        Intent upiUriIntent = new Intent();
        upiUriIntent.setData(uri);
        PackageManager packageManager = getApplication().getPackageManager();
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(upiUriIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (resolveInfoList != null) {
            for (ResolveInfo resolveInfo : resolveInfoList) {
                upiList.add(resolveInfo.activityInfo.packageName);
            }
        }
        return upiList;
    }

    private boolean isUPIAppInstalled(String packageName) {

        for (int i = 0; i < getInstalledUPIApps().size(); i++) {

            if (packageName.equalsIgnoreCase(getInstalledUPIApps().get(i))) {
                return true;
            }
        }

        return false;
    }

    void GetSubPurchasesNEWINAPP() {
        ArrayList<QueryProductDetailsParams.Product> productList = new ArrayList<>();

        productList.add(
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(packageItem.getProductId())
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
        );

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();


        billingClient.queryProductDetailsAsync(params, new ProductDetailsResponseListener() {
            @Override
            public void onProductDetailsResponse(@NonNull BillingResult billingResult, @NonNull List<ProductDetails> list) {
                if (list.size() > 0) {
                    LaunchSubPurchase(list.get(0));
                } else {
                    Toast.makeText(PaymentOptionsActivity.this, "No subscription available", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    void LaunchSubPurchase(ProductDetails productDetails) {
        assert productDetails.getSubscriptionOfferDetails() != null;
        ArrayList<BillingFlowParams.ProductDetailsParams> productList = new ArrayList<>();

        productList.add(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .setOfferToken(productDetails.getSubscriptionOfferDetails().get(0).getOfferToken())
                        .build()
        );

        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productList)
                .build();

        billingClient.launchBillingFlow(this, billingFlowParams);
    }

    void verifySubPurchase(Purchase purchases) {
        if (!purchases.isAcknowledged()) {
            billingClient.acknowledgePurchase(AcknowledgePurchaseParams
                    .newBuilder()
                    .setPurchaseToken(purchases.getPurchaseToken())
                    .build(), billingResult -> {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                   /* String pId="";
                    for (String pur : purchases.getProducts()) {
                        if (pur.equalsIgnoreCase(packageItem.getProductId())) {
                            pId = purchases.getOrderId();
                        }
                    }*/


                }
            });

            sendDataToServer(purchases.getOrderId(), "inApp");
        }
    }
}