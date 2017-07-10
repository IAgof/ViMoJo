package com.videonasocialmedia.vimojo.record.presentation.views.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.util.Range;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.videonasocialmedia.avrecorder.view.CustomManualFocusView;
import com.videonasocialmedia.camera.camera2.Camera2FocusHelper;
import com.videonasocialmedia.camera.camera2.Camera2MeteringModeHelper;
import com.videonasocialmedia.camera.camera2.Camera2WhiteBalanceHelper;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

import static com.videonasocialmedia.camera.camera2.Camera2FocusHelper.AF_MODE_AUTO;
import static com.videonasocialmedia.camera.camera2.Camera2FocusHelper.AF_MODE_MANUAL;
import static com.videonasocialmedia.camera.camera2.Camera2FocusHelper.AF_MODE_REGIONS;
import static com.videonasocialmedia.vimojo.utils.UIUtils.tintButton;

/**
 * Created by alvaro on 16/01/17.
 */

public class RecordCamera2Activity extends VimojoActivity implements RecordCamera2View  {

  private static final int SLIDE_SEEKBAR_MODE_UNACTIVE = 0;
  public static final int SLIDE_SEEKBAR_MODE_EXPOSURE_COMPENSATION = 1;
  public static final int SLIDE_SEEKBAR_MODE_ZOOM = 2;
  public static final int SLIDE_SEEKBAR_MODE_FOCUS_MANUAL = 3;
  public static final int TOUCH_AREA_MODE_METERING_POINT = 0;
  public static final int TOUCH_AREA_MODE_METERING_CENTER = 1;
  public static final int TOUCH_AREA_MODE_FOCUS_SELECTIVE = 2;
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
  @Bind(R.id.seekBar_upper_text)
  TextView seekbarUpperText;
  @Bind(R.id.seekBar_lower_text)
  TextView seekbarLowerText;
  @Bind(R.id.seekBar_lower_image)
  ImageView seekBarLowerImage;
  @Bind(R.id.seekBar_upper_image)
  ImageView seekBarUpperImage;
  @Bind(R.id.settings_bar)
  View settingsCameraBarView;
  @Bind(R.id.button_zoom)
  ImageButton zoomButton;
  @Bind(R.id.slide_seekbar_submenu)
  View slideSeekbarSubmenuView;

  @Bind(R.id.button_iso)
  ImageButton isoButton;
  @Bind(R.id.iso_submenu)
  LinearLayout isoSubmenuView;
  @Bind(R.id.iso_auto)
  TextView isoSettingAuto;

  @Bind(R.id.button_af_selection)
  ImageButton afSelectionButton;
  @Bind(R.id.af_selection_submenu)
  View afSelectionSubmenuView;
  @Bind(R.id.af_setting_auto)
  ImageButton afSettingAuto;
  @Bind(R.id.af_setting_manual)
  ImageButton afSettingManual;
  @Bind(R.id.af_setting_selective)
  ImageButton afSettingSelective;
  @Bind(R.id.button_white_balance)
  ImageButton whiteBalanceButton;
  @Bind(R.id.white_balance_submenu)
  View whiteBalanceSubmenuView;
  @Bind(R.id.wb_setting_auto)
  ImageButton wbSettingAuto;
  @Bind(R.id.wb_setting_cloudy)
  ImageButton wbSettingCloudy;
  @Bind(R.id.wb_setting_daylight)
  ImageButton wbSettingDaylight;
  @Bind(R.id.wb_setting_flash)
  ImageButton wbSettingFlash;
  @Bind(R.id.wb_setting_fluorescent)
  ImageButton wbSettingFluorescent;
  @Bind(R.id.wb_setting_incandescent)
  ImageButton wbSettingIncandescent;

  @Bind(R.id.button_metering_mode)
  ImageButton meteringModeButton;
  @Bind(R.id.metering_mode_submenu)
  View meteringModeSubmenuView;
  @Bind(R.id.metering_mode_auto)
  ImageButton meteringModeAuto;
  @Bind(R.id.metering_mode_exposure_compensation)
  ImageButton meteringModeExposureCompensation;
  @Bind(R.id.metering_mode_center)
  ImageButton meteringModeCenter;
  @Bind(R.id.metering_mode_spot)
  ImageButton meteringModeSpot;

  @Bind(R.id.button_camera_default)
  ImageButton cameraDefaultSettingsButton;
  @Bind(R.id.button_resolution_indicator)
  ImageView resolutionIndicatorButton;
  @Bind(R.id.customManualFocusView)
  CustomManualFocusView customManualFocusView;
  @Bind(R.id.camera_shutter)
  ImageView cameraShutter;
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

  private HashMap<String, ImageButton> whiteBalanceModeButtons;
  private HashMap<String, ImageButton> focusSelectionModeButtons;
  private ArrayList<ImageButton> supportedWhiteBalanceModeButtons = new ArrayList<>();
  private ArrayList<ImageButton> supportedFocusSelectionModeButtons = new ArrayList<>();
  private int slideSeekBarMode;
  private int currentSeekbarZoom;
  private int touchEventX = 0;
  private int touchEventY = 0;
  private HashMap<TextView, Integer> isoButtons = new HashMap<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(LOG_TAG, "onCreate");
    setContentView(R.layout.activity_record_camera2);
    keepScreenOn();
    ButterKnife.bind(this);
    setupActivityButtons();
//    configChronometer(); // TODO(jliarte): 26/06/17 make sure this is not needed anymore
    configShowThumbAndNumberClips();

    this.getActivityPresentersComponent().inject(this);

    createProgressDialogAdaptVideo();
    createAlertDialogBatteryAndStorage();
    initWhiteBalanceModesMap();
    initFocusSelectionModesMap();
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
    tintButton(afSettingAuto, button_color);
    tintButton(afSettingManual, button_color);
    tintButton(afSettingSelective, button_color);

    tintButton(whiteBalanceButton, button_color);
    tintButton(wbSettingAuto, button_color);
    tintButton(wbSettingCloudy, button_color);
    tintButton(wbSettingDaylight, button_color);
    tintButton(wbSettingFlash, button_color);
    tintButton(wbSettingFluorescent, button_color);
    tintButton(wbSettingIncandescent, button_color);

    tintButton(meteringModeButton, button_color);
    tintButton(meteringModeAuto, button_color);
    tintButton(meteringModeExposureCompensation, button_color);
    tintButton(meteringModeCenter, button_color);
    tintButton(meteringModeSpot, button_color);


    tintButton(gridButton, button_color);
    tintButton(cameraDefaultSettingsButton, button_color);
  }

  private void configChronometer() {
    // TODO(jliarte): 26/06/17 make sure this is not needed anymore
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
    presenter.onResume();
    presenter.initViews();
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
  public void setZoom(float value) {
    currentSeekbarZoom = (int) (value * 100);
    if (slideSeekBarMode == SLIDE_SEEKBAR_MODE_ZOOM) {
      slideSeekBar.setProgress(currentSeekbarZoom);
    }
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
  public void showError(String message) {
    Log.d(LOG_TAG, "showError " + message);
    showMessage(message);
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
    slideSeekbarSubmenuView.setVisibility(View.INVISIBLE);
    isoSubmenuView.setVisibility(View.INVISIBLE);
    afSelectionSubmenuView.setVisibility(View.INVISIBLE);
    whiteBalanceSubmenuView.setVisibility(View.INVISIBLE);
    meteringModeSubmenuView.setVisibility(View.INVISIBLE);
    settingsCameraBarView.setVisibility(View.INVISIBLE);
    slideSeekbarSubmenuView.setVisibility(View.INVISIBLE);
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
    slideSeekbarSubmenuView.setVisibility(View.INVISIBLE);
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
    if (settingsCameraButton.isSelected()) {
      settingsCameraBarView.setVisibility(View.VISIBLE);
    }
    if (zoomButton.isSelected()) {
      slideSeekbarSubmenuView.setVisibility(View.VISIBLE);
      return;
    }
    if (isoButton.isSelected()) {
      isoSubmenuView.setVisibility(View.VISIBLE);
      return;
    }
    if (afSelectionButton.isSelected()) {
      afSelectionSubmenuView.setVisibility(View.VISIBLE);
      return;
    }
    if (whiteBalanceButton.isSelected()) {
      whiteBalanceSubmenuView.setVisibility(View.VISIBLE);
      return;
    }
    if (meteringModeButton.isSelected()) {
      meteringModeSubmenuView.setVisibility(View.VISIBLE);
      return;
    }
  }

  @Override
  public void showSettingsCameraView() {
    settingsCameraBarView.setVisibility(View.VISIBLE);
    settingsCameraButton.setSelected(true);
    if (zoomButton.isSelected()) {
      slideSeekbarSubmenuView.setVisibility(View.VISIBLE);
      return;
    }
    if (isoButton.isSelected()) {
      isoSubmenuView.setVisibility(View.VISIBLE);
      return;
    }
    if (afSelectionButton.isSelected()) {
      afSelectionSubmenuView.setVisibility(View.VISIBLE);
      return;
    }
    if (whiteBalanceButton.isSelected()) {
      whiteBalanceSubmenuView.setVisibility(View.VISIBLE);
      return;
    }
    if (meteringModeButton.isSelected()) {
      meteringModeSubmenuView.setVisibility(View.VISIBLE);
      return;
    }
  }

  @Override
  public void hideSettingsCameraView() {
    settingsCameraBarView.setVisibility(View.INVISIBLE);
    settingsCameraButton.setSelected(false);

    slideSeekbarSubmenuView.setVisibility(View.INVISIBLE);
    isoSubmenuView.setVisibility(View.INVISIBLE);
    afSelectionSubmenuView.setVisibility(View.INVISIBLE);
    whiteBalanceSubmenuView.setVisibility(View.INVISIBLE);
    meteringModeSubmenuView.setVisibility(View.INVISIBLE);
  }

  @Override
  public void showAdvancedAFSelection() {
    afSelectionButton.setVisibility(View.VISIBLE);
  }

  @Override
  public void hideAdvancedAFSelection() {
    afSelectionButton.setVisibility(View.GONE);
  }

  @Override
  public void showISOSelection() {
    isoButton.setVisibility(View.VISIBLE);
  }

  @Override
  public void hideISOSelection() {
    isoButton.setVisibility(View.GONE);
  }

  @Override
  public void setupISOSupportedModesButtons(Range<Integer> supportedISORange) {
    isoSettingAuto.setSelected(true);
    isoSettingAuto.setTextColor(getResources().getColor(R.color.button_selected));
    clearISObuttons();
    isoButtons.put(isoSettingAuto, 0); // (jliarte): 27/06/17 convention for auto ISO setting
    isoSubmenuView.addView(isoSettingAuto);
    setIsoModeOnClickListener(0, isoSettingAuto);
    int[] isoValues = {50, 100, 200, 400, 800, 1600, 3200, 6400, 12800, 25600, 51200};
    for (final int isoValue : isoValues) {
      if (supportedISORange.contains(isoValue)) {
        final TextView isoModeButton = new TextView(this);
        isoModeButton.setLayoutParams(isoSettingAuto.getLayoutParams());
        isoModeButton.setText(String.valueOf(isoValue));
        isoModeButton.setTextColor(getResources().getColor(R.color.button_color_record_activity));
        setIsoModeOnClickListener(isoValue, isoModeButton);
        isoButtons.put(isoModeButton, isoValue);
        isoSubmenuView.addView(isoModeButton);
      }
    }
  }

  private void clearISObuttons() {
    isoSubmenuView.removeAllViews();
    isoButtons.clear();
  }

  private void setIsoModeOnClickListener(final int isoValue, final TextView isoModeButton) {
    isoModeButton.setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                deselectAllISOButtons();
                isoModeButton.setSelected(true);
                isoModeButton.setTextColor(getResources().getColor(R.color.button_selected));
                presenter.setISO(isoValue);
              }
            });
  }

  private void deselectAllISOButtons() {
    for (Map.Entry<TextView , Integer> isoMap: isoButtons.entrySet()) {
      TextView isoButton = isoMap.getKey();
      isoButton.setSelected(false);
      isoButton.setTextColor(getResources().getColor(R.color.button_color_record_activity));
    }
  }

  @Override
  public void showWhiteBalanceSelection() {
    whiteBalanceButton.setVisibility(View.VISIBLE);
  }

  @Override
  public void hideWhiteBalanceSelection() {
    whiteBalanceButton.setVisibility(View.GONE);
  }

  @Override
  public void setupWhiteBalanceSupportedModesButtons(List<String> values) {
    wbSettingAuto.setSelected(true);
    for (final String supportedWBMode : values) {
      final ImageButton wbModeButton = whiteBalanceModeButtons.get(supportedWBMode);
      if (wbModeButton != null) {
        supportedWhiteBalanceModeButtons.add(wbModeButton);
        wbModeButton.setVisibility(View.VISIBLE);
        wbModeButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            selectWhiteBalanceButton(wbModeButton);
            presenter.setWhiteBalanceMode(supportedWBMode);
          }
        });
      } else {
        Log.e(LOG_TAG, "Missing WB icon: " + supportedWBMode);
      }
    }
  }

  private void selectWhiteBalanceButton(ImageButton wbModeButton) {
    deselectAllWhiteBalanceButtons();
    wbModeButton.setSelected(true);
  }

  private void deselectAllWhiteBalanceButtons() {
    for (ImageButton wbModeButton : supportedWhiteBalanceModeButtons) {
      wbModeButton.setSelected(false);
    }
  }

  private void initWhiteBalanceModesMap() {
    whiteBalanceModeButtons = new HashMap();
    whiteBalanceModeButtons.put(Camera2WhiteBalanceHelper.WB_MODE_AUTO, wbSettingAuto);
    whiteBalanceModeButtons.put(Camera2WhiteBalanceHelper.WB_MODE_CLOUDY_DAYLIGHT, wbSettingCloudy);
    whiteBalanceModeButtons.put(Camera2WhiteBalanceHelper.WB_MODE_DAYLIGHT, wbSettingDaylight);
    whiteBalanceModeButtons.put(Camera2WhiteBalanceHelper.WB_MODE_FLASH, wbSettingFlash);
    whiteBalanceModeButtons.put(Camera2WhiteBalanceHelper.WB_MODE_FLUORESCENT,
            wbSettingFluorescent);
    whiteBalanceModeButtons.put(Camera2WhiteBalanceHelper.WB_MODE_INCANDESCENT,
            wbSettingIncandescent);
  }

  private void initFocusSelectionModesMap() {
    focusSelectionModeButtons = new HashMap();
    focusSelectionModeButtons.put(AF_MODE_AUTO, afSettingAuto);
    focusSelectionModeButtons.put(Camera2FocusHelper.AF_MODE_MANUAL, afSettingManual);
    focusSelectionModeButtons.put(Camera2FocusHelper.AF_MODE_REGIONS, afSettingSelective);

    afSettingSelective.setSelected(true);
  }

  @Override
  public void setupMeteringModeSupportedModesButtons(List<String> supportedMeteringModes) {
    meteringModeAuto.setSelected(true);
    if (supportedMeteringModes.contains(Camera2MeteringModeHelper.AE_MODE_EXPOSURE_COMPENSATION)) {
      meteringModeExposureCompensation.setVisibility(View.VISIBLE);
    }
    if (supportedMeteringModes.contains(Camera2MeteringModeHelper.AE_MODE_REGIONS)) {
      meteringModeCenter.setVisibility(View.VISIBLE);
      meteringModeSpot.setVisibility(View.VISIBLE);
      final int windowWidth = customManualFocusView.getWidth();
      final int windowHeight = customManualFocusView.getHeight();
      cameraShutter.setOnTouchListener(new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
          switch(motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_UP:
              onTouchSelectedArea(TOUCH_AREA_MODE_METERING_POINT, motionEvent);
              break;
            case MotionEvent.ACTION_MOVE:
              touchEventX = (int) Math.max(motionEvent.getRawX(), windowWidth);
              touchEventY = (int) Math.max(motionEvent.getRawY(), windowHeight);
              cameraShutter.setX(touchEventX - cameraShutter.getMeasuredWidth() / 2);
              cameraShutter.setY(touchEventY - cameraShutter.getMeasuredHeight() / 2);
              Log.d(LOG_TAG, "Move shutter to "+(int) (touchEventX - cameraShutter.getMeasuredWidth() / 2)
                      +" - "+(int) (touchEventY - cameraShutter.getMeasuredHeight() / 2));
              break;
            default:
              break;
          }
          return true;
        }
      });
    }
  }

  private void deselectAllMeteringModeButtons() {
    meteringModeAuto.setSelected(false);
    meteringModeCenter.setSelected(false);
    meteringModeSpot.setSelected(false);
  }

  @OnClick(R.id.metering_mode_auto)
  public void setAutoExposure() {
    deselectAllMeteringModeButtons();
    meteringModeExposureCompensation.setSelected(false);
    hideExposureCompensationSubmenu();
    disableSpotMeteringControl();
    meteringModeAuto.setSelected(true);
    presenter.resetMeteringMode();
  }

  @OnClick(R.id.metering_mode_exposure_compensation)
  public void clickExposureCompensationButton() {
    meteringModeExposureCompensation.setSelected(true);
    if (slideSeekbarSubmenuView.getVisibility() == View.VISIBLE) {
      hideExposureCompensationSubmenu();
    } else {
      showExposureCompensationSubmenu();
    }
  }

  @OnClick(R.id.metering_mode_center)
  public void clickCenterMeteringMode() {
    deselectAllMeteringModeButtons();
    meteringModeCenter.setSelected(true);
    onTouchSelectedArea(TOUCH_AREA_MODE_METERING_CENTER, null);
  }

  private void hideExposureCompensationSubmenu() {
    slideSeekbarSubmenuView.setVisibility(View.INVISIBLE);
    slideSeekBarMode = SLIDE_SEEKBAR_MODE_UNACTIVE;
    slideSeekbarSubmenuView.setVisibility(View.INVISIBLE);
  }

  private void showExposureCompensationSubmenu() {
    seekbarUpperText.setVisibility(View.VISIBLE);
    seekbarLowerText.setVisibility(View.VISIBLE);
    seekBarUpperImage.setVisibility(View.GONE);
    seekBarLowerImage.setVisibility(View.GONE);
    float maxEV = Math.round(presenter.getMaximumExposureCompensation()
            * presenter.getExposureCompensationStep());
    float minEV = Math.round(presenter.getMinimumExposureCompensation()
            * presenter.getExposureCompensationStep());
    seekbarUpperText.setText(maxEV + "EV");
    seekbarLowerText.setText(minEV + "EV");
    slideSeekBarMode = SLIDE_SEEKBAR_MODE_EXPOSURE_COMPENSATION;
    final int minExposure = presenter.getMinimumExposureCompensation();
    slideSeekBar.setOnSeekBarChangeListener(null); // clear an existing listener - don't want to call the listener when setting up the progress bar to match the existing state
    slideSeekBar.setMax( presenter.getMaximumExposureCompensation() - minExposure );
    slideSeekBar.setProgress( presenter.getCurrentExposureCompensation() - minExposure );
    slideSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int seekbarProgress, boolean b) {
        presenter.setExposureCompensation(minExposure + seekbarProgress);
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {

      }
    });
    slideSeekbarSubmenuView.setVisibility(View.VISIBLE);
  }

  @Override
  public void showMetteringModeSelection() {
    meteringModeButton.setVisibility(View.VISIBLE);
  }

  @Override
  public void hideMetteringModeSelection() {
    meteringModeButton.setVisibility(View.GONE);
  }

  @Override
  public void setupFocusSelectionSupportedModesButtons(List<String> values) {
    for (final String supportedFocusSelectionMode : values) {
      final ImageButton focusSelectionModeButton = focusSelectionModeButtons
          .get(supportedFocusSelectionMode);
      if (focusSelectionModeButton != null) {
        supportedFocusSelectionModeButtons.add(focusSelectionModeButton);
        focusSelectionModeButton.setVisibility(View.VISIBLE);
      } else {
        Log.e(LOG_TAG, "Missing focus selection icon: " + supportedFocusSelectionMode);
      }
    }
  }

  private void selectFocusSelectionButton(ImageButton focusSelectionModeButton) {
    deselectAllFocusSelectionButtons();
    focusSelectionModeButton.setSelected(true);

  }

  private void deselectAllFocusSelectionButtons() {
    for (ImageButton focusSelectionModeButton : supportedFocusSelectionModeButtons) {
      focusSelectionModeButton.setSelected(false);
    }
    hideFocusModeManualSlider();
  }

  @OnClick(R.id.af_setting_auto)
  public void onClickFocusModeAuto(){
    deselectAllFocusSelectionButtons();
    selectFocusSelectionButton(afSettingAuto);
    presenter.setFocusSelectionMode(AF_MODE_AUTO); // Ask for QA, UX.
  }

  @OnClick(R.id.af_setting_manual)
  public void onClickFocusModeManual(){
    deselectAllFocusSelectionButtons();
    selectFocusSelectionButton(afSettingManual);
    showFocusModeManualSlider();
    presenter.setFocusSelectionMode(AF_MODE_MANUAL);
  }

  @OnClick(R.id.af_setting_selective)
  public void onClickFocusModeSelective(){
    deselectAllFocusSelectionButtons();
    selectFocusSelectionButton(afSettingSelective);
    presenter.setFocusSelectionMode(AF_MODE_REGIONS);
  }

  private void showFocusModeManualSlider() {
    seekbarUpperText.setVisibility(View.GONE);
    seekbarLowerText.setVisibility(View.GONE);
    seekBarUpperImage.setVisibility(View.VISIBLE);
    seekBarLowerImage.setVisibility(View.VISIBLE);
    seekBarUpperImage.setImageResource(R.drawable.activity_record_ic_focus_infinite);
    seekBarLowerImage.setImageResource(R.drawable.activity_record_ic_focus_macro);
    slideSeekBarMode = SLIDE_SEEKBAR_MODE_FOCUS_MANUAL;
    slideSeekBar.setOnSeekBarChangeListener(null); // clear an existing listener - don't want to call the listener when setting up the progress bar to match the existing state
    slideSeekBar.setMax(100);
    slideSeekBar.setProgress(50);
    slideSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int seekbarProgress, boolean b) {
        presenter.setFocusSelectionModeManual(seekbarProgress);
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {

      }
    });
    slideSeekbarSubmenuView.setVisibility(View.VISIBLE);
  }

  private void hideFocusModeManualSlider() {
    slideSeekbarSubmenuView.setVisibility(View.INVISIBLE);
    slideSeekBarMode = SLIDE_SEEKBAR_MODE_UNACTIVE;
    slideSeekbarSubmenuView.setVisibility(View.INVISIBLE);
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
  public void setFocusModeManual(MotionEvent event){
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
  public void onClickListenerGridButton() {
    if (gridButton.isSelected()) {
      disableGrid();
    } else {
      enableGrid();
    }
  }

  private void enableGrid() {
    gridButton.setSelected(true);
    imageViewGrid.setVisibility(View.VISIBLE);
  }

  private void disableGrid() {
    gridButton.setSelected(false);
    imageViewGrid.setVisibility(View.INVISIBLE);
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
  public void onClickZoomListener() {
    if (zoomButton.isSelected()) {
      hideZoomSelectionSubmenu();
    } else {
      hideISOSelectionSubmenu();
      hideAFSelectionSubmenu();
      hideWhiteBalanceSubmenu();
      hideMeteringModeSelectionSubmenu();
      showZoomSelectionSubmenu();
    }
  }

  @OnClick (R.id.button_iso)
  public void onClickIsoListener() {
    if (isoButton.isSelected()) {
      hideISOSelectionSubmenu();
    } else {
      hideZoomSelectionSubmenu();
      hideAFSelectionSubmenu();
      hideWhiteBalanceSubmenu();
      hideMeteringModeSelectionSubmenu();
      showISOSelectionSubmenu();
    }
  }

  @OnClick (R.id.button_af_selection)
  public void onClickAfSelectionListener() {
    if (afSelectionButton.isSelected()) {
      hideAFSelectionSubmenu();
    } else {
      hideZoomSelectionSubmenu();
      hideISOSelectionSubmenu();
      hideWhiteBalanceSubmenu();
      hideMeteringModeSelectionSubmenu();
      showAFSelectionSubmenu();
    }
  }

  @OnClick (R.id.button_white_balance)
  public void onClickWhiteBalanceListener() {
    if (whiteBalanceButton.isSelected()) {
      hideWhiteBalanceSubmenu();
    } else {
      hideZoomSelectionSubmenu();
      hideISOSelectionSubmenu();
      hideAFSelectionSubmenu();
      hideMeteringModeSelectionSubmenu();
      showWhiteBalanceSubmenu();
    }
  }

  @OnClick(R.id.button_metering_mode)
  public void onClickMeasurementeModeListener() {
    if (meteringModeButton.isSelected()) {
      hideMeteringModeSelectionSubmenu();
    } else {
      hideZoomSelectionSubmenu();
      hideISOSelectionSubmenu();
      hideAFSelectionSubmenu();
      hideWhiteBalanceSubmenu();
      showMeteringModeSelectionSubmenu();
    }
  }

  @OnClick (R.id.button_camera_default)
  public void setCameraDefaultSettings() {
    // TODO(jliarte): 6/07/17 should move this logic to presenter?
    hideZoomSelectionSubmenu();
    slideSeekBar.setProgress(0);
    presenter.setZoom(0f);

    hideISOSelectionSubmenu();
    setAutoISO();

    disableGrid();

    hideAFSelectionSubmenu();

    hideWhiteBalanceSubmenu();
    deselectAllWhiteBalanceButtons();
    wbSettingAuto.setSelected(true);
    presenter.resetWhiteBalanceMode();
    deselectAllFocusSelectionButtons();
    presenter.resetFocusSelectionMode();
    setAutoSettingsFocusModeByDefault();
    setAutoExposure();
    hideMeteringModeSelectionSubmenu();
  }

  private void setAutoSettingsFocusModeByDefault() {
    afSettingSelective.setSelected(true);
  }

  private void showZoomSelectionSubmenu() {
    slideSeekBarMode = SLIDE_SEEKBAR_MODE_ZOOM;
    slideSeekBar.setOnSeekBarChangeListener(null);
    zoomButton.setSelected(true);
    slideSeekbarSubmenuView.setVisibility(View.VISIBLE);
    seekbarUpperText.setVisibility(View.VISIBLE);
    seekbarLowerText.setVisibility(View.VISIBLE);
    seekBarUpperImage.setVisibility(View.GONE);
    seekBarLowerImage.setVisibility(View.GONE);
    seekbarUpperText.setText("100%");
    seekbarLowerText.setText("0%");
    slideSeekBar.setProgress(currentSeekbarZoom);
    slideSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        currentSeekbarZoom = progress;
        presenter.onSeekBarZoom((float) (progress * 0.01));
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {

      }
    });
  }

  private void hideZoomSelectionSubmenu() {
    zoomButton.setSelected(false);
    slideSeekbarSubmenuView.setVisibility(View.INVISIBLE);
  }

  private void showISOSelectionSubmenu() {
    isoButton.setSelected(true);
    isoSubmenuView.setVisibility(View.VISIBLE);
  }

  private void hideISOSelectionSubmenu() {
    isoButton.setSelected(false);
    isoSubmenuView.setVisibility(View.INVISIBLE);
  }

  private void setAutoISO() {
    deselectAllISOButtons();
    isoSettingAuto.setSelected(true);
    isoSettingAuto.setTextColor(getResources().getColor(R.color.button_selected));
    presenter.setISO(0);
  }

  private void showAFSelectionSubmenu() {
    afSelectionButton.setSelected(true);
    afSelectionSubmenuView.setVisibility(View.VISIBLE);
    if(afSettingManual.isSelected()){
      showFocusModeManualSlider();
    }
  }

  private void hideAFSelectionSubmenu() {
    afSelectionButton.setSelected(false);
    afSelectionSubmenuView.setVisibility(View.INVISIBLE);
    hideFocusModeManualSlider();
  }

  private void showMeteringModeSelectionSubmenu() {
    meteringModeButton.setSelected(true);
    meteringModeSubmenuView.setVisibility(View.VISIBLE);
    if (meteringModeExposureCompensation.isSelected()) {
      showExposureCompensationSubmenu();
    }
  }

  private void hideMeteringModeSelectionSubmenu() {
    meteringModeButton.setSelected(false);
    meteringModeSubmenuView.setVisibility(View.INVISIBLE);
    hideExposureCompensationSubmenu();
  }

  private void showWhiteBalanceSubmenu() {
    whiteBalanceButton.setSelected(true);
    whiteBalanceSubmenuView.setVisibility(View.VISIBLE);
  }

  private void hideWhiteBalanceSubmenu() {
    whiteBalanceButton.setSelected(false);
    whiteBalanceSubmenuView.setVisibility(View.INVISIBLE);
  }

  @OnClick(R.id.button_record)
 public void onClickRecordButton(){
   if (!isRecording) {
     presenter.startRecord();
   } else {
     presenter.stopRecord();
     updatePercentFreeStorage();
     updateBatteryStatus();
   }
 }

  @OnTouch(R.id.customManualFocusView)
  boolean onTouchCustomManualFocusView(MotionEvent event) {
    if (event.getAction() == MotionEvent.ACTION_DOWN) {
      if(afSettingSelective.isSelected()) {
        onTouchSelectedArea(TOUCH_AREA_MODE_FOCUS_SELECTIVE, event);
      }
      return true;
    }

    if (event.getPointerCount() > 1) {
      // zoom touch
      presenter.onTouchZoom(event);
    }
    return false;
  }

  @OnClick(R.id.metering_mode_spot)
  public void onClickSpotMetering() {
    deselectAllMeteringModeButtons();
    meteringModeSpot.setSelected(true);
    enableSpotMeteringControl();
  }

  private void enableSpotMeteringControl() {
    cameraShutter.setVisibility(View.VISIBLE);
    cameraShutter.setEnabled(true);
  }

  private void disableSpotMeteringControl() {
    cameraShutter.setVisibility(View.GONE);
    cameraShutter.setEnabled(false);
  }

  // TODO(jliarte): 22/06/17 rename the view to include metering and focus
  boolean onTouchSelectedArea(int type, MotionEvent event) {
    Log.d(LOG_TAG, "-------------------- onTouchSelectedArea " + touchEventX
            + ", " + touchEventY + "--------------");
    int viewWidth = customManualFocusView.getWidth();
    int viewHeight = customManualFocusView.getHeight();
    if(type == TOUCH_AREA_MODE_METERING_POINT) {
      presenter.setMeteringPoint(touchEventX, touchEventY, viewWidth, viewHeight);
    }
    if(type == TOUCH_AREA_MODE_METERING_CENTER) {
      presenter.setMeteringPoint(viewWidth/2, viewHeight/2, viewWidth, viewHeight);
    }
    if(type == TOUCH_AREA_MODE_FOCUS_SELECTIVE){
      presenter.setFocusSelectionModeSelective((int) event.getRawX(), (int) event.getRawY(),
          viewWidth, viewHeight, event);
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
        showMessage(getString(R.string.toast_exit));
    }
  }

  @Override
  public void navigateTo(Class cls) {
    Intent intent = new Intent(VimojoApplication.getAppContext(), cls);
    startActivity(intent);
  }

  private void showMessage(String message) {
    Snackbar snackbar = Snackbar.make(chronometerAndRecPointView, message,
        Snackbar.LENGTH_SHORT);
    snackbar.show();
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
