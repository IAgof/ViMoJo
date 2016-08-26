/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Juan Javier Cabanas
 */

package com.videonasocialmedia.vimojo;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.karumi.dexter.Dexter;
import com.squareup.leakcanary.LeakCanary;
import io.fabric.sdk.android.Fabric;

public class VimojoApplication extends Application {

    private static Context context;

    Tracker appTracker;

    public static Context getAppContext() {
        return VimojoApplication.context;
    }

    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects (excluding content providers) have been created.
     * Implementations should be as quick as possible (for example using
     * lazy initialization of state) since the time spent in this function
     * directly impacts the performance of starting the first activity,
     * service, or receiver in a process.
     * If you override this method, be sure to call super.onCreate().
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        context = getApplicationContext();
        setupGoogleAnalytics();
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        Dexter.initialize(this);
        setupLeakCanary();
    }

    private void setupGoogleAnalytics() {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        if (BuildConfig.DEBUG)
            analytics.setDryRun(true);
        analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
        appTracker = analytics.newTracker(R.xml.app_tracker);
        appTracker.enableAdvertisingIdCollection(true);
    }

    private void setupLeakCanary() {
        if (BuildConfig.DEBUG) {
            LeakCanary.install(this);
        }
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * @return google analytics tracker
     */
    public synchronized Tracker getTracker() {
        return appTracker;
    }

}