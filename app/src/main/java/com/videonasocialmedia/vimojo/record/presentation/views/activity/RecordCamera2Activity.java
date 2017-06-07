package com.videonasocialmedia.vimojo.record.presentation.views.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.videonasocialmedia.avrecorder.view.CustomManualFocusView;
import com.videonasocialmedia.camera.customview.AutoFitTextureView;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.main.modules.ActivityPresentersModule;
import com.videonasocialmedia.vimojo.presentation.views.broadcastreceiver.BatteryReceiver;
import com.videonasocialmedia.vimojo.presentation.views.customviews.CircleImageView;
import com.videonasocialmedia.vimojo.record.presentation.mvp.presenters.RecordCamera2Presenter;
import com.videonasocialmedia.vimojo.record.presentation.mvp.views.RecordCamera2View;
import com.videonasocialmedia.vimojo.record.presentation.views.custom.AlertDialogWithInfoIntoCircle;
import com.videonasocialmedia.vimojo.settings.presentation.views.activity.SettingsActivity;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.IntentConstants;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

import static com.videonasocialmedia.vimojo.utils.UIUtils.tintButton;

/**
 * Created by alvaro on 16/01/17.
 */

public class RecordCamera2Activity extends VimojoActivity implements RecordCamera2View,
    SeekBar.OnSeekBarChangeListener {

  private final String LOG_TAG = getClass().getSimpleName();

  @Inject
  RecordCamera2Presenter presenter;

  @Bind(R.id.button_settings_camera)
  ImageButton settingsCameraButton;
  @Bind(R.id.button_toggle_flash)
  ImageButton flashButton;
  @Bind(R.id.button_change_camera)
  ImageButton changeCameraButton;
  @Bind(R.id.button_grid)
  ImageButton gridButton;
  @Bind(R.id.button_to_show_controls_right)
  ImageButton showControlsButton;
  @Bind(R.id.button_to_hide_controls)
  ImageButton hideControlsViewButton;
  @Bind(R.id.button_navigate_settings)
  ImageButton navigateSettingsButtons;
  @Bind(R.id.chronometer_record)
  Chronometer chronometer;
  @Bind(R.id.text_view_num_videos)
  TextView numVideosRecordedTextView;
  @Bind(R.id.record_text_view_edit_or_gallery)
  TextView editText;
  @Bind(R.id.button_navigate_edit_or_gallery)
  CircleImageView thumbClipRecordedButton;
  @Bind(R.id.button_record)
  ImageButton recordButton;
  @Bind(R.id.control_chronometer_and_rec_point)
  View chronometerAndRecPointView;
  @Bind(R.id.imageRecPoint)
  ImageView recordingIndicator;
  @Bind(R.id.clear_button)
  ImageButton clearButton;
  @Bind(R.id.hud)
  View hudView;
  @Bind(R.id.controls)
  View controlsView;
  @Bind(R.id.picometer)
  View picometerView;
  @Bind(R.id.seekBar_slide_left)
  SeekBar slideSeekBar;
  @Bind(R.id.settings_bar)
  View settingsCameraBarView;
  @Bind(R.id.button_zoom)
  ImageButton zoomButton;
  @Bind(R.id.zoom_submenu)
  View zoomSubmenuView;
  @Bind(R.id.button_iso)
  ImageButton isoButton;
  @Bind(R.id.iso_submenu)
  View isoSubmenuView;
  @Bind(R.id.button_af_selection)
  ImageButton afSelectionButton;
  @Bind(R.id.af_selection_submenu)
  View afSelectionSubmenuView;
  @Bind(R.id.button_white_balance)
  ImageButton whiteBalanceButton;
  @Bind(R.id.white_balance_submenu)
  View whiteBalanceSubmenuView;
  @Bind(R.id.button_metering_mode)
  ImageButton meteringModeButton;
  @Bind(R.id.metering_mode_submenu)
  View meteringModeSubmenuView;
  @Bind(R.id.button_camera_auto)
  ImageButton cameraAutoButton;
  @Bind(R.id.button_resolution_indicator)
  ImageView resolutionIndicatorButton;
  @Bind(R.id.customManualFocusView)
  CustomManualFocusView customManualFocusView;
  @Bind(R.id.rotateDeviceHint)
  ImageView rotateDeviceHint;
  @Bind(R.id.image_view_grid)
  ImageView imageViewGrid;

  @Bind(R.id.activity_record_icon_battery)
  ImageView batteryButton;
  @Bind(R.id.activity_record_icon_storage)
  ImageView storageButton;

  /**
   * An {@link AutoFitTextureView} for camera preview.
   */
  @Bind(R.id.textureView)
  AutoFitTextureView textureView;

  /**
   * if for result
   **/
  private boolean isRecording = false;
  private boolean buttonBackPressed = false;

  // TODO:(alvaro.martinez) 18/01/17 Move this values to Constants
  private final int RESOLUTION_SELECTED_HD720 = 720;
  private final int RESOLUTION_SELECTED_HD1080 = 1080;
  private final int RESOLUTION_SELECTED_HD4K = 2160;
  private OrientationHelper orientationHelper;

  private ProgressDialog progressDialogAdaptVideo;

  private BatteryReceiver batteryReceiver = new BatteryReceiver(){
    @Override
    public void onReceive(Context context, Intent intent){
      Bundle bundle = intent.getExtras();
      if (bundle != null) {
        updateBatteryStatus();
      }
    }
  };

  private AlertDialogWithInfoIntoCircle alertDialogBattery;
  private AlertDialogWithInfoIntoCircle alertDialogStorage;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(LOG_TAG, "onCreate");
    setContentView(R.layout.activity_record_camera2);
    keepScreenOn();
    ButterKnife.bind(this);
    setupActivityButtons();
    configChronometer();
    configShowThumbAndNumberClips();

    this.getActivityPresentersComponent().inject(this);

    createProgressDialogAdaptVideo();
    createAlertDialogBatteryAndStorage();

    slideSeekBar.setOnSeekBarChangeListener(this);
  }

  private void createProgressDialogAdaptVideo() {
    progressDialogAdaptVideo = new ProgressDialog(this);
    progressDialogAdaptVideo.setTitle(getString(R.string.dialog_title_record_adapting_video));
    progressDialogAdaptVideo.setMessage(getString(R.string.dialog_message_record_adapting_video));
    progressDialogAdaptVideo.setIndeterminate(false);
    progressDialogAdaptVideo.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
  }

  private void createAlertDialogBatteryAndStorage() {
    alertDialogBattery = new AlertDialogWithInfoIntoCircle(this, getString(R.string.battery));
    /*alertDialogBattery.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);*/

    alertDialogStorage = new AlertDialogWithInfoIntoCircle(this, getString(R.string.storage));

  }

  @Override
  public ActivityPresentersModule getActivityPresentersModule() {
    return new ActivityPresentersModule(this, Constants.PATH_APP_TEMP,
        textureView);
  }

  private void keepScreenOn() {
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
  }

  public void setupActivityButtons() {
    // TODO:(alvaro.martinez) 7/11/16 implement this functionality, use case check support camera
    tintRecordButtons(R.color.button_color_record_activity);

    // Disable until implement camera pro
    picometerView.setVisibility(View.INVISIBLE);
  }

  private void tintRecordButtons(int button_color) {
    tintButton(flashButton, button_color);
    tintButton(changeCameraButton, button_color);
    tintButton(showControlsButton, button_color);
    tintButton(hideControlsViewButton, button_color);
    tintButton(navigateSettingsButtons, button_color);
    tintButton(settingsCameraButton, button_color);
    tintButton(zoomButton, button_color);
    tintButton(isoButton, button_color);
    tintButton(afSelectionButton, button_color);
    tintButton(whiteBalanceButton, button_color);
    tintButton(meteringModeButton, button_color);
    tintButton(gridButton, button_color);
    tintButton(cameraAutoButton, button_color);
  }

  private void configChronometer() {
    chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
      @Override
      public void onChronometerTick(Chronometer chronometer) {
        long elapsedTime = SystemClock.elapsedRealtime() - chronometer.getBase();

        int h = (int) (elapsedTime / 3600000);
        int m = (int) (elapsedTime - h * 3600000) / 60000;
        int s = (int) (elapsedTime - h * 3600000 - m * 60000) / 1000;
        // String hh = h < 10 ? "0"+h: h+"";
        String mm = m < 10 ? "0" + m : m + "";
        String ss = s < 10 ? "0" + s : s + "";
        String time = mm + ":" + ss;
        chronometer.setText(time);
      }
    });
  }

  private void configShowThumbAndNumberClips() {
    thumbClipRecordedButton.setBorderWidth(5);
    thumbClipRecordedButton.setBorderColor(Color.WHITE);
    numVideosRecordedTextView.setVisibility(View.GONE);
  }

  private void initOrientationHelper() {
    orientationHelper = new OrientationHelper(this);
  }

  @Override
  public void onResume() {
    super.onResume();
    initOrientationHelper();
    presenter.initViews();
    presenter.onResume();
    hideSystemUi();
    registerReceiver(batteryReceiver,new IntentFilter(IntentConstants.BATTERY_NOTIFICATION));
    updateBatteryStatus();
    updatePercentFreeStorage();

  }

  private void hideSystemUi() {
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
  public void onPause() {
    unregisterReceiver(batteryReceiver);
    presenter.onPause();
    super.onPause();
  }

  /*.*.*.*.*.*.*.*.*.*.*.*.*. RecordCamera2View *.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*/

  @Override
  public void showRecordButton() {
    recordButton.setImageResource(R.drawable.activity_record_ic_rec);
    isRecording = false;
  }

  @Override
  public void showStopButton() {
    recordButton.setImageResource(R.drawable.activity_record_icon_stop);
    isRecording = true;
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

  @Override
  public void showNavigateToSettingsActivity() {
    navigateSettingsButtons.setEnabled(true);
  }

  @Override
  public void hideNavigateToSettingsActivity() {
    navigateSettingsButtons.setEnabled(false);
  }

  private void hideRecordingIndicator() {
    recordingIndicator.setVisibility(View.INVISIBLE);
  }

  @Override
  public void setFlash(boolean on) {
    // TODO:(alvaro.martinez) 18/01/17 Review flash tracking trackUserInteracted(AnalyticsConstants.CHANGE_FLASH, String.valueOf(on));
    flashButton.setActivated(on);
    flashButton.setSelected(on);
  }

  @Override
  public void setFlashSupported(boolean supported) {
    flashButton.setActivated(false);
    if (supported) {
      flashButton.setEnabled(true);
    } else {
      flashButton.setEnabled(false);
    }
  }

  @Override
  public void setZoom(float value){
    slideSeekBar.setProgress((int) (value * 100));
  }

  @Override
  public void showChangeCamera() {
    changeCameraButton.setEnabled(true);
    changeCameraButton.setActivated(true);
  }

  @Override
  public void hideChangeCamera() {
    changeCameraButton.setEnabled(false);
    changeCameraButton.setActivated(false);
  }

  @Override
  public void showError(int stringResourceId) {
    showMessage(stringResourceId);
  }

  @Override
  public void showProgressAdaptingVideo() {
    progressDialogAdaptVideo.show();
  }

  @Override
  public void hideProgressAdaptingVideo() {
    progressDialogAdaptVideo.dismiss();
  }

  @Override
  public void hidePrincipalViews() {
    clearButton.setImageResource(R.drawable.activity_record_ic_expand);
    clearButton.setActivated(true);
    hudView.setVisibility(View.INVISIBLE);
    controlsView.setVisibility(View.INVISIBLE);
    hideControlsViewButton.setVisibility(View.INVISIBLE);
    showControlsButton.setVisibility(View.INVISIBLE);
    zoomSubmenuView.setVisibility(View.INVISIBLE);
    isoSubmenuView.setVisibility(View.INVISIBLE);
    afSelectionSubmenuView.setVisibility(View.INVISIBLE);
    whiteBalanceSubmenuView.setVisibility(View.INVISIBLE);
    meteringModeSubmenuView.setVisibility(View.INVISIBLE);
    settingsCameraBarView.setVisibility(View.INVISIBLE);
    zoomSubmenuView.setVisibility(View.INVISIBLE);
  }

  @Override
  public void showPrincipalViews() {
    clearButton.setImageResource(R.drawable.activity_record_ic_shrink);
    clearButton.setBackground(getResources().getDrawable(R.drawable.circle_background));
    clearButton.setActivated(false);
    hudView.setVisibility(View.VISIBLE);
    showControlsButton.setVisibility(View.VISIBLE);
  }

  @Override
  public void hideRightControlsView() {
    hideControlsViewButton.setVisibility(View.INVISIBLE);
    showControlsButton.setVisibility(View.VISIBLE);
    controlsView.setVisibility(View.INVISIBLE);
    settingsCameraBarView.setVisibility(View.INVISIBLE);

    settingsCameraBarView.setVisibility(View.INVISIBLE);
    zoomSubmenuView.setVisibility(View.INVISIBLE);
    isoSubmenuView.setVisibility(View.INVISIBLE);
    afSelectionSubmenuView.setVisibility(View.INVISIBLE);
    whiteBalanceSubmenuView.setVisibility(View.INVISIBLE);
    meteringModeSubmenuView.setVisibility(View.INVISIBLE);
  }

  @Override
  public void showRightControlsView() {
    showControlsButton.setVisibility(View.INVISIBLE);
    hideControlsViewButton.setVisibility(View.VISIBLE);
    controlsView.setVisibility(View.VISIBLE);
    if(settingsCameraButton.isSelected()){
      settingsCameraBarView.setVisibility(View.VISIBLE);
    }
    if(zoomButton.isSelected()){
      zoomSubmenuView.setVisibility(View.VISIBLE);
      return;
    }
    if(isoButton.isSelected()){
      isoSubmenuView.setVisibility(View.VISIBLE);
      return;
    }
    if(afSelectionButton.isSelected()){
      afSelectionSubmenuView.setVisibility(View.VISIBLE);
      return;
    }
    if(whiteBalanceButton.isSelected()){
      whiteBalanceSubmenuView.setVisibility(View.VISIBLE);
      return;
    }
    if(meteringModeButton.isSelected()){
      meteringModeSubmenuView.setVisibility(View.VISIBLE);
      return;
    }
  }

  @Override
  public void showSettingsCameraView() {

    settingsCameraBarView.setVisibility(View.VISIBLE);
    settingsCameraButton.setSelected(true);

    if(zoomButton.isSelected()){
      zoomSubmenuView.setVisibility(View.VISIBLE);
      return;
    }
    if(isoButton.isSelected()){
      isoSubmenuView.setVisibility(View.VISIBLE);
      return;
    }
    if(afSelectionButton.isSelected()){
      afSelectionSubmenuView.setVisibility(View.VISIBLE);
      return;
    }
    if(whiteBalanceButton.isSelected()){
      whiteBalanceSubmenuView.setVisibility(View.VISIBLE);
      return;
    }
    if(meteringModeButton.isSelected()){
      meteringModeSubmenuView.setVisibility(View.VISIBLE);
      return;
    }
  }

  @Override
  public void hideSettingsCameraView() {
    settingsCameraBarView.setVisibility(View.INVISIBLE);
    settingsCameraButton.setSelected(false);

    zoomSubmenuView.setVisibility(View.INVISIBLE);
    isoSubmenuView.setVisibility(View.INVISIBLE);
    afSelectionSubmenuView.setVisibility(View.INVISIBLE);
    whiteBalanceSubmenuView.setVisibility(View.INVISIBLE);
    meteringModeSubmenuView.setVisibility(View.INVISIBLE);
  }

  @Override
  public void hideAdvancedAFSelection() {
    afSelectionButton.setVisibility(View.GONE);
  }

  @Override
  public void hideISOSelection() {
    isoButton.setVisibility(View.GONE);
  }

  @Override
  public void hideWhiteBalanceSelection() {
    whiteBalanceButton.setVisibility(View.GONE);
  }

  @Override
  public void hideMetteringModeSelection() {
    meteringModeButton.setVisibility(View.GONE);
  }

  @Override
  public void showRecordedVideoThumbWithText(String path) {
    thumbClipRecordedButton.setVisibility(View.VISIBLE);
    Glide.with(this).load(path).into(thumbClipRecordedButton);
    editText.setVisibility(View.VISIBLE);
  }

  @Override
  public void hideRecordedVideoThumbWithText() {
    thumbClipRecordedButton.setVisibility(View.INVISIBLE);
    editText.setVisibility(View.INVISIBLE);
  }

  @Override
  public void showVideosRecordedNumber(int numberOfVideos) {
    numVideosRecordedTextView.setVisibility(View.VISIBLE);
    numVideosRecordedTextView.setText(String.valueOf(numberOfVideos));
    editText.setText(getString(R.string.recordTextEdit));
  }

  @Override
  public void hideVideosRecordedNumber() {
    numVideosRecordedTextView.setVisibility(View.INVISIBLE);
  }

  @Override
  public void stopMonitoringRotation() {
    orientationHelper.stopMonitoringOrientation();
  }

  @Override
  public void setResolutionSelected(int resolutionSelected) {
    switch (resolutionSelected){
      case (RESOLUTION_SELECTED_HD720):
        resolutionIndicatorButton.setImageResource(R.drawable.activity_record_ic_resolution_720);
        break;
      case(RESOLUTION_SELECTED_HD1080):
        resolutionIndicatorButton.setImageResource(R.drawable.activity_record_ic_resolution_1080);
        break;
      case (RESOLUTION_SELECTED_HD4K):
        resolutionIndicatorButton.setImageResource(R.drawable.activity_record_ic_resolution_4k);
        break;
      default:
        resolutionIndicatorButton.setImageResource(R.drawable.activity_record_ic_resolution_720);
        break;
    }
  }

  @Override
  public void showBatteryStatus(Constants.BATTERY_STATUS batteryStatus, int batteryPercent) {
    switch (batteryStatus) {
      case CHARGING:
        batteryButton.setImageResource(R.drawable.activity_record_ic_battery_charging);
        break;
      case FULL:
        batteryButton.setImageResource(R.drawable.activity_record_ic_battery_full);
        break;
      case MEDIUM:
        batteryButton.setImageResource(R.drawable.activity_record_ic_battery_medium);
        break;
      case LOW:
        batteryButton.setImageResource(R.drawable.activity_record_ic_battery_low);
        break;
      case CRITICAL:
        batteryButton.setImageResource(R.drawable.activity_record_ic_battery_alert);
        break;
      default:
        batteryButton.setImageResource(R.drawable.activity_record_ic_battery_full);
    }
      updateProgressBarBattery(batteryStatus, batteryPercent);
      updatePercentBattery(batteryPercent);
  }

  private void updatePercentBattery(int batteryPercent) {
    alertDialogBattery.setPercentLevel(batteryPercent);
  }

  private void updateProgressBarBattery(Constants.BATTERY_STATUS batteryStatus,
                                        int batteryPercent) {
    alertDialogBattery.setPercentLevel(batteryPercent);
    setColorProgressBarBattery(batteryStatus);
  }

  private void setColorProgressBarBattery(Constants.BATTERY_STATUS batteryStatus) {

    switch (batteryStatus) {
      case CHARGING:
      case FULL:
        alertDialogBattery.setPercentColor(getResources().getColor(R.color.recordAlertInfoGreen));
        break;
      case MEDIUM:
        alertDialogBattery.setPercentColor(getResources().getColor(R.color.recordAlertInfoYellow));
        break;
      case LOW:
      case CRITICAL:
      default:
        alertDialogBattery.setPercentColor(getResources().getColor(R.color.recordAlertInfoRed));
        break;
    }
  }

  @Override
  public void showAlertDialogBattery() {
    alertDialogBattery.show();
  }

  @Override
  public void showFreeStorageSpace(Constants.MEMORY_STATUS storageStatus, int storagePercent,
                                   String freeMemoryInBytes, String totalMemoryInBytes) {
    switch (storageStatus) {
      case MEDIUM:
        storageButton.setImageTintList(ColorStateList.valueOf(getResources()
            .getColor(R.color.recordAlertInfoYellow)));
        break;
      case CRITICAL:
        storageButton.setImageTintList(ColorStateList.valueOf(getResources()
            .getColor(R.color.recordAlertInfoRed)));
        break;
      case OKAY:
        storageButton.setImageTintList(ColorStateList.valueOf(getResources()
            .getColor(R.color.recordAlertInfoGreen)));
        break;
      default:
        storageButton.setImageTintList(ColorStateList.valueOf(Color.WHITE));
    }
    updateMessageStorage(freeMemoryInBytes, totalMemoryInBytes);
    updateProgressBarStorage(storageStatus, storagePercent);
    updatePercentStorage(storagePercent);
  }

  private void updateMessageStorage(String freeMemoryInBytes, String totalMemoryInBytes) {
    alertDialogStorage.setTextMessage(getResources().getText(R.string.free_memory_space) + " "
      + freeMemoryInBytes + " " + getResources().getText(R.string.preposition_of)+ " "
      + totalMemoryInBytes);
  }

  private void updatePercentStorage(int storagePercent) {
    alertDialogStorage.setPercentLevel(storagePercent);
  }

  private void updateProgressBarStorage(Constants.MEMORY_STATUS memoryStatus, int memoryPercent) {
    alertDialogStorage.setPercentLevel(memoryPercent);
    setColorProgressBarMemory(memoryStatus);
  }

  private void setColorProgressBarMemory(Constants.MEMORY_STATUS memoryStatus) {

    switch (memoryStatus) {
      case OKAY:
        alertDialogStorage.setPercentColor(getResources().getColor(R.color.recordAlertInfoGreen));
        break;
      case MEDIUM:
        alertDialogStorage.setPercentColor(getResources().getColor(R.color.recordAlertInfoYellow));
        break;
      case CRITICAL:
        alertDialogStorage.setPercentColor(getResources().getColor(R.color.recordAlertInfoRed));
        break;
      default:
        alertDialogStorage.setPercentColor(Color.WHITE);
    }
  }

  @Override
  public void showAlertDialogStorage() {
    alertDialogStorage.show();
  }

  @Override
  public void setFocus(MotionEvent event) {
    customManualFocusView.onTouchEvent(event);
  }

  /*.*.*.*.*.*.*.*.*.*.*.*.*. OnClicks *.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*/

  @OnClick(R.id.button_toggle_flash)
  public void toggleFlash() {
    presenter.isFlashEnabled(flashButton.isSelected());
  }

  @OnClick(R.id.button_change_camera)
  public void changeCamera() {
   presenter.switchCamera();
  }

  @OnClick (R.id.button_navigate_edit_or_gallery)
  public void navigateToEditOrGallery() {
    if (!isRecording) {
      presenter.setFlashOff();
      presenter.navigateToEditOrGallery();
    }
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

  @OnClick(R.id.button_settings_camera)
  public void showHideBottomSettingsCamera(){
    presenter.buttonSettingsCamera(settingsCameraButton.isSelected());
  }

  @OnClick(R.id.button_navigate_settings)
  public void navigateToSettings() {
    if (!isRecording) {
      navigateTo(SettingsActivity.class);
    }
  }

  @OnClick(R.id.clear_button)
  public void clearAndShrinkScreen() {
    if (clearButton.isActivated() == true) {
      showPrincipalViews();
      showRightControlsView();
    } else {
      hidePrincipalViews();
    }
  }

  @OnClick(R.id.button_to_show_controls_right)
  public void showRightControls() {
    presenter.showRightControls();
  }

  @OnClick(R.id.button_to_hide_controls)
  public void hideRightControls(){
    presenter.hideRightControls();
  }

  @OnClick(R.id.activity_record_icon_battery)
  public void showDialogWithLevelBattery(){
    if(!alertDialogBattery.isShowing()) {
      Intent batteryStatus = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
      int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
      int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
      int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
      presenter.batteryDialog(level, status, scale);
    }
  }

  @OnClick(R.id.activity_record_icon_storage)
  public void showDialogWithLevelStorage(){
    if(!alertDialogStorage.isShowing()) {
      StatFs statFs= new StatFs(Environment.getDataDirectory().getPath());
      long totalStorage= getTotalStorage(statFs);
      long freeStorage= getFreeStorage(statFs);
      presenter.storageDialog(totalStorage, freeStorage);
    }
  }

  @OnClick (R.id.button_zoom)
  public void onClickZoomListener(){
    if(zoomButton.isSelected()){
      zoomSubmenuView.setVisibility(View.INVISIBLE);
      zoomButton.setSelected(false);
    } else {
      zoomButton.setSelected(true);
      zoomSubmenuView.setVisibility(View.VISIBLE);

      isoButton.setSelected(false);
      isoSubmenuView.setVisibility(View.INVISIBLE);
      afSelectionButton.setSelected(false);
      afSelectionSubmenuView.setVisibility(View.INVISIBLE);
      whiteBalanceButton.setSelected(false);
      whiteBalanceSubmenuView.setVisibility(View.INVISIBLE);
      meteringModeButton.setSelected(false);
      meteringModeSubmenuView.setVisibility(View.INVISIBLE);
    }
  }

  @OnClick (R.id.button_iso)
  public void onClickIsoListener(){
    if(isoButton.isSelected()){
      isoButton.setSelected(false);
      isoSubmenuView.setVisibility(View.INVISIBLE);
    } else {
      isoButton.setSelected(true);
      isoSubmenuView.setVisibility(View.VISIBLE);

      zoomButton.setSelected(false);
      zoomSubmenuView.setVisibility(View.INVISIBLE);
      afSelectionButton.setSelected(false);
      afSelectionSubmenuView.setVisibility(View.INVISIBLE);
      whiteBalanceButton.setSelected(false);
      whiteBalanceSubmenuView.setVisibility(View.INVISIBLE);
      meteringModeButton.setSelected(false);
      meteringModeSubmenuView.setVisibility(View.INVISIBLE);
    }
  }

  @OnClick (R.id.button_af_selection)
  public void onClickAfSelectionListener(){
    if(afSelectionButton.isSelected()){
      afSelectionButton.setSelected(false);
      afSelectionSubmenuView.setVisibility(View.INVISIBLE);
    } else {
      afSelectionButton.setSelected(true);
      afSelectionSubmenuView.setVisibility(View.VISIBLE);

      zoomButton.setSelected(false);
      zoomSubmenuView.setVisibility(View.INVISIBLE);
      isoButton.setSelected(false);
      isoSubmenuView.setVisibility(View.INVISIBLE);
      whiteBalanceButton.setSelected(false);
      whiteBalanceSubmenuView.setVisibility(View.INVISIBLE);
      meteringModeButton.setSelected(false);
      meteringModeSubmenuView.setVisibility(View.INVISIBLE);
    }
  }

  @OnClick (R.id.button_white_balance)
  public void onClickWhiteBalanceListener(){
    if(whiteBalanceButton.isSelected()){
      whiteBalanceButton.setSelected(false);
      whiteBalanceSubmenuView.setVisibility(View.INVISIBLE);
    } else{
      whiteBalanceButton.setSelected(true);
      whiteBalanceSubmenuView.setVisibility(View.VISIBLE);

      zoomButton.setSelected(false);
      zoomSubmenuView.setVisibility(View.INVISIBLE);
      isoButton.setSelected(false);
      isoSubmenuView.setVisibility(View.INVISIBLE);
      afSelectionButton.setSelected(false);
      afSelectionSubmenuView.setVisibility(View.INVISIBLE);
      meteringModeButton.setSelected(false);
      meteringModeSubmenuView.setVisibility(View.INVISIBLE);
    }
  }

  @OnClick(R.id.button_metering_mode)
  public void onClickMeasurementeModeListener(){
    if(meteringModeButton.isSelected()){
      meteringModeButton.setSelected(false);
      meteringModeSubmenuView.setVisibility(View.INVISIBLE);
    } else {
      meteringModeButton.setSelected(true);
      meteringModeSubmenuView.setVisibility(View.VISIBLE);

      zoomButton.setSelected(false);
      zoomSubmenuView.setVisibility(View.INVISIBLE);
      isoButton.setSelected(false);
      isoSubmenuView.setVisibility(View.INVISIBLE);
      afSelectionButton.setSelected(false);
      afSelectionSubmenuView.setVisibility(View.INVISIBLE);
      whiteBalanceButton.setSelected(false);
      whiteBalanceSubmenuView.setVisibility(View.INVISIBLE);
    }
  }

  @OnClick (R.id.button_camera_auto)
  public void onClickCameraAutoListener(){

    zoomButton.setSelected(false);
    zoomSubmenuView.setVisibility(View.INVISIBLE);
    slideSeekBar.setProgress(0);
    presenter.setZoom(0f);

    isoButton.setSelected(false);
    isoSubmenuView.setVisibility(View.INVISIBLE);
    afSelectionButton.setSelected(false);
    afSelectionSubmenuView.setVisibility(View.INVISIBLE);
    whiteBalanceButton.setSelected(false);
    whiteBalanceSubmenuView.setVisibility(View.INVISIBLE);
    meteringModeButton.setSelected(false);
    meteringModeSubmenuView.setVisibility(View.INVISIBLE);
  }


  @OnTouch(R.id.button_record)
  boolean onTouch(MotionEvent event) {
    if (event.getAction() == MotionEvent.ACTION_DOWN) {
      if (!isRecording) {
        presenter.startRecord();
        isRecording = true;
      } else {
        presenter.stopRecord();
        updatePercentFreeStorage();
        updateBatteryStatus();
      }
    }
    return true;
  }

  @OnTouch(R.id.customManualFocusView)
  boolean onTouchCustomManualFocusView(MotionEvent event) {
    if (event.getAction() == MotionEvent.ACTION_DOWN) {
      // TODO:(alvaro.martinez) 27/01/17 single touch logic
      presenter.onTouchFocus(event);
      return true;
    }

    if (event.getPointerCount() > 1) {
      // zoom touch
      presenter.onTouchZoom(event);
    }
    return true;
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
        showMessage(R.string.toast_exit);
    }
  }

  @Override
  public void navigateTo(Class cls) {
    Intent intent = new Intent(VimojoApplication.getAppContext(), cls);
    startActivity(intent);
  }

  private void showMessage(int stringResourceId) {
    Snackbar snackbar = Snackbar.make(chronometerAndRecPointView, stringResourceId,
        Snackbar.LENGTH_SHORT);
    snackbar.show();
  }

  @Override
  public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    presenter.onSeekBarZoom((float) (progress * 0.01));
  }

  @Override
  public void onStartTrackingTouch(SeekBar seekBar) {

  }

  @Override
  public void onStopTrackingTouch(SeekBar seekBar) {

  }


  public void updateBatteryStatus() {
    Intent batteryStatus = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
    int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
    int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
    presenter.updateBatteryStatus(status, level, scale);
  }

  private void updatePercentFreeStorage() {
    StatFs statFs= new StatFs(Environment.getDataDirectory().getPath());
    long totalStorage= getTotalStorage(statFs);
    long freeStorage= getFreeStorage(statFs);
    presenter.updateFreeStorageSpace(totalStorage,freeStorage);
  }

  private long getTotalStorage(StatFs statFs) {
    long   totalStorage  = (statFs.getBlockCountLong() *  statFs.getBlockSizeLong());
    return totalStorage;
  }
  private long getFreeStorage(StatFs statFs) {
    long   freeStorage   = (statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong());
    return freeStorage;
  }

  private class OrientationHelper extends OrientationEventListener {
    Context context;
    private boolean orientationHaveChanged = false;

    public OrientationHelper(Context context) {
      super(context);
      this.context = context;
      if (this.canDetectOrientation())
        this.enable();
    }

    public void stopMonitoringOrientation() {
      this.disable();
    }

    @Override
    public void onOrientationChanged(int orientation) {
      checkShowRotateDeviceImage(orientation);
      if (orientation > 85 && orientation < 95) {
        if (orientationHaveChanged) {
          Log.d(LOG_TAG, "onOrientationChanged  rotationView changed " + orientation);
          orientationHaveChanged = false;
          restartPreview();
        }
      } else if (orientation > 265 && orientation < 275) {
        if (!orientationHaveChanged) {
          Log.d(LOG_TAG, "onOrientationChanged  rotationView changed " + orientation);
          orientationHaveChanged = true;
          restartPreview();
        }
      }
    }

    private void restartPreview() {
      Runnable r = new Runnable() {
        @Override
        public void run(){
          presenter.restartPreview();
        }
      };
      Handler h = new Handler();
      h.postDelayed(r, 300);
    }


    private void checkShowRotateDeviceImage(int orientation) {
      if (( orientation > 345 || orientation < 15 ) && orientation != -1) {
        rotateDeviceHint.setRotation(270);
        rotateDeviceHint.setRotationX(0);
        rotateDeviceHint.setVisibility(View.VISIBLE);
        if(!isRecording)
          recordButton.setEnabled(false);
      } else if (orientation > 165 && orientation < 195) {
        rotateDeviceHint.setRotation(-270);
        rotateDeviceHint.setRotationX(180);
        rotateDeviceHint.setVisibility(View.VISIBLE);
        if(!isRecording)
          recordButton.setEnabled(false);
      } else {
        rotateDeviceHint.setVisibility(View.GONE);
        recordButton.setEnabled(true);
      }
    }

  }

}
