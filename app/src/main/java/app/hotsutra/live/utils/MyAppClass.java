package app.hotsutra.live.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import app.hotsutra.live.AppConfig;
import app.hotsutra.live.BuildConfig;
import app.hotsutra.live.NotificationClickHandler;
import app.hotsutra.live.database.DatabaseHelper;
import app.hotsutra.live.network.RetrofitClient;
import app.hotsutra.live.network.apis.SubscriptionApi;
import app.hotsutra.live.network.model.ActiveStatus;
import com.onesignal.OneSignal;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MyAppClass extends Application {

    public static final String NOTIFICATION_CHANNEL_ID = "download_channel_id";
    public static final String NOTIFICATION_CHANNEL_NAME = "download_channel";
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        Picasso.setSingletonInstance(getCustomPicasso());
        mContext = this;
        createNotificationChannel();

        //OneSignal setup
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.ERROR, OneSignal.LOG_LEVEL.NONE);
        OneSignal.setAppId(AppConfig.ONE_SIGNAL_APP_ID);
        OneSignal.initWithContext(this);
        OneSignal.setNotificationOpenedHandler((OneSignal.OSNotificationOpenedHandler) new NotificationClickHandler(mContext));
        SharedPreferences preferences = getSharedPreferences("push", MODE_PRIVATE);
        OneSignal.disablePush(!preferences.getBoolean("status", true));

        if (!getFirstTimeOpenStatus()) {
            changeSystemDarkMode(AppConfig.DEFAULT_DARK_THEME_ENABLE);
            saveFirstTimeOpenStatus(true);
        }

        // fetched and save the user active status if user is logged in
        String userId = PreferenceUtils.getUserId(this);
        if (userId != null && !userId.equals("")) {
            updateActiveStatus(userId);
        }

        setupActivityListener();
    }

    private Picasso getCustomPicasso() {
        Picasso.Builder builder = new Picasso.Builder(this);
        //set 12% of available app memory for image cachecc
        builder.memoryCache(new LruCache(getBytesForMemCache(12)));
        //set request transformer
        Picasso.RequestTransformer requestTransformer = request -> request;
        builder.requestTransformer(requestTransformer);

        builder.listener((picasso, uri, exception) -> Log.d("image load error", uri.getPath()));

        return builder.build();
    }

    private int getBytesForMemCache(int percent) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager)
                getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);

        double availableMemory = mi.availMem;

        return (int) (percent * availableMemory / 100);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

    public void changeSystemDarkMode(boolean dark) {
        SharedPreferences.Editor editor = getSharedPreferences("push", MODE_PRIVATE).edit();
        editor.putBoolean("dark", dark);
        editor.apply();
    }

    public void saveFirstTimeOpenStatus(boolean dark) {
        SharedPreferences.Editor editor = getSharedPreferences("push", MODE_PRIVATE).edit();
        editor.putBoolean("firstTimeOpen", true);
        editor.apply();

    }

    public boolean getFirstTimeOpenStatus() {
        SharedPreferences preferences = getSharedPreferences("push", MODE_PRIVATE);
        return preferences.getBoolean("firstTimeOpen", false);
    }

    public static Context getContext() {
        return mContext;
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
                    ActiveStatus activeStatus = response.body();
                    DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                    db.deleteAllActiveStatusData();
                    db.insertActiveStatusData(activeStatus);
                }else if (response.code() == 412) {
                    try {
                        if (response.errorBody() != null) {
                            ApiResources.openLoginScreen(response.errorBody().string(),
                                    mContext);
                        }
                    } catch (Exception e) {
                        Toast.makeText(mContext,
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

    private void setupActivityListener() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);            }
            @Override
            public void onActivityStarted(Activity activity) {
            }
            @Override
            public void onActivityResumed(Activity activity) {

            }
            @Override
            public void onActivityPaused(Activity activity) {

            }
            @Override
            public void onActivityStopped(Activity activity) {
            }
            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }
            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }

}
