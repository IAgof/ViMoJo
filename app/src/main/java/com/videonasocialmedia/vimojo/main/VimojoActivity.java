/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.main;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.analytics.Tracker;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.EmptyMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.domain.editor.LoadCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.main.modules.ActivityPresentersModule;
import com.videonasocialmedia.vimojo.presentation.views.activity.InitAppActivity;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.AnalyticsConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import javax.inject.Inject;

/**
 * Videona base activity.
 *
 * @author vlf
 * @since 04/05/2015
 */
public abstract class VimojoActivity extends AppCompatActivity {
    private final TrackerDelegate trackerDelegate = new TrackerDelegate();
    protected MixpanelAPI mixpanel;
    protected Tracker tracker;
    protected boolean criticalPermissionDenied = false;
    protected MultiplePermissionsListener dialogMultiplePermissionsListener;
    @Inject protected LoadCurrentProjectUseCase loadCurrentProjectUseCase;
    @Inject ProjectRepository projectRepository;

    public SystemComponent getSystemComponent() {
        return ((VimojoApplication)getApplication()).getSystemComponent();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSystemComponent().inject(this);
        loadCurrentProjectUseCase.loadCurrentProject();
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
        configPermissions();
        trackerDelegate.onCreate();

        View root = findViewById(android.R.id.content);
    }

    private void configPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dialogMultiplePermissionsListener = DialogOnAnyDeniedMultiplePermissionsListener.Builder
                    .withContext(this)
                    .withTitle("Camera & audio permission")
                    .withMessage("Both camera and audio permission are needed to take awsome videos with Videona")
                    .withButtonText(android.R.string.ok)
                    .build();
            Dexter.continuePendingRequestsIfPossible(dialogMultiplePermissionsListener);
        }
    }

    protected ActivityPresentersComponent getActivityPresentersComponent() {
        return DaggerActivityPresentersComponent.builder()
                .activityPresentersModule(getActivityPresentersModule())
                .systemComponent(((VimojoApplication)getApplication()).getSystemComponent())
                .build();
    }

    @NonNull
    public ActivityPresentersModule getActivityPresentersModule() {
        return new ActivityPresentersModule(this);
    }

//    protected UserEventTracker getUserEventTracker() {
//        return UserEventTracker.getInstance(MixpanelAPI
//                .getInstance(this, BuildConfig.MIXPANEL_TOKEN));
//    }

    @Override
    protected void onDestroy() {
        mixpanel.flush();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JSONObject activityProperties = new JSONObject();
        try {
            activityProperties.put(AnalyticsConstants.ACTIVITY, getClass().getSimpleName());
            mixpanel.track(AnalyticsConstants.TIME_IN_ACTIVITY, activityProperties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mixpanel.timeEvent(AnalyticsConstants.TIME_IN_ACTIVITY);
    }

    protected final void closeApp() {
        Intent intent = new Intent(getApplicationContext(), InitAppActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }

    protected void checkAndRequestPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            checkContacts();
            Dexter.checkPermissions(dialogMultiplePermissionsListener, Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_CONTACTS);
        }
    }

//    private void checkContacts() {
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, PermissionConstants.PERMISSIONS_CONTACTS,
//                    PermissionConstants.REQUEST_CONTACTS);
//        }
//    }


    protected boolean isLandscapeOriented() {
        return getOrientation() == Configuration.ORIENTATION_LANDSCAPE;
    }

    private int getOrientation() {
        return getResources().getConfiguration().orientation;
    }

    public boolean isPortraitOriented() {
        return getOrientation() == Configuration.ORIENTATION_PORTRAIT;
    }

    class CustomPermissionListener extends EmptyMultiplePermissionsListener {

        private final Context context;
        private final String title;
        private final String message;
        private final String positiveButtonText;
        private final Drawable icon;

        private AlertDialog dialog;

        private CustomPermissionListener(Context context, String title,
                                         String message, String positiveButtonText, Drawable icon) {
            this.context = context;
            this.title = title;
            this.message = message;
            this.positiveButtonText = positiveButtonText;
            this.icon = icon;
        }

        @Override
        public void onPermissionsChecked(MultiplePermissionsReport report) {
            super.onPermissionsChecked(report);

            if (!report.areAllPermissionsGranted()) {
                showDialog();
            } else {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        }

        @Override
        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions,
                                                       PermissionToken token) {
            super.onPermissionRationaleShouldBeShown(permissions, token);
            token.continuePermissionRequest();
        }

        private void showDialog() {
            dialog = new AlertDialog.Builder(context, R.style.VideonaAlertDialog)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.parse("package:" + context.getPackageName()));
                            myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                            myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(myAppSettings);
                        }
                    })
                    .setIcon(icon)
                    .show();
        }
    }

    // TODO(jliarte): 27/10/16 move this delegate out of the class.
    //                Maybe its necessary to move all mixpanel uses to UserEventTracker class
    private class TrackerDelegate {
        protected static final String ANDROID_PUSH_SENDER_ID = "783686583047";

        public void onCreate() {
            mixpanel = MixpanelAPI.getInstance(VimojoActivity.this, BuildConfig.MIXPANEL_TOKEN);
            if (mixpanel != null) {
                mixpanel.getPeople().identify(mixpanel.getPeople().getDistinctId());
                mixpanel.getPeople().initPushHandling(ANDROID_PUSH_SENDER_ID);
            }
            VimojoApplication app = (VimojoApplication) getApplication();
            tracker = app.getTracker();
        }
    }
}