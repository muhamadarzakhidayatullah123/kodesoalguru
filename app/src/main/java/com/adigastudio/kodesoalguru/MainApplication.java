package com.adigastudio.kodesoalguru;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.google.android.gms.ads.MobileAds;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import androidx.multidex.MultiDex;

public class MainApplication extends Application {
    protected static MainApplication context;
    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(getApplicationContext(), getString(R.string.ad_app_id));
        context = this;
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not checkToken your app in this process.
            return;
        }
        refWatcher = LeakCanary.install(this);
    }

    public static Resources getResource(){
        return context.getResources();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static RefWatcher getRefWatcher(Context context) {
        MainApplication application = (MainApplication) context.getApplicationContext();
        return application.refWatcher;
    }
}