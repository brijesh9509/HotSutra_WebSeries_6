package com.instamojo.android;

import android.app.Application;

public class InstamojoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // By default SDK gets initialized for TEST environment
        Instamojo.getInstance().initialize(this, Instamojo.Environment.TEST);
    }
}
