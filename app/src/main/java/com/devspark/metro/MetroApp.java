package com.devspark.metro;

import android.app.Application;
import android.os.StrictMode;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class MetroApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            enableStrictMode();
        } else {
            enableCrashlytics();
        }
    }

    private void enableStrictMode() {
        StrictMode.setVmPolicy(
                new StrictMode.VmPolicy.Builder()
                        .detectAll()
                        .penaltyLog()
                        .penaltyDeath()
                        .build());
        StrictMode.setThreadPolicy(
                new StrictMode.ThreadPolicy.Builder()
                        .detectAll()
                        .penaltyLog()
                        .penaltyDeath()
                        .build());
    }

    private void enableCrashlytics() {
        Fabric.with(this, new Crashlytics());
    }
}