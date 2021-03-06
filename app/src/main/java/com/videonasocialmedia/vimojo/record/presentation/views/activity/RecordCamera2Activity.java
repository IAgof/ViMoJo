package com.videonasocialmedia.vimojo.record.presentation.views.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.Range;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;
import com.videonasocialmedia.camera.camera2.Camera2FocusHelper;
import com.videonasocialmedia.camera.camera2.Camera2MeteringModeHelper;
import com.videonasocialmedia.camera.camera2.Camera2WhiteBalanceHelper;
import com.videonasocialmedia.camera.customview.AutoFitTextureView;
import com.videonasocialmedia.camera.customview.CustomManualFocusView;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.cameraSettings.presentation.view.activity.CameraSettingsActivity;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.main.modules.ActivityPresentersModule;
import com.videonasocialmedia.vimojo.presentation.views.activity.GalleryActivity;
import com.videonasocialmedia.vimojo.presentation.views.customviews.CircleImageView;
import com.videonasocialmedia.vimojo.record.presentation.mvp.presenters.RecordCamera2Presenter;
import com.videonasocialmedia.vimojo.record.presentation.mvp.views.RecordCamera2View;
import com.videonasocialmedia.vimojo.record.presentation.views.broadcastreceiver.BatteryReceiver;
import com.videonasocialmedia.vimojo.record.presentation.views.custom.dialogs.AlertDialogWithInfoIntoCircle;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.activity.TutorialRecordActivity;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.IntentConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.inject.Inject;

import butterknife.BindView;
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
  private static final int SLIDE_SEEKBAR_MODE_EXPOSURE_COMPENSATION = 1;
  private static final int SLIDE_SEEKBAR_MODE_ZOOM = 2;
  private static final int SLIDE_SEEKBAR_MODE_FOCUS_MANUAL = 3;
  private static final int SLIDE_SEEKBAR_MODE_AUDIO_GAIN = 4;
  private static final int SLIDE_SEEKBAR_MODE_MANUAL_EXPOSURE_TIME = 5;
  public static final int DEFAULT_AUDIO_GAIN = 100;
  public static final float SCALE_AND_PICOMETER_PROGRESS = 2f;
  private final String LOG_TAG = getClass().getSimpleName();

  public static final String GRID_MODE_LINES = "lines";
  public static final String GRID_MODE_ONE_ONE = "one_one";
  public static final String GRID_MODE_CROSSES = "crosses";
  public static final String GRID_MODE_FIBONACCI = "fibonacci";

  @Inject
  RecordCamera2Presenter presenter;
  @BindView(R.id.button_navigate_tutorial)
  ImageButton tutorial;

  @BindView(R.id.button_settings_camera)
  ImageButton settingsCameraButton;
  @BindView(R.id.button_toggle_flash)
  ImageButton flashButton;
  @BindView(R.id.button_change_camera)
  ImageButton changeCameraButton;
  @BindView(R.id.button_to_show_controls_right)
  ImageButton showControlsButton;
  @BindView(R.id.button_to_hide_controls)
  ImageButton hideControlsViewButton;
  @BindView(R.id.button_navigate_settings)
  ImageButton navigateSettingsButtons;
  @BindView(R.id.chronometer_record)
  Chronometer chronometer;
  @BindView(R.id.imageRecPoint)
  ImageView recPointImage;
  @BindView(R.id.text_view_info_resolution)
  TextView textViewInfoResolution;
  @BindView(R.id.text_view_info_quality)
  TextView textViewInfoQuality;
  @BindView(R.id.text_view_info_frame_rate)
  TextView textViewInfoFrameRate;
  @BindView(R.id.text_view_num_videos)
  TextView numVideosRecordedTextView;
  @BindView(R.id.edit_or_gallery_text_view)
  TextView editOrGalleryTextView;
  @BindView(R.id.edit_or_gallery_cardview)
  CardView editOrGalleryTextCardView;
  @BindView(R.id.button_navigate_edit)
  CircleImageView thumbClipRecordedButton;
  @BindView(R.id.button_navigate_gallery)
  ImageButton navigateGalleryButton;
  @BindView(R.id.button_record)
  ImageButton recordButton;
  @BindView(R.id.control_chronometer_and_rec_point)
  View chronometerAndRecPointView;
  @BindView(R.id.clear_button)
  ImageButton clearButton;
  @BindView(R.id.show_all_controls_button)
  ImageButton showAllControlsButton;
  @BindView(R.id.hud_cardview)
  CardView hudCardView;
  @BindView(R.id.seekBar_slide_left)
  SeekBar slideSeekBar;
  @BindView(R.id.seekBar_upper_text)
  TextView seekbarUpperText;
  @BindView(R.id.seekBar_lower_text)
  TextView seekbarLowerText;
  @BindView(R.id.seekBar_lower_image)
  ImageView seekBarLowerImage;
  @BindView(R.id.seekBar_upper_image)
  ImageView seekBarUpperImage;
  @BindView(R.id.settings_bar_cardview)
  CardView settingsCameraCardView;
  @BindView(R.id.button_zoom)
  ImageButton zoomButton;
  @BindView(R.id.slide_seekbar_submenu_cardview)
  CardView slideSeekbarSubmenuView;

  @BindView(R.id.button_manual_exposure)
  ImageButton manualExposureButton;
  @BindView(R.id.manual_exposure_submenu)
  LinearLayout manualExposureSubmenu;
  @BindView(R.id.iso_submenu)
  LinearLayout isoSubmenu;
  @BindView(R.id.icon_iso)
  ImageView iconISO;
  @Nullable @BindView(R.id.exposure_time_seekbar_vertical)
  VerticalSeekBar exposureTimeSeekBarVertical;
  @Nullable @BindView(R.id.exposure_time_seekbar)
  SeekBar exposureTimeSeekBar;
  @BindView(R.id.exposure_time_seekBar_text_max)
  TextView maxExposureText;
  @BindView(R.id.exposure_time_seekBar_text_min)
  TextView minExposureText;

  @BindView(R.id.button_af_selection)
  ImageButton afSelectionButton;
  @BindView(R.id.af_selection_submenu_cardview)
  CardView afSelectionSubmenuCardView;
  @BindView(R.id.af_setting_auto)
  ImageButton afSettingAuto;
  @BindView(R.id.af_setting_manual)
  ImageButton afSettingManual;
  @BindView(R.id.af_setting_selective)
  ImageButton afSettingSelective;
  @BindView(R.id.button_white_balance)
  ImageButton whiteBalanceButton;
  @BindView(R.id.white_balance_submenu_cardview)
  CardView whiteBalanceSubmenuCardView;
  @BindView(R.id.wb_setting_auto)
  ImageButton wbSettingAuto;
  @BindView(R.id.wb_setting_cloudy)
  ImageButton wbSettingCloudy;
  @BindView(R.id.wb_setting_daylight)
  ImageButton wbSettingDaylight;
  @BindView(R.id.wb_setting_flash)
  ImageButton wbSettingFlash;
  @BindView(R.id.wb_setting_fluorescent)
  ImageButton wbSettingFluorescent;
  @BindView(R.id.wb_setting_incandescent)
  ImageButton wbSettingIncandescent;

  @BindView(R.id.button_metering_mode)
  ImageButton meteringModeButton;
  @BindView(R.id.metering_mode_submenu_cardview)
  CardView meteringModeSubmenuCardView;
  @BindView(R.id.metering_mode_auto)
  ImageButton meteringModeAuto;
  @BindView(R.id.metering_mode_exposure_compensation)
  ImageButton meteringModeExposureCompensation;
  @BindView(R.id.metering_mode_center)
  ImageButton meteringModeCenter;
  @BindView(R.id.metering_mode_spot)
  ImageButton meteringModeSpot;

  @BindView(R.id.button_grid)
  ImageButton gridButton;
  @BindView(R.id.grid_mode_submenu_cardview)
  CardView gridModeSubmenuCardview;
  @BindView(R.id.grid_mode_lines)
  ImageButton gridModeLines;
  @BindView(R.id.grid_mode_one_one)
  ImageButton gridModeOneOne;
  @BindView(R.id.grid_mode_crosses)
  ImageButton gridModeCrosses;
  @BindView(R.id.grid_mode_fibonacci)
  ImageButton gridModeFibonacci;

  @BindView(R.id.button_camera_default)
  ImageButton cameraDefaultSettingsButton;
  @BindView(R.id.customManualFocusView)
  CustomManualFocusView customManualFocusView;
  @BindView(R.id.camera_shutter)
  ImageView cameraShutter;
  @BindView(R.id.rotateDeviceHint)
  ImageView rotateDeviceHint;
  @BindView(R.id.image_view_grid)
  ImageView imageViewGrid;

  @BindView(R.id.activity_record_icon_battery)
  ImageView batteryButton;
  @BindView(R.id.activity_record_icon_storage)
  ImageView storageButton;
  @BindView(R.id.mic_plug_imageview)
  ImageButton micPlugButton;
  @BindView(R.id.picometer_progressbar)
  ProgressBar picometerProgress;
  @BindView(R.id.button_sound_volume)
  ImageButton soundVolumeButton;

  /**
   * An {@link AutoFitTextureView} for camera preview.
   */
  @BindView(R.id.textureView)
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

  private BatteryReceiver batteryReceiver = new BatteryReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
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
  private HashMap<String, ImageButton> gridModeButtons;
  private List<String> listGridModeValues;
  private ArrayList<ImageButton> supportedWhiteBalanceModeButtons = new ArrayList<>();
  private ArrayList<ImageButton> supportedFocusSelectionModeButtons = new ArrayList<>();
  private ArrayList<ImageButton> supportedGridModeButtons = new ArrayList<>();
  private int slideSeekBarMode;
  private int currentSeekbarZoom;
  private int touchEventX = 0;
  private int touchEventY = 0;
  private HashMap<TextView, Integer> isoButtons = new HashMap<>();

  // TODO:(alvaro.martinez) 10/07/17 Study unify broadcast receiver with batteryReceiver
  BroadcastReceiver jackConnectorReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      Bundle bundle = intent.getExtras();
      if (bundle != null && intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
          updateMicrophoneStatus(intent);
      }
    }
  };
  public int audioGainSeekBarProgress = DEFAULT_AUDIO_GAIN;

  private final SeekBar.OnSeekBarChangeListener recordActivitySeekBarChangeListener =
          new SeekBar.OnSeekBarChangeListener() {
    @Override
    public void onProgressChanged(SeekBar seekBar, int seekbarProgress, boolean fromUser) {
      if (!fromUser) {
        return;
      }
      switch (slideSeekBarMode) {
        case SLIDE_SEEKBAR_MODE_EXPOSURE_COMPENSATION:
          presenter.setExposureCompensation(minExposureCompensation + seekbarProgress);
          break;
        case SLIDE_SEEKBAR_MODE_ZOOM:
          currentSeekbarZoom = seekbarProgress;
          presenter.onSeekBarZoom((float) (seekbarProgress * 0.01));
          break;
        case SLIDE_SEEKBAR_MODE_FOCUS_MANUAL:
          presenter.setFocusSelectionModeManual(seekbarProgress);
          break;
        case SLIDE_SEEKBAR_MODE_AUDIO_GAIN:
          // TODO(jliarte): 13/07/17 should we save these adjusts on activity pause???
          audioGainSeekBarProgress = seekbarProgress;
          presenter.setAudioGain(audioGainSeekBarProgress);
          break;
        case SLIDE_SEEKBAR_MODE_MANUAL_EXPOSURE_TIME:
          presenter.setExposureTime(minExposureTime + seekbarProgress);
          break;
        case SLIDE_SEEKBAR_MODE_UNACTIVE:
        default:
//          do nothing
      }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
  };
  private int minExposureCompensation;
  private int minExposureTime;
  /**
   * boolean to control decision am i a vertical app?
   * Update value on init record screen and used logic in activity in order to NOT update all
   * methods involve and moved to presenter.
   */
  private boolean amIAVerticalApp;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_record_camera2);
    keepScreenOn();
    ButterKnife.bind(this);
    setupActivityButtons();
    setupSlideSeekBar();
//    configChronometer(); // TODO(jliarte): 26/06/17 make sure this is not needed anymore
    configShowThumbAndNumberClips();

    this.getActivityPresentersComponent().inject(this);

    createAlertDialogBatteryAndStorage();
    initWhiteBalanceModesMap();
    initFocusSelectionModesMap();
    initGridModesMap();
    initPicometer();
  }

  @Override
  public void onResume() {
    super.onResume();
    initOrientationHelper();
    presenter.initViews();
    presenter.updatePresenter();
    hideSystemUi();
    registerReceiver(batteryReceiver,new IntentFilter(IntentConstants.BATTERY_NOTIFICATION));
    registerReceiver(jackConnectorReceiver,new IntentFilter(Intent.ACTION_HEADSET_PLUG));
    updateBatteryStatus();
    updatePercentFreeStorage();
  }

  @Override
  public void onPause() {
    unregisterReceiver(batteryReceiver);
    unregisterReceiver(jackConnectorReceiver);
    presenter.onPause();
    super.onPause();
  }

  private void initPicometer() {
    tintProgress(ColorStateList.valueOf(getResources().getColor(R.color.recordActivityInfoGreen)));
    picometerProgress.setScaleY(SCALE_AND_PICOMETER_PROGRESS);
  }

  private void createAlertDialogBatteryAndStorage() {
    alertDialogBattery = new AlertDialogWithInfoIntoCircle(this, getString(R.string.battery));
    alertDialogStorage = new AlertDialogWithInfoIntoCircle(this, getString(R.string.storage));
  }

  @Override
  public ActivityPresentersModule getActivityPresentersModule() {
    return new ActivityPresentersModule(this, Constants.PATH_APP_TEMP,
        textureView, getFreeStorage(new StatFs(Environment.getDataDirectory().getPath())));
  }

  private void keepScreenOn() {
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
  }

  public void setupActivityButtons() {
    // TODO:(alvaro.martinez) 7/11/16 implement this functionality, use case check support camera
    tintRecordButtons(R.color.button_color_record_activity);
  }

  private void tintRecordButtons(int button_color) {
    tintButton(flashButton, button_color);
    tintButton(changeCameraButton, button_color);
    tintButton(showControlsButton, button_color);
    tintButton(hideControlsViewButton, button_color);
    tintButton(navigateSettingsButtons, button_color);
    tintButton(settingsCameraButton, button_color);
    tintButton(zoomButton, button_color);
    tintButton(manualExposureButton, button_color);

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
    tintButton(gridModeLines, button_color);
    tintButton(gridModeOneOne, button_color);
    tintButton(gridModeCrosses, button_color);
    tintButton(gridModeFibonacci, button_color);

    tintButton(cameraDefaultSettingsButton, button_color);
    tintButton(soundVolumeButton, button_color);
  }

  private void setupSlideSeekBar() {
    slideSeekBar.setOnSeekBarChangeListener(recordActivitySeekBarChangeListener);
  }

  private void setupExposureTimeSeekBar() {
    if (amIAVerticalApp) {
      exposureTimeSeekBarVertical.setOnSeekBarChangeListener(recordActivitySeekBarChangeListener);
    } else {
      exposureTimeSeekBar.setOnSeekBarChangeListener(recordActivitySeekBarChangeListener);
    }
    disableExposureTimeSeekBar();
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
    numVideosRecordedTextView.setVisibility(View.GONE);
  }

  private void initOrientationHelper() {
    orientationHelper = new OrientationHelper(this);
  }

  private void tintProgress(final ColorStateList tint) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        picometerProgress.setProgressTintList(tint);
      }
    });
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
  public void startChronometer() {
    resetChronometer();
    chronometer.start();
  }

  @Override
  public void stopChronometer() {
    chronometer.stop();
  }

  @Override
  public void resetChronometer() {
    chronometer.setBase(SystemClock.elapsedRealtime());
    chronometer.setText("00:00");  }

  @Override
  public void showRecordPointIndicator() {
    recPointImage.setVisibility(View.VISIBLE);
  }

  @Override
  public void hideRecordPointIndicator() {
    recPointImage.setVisibility(View.INVISIBLE);
  }

  @Override
  public void showNavigateToSettingsActivity() {
    navigateSettingsButtons.setEnabled(true);
  }

  @Override
  public void hideNavigateToSettingsActivity() {
    navigateSettingsButtons.setEnabled(false);
  }

  @Override
  public void setFlash(boolean on) {
    // TODO:(alvaro.martinez) 18/01/17 Review flash tracking
    // trackUserInteracted(AnalyticsConstants.CHANGE_FLASH, String.valueOf(on));
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
  public void setSwitchCameraSupported(boolean supported) {
    if(supported) {
      changeCameraButton.setEnabled(true);
    } else {
      changeCameraButton.setEnabled(false);
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
  public void enableChangeCameraIcon() {
    changeCameraButton.setEnabled(true);
    changeCameraButton.setActivated(true);
  }

  @Override
  public void disableChangeCameraIcon() {
    changeCameraButton.setEnabled(false);
    changeCameraButton.setActivated(false);
  }

  @Override
  public void showError(String message) {
    Log.d(LOG_TAG, "showError " + message);
    showMessage(message);
  }

  @Override
  public void hidePrincipalViews() {
    showAllControlsButton.setVisibility(View.VISIBLE);
    hudCardView.setVisibility(View.INVISIBLE);
    hideControlsViewButton.setVisibility(View.INVISIBLE);
    showControlsButton.setVisibility(View.INVISIBLE);
    slideSeekbarSubmenuView.setVisibility(View.INVISIBLE);
    manualExposureSubmenu.setVisibility(View.INVISIBLE);
    afSelectionSubmenuCardView.setVisibility(View.INVISIBLE);
    whiteBalanceSubmenuCardView.setVisibility(View.INVISIBLE);
    meteringModeSubmenuCardView.setVisibility(View.INVISIBLE);
    gridModeSubmenuCardview.setVisibility(View.INVISIBLE);
    settingsCameraCardView.setVisibility(View.INVISIBLE);
    slideSeekbarSubmenuView.setVisibility(View.INVISIBLE);
    editOrGalleryTextCardView.setVisibility(View.INVISIBLE);
  }

  @Override
  public void showPrincipalViews() {
    showAllControlsButton.setVisibility(View.GONE);
    hudCardView.setVisibility(View.VISIBLE);
    showControlsButton.setVisibility(View.VISIBLE);
    editOrGalleryTextCardView.setVisibility(View.VISIBLE);
  }

  @Override
  public void hideRightControlsView() {
    hideControlsViewButton.setVisibility(View.INVISIBLE);
    showControlsButton.setVisibility(View.VISIBLE);
    settingsCameraCardView.setVisibility(View.INVISIBLE);

    settingsCameraCardView.setVisibility(View.INVISIBLE);
    slideSeekbarSubmenuView.setVisibility(View.INVISIBLE);
    manualExposureSubmenu.setVisibility(View.INVISIBLE);
    afSelectionSubmenuCardView.setVisibility(View.INVISIBLE);
    whiteBalanceSubmenuCardView.setVisibility(View.INVISIBLE);
    meteringModeSubmenuCardView.setVisibility(View.INVISIBLE);
    gridModeSubmenuCardview.setVisibility(View.INVISIBLE);
  }

  @Override
  public void showRightControlsView() {
    showControlsButton.setVisibility(View.INVISIBLE);
    hideControlsViewButton.setVisibility(View.VISIBLE);
    if (settingsCameraButton.isSelected()) {
      settingsCameraCardView.setVisibility(View.VISIBLE);
    }
    if (zoomButton.isSelected()) {
      slideSeekbarSubmenuView.setVisibility(View.VISIBLE);
      return;
    }
    if (manualExposureButton.isSelected()) {
      manualExposureSubmenu.setVisibility(View.VISIBLE);
      return;
    }
    if (afSelectionButton.isSelected()) {
      afSelectionSubmenuCardView.setVisibility(View.VISIBLE);
      return;
    }
    if (whiteBalanceButton.isSelected()) {
      whiteBalanceSubmenuCardView.setVisibility(View.VISIBLE);
      return;
    }
    if (meteringModeButton.isSelected()) {
      meteringModeSubmenuCardView.setVisibility(View.VISIBLE);
      return;
    }
    if(gridButton.isSelected()) {
      gridModeSubmenuCardview.setVisibility(View.VISIBLE);
    }
  }

  @Override
  public void showSettingsCameraView() {
    settingsCameraCardView.setVisibility(View.VISIBLE);
    settingsCameraButton.setSelected(true);
    if (zoomButton.isSelected()) {
      slideSeekbarSubmenuView.setVisibility(View.VISIBLE);
      return;
    }
    if (manualExposureButton.isSelected()) {
      manualExposureSubmenu.setVisibility(View.VISIBLE);
      return;
    }
    if (afSelectionButton.isSelected()) {
      afSelectionSubmenuCardView.setVisibility(View.VISIBLE);
      return;
    }
    if (whiteBalanceButton.isSelected()) {
      whiteBalanceSubmenuCardView.setVisibility(View.VISIBLE);
      return;
    }
    if (meteringModeButton.isSelected()) {
      meteringModeSubmenuCardView.setVisibility(View.VISIBLE);
      return;
    }
    if(gridButton.isSelected()) {
      gridModeSubmenuCardview.setVisibility(View.VISIBLE);
    }
  }

  @Override
  public void hideSettingsCameraView() {
    settingsCameraCardView.setVisibility(View.INVISIBLE);
    settingsCameraButton.setSelected(false);

    slideSeekbarSubmenuView.setVisibility(View.INVISIBLE);
    manualExposureSubmenu.setVisibility(View.INVISIBLE);
    afSelectionSubmenuCardView.setVisibility(View.INVISIBLE);
    whiteBalanceSubmenuCardView.setVisibility(View.INVISIBLE);
    meteringModeSubmenuCardView.setVisibility(View.INVISIBLE);
    gridModeSubmenuCardview.setVisibility(View.INVISIBLE);
  }

  @Override
  public void showAdvancedAFSelection() {
    afSelectionButton.setVisibility(View.VISIBLE);
  }

  @Override
  public void hideAdvancedAFSelection() {
    afSelectionButton.setVisibility(View.GONE);
    afSettingSelective.setSelected(false);
  }

  @Override
  public void showISOSelection() {
    manualExposureButton.setVisibility(View.VISIBLE);
  }

  @Override
  public void hideISOSelection() {
    manualExposureButton.setVisibility(View.GONE);
  }

  @Override
  public void setupISOSupportedModesButtons(Range<Integer> supportedISORange) {
    clearISObuttons();
    int[] isoValues = {50, 100, 200, 400, 800, 1600, 3200, 6400, 12800, 25600, 51200};
    for (final int isoValue : isoValues) {
      if (supportedISORange.contains(isoValue)) {
        final TextView isoModeButton = new TextView(this);
        isoModeButton.setLayoutParams(iconISO.getLayoutParams());
        isoModeButton.setText(String.valueOf(isoValue));
        isoModeButton.setGravity(Gravity.CENTER);
        isoModeButton.setTextColor(getResources().getColor(R.color.button_color_record_activity));
        setIsoModeOnClickListener(isoValue, isoModeButton);
        isoButtons.put(isoModeButton, isoValue);
        isoSubmenu.addView(isoModeButton);
      }
    }
  }

  private void clearISObuttons() {
    for (TextView button : isoButtons.keySet()) {
      isoSubmenu.removeView(button);
    }
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

  @Override
  public void deselectAllISOButtons() {
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

  @Override
  public void deselectAllWhiteBalanceButtons() {
    for (ImageButton wbModeButton : supportedWhiteBalanceModeButtons) {
      wbModeButton.setSelected(false);
    }
  }

  @Override
  public void selectWbSettingAuto() {
    wbSettingAuto.setSelected(true);
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

  private void initGridModesMap() {
    gridModeButtons = new HashMap();
    gridModeButtons.put(GRID_MODE_LINES, gridModeLines);
    gridModeButtons.put(GRID_MODE_ONE_ONE, gridModeOneOne);
    gridModeButtons.put(GRID_MODE_CROSSES, gridModeCrosses);

    listGridModeValues = new ArrayList<>();
    listGridModeValues.add(GRID_MODE_LINES);
    listGridModeValues.add(GRID_MODE_ONE_ONE);
    listGridModeValues.add(GRID_MODE_CROSSES);

    if (!amIAVerticalApp) {
      gridModeButtons.put(GRID_MODE_FIBONACCI, gridModeFibonacci);
      listGridModeValues.add(GRID_MODE_FIBONACCI);
    }
  }

  @Override
  public void setupMeteringModeSupportedModesButtons(List<String> supportedMeteringModes) {
    meteringModeAuto.setSelected(true);
    if (supportedMeteringModes.contains(Camera2MeteringModeHelper.AE_MODE_EXPOSURE_COMPENSATION)) {
      meteringModeExposureCompensation.setVisibility(View.VISIBLE);
      this.minExposureCompensation = presenter.getMinimumExposureCompensation();
    }
    if (supportedMeteringModes.contains(Camera2MeteringModeHelper.AE_MODE_REGIONS)) {
      meteringModeCenter.setVisibility(View.VISIBLE);
      meteringModeSpot.setVisibility(View.VISIBLE);
      resetSpotMeteringSelector();
      cameraShutter.setOnTouchListener(new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
          switch(motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_UP:
              presenter.setMeteringPoint(touchEventX, touchEventY,
                      getCustomFocusViewWidth(), getCustomFocusViewHeight());
              break;
            case MotionEvent.ACTION_MOVE:
              touchEventX = (int) Math.min(motionEvent.getRawX(), getCustomFocusViewWidth());
              touchEventY = (int) Math.min(motionEvent.getRawY(), getCustomFocusViewHeight());
              moveSpotMeteringSelectorTo(touchEventX - cameraShutter.getMeasuredWidth() / 2,
                      touchEventY - cameraShutter.getMeasuredHeight() / 2);
              Log.d(LOG_TAG, "Move shutter to " +
                      (touchEventX - cameraShutter.getMeasuredWidth() / 2) + " - "
                      + (touchEventY - cameraShutter.getMeasuredHeight() / 2));
              break;
          }
          return true;
        }
      });
    }
  }

  private int getCustomFocusViewHeight() {
    return customManualFocusView.getHeight();
  }

  private int getCustomFocusViewWidth() {
    return customManualFocusView.getWidth();
  }

  @Override
  public void deselectAllMeteringModeButtons() {
    meteringModeAuto.setSelected(false);
    meteringModeCenter.setSelected(false);
    meteringModeSpot.setSelected(false);
    deselectManualExposureButton();
  }

  public void deselectManualExposureButton() {
    manualExposureButton.setSelected(false);
  }

  @Override
  public void disableManualExposure() {
    if (!meteringModeAuto.isSelected()) {
      selectMeteringModeAutoButton();
    }
    hideManualExposureSubmenu();
    resetManualExposure();
    deselectManualExposureButton();
  }

  @OnClick(R.id.metering_mode_auto)
  public void setAutoExposure() {
    presenter.resetMeteringMode();
  }


  @Override
  public void setManualExposure() {
    deselectAllMeteringModeButtons();
    manualExposureButton.setSelected(true);
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
    resetManualExposure();
    disableSpotMeteringControl();
    meteringModeCenter.setSelected(true);
    resetSpotMeteringSelector();
    presenter.setMeteringPoint(getCustomFocusViewWidth() /2,
            getCustomFocusViewHeight() /2,
            getCustomFocusViewWidth(), getCustomFocusViewHeight());
  }

  @Override
  public void resetSpotMeteringSelector() {
    moveSpotMeteringSelectorTo(
            getCustomFocusViewWidth() / 2 - cameraShutter.getMeasuredWidth() / 2,
            getCustomFocusViewHeight() / 2 - cameraShutter.getMeasuredHeight() / 2);
  }

  private void moveSpotMeteringSelectorTo(int x, int y) {
    cameraShutter.setX(x);
    cameraShutter.setY(y);
  }

  @Override
  public void hideExposureCompensationSubmenu() {
    slideSeekbarSubmenuView.setVisibility(View.INVISIBLE);
    slideSeekBarMode = SLIDE_SEEKBAR_MODE_UNACTIVE;
    slideSeekbarSubmenuView.setVisibility(View.INVISIBLE);
  }

  private void showExposureCompensationSubmenu() {
    float maxEV = Math.round(presenter.getMaximumExposureCompensation()
            * presenter.getExposureCompensationStep());
    float minEV = Math.round(presenter.getMinimumExposureCompensation()
            * presenter.getExposureCompensationStep());
    hideSlideSeekBarIcons();
    setSlideSeekBarTexts(maxEV + "EV", minEV + "EV");
    slideSeekBarMode = SLIDE_SEEKBAR_MODE_EXPOSURE_COMPENSATION;
    slideSeekBar.setMax(presenter.getMaximumExposureCompensation() - minExposureCompensation);
    slideSeekBar.setProgress(presenter.getCurrentExposureCompensation() - minExposureCompensation);
    slideSeekBar.setEnabled(true);
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

  @Override
  public void deselectAllFocusSelectionButtons() {
    for (ImageButton focusSelectionModeButton : supportedFocusSelectionModeButtons) {
      focusSelectionModeButton.setSelected(false);
    }
    hideFocusModeManualSlider();
  }

  @OnClick(R.id.af_setting_auto)
  public void onClickFocusModeAuto() {
    deselectAllFocusSelectionButtons();
    selectFocusSelectionButton(afSettingAuto);
    presenter.setFocusSelectionMode(AF_MODE_AUTO); // Ask for QA, UX.
  }

  @OnClick(R.id.af_setting_manual)
  public void onClickFocusModeManual() {
    deselectAllFocusSelectionButtons();
    selectFocusSelectionButton(afSettingManual);
    presenter.setFocusSelectionMode(AF_MODE_MANUAL);
    showFocusModeManualSlider();
  }

  @OnClick(R.id.af_setting_selective)
  public void onClickFocusModeSelective() {
    deselectAllFocusSelectionButtons();
    selectFocusSelectionButton(afSettingSelective);
    presenter.setFocusSelectionMode(AF_MODE_REGIONS);
  }

  private void showFocusModeManualSlider() {
    hideSlideSeekBarTexts();
    showSlideSeekBarIcons(R.drawable.activity_record_ic_focus_infinite,
            R.drawable.activity_record_ic_focus_macro);
    slideSeekBarMode = SLIDE_SEEKBAR_MODE_FOCUS_MANUAL;
    slideSeekBar.setMax(100);
    slideSeekBar.setProgress(presenter.getCurrentFocusSeekBarProgress());
    slideSeekBar.setEnabled(true);
    slideSeekbarSubmenuView.setVisibility(View.VISIBLE);
  }

  private void showSlideSeekBarIcons(int upperImageResource, int lowerImageResource) {
    seekBarUpperImage.setVisibility(View.VISIBLE);
    seekBarLowerImage.setVisibility(View.VISIBLE);
    seekBarUpperImage.setImageResource(upperImageResource);
    seekBarLowerImage.setImageResource(lowerImageResource);
  }

  private void hideSlideSeekBarTexts() {
    seekbarUpperText.setVisibility(View.GONE);
    seekbarLowerText.setVisibility(View.GONE);
  }

  private void hideFocusModeManualSlider() {
    slideSeekbarSubmenuView.setVisibility(View.INVISIBLE);
    slideSeekBarMode = SLIDE_SEEKBAR_MODE_UNACTIVE;
    slideSeekbarSubmenuView.setVisibility(View.INVISIBLE);
  }

  @Override
  public void showRecordedVideoThumbWithText(String path) {
    navigateGalleryButton.setVisibility(View.INVISIBLE);
    thumbClipRecordedButton.setVisibility(View.VISIBLE);
    Glide.with(this).load(path).into(thumbClipRecordedButton);
    editOrGalleryTextCardView.setVisibility(View.VISIBLE);
  }

  @Override
  public void hideRecordedVideoThumbWithText() {
    thumbClipRecordedButton.setVisibility(View.INVISIBLE);
    editOrGalleryTextCardView.setVisibility(View.INVISIBLE);
    navigateGalleryButton.setVisibility(View.INVISIBLE);
  }

  @Override
  public void showVideosRecordedNumber(int numberOfVideos) {
    numVideosRecordedTextView.setVisibility(View.VISIBLE);
    numVideosRecordedTextView.setText(String.valueOf(numberOfVideos));
    editOrGalleryTextView.setText(getString(R.string.recordTextEdit));
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
  public void setCameraSettingSelected(String resolution, String quality, String frameRate) {
    textViewInfoResolution.setText(resolution);
    textViewInfoQuality.setText(quality);
    textViewInfoFrameRate.setText(frameRate);
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
        alertDialogBattery.setPercentColor(getResources()
                .getColor(R.color.recordActivityInfoGreen));
        break;
      case MEDIUM:
        alertDialogBattery.setPercentColor(getResources()
                .getColor(R.color.recordActivityInfoYellow));
        break;
      case LOW:
      case CRITICAL:
      default:
        alertDialogBattery.setPercentColor(getResources().getColor(R.color.recordActivityInfoRed));
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
        storageButton.setImageResource(R.drawable.activity_record_ic_storage_medium);
        break;
      case CRITICAL:
        storageButton.setImageResource(R.drawable.activity_record_ic_storage_low);
        break;
      case OKAY:
        storageButton.setImageResource(R.drawable.activity_record_ic_storage_normal);
        break;
      default:
        storageButton.setImageResource(R.drawable.activity_record_ic_storage_full);
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
        alertDialogStorage.setPercentColor(getResources()
                .getColor(R.color.recordActivityInfoGreen));
        break;
      case MEDIUM:
        alertDialogStorage.setPercentColor(getResources()
                .getColor(R.color.recordActivityInfoYellow));
        break;
      case CRITICAL:
        alertDialogStorage.setPercentColor(getResources().getColor(R.color.recordActivityInfoRed));
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
  public void updateAudioGainSeekbarDisability() {
    if (slideSeekBarMode != SLIDE_SEEKBAR_MODE_AUDIO_GAIN) {
      return;
    }
    if (isRecording) {
      slideSeekBar.setEnabled(false);
    } else {
      slideSeekBar.setEnabled(true);
    }
  }

  @Override
  public void showProgressPicometer(int progress, int color) {
    tintProgress(ColorStateList.valueOf(getResources().getColor(color)));
    picometerProgress.setProgress(progress);
  }

  @Override
  public void showExternalMicrophoneConnected() {
    micPlugButton.setImageResource(R.drawable.activity_record_ic_micro_activated);
  }

  @Override
  public void showSmartphoneMicrophoneWorking() {
    micPlugButton.setImageResource(R.drawable.activity_record_ic_phone_micro_activated);
  }

  @Override
  public void setFocusModeManual(MotionEvent event) {
    customManualFocusView.onTouchEvent(event);
  }

  @Override
  public void hideGridModeSelectionSubmenu() {
    gridButton.setSelected(false);
    gridModeSubmenuCardview.setVisibility(View.INVISIBLE);
  }

  @Override
  public void showGridModeSelectionSubmenu() {
    gridButton.setSelected(true);
    gridModeSubmenuCardview.setVisibility(View.VISIBLE);
    // Select grid mode line by default if has been not selected any grid mode.
    boolean isSomeGridActivated = false;
    for (ImageButton gridModeButton : supportedGridModeButtons) {
      if (gridModeButton.isSelected()) {
        isSomeGridActivated = true;
      }
    }
    if (!isSomeGridActivated) {
      selectGridButton(gridModeButtons.get(GRID_MODE_LINES));
      presenter.setGridMode(GRID_MODE_LINES);
    }
  }

  @Override
  public void setupGridMode() {
    for (final String gridMode : listGridModeValues) {
      final ImageButton gridModeButton = gridModeButtons.get(gridMode);
      if (gridModeButton != null) {
        supportedGridModeButtons.add(gridModeButton);
        gridModeButton.setVisibility(View.VISIBLE);
        gridModeButton.setOnClickListener(view -> {
          if(view.isSelected()) {
            imageViewGrid.setVisibility(View.INVISIBLE);
            view.setSelected(false);
          } else {
            selectGridButton(gridModeButton);
            presenter.setGridMode(gridMode);
          }
        });
      } else {
        Log.e(LOG_TAG, "Missing WB icon: " + gridMode);
      }
    }
  }

  private void selectGridButton(ImageButton gridModeButton) {
    deselectAllGridButtons();
    gridModeButton.setSelected(true);
  }

  @Override
  public void deselectAllGridButtons() {

    for (ImageButton gridModeButton : supportedGridModeButtons) {
      gridModeButton.setSelected(false);
    }
  }

  /*.*.*.*.*.*.*.*.*.*.*.*.*. OnClicks *.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*/

  @OnClick(R.id.button_toggle_flash)
  public void toggleFlash() {
    presenter.toggleFlash(flashButton.isSelected());
  }

  @OnClick(R.id.button_change_camera)
  public void changeCamera() {
   presenter.switchCamera();
  }

  @OnClick (R.id.button_navigate_edit)
  public void onClickNavigateToEdit() {
    if (!isRecording) {
      presenter.setFlashOff();
      presenter.navigateToEdit();
    }
  }

  @OnClick(R.id.button_navigate_gallery)
  public void onClickNavigateGallery() {
    navigateTo(GalleryActivity.class);
  }

  @OnClick(R.id.button_settings_camera)
  public void showHideBottomSettingsCamera() {
    presenter.buttonSettingsCamera(settingsCameraButton.isSelected());
  }

  @OnClick(R.id.button_navigate_tutorial)
  public void navigateToTutorial() {
    if (!isRecording) {
      navigateTo(TutorialRecordActivity.class);
    }
  }

  @OnClick(R.id.button_navigate_settings)
  public void navigateToSettings() {
    if (!isRecording) {
      navigateTo(CameraSettingsActivity.class);
    }
  }

  @OnClick(R.id.clear_button)
  public void clearAndShrinkScreen() {
    hidePrincipalViews();
  }

  @OnClick(R.id.show_all_controls_button)
  public void showAllControlsInCamera() {
    showPrincipalViews();
    showRightControlsView();
  }

  @OnClick(R.id.button_to_show_controls_right)
  public void showRightControls() {
    presenter.showRightControls();
  }

  @OnClick(R.id.button_to_hide_controls)
  public void hideRightControls() {
    presenter.hideRightControls();
  }

  @OnClick(R.id.activity_record_icon_battery)
  public void showDialogWithLevelBattery() {
    if(!alertDialogBattery.isShowing()) {
      Intent batteryStatus = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
      int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
      int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
      int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
      presenter.batteryDialog(level, status, scale);
    }
  }

  @OnClick(R.id.activity_record_icon_storage)
  public void showDialogWithLevelStorage() {
    if(!alertDialogStorage.isShowing()) {
      StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
      long totalStorage = getTotalStorage(statFs);
      long freeStorage = getFreeStorage(statFs);
      presenter.storageDialog(totalStorage, freeStorage);
    }
  }

  @OnClick (R.id.button_zoom)
  public void onClickZoomListener() {
    if (zoomButton.isSelected()) {
      hideZoomSelectionSubmenu();
    } else {
      hideManualExposureSubmenu();
      hideAFSelectionSubmenu();
      hideWhiteBalanceSubmenu();
      hideMeteringModeSelectionSubmenu();
      hideSoundVolumeSubmenu();
      hideGridModeSelectionSubmenu();
      showZoomSelectionSubmenu();
    }
  }

  @OnClick (R.id.button_manual_exposure)
  public void onClickManualExposure() {
    hideMeteringModeSelectionSubmenu();
    meteringModeButton.setSelected(true);
    showManualExposureSubmenu();
  }

  private void setSlideSeekBarTexts(String upperText, String lowerText) {
    seekbarUpperText.setVisibility(View.VISIBLE);
    seekbarLowerText.setVisibility(View.VISIBLE);
    seekbarUpperText.setText(upperText);
    seekbarLowerText.setText(lowerText);
  }

  @OnClick (R.id.button_af_selection)
  public void onClickAfSelectionListener() {
    if (afSelectionButton.isSelected()) {
      hideAFSelectionSubmenu();
    } else {
      hideZoomSelectionSubmenu();
      hideManualExposureSubmenu();
      hideWhiteBalanceSubmenu();
      hideMeteringModeSelectionSubmenu();
      hideSoundVolumeSubmenu();
      hideGridModeSelectionSubmenu();
      showAFSelectionSubmenu();
    }
  }

  @OnClick (R.id.button_white_balance)
  public void onClickWhiteBalanceListener() {
    if (whiteBalanceButton.isSelected()) {
      hideWhiteBalanceSubmenu();
    } else {
      hideZoomSelectionSubmenu();
      hideManualExposureSubmenu();
      hideAFSelectionSubmenu();
      hideMeteringModeSelectionSubmenu();
      hideSoundVolumeSubmenu();
      hideGridModeSelectionSubmenu();
      showWhiteBalanceSubmenu();
    }
  }

  @OnClick(R.id.button_metering_mode)
  public void onClickMeasurementeModeListener() {
    if (meteringModeButton.isSelected()) {
      hideManualExposureSubmenu();
      hideMeteringModeSelectionSubmenu();
    } else {
      hideZoomSelectionSubmenu();
      hideManualExposureSubmenu();
      hideAFSelectionSubmenu();
      hideWhiteBalanceSubmenu();
      hideSoundVolumeSubmenu();
      hideGridModeSelectionSubmenu();
      showMeteringModeSelectionSubmenu();
    }
  }

  @OnClick (R.id.button_grid)
  public void onClickListenerGridButton() {
    if (gridButton.isSelected()) {
      hideGridModeSelectionSubmenu();
    } else {
      hideZoomSelectionSubmenu();
      hideManualExposureSubmenu();
      hideAFSelectionSubmenu();
      hideWhiteBalanceSubmenu();
      hideMeteringModeSelectionSubmenu();
      hideSoundVolumeSubmenu();
      showGridModeSelectionSubmenu();
    }
  }

  public void disableGrid() {
    gridButton.setSelected(false);
    imageViewGrid.setVisibility(View.INVISIBLE);
  }

  @Override
  public void enableGrid() {
    imageViewGrid.setVisibility(View.VISIBLE);
  }

  @OnClick(R.id.button_sound_volume)
  public void onClickAudioGainButton() {
    if (soundVolumeButton.isSelected()) {
      hideSoundVolumeSubmenu();
    } else {
      hideZoomSelectionSubmenu();
      hideManualExposureSubmenu();
      hideWhiteBalanceSubmenu();
      hideMeteringModeSelectionSubmenu();
      hideAFSelectionSubmenu();
      hideGridModeSelectionSubmenu();
      showSoundVolumeSubmenu();
    }
  }

  @OnClick (R.id.button_camera_default)
  public void setCameraDefaultSettings() {
    presenter.setCameraDefaultSettings();
  }

  @Override
  public void showDefaultButton() {
    cameraDefaultSettingsButton.setVisibility(View.VISIBLE);
  }

  @Override
  public void hideDefaultButton() {
    cameraDefaultSettingsButton.setVisibility(View.GONE);
  }

  @Override
  public void showGridModeLines() {
    if (amIAVerticalApp) {
      imageViewGrid.setImageResource(R.drawable.activity_record_grid_lines_background_vertical);
    } else {
      imageViewGrid.setImageResource(R.drawable.activity_record_grid_lines_background);
    }
  }

  @Override
  public void showGridModeOneOne() {
    if (amIAVerticalApp) {
      imageViewGrid.setImageResource(R.drawable.activity_record_grid_one_one_background_vertical);
    } else {
      imageViewGrid.setImageResource(R.drawable.activity_record_grid_one_one_background);
    }
  }

  @Override
  public void showGridModeCrosses() {
    if (amIAVerticalApp) {
      imageViewGrid.setImageResource(R.drawable.activity_record_grid_crosses_bakcground_vertical);
    } else {
      imageViewGrid.setImageResource(R.drawable.activity_record_grid_crosses_bakcground);
    }
  }

  @Override
  public void showGridModeFibonacci() {
    imageViewGrid.setImageResource(R.drawable.activity_record_grid_fibonacci_background);
  }

  @Override
  public void screenOrientationPortrait() {
    setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
  }

  @Override
  public void screenOrientationLandscape() {
    setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
  }

  @Override
  public void hideTutorials() {
    tutorial.setVisibility(View.GONE);
  }

  @Override
  public void setupAmIAVerticalApp(boolean amIAVerticalApp) {
    this.amIAVerticalApp = amIAVerticalApp;
  }

  @Override
  public void setupManualExposureTime(int minimumExposureCompensation) {
    this.minExposureTime = presenter.getMinimunExposureTime();
  }

  @Override
  public void disableAudioGainControls() {
    soundVolumeButton.setVisibility(View.GONE);
  }

  @Override
  public void enableExposureTimeSeekBar() {
    setupExposureTimeSeekBar();
    if (amIAVerticalApp) {
      exposureTimeSeekBarVertical.setEnabled(true);
    } else {
      exposureTimeSeekBar.setEnabled(true);
    }
  }

  @Override
  public void disableExposureTimeSeekBar() {
    if (amIAVerticalApp) {
      exposureTimeSeekBarVertical.setEnabled(false);
    } else {
      exposureTimeSeekBar.setEnabled(false);
    }
  }

  @Override
  public void setAutoSettingsFocusModeByDefault() {
    afSettingSelective.setSelected(true);
  }

  private void showZoomSelectionSubmenu() {
    zoomButton.setSelected(true);
    slideSeekBarMode = SLIDE_SEEKBAR_MODE_ZOOM;
    hideSlideSeekBarIcons();
    setSlideSeekBarTexts("100%", "0%");
    slideSeekBar.setMax(100);
    slideSeekBar.setProgress(currentSeekbarZoom);
    slideSeekBar.setEnabled(true);
    slideSeekbarSubmenuView.setVisibility(View.VISIBLE);
  }

  @Override
  public void hideZoomSelectionSubmenu() {
    zoomButton.setSelected(false);
    slideSeekbarSubmenuView.setVisibility(View.INVISIBLE);
  }

  private void showManualExposureSubmenu() {
    manualExposureSubmenu.setVisibility(View.VISIBLE);
    slideSeekBarMode = SLIDE_SEEKBAR_MODE_MANUAL_EXPOSURE_TIME;
    int maxShutterSpeed = presenter.getMaximumExposureTime();
    maxExposureText.setText("1/" + new Double(1/(maxShutterSpeed/1000000000d)).toString());
    minExposureText.setText("1/" + (1000000000 / minExposureTime));
    if (amIAVerticalApp) {
      exposureTimeSeekBarVertical.setMax(maxShutterSpeed- minExposureTime);
      exposureTimeSeekBarVertical.setProgress(presenter.getCurrentExposureTimeSeekBarProgress());
    } else {
      exposureTimeSeekBar.setMax(maxShutterSpeed - minExposureTime);
      exposureTimeSeekBar.setProgress(presenter.getCurrentExposureTimeSeekBarProgress());
    }
  }

  @Override
  public void hideManualExposureSubmenu() {
    manualExposureSubmenu.setVisibility(View.INVISIBLE);
  }

  private void showAFSelectionSubmenu() {
    afSelectionButton.setSelected(true);
    afSelectionSubmenuCardView.setVisibility(View.VISIBLE);
    if(afSettingManual.isSelected()) {
      showFocusModeManualSlider();
    }
  }

  @Override
  public void hideAFSelectionSubmenu() {
    afSelectionButton.setSelected(false);
    afSelectionSubmenuCardView.setVisibility(View.INVISIBLE);
    hideFocusModeManualSlider();
  }

  private void showMeteringModeSelectionSubmenu() {
    meteringModeButton.setSelected(true);
    meteringModeSubmenuCardView.setVisibility(View.VISIBLE);
    if (meteringModeExposureCompensation.isSelected()) {
      showExposureCompensationSubmenu();
    }
  }

  @Override
  public void hideMeteringModeSelectionSubmenu() {
    meteringModeButton.setSelected(false);
    meteringModeSubmenuCardView.setVisibility(View.INVISIBLE);
    hideExposureCompensationSubmenu();
  }

  private void showWhiteBalanceSubmenu() {
    whiteBalanceButton.setSelected(true);
    whiteBalanceSubmenuCardView.setVisibility(View.VISIBLE);
  }

  @Override
  public void hideWhiteBalanceSubmenu() {
    whiteBalanceButton.setSelected(false);
    whiteBalanceSubmenuCardView.setVisibility(View.INVISIBLE);
  }

  private void showSoundVolumeSubmenu() {
    soundVolumeButton.setSelected(true);
    slideSeekBarMode = SLIDE_SEEKBAR_MODE_AUDIO_GAIN;
    hideSlideSeekBarIcons();
    setSlideSeekBarTexts("100%", "0%");
    slideSeekBar.setMax(100);
    slideSeekBar.setProgress(audioGainSeekBarProgress);
    updateAudioGainSeekbarDisability();
    slideSeekbarSubmenuView.setVisibility(View.VISIBLE);
  }

  private void hideSlideSeekBarIcons() {
    seekBarUpperImage.setVisibility(View.GONE);
    seekBarLowerImage.setVisibility(View.GONE);
  }

  @Override
  public void hideSoundVolumeSubmenu() {
    slideSeekBarMode = SLIDE_SEEKBAR_MODE_UNACTIVE;
    soundVolumeButton.setSelected(false);
    slideSeekbarSubmenuView.setVisibility(View.GONE);
  }

  @Override
  public void showAudioGainButton() {
    soundVolumeButton.setVisibility(View.VISIBLE);
  }

  @Override
  public void hideAudioGainButton() {
    soundVolumeButton.setVisibility(View.GONE);
  }

  @Override
  public void setAudioGain(int defaultAudioGain) {
    audioGainSeekBarProgress = defaultAudioGain;
  }

  @OnClick(R.id.button_record)
  public void onClickRecordButton() {
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
      if (afSettingSelective.isSelected()) {
        Log.d(LOG_TAG, "-------------------- onTouchSelectedArea " + touchEventX
                + ", " + touchEventY + "--------------");
        presenter.setFocusSelectionModeSelective((int) event.getRawX(), (int) event.getRawY(),
                getCustomFocusViewWidth(), getCustomFocusViewHeight(), event);
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
    resetManualExposure();
    meteringModeSpot.setSelected(true);
    enableSpotMeteringControl();
    presenter.setMeteringPoint(touchEventX, touchEventY,
            getCustomFocusViewWidth(), getCustomFocusViewHeight());
  }

  private void enableSpotMeteringControl() {
    cameraShutter.setVisibility(View.VISIBLE);
    cameraShutter.setEnabled(true);
  }

  @Override
  public void disableSpotMeteringControl() {
    cameraShutter.setVisibility(View.GONE);
    cameraShutter.setEnabled(false);
  }

  @Override
  public void selectMeteringModeAutoButton() {
    meteringModeAuto.setSelected(true);
  }

  @Override
  public void deselectExposureCompensation() {
    meteringModeExposureCompensation.setSelected(false);
  }

  @Override
  public void resetManualExposure() {
    deselectAllISOButtons();
    disableExposureTimeSeekBar();
  }

  @Override
  public void exposureTimeChanged(long exposureTime) {
    if (slideSeekBarMode == SLIDE_SEEKBAR_MODE_MANUAL_EXPOSURE_TIME) {
      if (amIAVerticalApp) {
        exposureTimeSeekBarVertical.setProgress((int) exposureTime);
      } else {
        exposureTimeSeekBar.setProgress((int) exposureTime);
      }
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

  private void updateMicrophoneStatus(Intent intent) {
    int state = intent.getIntExtra("state", -1);
    int microphone = intent.getIntExtra("microphone", -1);
    presenter.setMicrophoneStatus(state, microphone);
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
      if (amIAVerticalApp) {
        if (orientation > 255 && orientation < 5) {
          if (orientationHaveChanged) {
            Log.d(LOG_TAG, "onOrientationChanged  rotationView changed " + orientation);
            orientationHaveChanged = false;
            restartPreview();
          }
        } else if (orientation > 175 && orientation < 185) {
          if (!orientationHaveChanged) {
            Log.d(LOG_TAG, "onOrientationChanged  rotationView changed " + orientation);
            orientationHaveChanged = true;
            restartPreview();
          }
        }
      } else {
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
    }

    private void restartPreview() {
      Runnable r = new Runnable() {
        @Override
        public void run() {
          presenter.restartPreview();
        }
      };
      Handler h = new Handler();
      h.postDelayed(r, 300);
    }

    private void checkShowRotateDeviceImage(int orientation) {
      if (amIAVerticalApp) {
        if ((orientation > 75 && orientation < 105) && orientation != -1) {
          rotateDeviceHint.setRotation(90);
          rotateDeviceHint.setRotationX(0);
          rotateDeviceHint.setVisibility(View.VISIBLE);
          if (!isRecording) {
            recordButton.setEnabled(false);
          }
        } else if (orientation > 245 && orientation < 285) {
          rotateDeviceHint.setRotation(-90);
          rotateDeviceHint.setRotationX(180);
          rotateDeviceHint.setVisibility(View.VISIBLE);
          if (!isRecording) {
            recordButton.setEnabled(false);
          }
        } else if (orientation > 165 && orientation < 195) {
          rotateDeviceHint.setRotation(-270);
          rotateDeviceHint.setRotationX(180);
          rotateDeviceHint.setVisibility(View.VISIBLE);
          if (!isRecording) {
            recordButton.setEnabled(false);
          }
        } else {
          rotateDeviceHint.setVisibility(View.GONE);
          recordButton.setEnabled(true);
        }
      } else {
        if ((orientation > 345 || orientation < 15) && orientation != -1) {
          rotateDeviceHint.setRotation(270);
          rotateDeviceHint.setRotationX(0);
          rotateDeviceHint.setVisibility(View.VISIBLE);
          if (!isRecording) {
            recordButton.setEnabled(false);
          }
        } else if (orientation > 165 && orientation < 195) {
          rotateDeviceHint.setRotation(-270);
          rotateDeviceHint.setRotationX(180);
          rotateDeviceHint.setVisibility(View.VISIBLE);
          if (!isRecording) {
            recordButton.setEnabled(false);
          }
        } else {
          rotateDeviceHint.setVisibility(View.GONE);
          recordButton.setEnabled(true);
        }
      }
    }
  }
}
