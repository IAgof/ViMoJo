/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Juan Javier Cabanas
 */

package com.videonasocialmedia.vimojo.main;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.FirebaseApp;
import com.karumi.dexter.Dexter;
import com.squareup.leakcanary.LeakCanary;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.composition.domain.usecase.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.composition.domain.usecase.SaveComposition;
import com.videonasocialmedia.vimojo.main.modules.ApplicationModule;
import com.videonasocialmedia.vimojo.main.modules.DataRepositoriesModule;
import com.videonasocialmedia.vimojo.main.modules.ActivityPresentersModule;
import com.videonasocialmedia.vimojo.main.modules.TrackerModule;
import com.videonasocialmedia.vimojo.main.modules.UploadToPlatformModule;
import com.videonasocialmedia.vimojo.main.modules.VimojoApplicationModule;
import com.videonasocialmedia.vimojo.model.VimojoMigration;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.repository.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.util.concurrent.Executors;

import javax.inject.Inject;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;

import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_CAMERA_SETTINGS_CAMERA_ID_SELECTED;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_CAMERA_SETTINGS_CAMERA_ID_SELECTED_VERTICAL_APP;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_WATERMARK_STATE;

public class VimojoApplication extends Application implements ProjectInstanceCache {
    private SystemComponent systemComponent;
    private static Context context;

    Tracker appTracker;
    private DataRepositoriesModule dataRepositoriesModule;
    private VimojoApplicationModule vimojoApplicationModule;
    private ApplicationModule applicationModule;
    /**
     * Project instance across all application
     */
    private Project currentProject;

    @Inject ProjectRepository projectRepository;
    @Inject CreateDefaultProjectUseCase createDefaultProjectUseCase;
    @Inject SharedPreferences sharedPreferences;
    @Inject SaveComposition saveComposition;

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
        initSystemComponent();
        // Set up Crashlytics, disabled for debug builds
        Crashlytics crashlyticsKit = new Crashlytics.Builder()
            .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
            .build();
        Fabric.with(this, crashlyticsKit);
        context = getApplicationContext();
        setupGoogleAnalytics();
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        Dexter.initialize(this);
        setupLeakCanary();
        setupDataBase();
        DaggerVimojoApplicationComponent.builder()
                .vimojoApplicationModule(getVimojoApplicationModule())
                .dataRepositoriesModule(getDataRepositoriesModule())
                .build().inject(this);
    }

    void initSystemComponent() {
        this.systemComponent = DaggerSystemComponent.builder()
                .applicationModule(getApplicationModule())
                .dataRepositoriesModule(getDataRepositoriesModule())
                .trackerModule(getTrackerModule())
                .uploadToPlatformModule(getUploadToPlatformModule())
                .build();
    }

    @NonNull
    public ApplicationModule getApplicationModule() {
        if (applicationModule == null) {
            applicationModule = new ApplicationModule(this);
        }
        return applicationModule;
    }

    private TrackerModule getTrackerModule() {
        return new TrackerModule(this);
    }

    private UploadToPlatformModule getUploadToPlatformModule() {
        return new UploadToPlatformModule(this);
    }

    private ActivityPresentersModule getActivityPresentersModule() {
        return new ActivityPresentersModule(null);
    }

    public SystemComponent getSystemComponent() {
        return this.systemComponent;
    }

    public DataRepositoriesModule getDataRepositoriesModule() {
        if (dataRepositoriesModule == null) {
            dataRepositoriesModule = new DataRepositoriesModule();
        }
        return dataRepositoriesModule;
    }

    private void setupGoogleAnalytics() {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        if (BuildConfig.DEBUG)
            analytics.setDryRun(true);
        analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
        appTracker = analytics.newTracker(R.xml.app_tracker);
        appTracker.enableAdvertisingIdCollection(true);

      FirebaseApp.initializeApp(context);
    }

    private void setupLeakCanary() {
        if (BuildConfig.DEBUG) {
            if (LeakCanary.isInAnalyzerProcess(this)) {
                // This process is dedicated to LeakCanary for heap analysis.
                // You should not init your app in this process.
                return;
            }
            LeakCanary.install(this);
        }
    }

    public VimojoApplicationModule getVimojoApplicationModule() {
        int defaultCameraIdSelected = DEFAULT_CAMERA_SETTINGS_CAMERA_ID_SELECTED;
        if (BuildConfig.FEATURE_VERTICAL_VIDEOS) {
            defaultCameraIdSelected = DEFAULT_CAMERA_SETTINGS_CAMERA_ID_SELECTED_VERTICAL_APP;
        }
        if (vimojoApplicationModule == null) {
            vimojoApplicationModule = new VimojoApplicationModule(this,
                defaultCameraIdSelected);
        }
        return vimojoApplicationModule;
    }

    protected void setupDataBase() {
        // initialize Realm
        Realm.init(getApplicationContext());

        // create your Realm configuration
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name("vimojoDB")
                .schemaVersion(13) //from v0.14.4 8-3-2018 to v0.19.0 7-6-2018
                .migration(new VimojoMigration())
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
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

    @Override
    public void setCurrentProject(Project currentProject) {
        synchronized (VimojoApplication.class) {
            this.currentProject = currentProject;
        }
    }

    @Override
    public Project getCurrentProject() {
        if (currentProject == null) {
            currentProject = getDefaultProjectInstance();
        }
        return currentProject;
    }

    public Project getDefaultProjectInstance() {
        if (projectRepositoryIsEmpty()) {
            Drawable drawableFadeTransitionVideo = getDrawable(R.drawable.alpha_transition_white);
            Project project = createDefaultProjectUseCase.createProject(Constants.PATH_APP,
                    Constants.PATH_APP_ANDROID, isWatermarkActivated(),
                    drawableFadeTransitionVideo, BuildConfig.FEATURE_VERTICAL_VIDEOS);
            ListeningExecutorService executorPool = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(1));
            executorPool.submit(() -> saveComposition.saveComposition(project));
            return project;
        } else {
            return projectRepository.getLastModifiedProject();
        }
    }

    private boolean isWatermarkActivated() {
        return BuildConfig.FEATURE_FORCE_WATERMARK || sharedPreferences.getBoolean(ConfigPreferences.WATERMARK, DEFAULT_WATERMARK_STATE);
    }

    private boolean projectRepositoryIsEmpty() {
        return (projectRepository.getLastModifiedProject() == null);
    }
}
