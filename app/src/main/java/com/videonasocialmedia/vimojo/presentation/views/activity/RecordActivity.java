/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.presentation.views.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.videonasocialmedia.avrecorder.view.GLCameraView;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.RecordPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.views.RecordView;
import com.videonasocialmedia.vimojo.presentation.views.customviews.CircleImageView;
import com.videonasocialmedia.vimojo.presentation.views.services.ExportProjectService;
import com.videonasocialmedia.vimojo.utils.AnalyticsConstants;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

/**
 * @author Álvaro Martínez Marco
 */

/**
 * RecordActivity manages a single live record.
 */
public class RecordActivity extends VideonaActivity implements RecordView {

    private final String LOG_TAG = getClass().getSimpleName();

    @Bind(R.id.button_record)
    ImageButton recButton;
    @Bind(R.id.button_share)
    ImageButton shareButton;
    @Bind(R.id.cameraPreview)
    GLCameraView cameraView;
    @Bind(R.id.button_change_camera)
    ImageButton rotateCameraButton;
    @Bind(R.id.imageRecPoint)
    ImageView recordingIndicator;
    @Bind(R.id.chronometer_record)
    Chronometer chronometer;
    @Bind(R.id.button_toggle_flash)
    ImageButton flashButton;
    @Bind(R.id.rotateDeviceHint)
    ImageView rotateDeviceHint;

    private RecordPresenter recordPresenter;
    private boolean buttonBackPressed;
    private boolean recording;
    private OrientationHelper orientationHelper;
    private AlertDialog progressDialog;
    private boolean mUseImmersiveMode = true;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    /**
     * if for result
     **/
    private String resultVideoPath;
    private boolean externalIntent = false;

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String videoToSharePath = bundle.getString(ExportProjectService.FILEPATH);
                int resultCode = bundle.getInt(ExportProjectService.RESULT);
                if (resultCode == RESULT_OK) {
                    hideProgressDialog();
                    goToShare(videoToSharePath);
                } else {
                    hideProgressDialog();
                    showError(R.string.addMediaItemToTrackError);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate");
        setContentView(R.layout.record);
        keepScreenOn();
        ButterKnife.bind(this);
        checkAction();
        sharedPreferences = getSharedPreferences(
                ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        recordPresenter = new RecordPresenter(this, this, cameraView, sharedPreferences, externalIntent);

        configChronometer();
        initOrientationHelper();

        createProgressDialog();

    }

    private void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void checkAction() {
        if (getIntent().getAction() != null) {
            if (getIntent().getAction().equals(MediaStore.ACTION_VIDEO_CAPTURE)) {
                if (getIntent().getClipData() != null) {
                    resultVideoPath = getIntent().getClipData().getItemAt(0).getUri().toString();
                    if (resultVideoPath.startsWith("file://"))
                        resultVideoPath = resultVideoPath.replace("file://", "");
                }
                externalIntent = true;
            }
        }
    }


    private void configChronometer() {
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long elapsedTime = SystemClock.elapsedRealtime() - chronometer.getBase();

                int h = (int) ( elapsedTime / 3600000 );
                int m = (int) ( elapsedTime - h * 3600000 ) / 60000;
                int s = (int) ( elapsedTime - h * 3600000 - m * 60000 ) / 1000;
                // String hh = h < 10 ? "0"+h: h+"";
                String mm = m < 10 ? "0" + m : m + "";
                String ss = s < 10 ? "0" + s : s + "";
                String time = mm + ":" + ss;
                chronometer.setText(time);
            }
        });
    }

    private void initOrientationHelper() {
        orientationHelper = new OrientationHelper(this);
    }



    private void createProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_export_progress, null);
        progressDialog = builder.setCancelable(false)
                .setView(dialogView)
                .create();
    }

    @Override
    protected void onDestroy() {
        recordPresenter.onDestroy();
        cameraView = null;
        Log.d(LOG_TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onPause() {
        unregisterReceiver(receiver);
        recordPresenter.onPause();
        orientationHelper.stopMonitoringOrientation();
        Log.d(LOG_TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
        registerReceiver(receiver, new IntentFilter(ExportProjectService.NOTIFICATION));
        recordPresenter.onResume();
        recording = false;
        hideSystemUi();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart");
        recordPresenter.onStart();
    }

    private void hideSystemUi() {
        if (!Utils.isKitKat() || !mUseImmersiveMode) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else if (mUseImmersiveMode) {
            setKitKatWindowFlags();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setKitKatWindowFlags() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    @Override
    protected void onStop() {
        recordPresenter.onStop();
        Log.d(LOG_TAG, "OnStop");
        super.onStop();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (Utils.isKitKat() && hasFocus && mUseImmersiveMode) {
            setKitKatWindowFlags();
        }
    }

    @OnTouch(R.id.button_record)
    boolean onTouch(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!recording) {
                recordPresenter.requestRecord();
            } else {
                recordPresenter.stopRecord();
            }
        }
        return true;
    }


    @Override
    public void showRecordButton() {
        recButton.setImageResource(R.drawable.activity_record_icon_rec_normal);
        recButton.setAlpha(1f);
        recording = false;
        unLockNavigator();

    }

    @Override
    public void showStopButton() {
        recButton.setImageResource(R.drawable.activity_record_icon_stop_normal);
        recButton.setAlpha(1f);
        recording = true;
        lockNavigator();
    }

    @Override
    public void showMenuOptions() {

    }

    @Override
    public void hideMenuOptions() {

    }


    @Override
    public void showChronometer() {
        chronometer.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideChronometer() {
        chronometer.setVisibility(View.INVISIBLE);
    }


    @Override
    public void startChronometer() {
        resetChronometer();
        chronometer.start();
        showRecordingIndicator();
    }

    private void resetChronometer() {
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.setText("00:00");
    }

    private void showRecordingIndicator() {
        recordingIndicator.setVisibility(View.VISIBLE);
        AnimationDrawable frameAnimation = (AnimationDrawable) recordingIndicator.getDrawable();
        frameAnimation.setCallback(recordingIndicator);
        frameAnimation.setVisible(true, true);
    }

    @Override
    public void stopChronometer() {
        chronometer.stop();
        hideRecordingIndicator();
    }

    private void hideRecordingIndicator() {
        recordingIndicator.setVisibility(View.INVISIBLE);
    }


    @Override
    public void lockScreenRotation() {
        orientationHelper.stopMonitoringOrientation();
    }

    @Override
    public void unlockScreenRotation() {

        try {
            orientationHelper.startMonitoringOrientation();
        } catch (OrientationHelper.NoOrientationSupportException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reStartScreenRotation() {
        try {
            orientationHelper.startMonitoringOrientation();
        } catch (OrientationHelper.NoOrientationSupportException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void lockNavigator() {

    }

    @Override
    public void unLockNavigator() {

    }

    @Override
    public void showFlashOn(boolean on) {
        trackUserInteracted(AnalyticsConstants.CHANGE_FLASH, String.valueOf(on));
        flashButton.setActivated(on);
    }

    @Override
    public void showFlashSupported(boolean supported) {
        if (supported) {
            flashButton.setImageAlpha(255);
            flashButton.setActivated(false);
            flashButton.setActivated(false);
            flashButton.setEnabled(true);
        } else {
            flashButton.setImageAlpha(65);
            flashButton.setActivated(false);
            flashButton.setEnabled(false);
        }
    }

    @Override
    public void showFrontCameraSelected() {
        rotateCameraButton.setActivated(false);
        trackUserInteracted(AnalyticsConstants.CHANGE_CAMERA, AnalyticsConstants.CAMERA_FRONT);
        try {
            orientationHelper.reStartMonitoringOrientation();
        } catch (OrientationHelper.NoOrientationSupportException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showBackCameraSelected() {
        rotateCameraButton.setActivated(false);
        trackUserInteracted(AnalyticsConstants.CHANGE_CAMERA, AnalyticsConstants.CAMERA_BACK);
        try {
            orientationHelper.reStartMonitoringOrientation();
        } catch (OrientationHelper.NoOrientationSupportException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        mixpanel.track(AnalyticsConstants.VIDEO_EXPORTED);
    }

    @Override
    public void showError(int stringResourceId) {
        Toast.makeText(this, this.getText(stringResourceId), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void goToShare(String videoToSharePath) {
        trackVideoExported();
        saveVideoFeaturesToConfig();
        Intent intent = new Intent(this, ShareActivity.class);
        intent.putExtra(Constants.VIDEO_TO_SHARE_PATH, videoToSharePath);
        startActivity(intent);
    }

    @Override
    public void showProgressDialog() {
        progressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }


    public void showMessage(final int message) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void finishActivityForResult(String originalVideoPath) {
        try {
            if (resultVideoPath != null) {
                Utils.copyFile(originalVideoPath, resultVideoPath);
                Utils.removeVideo(originalVideoPath);
            } else
                resultVideoPath = originalVideoPath;
            Uri videoUri = Uri.fromFile(new File(resultVideoPath));
            Intent returnIntent = new Intent();
            returnIntent.setData(videoUri);
            setResult(RESULT_OK, returnIntent);
            finish();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void trackVideoExported() {
        JSONObject videoExportedProperties = new JSONObject();
        try {
            int projectDuration = recordPresenter.getProjectDuration();
            int numVideosOnProject = recordPresenter.getNumVideosOnProject();
            videoExportedProperties.put(AnalyticsConstants.VIDEO_LENGTH, projectDuration);
            videoExportedProperties.put(AnalyticsConstants.RESOLUTION,
                    recordPresenter.getResolution());
            videoExportedProperties.put(AnalyticsConstants.NUMBER_OF_CLIPS, numVideosOnProject);
            mixpanel.track(AnalyticsConstants.VIDEO_EXPORTED, videoExportedProperties);
            Log.d("ANALYTICS", "Tracked video exported event");
        } catch (JSONException e) {
            Log.e("TRACK_FAILED", String.valueOf(e));
        }
    }

    private void saveVideoFeaturesToConfig() {
        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
        preferencesEditor.putLong(ConfigPreferences.VIDEO_DURATION, recordPresenter.getProjectDuration());
        preferencesEditor.putInt(ConfigPreferences.NUMBER_OF_CLIPS, recordPresenter.getNumVideosOnProject());
        preferencesEditor.putString(ConfigPreferences.RESOLUTION, recordPresenter.getResolution());
        preferencesEditor.commit();
    }

    private void trackUserInteracted(String interaction, String result) {
        JSONObject userInteractionsProperties = new JSONObject();
        try {
            userInteractionsProperties.put(AnalyticsConstants.ACTIVITY, getClass().getSimpleName());
            userInteractionsProperties.put(AnalyticsConstants.RECORDING, recording);
            userInteractionsProperties.put(AnalyticsConstants.INTERACTION, interaction);
            userInteractionsProperties.put(AnalyticsConstants.RESULT, result);
            mixpanel.track(AnalyticsConstants.USER_INTERACTED, userInteractionsProperties);
            Log.d("ANALYTICS", "Tracked User Interacted event");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @OnClick(R.id.button_toggle_flash)
    public void toggleFlash() {
        recordPresenter.toggleFlash();
    }

    @Override
    public void onBackPressed() {
        if (buttonBackPressed) {
            buttonBackPressed = false;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
            startActivity(intent);
            finish();
            System.exit(0);
        } else {
            buttonBackPressed = true;
            Toast.makeText(getApplicationContext(), getString(R.string.toast_exit),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.button_change_camera)
    public void changeCamera() {
        recordPresenter.setFlashOff();
        recordPresenter.changeCamera();
    }

    @OnClick(R.id.edit_button)
    public void navigateToEdit() {
        if (!recording) {
            //TODO(alvaro 130616) Save flash state
            recordPresenter.setFlashOff();
            Intent edit = new Intent(this, EditActivity.class);
            //edit.putExtra("SHARE", false);
            startActivity(edit);
        }
    }

    @OnClick(R.id.button_share)
    public void exportAndShare() {
        if (!recording) {
            recordPresenter.setFlashOff();
            Intent intent = new Intent(this, ExportProjectService.class);
            startService(intent);
            showProgressDialog();
            mixpanel.timeEvent(AnalyticsConstants.VIDEO_EXPORTED);
        }
    }

    private class OrientationHelper extends OrientationEventListener {

        Context context;
        private int rotationView;
        private boolean orientationHaveChanged = false;
        private boolean isNormalOrientation;

        public OrientationHelper(Context context) {
            super(context);

            this.context = context;
        }

        /**
         *
         */
        public void startMonitoringOrientation() throws NoOrientationSupportException {
            rotationView = ( (Activity) context ).getWindowManager().getDefaultDisplay().getRotation();
            if (rotationView == Surface.ROTATION_90) {
                isNormalOrientation = true;
                orientationHaveChanged = false;
            } else {
                isNormalOrientation = false;
            }
            determineOrientation(rotationView);
            if (this.canDetectOrientation()) {
                this.enable();
            } else {
                this.disable();
                throw new NoOrientationSupportException();
            }
        }

        private void determineOrientation(int rotationView) {
            Log.d(LOG_TAG, " determineOrientation" + " rotationView " + rotationView);
            int rotation = -1;
            if (rotationView == Surface.ROTATION_90) {
                rotation = 90;
                recordPresenter.rotateCamera(rotationView);
            } else {
                if (rotationView == Surface.ROTATION_270) {
                    rotation = 270;
                    recordPresenter.rotateCamera(rotationView);
                }
            }
            Log.d(LOG_TAG, "determineOrientation rotationPreview " + rotation +
                    " cameraInfoOrientation ");
        }

        public void stopMonitoringOrientation() {
            this.disable();
        }

        public void reStartMonitoringOrientation() throws NoOrientationSupportException {
            rotationView = ( (Activity) context ).getWindowManager().getDefaultDisplay().getRotation();
            if (rotationView == Surface.ROTATION_90) {
                isNormalOrientation = true;
                orientationHaveChanged = false;
            } else {
                isNormalOrientation = false;
            }
            determineOrientation(rotationView);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation > 85 && orientation < 95) {
                if (isNormalOrientation && !orientationHaveChanged) {
                    Log.d(LOG_TAG, "onOrientationChanged  rotationView changed " + orientation);
                    recordPresenter.rotateCamera(Surface.ROTATION_270);
                    orientationHaveChanged = true;
                } else {
                    if (orientationHaveChanged) {
                        Log.d(LOG_TAG, "onOrientationChanged  rotationView changed " + orientation);
                        recordPresenter.rotateCamera(Surface.ROTATION_270);
                        orientationHaveChanged = false;
                    }
                }
            } else if (orientation > 265 && orientation < 275) {
                if (isNormalOrientation) {
                    if (orientationHaveChanged) {
                        Log.d(LOG_TAG, "onOrientationChanged  rotationView changed " + orientation);
                        recordPresenter.rotateCamera(Surface.ROTATION_90);
                        orientationHaveChanged = false;
                    }
                } else {
                    if (!orientationHaveChanged) {
                        Log.d(LOG_TAG, "onOrientationChanged  rotationView changed " + orientation);
                        recordPresenter.rotateCamera(Surface.ROTATION_90);
                        orientationHaveChanged = true;
                    }
                }
            }
            checkShowRotateDevice(orientation);
        }

        // Show image to Rotate Device
        private void checkShowRotateDevice(int orientation) {
            if (( orientation > 345 || orientation < 15 ) && orientation != -1) {
                rotateDeviceHint.setRotation(270);
                rotateDeviceHint.setRotationX(0);
                rotateDeviceHint.setVisibility(View.VISIBLE);
            } else if (orientation > 165 && orientation < 195) {
                rotateDeviceHint.setRotation(-270);
                rotateDeviceHint.setRotationX(180);
                rotateDeviceHint.setVisibility(View.VISIBLE);
            } else {
                rotateDeviceHint.setVisibility(View.GONE);
            }
        }

        private class NoOrientationSupportException extends Exception {
        }
    }

}