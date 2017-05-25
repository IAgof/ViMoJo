package com.videonasocialmedia.vimojo.record.presentation.views.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.videonasocialmedia.avrecorder.view.CustomManualFocusView;
import com.videonasocialmedia.camera.customview.AutoFitTextureView;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.main.modules.ActivityPresentersModule;
import com.videonasocialmedia.vimojo.presentation.views.customviews.CircleImageView;
import com.videonasocialmedia.vimojo.record.presentation.mvp.presenters.RecordCamera2Presenter;
import com.videonasocialmedia.vimojo.record.presentation.mvp.views.RecordCamera2View;
import com.videonasocialmedia.vimojo.settings.presentation.views.activity.SettingsActivity;
import com.videonasocialmedia.vimojo.utils.Constants;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

import static com.videonasocialmedia.vimojo.utils.UIUtils.tintButton;

/**
 * Created by alvaro on 16/01/17.
 */

public class RecordCamera2Activity extends VimojoActivity implements RecordCamera2View {

  private final String LOG_TAG = getClass().getSimpleName();

  @Inject
  RecordCamera2Presenter presenter;

  @Bind(R.id.button_settings_camera)
  ImageButton settingsCameraButton;
  @Bind(R.id.button_toggle_flash)
  ImageButton flashButton;
  @Bind(R.id.button_change_camera)
  ImageButton changeCameraButton;
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
  @Bind(R.id.zoom_bar)
  View zommBarView;
  @Bind(R.id.settings_bar)
  View settingsBarView;
  @Bind(R.id.settings_bar_submenu)
  View settingsBarSubmenuView;
  @Bind(R.id.button_resolution_indicator)
  ImageView resolutionIndicatorButton;
  @Bind(R.id.customManualFocusView)
  CustomManualFocusView customManualFocusView;
  @Bind(R.id.rotateDeviceHint)
  ImageView rotateDeviceHint;

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

  private boolean isFrontCameraSelected = false;

  private final int RESOLUTION_SELECTED_HD720 = 720;
  private final int RESOLUTION_SELECTED_HD1080 = 1080;
  private final int RESOLUTION_SELECTED_HD4K = 2160;
  private OrientationHelper orientationHelper;
  private boolean isPrincipalViewsSelected = false;
  private boolean isControlsViewSelected = false;

  private ProgressDialog progressDialog;

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
    //initOrientationHelper();


    this.getActivityPresentersComponent().inject(this);

    createProgressDialog();
  }

  private void createProgressDialog() {
    progressDialog = new ProgressDialog(this);
    progressDialog.setTitle(getString(R.string.dialog_title_record_adapting_video));
    progressDialog.setMessage(getString(R.string.dialog_message_record_adapting_video));
    progressDialog.setIndeterminate(false);
    progressDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
  }

  @Override
  public ActivityPresentersModule getActivityPresentersModule() {
    return new ActivityPresentersModule(this, isFrontCameraSelected, isPrincipalViewsSelected,
        isControlsViewSelected, Constants.PATH_APP_TEMP, textureView);
  }

  private void keepScreenOn() {
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
  }

  public void setupActivityButtons() {
    // TODO:(alvaro.martinez) 7/11/16 implement this functionality, use case check support camera
    settingsCameraButton.setEnabled(false);
    tintRecordButtons(R.color.button_color_record_activity);

    // Disable until implement camera pro
    picometerView.setVisibility(View.INVISIBLE);
    zommBarView.setVisibility(View.INVISIBLE);
    settingsBarSubmenuView.setVisibility(View.INVISIBLE);
    settingsBarView.setVisibility(View.INVISIBLE);
  }

  private void tintRecordButtons(int button_color) {
    tintButton(flashButton, button_color);
    tintButton(changeCameraButton, button_color);
    tintButton(showControlsButton, button_color);
    tintButton(hideControlsViewButton, button_color);
    tintButton(navigateSettingsButtons, button_color);
    tintButton(settingsCameraButton, button_color);
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
    presenter.onPause();
    super.onPause();
  }

  /*.*.*.*.*.*.*.*.*.*.*.*.*. RecordCamera2View *.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*/

  @Override
  public void showRecordButton() {
    recordButton.setImageResource(R.drawable.record_activity_ic_rec);
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
    // TODO:(alvaro.martinez) 27/01/17 Implement zoom_bar_view
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
    progressDialog.show();
  }

  @Override
  public void hideProgressAdaptingVideo() {
    progressDialog.dismiss();
  }

  @Override
  public void hidePrincipalViews() {
    clearButton.setImageResource(R.drawable.record_activity_ic_shrink);
    clearButton.setAlpha(0.5f);
    clearButton.setBackground(null);
    clearButton.setActivated(true);
    hudView.setVisibility(View.INVISIBLE);
    controlsView.setVisibility(View.INVISIBLE);
    hideControlsViewButton.setVisibility(View.INVISIBLE);
    showControlsButton.setVisibility(View.INVISIBLE);
  }

  @Override
  public void showPrincipalViews() {
    clearButton.setImageResource(R.drawable.record_activity_ic_expand);
    clearButton.setBackground(getResources().getDrawable(R.drawable.circle_background));
    clearButton.setAlpha(1f);
    clearButton.setActivated(false);
    hudView.setVisibility(View.VISIBLE);
    showControlsButton.setVisibility(View.VISIBLE);
  }

  @Override
  public void hideRightControlsView() {
    hideControlsViewButton.setVisibility(View.INVISIBLE);
    showControlsButton.setVisibility(View.VISIBLE);
    controlsView.setVisibility(View.INVISIBLE);
  }

  @Override
  public void showRightControlsView() {
    showControlsButton.setVisibility(View.INVISIBLE);
    hideControlsViewButton.setVisibility(View.VISIBLE);
    controlsView.setVisibility(View.VISIBLE);
  }

  @Override
  public void showBottomControlsView() {
    settingsBarSubmenuView.setVisibility(View.VISIBLE);
    settingsCameraButton.setSelected(true);
  }

  @Override
  public void hideBottomControlsView() {
    settingsBarSubmenuView.setVisibility(View.GONE);
    settingsCameraButton.setSelected(false);
  }

  @Override
  public void showRecordedVideoThumb(String path) {
    thumbClipRecordedButton.setVisibility(View.VISIBLE);
    Glide.with(this).load(path).into(thumbClipRecordedButton);
  }

  @Override
  public void hideRecordedVideoThumb() {
    thumbClipRecordedButton.setVisibility(View.INVISIBLE);
  }

  @Override
  public void showVideosRecordedNumber(int numberOfVideos) {
    numVideosRecordedTextView.setVisibility(View.VISIBLE);
    numVideosRecordedTextView.setText(String.valueOf(numberOfVideos));
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
        resolutionIndicatorButton.setImageResource(R.drawable.record_activity_ic_resolution_720);
        break;
      case(RESOLUTION_SELECTED_HD1080):
        resolutionIndicatorButton.setImageResource(R.drawable.record_activity_ic_resolution_1080);
        break;
      case (RESOLUTION_SELECTED_HD4K):
        resolutionIndicatorButton.setImageResource(R.drawable.record_activity_ic_resolution_4k);
        break;
      default:
        resolutionIndicatorButton.setImageResource(R.drawable.record_activity_ic_resolution_720);
        break;
    }
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

    if (!isFrontCameraSelected) {
      isFrontCameraSelected = true;
    } else {
      isFrontCameraSelected = false;
    }

   presenter.switchCamera(isFrontCameraSelected);
  }

  @OnClick (R.id.button_navigate_edit_or_gallery)
  public void navigateToEditOrGallery() {
    if (!isRecording) {
      presenter.setFlashOff();
      presenter.navigateToEditOrGallery();
    }
  }

  @OnClick(R.id.button_settings_camera)
  public void showHideBottomSettingsCamera(){
    presenter.bottomSettingsCamera(settingsCameraButton.isSelected());
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

  @OnTouch(R.id.button_record)
  boolean onTouch(MotionEvent event) {
    if (event.getAction() == MotionEvent.ACTION_DOWN) {
      if (!isRecording) {
        presenter.startRecord();
        isRecording = true;
      } else {
        presenter.stopRecord();
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
    Snackbar snackbar = Snackbar.make(chronometerAndRecPointView, stringResourceId, Snackbar.LENGTH_SHORT);
    snackbar.show();
  }

  private class OrientationHelper extends OrientationEventListener {
    Context context;
    private boolean orientationHaveChanged = false;
    private boolean isNormalOrientation;

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
      if (orientation > 85 && orientation < 95) {
        if (orientationHaveChanged) {
          Log.d(LOG_TAG, "onOrientationChanged  rotationView changed " + orientation);
          presenter.restartPreview();
          orientationHaveChanged = false;
        }
      } else if (orientation > 265 && orientation < 275) {
        if (!orientationHaveChanged) {
          Log.d(LOG_TAG, "onOrientationChanged  rotationView changed " + orientation);
          presenter.restartPreview();
          orientationHaveChanged = true;
        }
      }
      checkShowRotateDeviceImage(orientation);
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
