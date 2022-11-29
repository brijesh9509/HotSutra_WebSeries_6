package app.hotsutra.live;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import app.hotsutra.live.database.DatabaseHelper;
import app.hotsutra.live.network.apis.ConfigurationApi;
import app.hotsutra.live.network.model.config.ApkUpdateInfo;
import app.hotsutra.live.network.model.config.Configuration;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import app.hotsutra.live.network.RetrofitClient;
import app.hotsutra.live.utils.HelperUtils;
import app.hotsutra.live.utils.PreferenceUtils;
import app.hotsutra.live.utils.ApiResources;
import app.hotsutra.live.utils.Constants;
import app.hotsutra.live.utils.ToastMsg;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {
    private static final String TAG = "SplashScreen";
    private final int PERMISSION_REQUEST_CODE = 100;
    private final int SPLASH_TIME = 1500;
    private Thread timer;
    private DatabaseHelper db;
    private final boolean isRestricted = false;
    private final boolean isUpdate = false;
    private boolean vpnStatus = false;
    private HelperUtils helperUtils;

    @Override
    protected void onStart() {
        super.onStart();
        //check any restricted app is available or not
       /* ApplicationInfo restrictedApp = helperUtils.getRestrictApp();
        if (restrictedApp != null){
            boolean isOpenInBackground = helperUtils.isForeground(restrictedApp.packageName);
            if (isOpenInBackground){
                Log.e(TAG, restrictedApp.loadLabel(this.getPackageManager()).toString() + ", is open in background.");
            }else {
                Log.e(TAG, "No restricted app is running in background.");
            }
        }else {
            Log.e(TAG, "No restricted app installed!!");
        }*/


        //check VPN connection is set or not
        vpnStatus = new HelperUtils(SplashScreenActivity.this).isVpnConnectionAvailable();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splashscreen);

        db = new DatabaseHelper(SplashScreenActivity.this);
        helperUtils = new HelperUtils(SplashScreenActivity.this);
        vpnStatus = new HelperUtils(SplashScreenActivity.this).isVpnConnectionAvailable();

        //print keyHash for facebook login
        // createKeyHash(SplashScreenActivity.this, BuildConfig.APPLICATION_ID);


        timer = new Thread() {
            public void run() {
                try {
                    sleep(SPLASH_TIME);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                    if (PreferenceUtils.isLoggedIn(SplashScreenActivity.this)) {
                        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        finish();
                    } else {

                        if (isLoginMandatory()) {
                            Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                            finish();
                        }
                    }

                }
            }
        };

        VideoView videoView = findViewById(R.id.vdVw);
        //Set MediaController  to enable play, pause, forward, etc options.
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        mediaController.hide();
        mediaController.setVisibility(View.GONE);
        //Location of Media File
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video_2);
        //Starting VideView By Setting MediaController and URI
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();

        videoView.setOnCompletionListener(mp -> {
            // not playVideo
            // playVideo();
            Log.e("FINISH", "FINISH");
            //videoView.setVisibility(View.GONE);

            // checking storage permission

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                getConfigurationData();
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkStoragePermission()) {
                        getConfigurationData();
                    }
                } else {
                    getConfigurationData();
                }
            }
        });

        // Log.e("OneSignal User ID", OneSignal.getDeviceState().getUserId().toString());
    }

    public boolean isLoginMandatory() {
        return db.getConfigurationData().getAppConfig().getMandatoryLogin();
    }

    public void getConfigurationData() {
        String userId = PreferenceUtils.getUserId(this);
        if (!vpnStatus) {
            Retrofit retrofit = RetrofitClient.getRetrofitInstance();
            ConfigurationApi api = retrofit.create(ConfigurationApi.class);
            Call<Configuration> call = api.getConfigurationData(AppConfig.API_KEY, BuildConfig.VERSION_CODE, userId);
            call.enqueue(new Callback<Configuration>() {
                @Override

                public void onResponse(@NonNull Call<Configuration> call, @NonNull Response<Configuration> response) {
                    if (response.code() == 200) {
                        Configuration configuration = response.body();
                        if (configuration != null) {
                            configuration.setId(1);

                            ApiResources.CURRENCY = configuration.getPaymentConfig().getCurrency();
                            ApiResources.PAYPAL_CLIENT_ID = configuration.getPaymentConfig().getPaypalClientId();
                            ApiResources.EXCHSNGE_RATE = configuration.getPaymentConfig().getExchangeRate();
                            ApiResources.RAZORPAY_EXCHANGE_RATE = configuration.getPaymentConfig().getRazorpayExchangeRate();
                            //save genre, country and tv category list to constants
                            Constants.genreList = configuration.getGenre();
                            Constants.countryList = configuration.getCountry();
                            Constants.tvCategoryList = configuration.getTvCategory();

                            db.deleteAllDownloadData();
                            //db.deleteAllAppConfig();
                            if (db.getConfigurationCount() != 1) {
                                db.deleteAllAppConfig();
                                //db.insertConfigurationData(configuration);
                                db.insertConfigurationData(configuration);
                            }
                            //db.updateConfigurationData(configuration, 1);
                            db.updateConfigurationData(configuration, 1);

                            //apk update check
                            if (isNeedUpdate(configuration.getApkUpdateInfo().getVersionCode())) {
                                showAppUpdateDialog(configuration.getApkUpdateInfo());
                                return;
                            }

                            if (db.getConfigurationData() != null) {
                                timer.start();
                            } else {
                                showErrorDialog(getString(R.string.error_toast), getString(R.string.no_configuration_data_found));
                            }
                        } else {
                            showErrorDialog(getString(R.string.error_toast), getString(R.string.failed_to_communicate));
                        }
                    } else if (response.code() == 412) {
                        try {
                            if (response.errorBody() != null) {
                                ApiResources.openLoginScreen(response.errorBody().string(),
                                        SplashScreenActivity.this);
                                finish();
                            }
                        } catch (Exception e) {
                            Toast.makeText(SplashScreenActivity.this,
                                    e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        showErrorDialog(getString(R.string.error_toast), getString(R.string.failed_to_communicate));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Configuration> call, @NonNull Throwable t) {
                    Log.e("ConfigError", t.getLocalizedMessage());
                    showErrorDialog(getString(R.string.error_toast), getString(R.string.failed_to_communicate));
                }
            });
        } else {
            helperUtils.showWarningDialog(SplashScreenActivity.this, getString(R.string.vpn_detected), getString(R.string.close_vpn));
        }
    }

    private void showAppUpdateDialog(final ApkUpdateInfo info) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("New version: " + info.getVersionName())
                .setMessage(info.getWhatsNew())
                .setPositiveButton("Update Now", (dialog, which) -> {
                    //update clicked
                    dialog.dismiss();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(info.getApkUrl()));
                    startActivity(browserIntent);
                    finish();
                })
                .setNegativeButton("Later", (dialog, which) -> {
                    //exit clicked
                    if (info.isSkipable()) {
                        if (db.getConfigurationData() != null) {
                            timer.start();
                        } else {
                            new ToastMsg(SplashScreenActivity.this).toastIconError(getString(R.string.error_toast));
                            finish();
                        }
                    } else {
                        System.exit(0);
                    }
                    dialog.dismiss();
                })
                .setCancelable(false)
                .show();
    }

    private void showErrorDialog(String title, String message) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton("Ok", (dialog, which) -> {
                    System.exit(0);
                    finish();
                })
                .show();
    }

    private boolean isNeedUpdate(String versionCode) {
        return Integer.parseInt(versionCode) > BuildConfig.VERSION_CODE;
    }

    // ------------------ checking storage permission ------------
    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                Log.v(TAG, "Permission is granted");
                return true;

            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            //resume tasks needing this permission
            getConfigurationData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        vpnStatus = helperUtils.isVpnConnectionAvailable();
        if (vpnStatus) {
            helperUtils.showWarningDialog(SplashScreenActivity.this, getString(R.string.vpn_detected), getString(R.string.close_vpn));
        }
    }
}
