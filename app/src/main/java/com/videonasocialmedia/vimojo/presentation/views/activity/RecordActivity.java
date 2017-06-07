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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.videonasocialmedia.avrecorder.view.GLCameraView;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.main.modules.ActivityPresentersModule;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.RecordPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.views.RecordView;
import com.videonasocialmedia.vimojo.presentation.views.broadcastreceiver.BatteryReceiver;
import com.videonasocialmedia.vimojo.presentation.views.customviews.CircleImageView;
import com.videonasocialmedia.vimojo.presentation.views.services.ExportProjectService;
import com.videonasocialmedia.vimojo.settings.presentation.views.activity.SettingsActivity;
import com.videonasocialmedia.vimojo.utils.AnalyticsConstants;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.IntentConstants;
import com.videonasocialmedia.vimojo.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

import static com.videonasocialmedia.vimojo.utils.UIUtils.tintButton;

/**
 * @author Álvaro Martínez Marco
 */

/**
 * RecordActivity manages a single live record.
 * @deprecated Update and clean activity RecordCamera2Activity. Delete dependency with camera1 and avrecorder module
 */
public class RecordActivity extends VimojoActivity implements RecordView {
    private final String LOG_TAG = getClass().getSimpleName();
    private final int RESOLUTION_SELECTED_HD720 = 720;
    private final int RESOLUTION_SELECTED_HD1080 = 1080;
    private final int RESOLUTION_SELECTED_HD4K = 2160;
    @Inject RecordPresenter recordPresenter;
    @Inject SharedPreferences sharedPreferences;

    @Bind(R.id.button_record)
    ImageButton recButton;
    @Bind(R.id.cameraPreview)
    GLCameraView cameraView;
    @Bind(R.id.button_change_camera)
    ImageButton rotateCameraButton;
    @Bind(R.id.button_navigate_settings)
    ImageButton buttonNavigateSettings;
    @Bind(R.id.button_settings_camera)
    ImageButton settingsCameraButton;
    @Bind(R.id.button_grid)
    ImageButton gridButton;
    @Bind(R.id.button_navigate_edit_or_gallery)
    CircleImageView buttonThumbClipRecorded;
    @Bind(R.id.text_view_num_videos)
    TextView numVideosRecorded;
    @Bind(R.id.record_text_view_edit_or_gallery)
    TextView editText;
    @Bind(R.id.imageRecPoint)
    ImageView recordingIndicator;
    @Bind(R.id.chronometer_record)
    Chronometer chronometer;
    @Bind(R.id.button_toggle_flash)
    ImageButton flashButton;
    @Bind(R.id.rotateDeviceHint)
    ImageView rotateDeviceHint;
    @Bind(R.id.clear_button)
    ImageButton clearButton;
    @Bind(R.id.hud)
    View hud;
    @Bind(R.id.control_chronometer_and_rec_point)
    View chronometerAndRecPointView;
    @Bind(R.id.picometer)
    View picometer;
    @Bind(R.id.controls)
    View controlsView;
    @Bind(R.id.zoom_submenu)
    View zommBarView;
    @Bind(R.id.settings_bar)
    View settingsBar;
    @Bind(R.id.white_balance_submenu)
    View settingsBarSubmenu;
    @Bind(R.id.button_to_hide_controls)
    ImageButton buttonToHideControlsView;
    @Bind (R.id.button_to_show_controls_right)
    ImageButton buttonToShowControls;
    @Bind(R.id.button_resolution_indicator)
    ImageView resolutionIndicator;
    @Bind(R.id.image_view_grid)
    ImageView imageViewGrid;
    @Bind(R.id.activity_record_icon_battery)
    ImageButton battery;
    @Bind(R.id.activity_record_icon_storage)
    ImageButton memory;

    @Nullable @Bind(R.id.progressBar_level)
    ProgressBar progressBarBatteryOrMemory;
    @Nullable @Bind(R.id.text_percent_level)
    TextView percentLevel;
    @Nullable @Bind(R.id.text_message)
    TextView freeMemorySpace;


    private boolean buttonBackPressed;
    private boolean recording;
    private OrientationHelper orientationHelper;
    private AlertDialog progressDialog;
    private AlertDialog progressDialogBatteryOrMemory;
    private boolean mUseImmersiveMode = true;
//    private SharedPreferences sharedPreferences;
//    private SharedPreferences.Editor editor;
    private boolean isProjectHasVideo= false;

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
                if (videoToSharePath!=null) {
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
        }
    };
    private BatteryReceiver batteryReceiver = new BatteryReceiver(){
        @Override
        public void onReceive(Context context, Intent intent){
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                updateBatteryStatus();
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
        setupActivityButtons();
        checkAction();

//        sharedPreferences = getSharedPreferences(
//                ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
//                Context.MODE_PRIVATE);
//        editor = sharedPreferences.edit();

        this.getActivityPresentersComponent().inject(this);
//        recordPresenter = new RecordPresenter(VimojoApplication.getAppContext(), this, cameraView, sharedPreferences, externalIntent);

        configChronometer();
        initOrientationHelper();
        createProgressDialog();
        createProgressDialogBatteryOrMemory();
        configShowThumbAndNumberClips();
    }

    @Override
    public ActivityPresentersModule getActivityPresentersModule() {
        return new ActivityPresentersModule(this, externalIntent, cameraView);
    }

    private void configShowThumbAndNumberClips() {
        buttonThumbClipRecorded.setBorderWidth(5);
        buttonThumbClipRecorded.setBorderColor(Color.WHITE);
        numVideosRecorded.setVisibility(View.GONE);
        editText.setVisibility(View.VISIBLE);
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

  private void createProgressDialogBatteryOrMemory() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.VideonaAlertDialog);
    View dialogView = getLayoutInflater().inflate(R.layout.alert_dialog_with_info_into_circle, null);
    progressBarBatteryOrMemory = (ProgressBar) dialogView.findViewById(R.id.progressBar_level);
    percentLevel = (TextView) dialogView.findViewById(R.id.text_percent_level);
    freeMemorySpace=(TextView)dialogView.findViewById(R.id.text_message);
    progressDialogBatteryOrMemory = builder.setCancelable(true)
        .setView(dialogView)
        .setTitle("Información detallada")
        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
          }
        })
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
        Log.d(LOG_TAG, "onPause");
        if(recording)
            recordPresenter.stopRecord();
        unregisterReceiver(receiver);
        unregisterReceiver(batteryReceiver);
        orientationHelper.stopMonitoringOrientation();
        recordPresenter.onPause();
        super.onPause();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "init");
        checkReceiver();
        updateBatteryStatus();
        updatePercentFreeMemory();
        recordPresenter.onResume();
        recording = false;
        hideSystemUi();

    }



  private void checkReceiver() {
        registerReceiver(receiver, new IntentFilter(ExportProjectService.NOTIFICATION));
        registerReceiver(batteryReceiver,new IntentFilter(IntentConstants.BATTERY_NOTIFICATION));
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
                updateBatteryStatus();
                updatePercentFreeMemory();
            }
        }
        return true;
    }

    public void updateBatteryStatus() {
        Intent batteryStatus = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        recordPresenter.updateBatteryStatus(status, level, scale);
    }

    private void updatePercentFreeMemory() {
      StatFs statFs= new StatFs(Environment.getDataDirectory().getPath());
      long totalMemory= getTotalMemory(statFs);
      long freeMemory= getFreeMemory(statFs);
      recordPresenter.updateFreeMemorySpace(totalMemory,freeMemory);
    }

    private long getTotalMemory(StatFs statFs) {
      long   totalMemory  = (statFs.getBlockCountLong() *  statFs.getBlockSizeLong());
      return totalMemory;
    }
    private long getFreeMemory(StatFs statFs) {
      long   freeMemory   = (statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong());
      return freeMemory;
    }

  @Override
    public void showRecordButton() {
        recButton.setImageResource(R.drawable.activity_record_ic_rec);
        recording = false;
    }

    @Override
    public void showStopButton() {
        recButton.setImageResource(R.drawable.activity_record_icon_stop);
        recording = true;
    }

    @Override
    public void showSettingsOptions() {
        buttonNavigateSettings.setEnabled(true);
    }

    @Override
    public void hideSettingsOptions() {
        buttonNavigateSettings.setEnabled(false);
    }


    @Override
    public void showChronometer() {
        chronometerAndRecPointView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideChronometer() {
        chronometerAndRecPointView.setVisibility(View.INVISIBLE);
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
    public void showFlashOn(boolean on) {
        trackUserInteracted(AnalyticsConstants.CHANGE_FLASH, String.valueOf(on));
        flashButton.setActivated(on);
        flashButton.setSelected(on);
    }

    @Override
    public void showFlashSupported(boolean supported) {
        flashButton.setActivated(false);
        if (supported) {
            flashButton.setEnabled(true);
        } else {
            flashButton.setEnabled(false);
        }
    }

    @Override
    public void showFrontCameraSelected() {
       //TODO: (28/10/2016) No debería ser setActivate(true)?
        rotateCameraButton.setActivated(false);
        rotateCameraButton.setSelected(true);
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
        rotateCameraButton.setSelected(false);
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

    @Override
    public void hidePrincipalViews() {
        clearButton.setImageResource(R.drawable.activity_record_ic_shrink);
        clearButton.setAlpha(0.5f);
        clearButton.setActivated(true);
        controlsView.setVisibility(View.INVISIBLE);
        buttonToHideControlsView.setVisibility(View.INVISIBLE);
        picometer.setVisibility(View.INVISIBLE);
        zommBarView.setVisibility(View.INVISIBLE);
        settingsBarSubmenu.setVisibility(View.INVISIBLE);
        settingsBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showPrincipalViews() {
        clearButton.setImageResource(R.drawable.activity_record_ic_expand);
        clearButton.setBackground(getResources().getDrawable(R.drawable.circle_background));
        clearButton.setAlpha(1f);
        clearButton.setActivated(false);
        hud.setVisibility(View.VISIBLE);
        buttonToShowControls.setVisibility(View.VISIBLE);
    }

    @Override
    public void showRecordedVideoThumbWithText(String path) {
        buttonThumbClipRecorded.setVisibility(View.VISIBLE);
        Glide.with(this).load(path).into(buttonThumbClipRecorded);
        editText.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideRecordedVideoThumbWithText() {
        buttonThumbClipRecorded.setVisibility(View.INVISIBLE);
        editText.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showVideosRecordedNumber(int numberOfVideos) {
        numVideosRecorded.setVisibility(View.VISIBLE);
        numVideosRecorded.setText(String.valueOf(numberOfVideos));
        editText.setText(getString(R.string.recordTextEdit));
        isProjectHasVideo=true;
    }

    @Override
    public void hideVideosRecordedNumber() {
        numVideosRecorded.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showResolutionSelected(int resolutionSelected) {
        switch (resolutionSelected){
            case (RESOLUTION_SELECTED_HD720):
              resolutionIndicator.setImageResource(R.drawable.activity_record_ic_resolution_720);
                break;
            case(RESOLUTION_SELECTED_HD1080):
                resolutionIndicator.setImageResource(R.drawable.activity_record_ic_resolution_1080);
                break;
            case (RESOLUTION_SELECTED_HD4K):
                resolutionIndicator.setImageResource(R.drawable.activity_record_ic_resolution_4k);
                break;
            default:
                resolutionIndicator.setImageResource(R.drawable.activity_record_ic_resolution_720);
                break;
        }
    }

    @Override
    public void showBatteryStatus(Constants.BATTERY_STATUS batteryStatus, int batteryPercent) {
      progressDialogBatteryOrMemory.setTitle(R.string.battery);
      freeMemorySpace.setVisibility(View.GONE);
      switch (batteryStatus) {
             case CHARGING:
                 battery.setImageResource(R.drawable.activity_record_ic_battery_charging);
                 break;
            case FULL:
                battery.setImageResource(R.drawable.activity_record_ic_battery_full);
                break;
            case MEDIUM:
                battery.setImageResource(R.drawable.activity_record_ic_battery_medium);
                break;
            case LOW:
                battery.setImageResource(R.drawable.activity_record_ic_battery_low);
                break;
            case CRITICAL:
                battery.setImageResource(R.drawable.activity_record_ic_battery_alert);
                break;
             default:
               battery.setImageResource(R.drawable.activity_record_ic_battery_full);
         }
         if(progressDialogBatteryOrMemory.isShowing()){
           updateProgressBarBattery(batteryStatus, batteryPercent);
           updatePercentBattery(batteryPercent);
         }
    }

  private void updatePercentBattery(int batteryPercent) {
    percentLevel.setText(batteryPercent + " %");
  }

  private void updateProgressBarBattery(Constants.BATTERY_STATUS batteryStatus, int batteryPercent) {
    progressBarBatteryOrMemory.setProgress(batteryPercent);
    setColorProgressBarBattery(batteryStatus);
  }

  private void setColorProgressBarBattery(Constants.BATTERY_STATUS batteryStatus) {

    GradientDrawable drawableProgressBar = getDrawableProgressBar();

    switch (batteryStatus) {
      case CHARGING:
      case FULL:
        drawableProgressBar.setColor(Color.GREEN);
        break;
      case MEDIUM:
        drawableProgressBar.setColor(Color.YELLOW);
        break;
      case LOW:
      case CRITICAL:
      default:
        drawableProgressBar.setColor(Color.RED);
        break;
    }
  }

  @Override
  public void showFreeMemorySpace(Constants.MEMORY_STATUS memoryStatus, int memoryPercent, String freeMemoryInBytes, String totalMemoryInBytes) {
    progressDialogBatteryOrMemory.setTitle(R.string.storage);
    freeMemorySpace.setVisibility(View.VISIBLE);

    freeMemorySpace.setText(getResources().getText(R.string.free_memory_space) + " " +freeMemoryInBytes+ " " +
    getResources().getText(R.string.preposition_of)+ " "+totalMemoryInBytes);
    switch (memoryStatus) {
      case MEDIUM:
        memory.setImageTintList(ColorStateList.valueOf(Color.YELLOW));
        break;
      case CRITICAL:
        memory.setImageTintList(ColorStateList.valueOf(Color.RED));
        break;
      case OKAY:
        memory.setImageTintList(ColorStateList.valueOf(Color.GREEN));
        break;
      default:
        memory.setImageTintList(ColorStateList.valueOf(Color.WHITE));
    }
    if(progressDialogBatteryOrMemory.isShowing()) {
      updateProgressBarMemory(memoryStatus, memoryPercent);
      updatePercentMemory(memoryPercent);
    }
  }

  private void updatePercentMemory(int memoryPercent) {
    percentLevel.setText(memoryPercent + " %");
  }

  private void updateProgressBarMemory(Constants.MEMORY_STATUS memoryStatus, int memoryPercent) {
    progressBarBatteryOrMemory.setProgress(memoryPercent);
    setColorProgressBarMemory(memoryStatus);
  }

  private void setColorProgressBarMemory(Constants.MEMORY_STATUS memoryStatus) {
    GradientDrawable drawableProgressBar = getDrawableProgressBar();

    switch (memoryStatus) {
      case OKAY:
        drawableProgressBar.setColor(Color.GREEN);
        break;
      case MEDIUM:
        drawableProgressBar.setColor(Color.YELLOW);
        break;
      case CRITICAL:
        drawableProgressBar.setColor(Color.RED);
        break;
      default:
        drawableProgressBar.setColor(Color.WHITE);
    }
  }

  private GradientDrawable getDrawableProgressBar() {
    LayerDrawable drawableProgressBar = (LayerDrawable) progressBarBatteryOrMemory.getProgressDrawable();
    return (GradientDrawable) drawableProgressBar
        .findDrawableByLayerId(R.id.progressbar_alert_dialog_circular_progress);
  }

  public void setupActivityButtons() {
        // TODO:(alvaro.martinez) 7/11/16 implement this functionality
        tintRecordButtons(R.color.button_color_record_activity);
    }

    private void tintRecordButtons(int button_color) {
        tintButton(flashButton, button_color);
        tintButton(rotateCameraButton,button_color);
        tintButton(buttonToShowControls,button_color);
        tintButton(buttonToHideControlsView,button_color);
        tintButton(buttonNavigateSettings,button_color);
        tintButton(gridButton, button_color);
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

    // TODO(jliarte): 13/12/16 do we still need to use sharedPreferences for this?
    private void saveVideoFeaturesToConfig() {
        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
        preferencesEditor.putLong(ConfigPreferences.VIDEO_DURATION, recordPresenter.getProjectDuration());
        preferencesEditor.putInt(ConfigPreferences.NUMBER_OF_CLIPS, recordPresenter.getNumVideosOnProject());
        preferencesEditor.putString(ConfigPreferences.RESOLUTION, recordPresenter.getResolution());
        preferencesEditor.commit();
    }

    // TODO(jliarte): 13/12/16 move this to presenter and/or UserEventTracker
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

    @OnClick (R.id.button_grid)
    public void onClickListenerGridButton(){
        if(gridButton.isSelected()){
            gridButton.setSelected(false);
            imageViewGrid.setVisibility(View.INVISIBLE);
        } else {
            gridButton.setSelected(true);
            imageViewGrid.setVisibility(View.VISIBLE);
        }
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

    @OnClick (R.id.button_navigate_edit_or_gallery)
    public void navigateToEditOrGallery() {
        if (!recording) {
            //TODO(alvaro 130616) Save flash state
            recordPresenter.setFlashOff();
            if (isProjectHasVideo){
                navigateTo(EditActivity.class);
            }else {
                navigateTo(GalleryActivity.class);
            }
            //finish();
        }
    }

    @OnClick(R.id.button_navigate_settings)
    public void navigateToSettings() {
        if (!recording) {
            navigateTo(SettingsActivity.class);
            //finish();
        }
    }

    @OnClick(R.id.clear_button)
    public void clearAndShrinkScreen() {

        if (clearButton.isActivated() == true) {
            showPrincipalViews();
        } else {
            hidePrincipalViews();
        }
    }
    @OnClick(R.id.button_to_show_controls_right)
    public void showControls() {
        buttonToShowControls.setVisibility(View.INVISIBLE);
        buttonToHideControlsView.setVisibility(View.VISIBLE);
        controlsView.setVisibility(View.VISIBLE);
        hud.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.button_to_hide_controls)
    public void hideControls(){
        buttonToHideControlsView.setVisibility(View.INVISIBLE);
        buttonToShowControls.setVisibility(View.VISIBLE);
        controlsView.setVisibility(View.INVISIBLE);
        hud.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.activity_record_icon_battery)
    public void showDialogWithLevelBattery(){
      if(!progressDialogBatteryOrMemory.isShowing()) {
        showDialogBatteryOrMemory();
        updateBatteryStatus();
      }
    }

  @OnClick(R.id.activity_record_icon_storage)
  public void showDialogWithLevelMemory(){
    if(!progressDialogBatteryOrMemory.isShowing()) {
      showDialogBatteryOrMemory();
      updatePercentFreeMemory();
    }
  }

  public void navigateTo(Class cls) {
        Intent intent = new Intent(VimojoApplication.getAppContext(), cls);
        startActivity(intent);
    }

  private void showDialogBatteryOrMemory() {
    progressDialogBatteryOrMemory.show();
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
