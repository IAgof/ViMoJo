/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.init.presentation.mvp.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.CameraAccessException;
import android.util.Log;
import android.util.Range;
import android.util.Size;

import com.videonasocialmedia.camera.utils.Camera2Settings;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.auth0.UserAuth0Helper;
import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettings;
import com.videonasocialmedia.vimojo.cameraSettings.model.FrameRateSetting;
import com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting;
import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraSettingsDataSource;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.domain.usecase.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.composition.domain.usecase.SaveComposition;
import com.videonasocialmedia.vimojo.init.presentation.mvp.views.InitAppView;
import com.videonasocialmedia.vimojo.init.presentation.views.activity.InitRegisterLoginActivity;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.record.presentation.views.activity.RecordCamera2Activity;
import com.videonasocialmedia.vimojo.sync.helper.RunSyncAdapterHelper;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.DateUtils;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.view.BackgroundExecutor;
import com.videonasocialmedia.vimojo.view.VimojoPresenter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

import static com.videonasocialmedia.vimojo.cameraSettings.model.FrameRateSetting.CAMERA_SETTING_FRAME_RATE_24_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.FrameRateSetting.CAMERA_SETTING_FRAME_RATE_25_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.FrameRateSetting.CAMERA_SETTING_FRAME_RATE_30_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting.CAMERA_SETTING_RESOLUTION_1080_BACK_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting.CAMERA_SETTING_RESOLUTION_1080_FRONT_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting.CAMERA_SETTING_RESOLUTION_2160_BACK_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting.CAMERA_SETTING_RESOLUTION_2160_FRONT_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting.CAMERA_SETTING_RESOLUTION_720_BACK_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting.CAMERA_SETTING_RESOLUTION_720_FRONT_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.BACK_CAMERA_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_WATERMARK_STATE;
import static com.videonasocialmedia.vimojo.utils.Constants.FRONT_CAMERA_ID;

/**
 * Created by jliarte on 22/10/16.
 */
public class InitAppPresenter extends VimojoPresenter {
  private String LOG_TAG = InitAppPresenter.class.getCanonicalName();
  private final Context context;
  private final InitAppView initAppView;
  private final CameraSettingsDataSource cameraSettingsRepository;
  private final ProjectInstanceCache projectInstanceCache;
  private final SaveComposition saveComposition;
  private RunSyncAdapterHelper runSyncAdapterHelper;
  private CreateDefaultProjectUseCase createDefaultProjectUseCase;
  private SharedPreferences sharedPreferences;
  private CameraSettings cameraSettings;
  private UserAuth0Helper userAuth0Helper;
  private UserEventTracker userEventTracker;

  private boolean watermarkIsForced;
  private boolean showAds;
  private boolean amIAVerticalApp;
  private String defaultResolutionSetting;
  private boolean isAppOutOfDate;
  protected boolean vimojoPlatformAvailable;

  @Inject
  public InitAppPresenter(
      Context context, InitAppView initAppView, SharedPreferences sharedPreferences,
      CreateDefaultProjectUseCase createDefaultProjectUseCase,
      CameraSettingsDataSource cameraSettingsRepository,
      RunSyncAdapterHelper runSyncAdapterHelper, ProjectInstanceCache projectInstanceCache,
      SaveComposition saveComposition, @Named("watermarkIsForced") boolean watermarkIsForced,
      @Named("showAds") boolean showAds, @Named("amIAVerticalApp") boolean amIAVerticalApp,
      @Named("defaultResolutionSetting") String defaultResolutionSetting,
      @Named("isAppOutOfDate") boolean isAppOutOfDate,
      @Named("vimojoPlatformAvailable") boolean vimojoPlatformAvailable,
      UserAuth0Helper userAuth0Helper, UserEventTracker userEventTracker,
      BackgroundExecutor backgroundExecutor) {
    super(backgroundExecutor, userEventTracker);
    this.context = context;
    this.initAppView = initAppView;
    this.sharedPreferences = sharedPreferences;
    this.createDefaultProjectUseCase = createDefaultProjectUseCase;
    this.cameraSettingsRepository = cameraSettingsRepository;
    this.runSyncAdapterHelper = runSyncAdapterHelper;
    this.projectInstanceCache = projectInstanceCache;
    this.vimojoPlatformAvailable = vimojoPlatformAvailable;
    this.userAuth0Helper = userAuth0Helper;
    this.userEventTracker = userEventTracker;
    this.saveComposition = saveComposition;
    this.watermarkIsForced = watermarkIsForced;
    this.showAds = showAds;
    this.amIAVerticalApp = amIAVerticalApp;
    this.defaultResolutionSetting = defaultResolutionSetting;
    this.isAppOutOfDate = isAppOutOfDate;
  }

  public void onAppPathsCheckSuccess(String rootPath, String privatePath,
                                     Drawable drawableFadeTransitionVideo) {
    if (projectInstanceCache.getCurrentProject() == null) {
      // TODO(jliarte): 23/04/18 in fact, there will be always a project instance, consider removing
      Project project = createDefaultProjectUseCase.createProject(rootPath, privatePath,
              isWatermarkActivated(), drawableFadeTransitionVideo, amIAVerticalApp);
      projectInstanceCache.setCurrentProject(project);
      executeUseCaseCall(() -> saveComposition.saveComposition(project));
    }
    setupAds();
  }

  public boolean isWatermarkActivated() {
    return watermarkIsForced
        || sharedPreferences.getBoolean(ConfigPreferences.WATERMARK, DEFAULT_WATERMARK_STATE);
  }

  public void checkCamera2FrameRateAndResolutionSupported() {
    Camera2Settings camera2Settings = null;
    try {
      camera2Settings = new Camera2Settings(context);
      checkCamera2ResolutionSupported(camera2Settings);
      checkCamera2FrameRateSupported(camera2Settings.getFPSRange(BACK_CAMERA_ID));
    } catch (CameraAccessException e) {
      e.printStackTrace();
      Log.d(LOG_TAG, "CameraAccessException " + e.getMessage());
      // TODO: 15/11/2017 Manage Error ¿?
      return;
    }

  }

  private void checkCamera2ResolutionSupported(Camera2Settings camera2Settings)
          throws CameraAccessException {

    HashMap<Integer, Boolean> resolutionsSupportedMap = new HashMap<>();

    boolean resolutionBack720pSupported = false;
    boolean resolutionBack1080pSupported = false;
    boolean resolutionBack2160pSupported = false;
    boolean resolutionFront720pSupported = false;
    boolean resolutionFront1080pSupported = false;
    boolean resolutionFront2160pSupported = false;

    for(Size sizeBackCamera: camera2Settings.getSupportedVideoSizes(BACK_CAMERA_ID)) {
      if (sizeBackCamera.getWidth() == 1280 && sizeBackCamera.getHeight() == 720) {
        resolutionBack720pSupported = true;
        }
      if (sizeBackCamera.getWidth() == 1920 && sizeBackCamera.getHeight() == 1080) {
        resolutionBack1080pSupported = true;
        }
      if (sizeBackCamera.getWidth() == 3840 && sizeBackCamera.getHeight() == 2160) {
        resolutionBack2160pSupported = true;
        }
    }

    if (camera2Settings.hasFrontCamera()) {
      for(Size sizeFrontCamera: camera2Settings.getSupportedVideoSizes(FRONT_CAMERA_ID)) {
        if (sizeFrontCamera.getWidth() == 1280 && sizeFrontCamera.getHeight() == 720) {
          resolutionFront720pSupported = true;
        }
        if (sizeFrontCamera.getWidth() == 1920 && sizeFrontCamera.getHeight() == 1080) {
          resolutionFront1080pSupported = true;
        }
        if (sizeFrontCamera.getWidth() == 3840 && sizeFrontCamera.getHeight() == 2160) {
          resolutionFront2160pSupported = true;
        }
      }
    }

    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_720_BACK_ID, resolutionBack720pSupported);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_1080_BACK_ID, resolutionBack1080pSupported);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_2160_BACK_ID, resolutionBack2160pSupported);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_720_FRONT_ID, resolutionFront720pSupported);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_1080_FRONT_ID, resolutionFront1080pSupported);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_2160_FRONT_ID, resolutionFront2160pSupported);

    ResolutionSetting resolutionSetting = new ResolutionSetting(defaultResolutionSetting,
            resolutionsSupportedMap);

    cameraSettings = cameraSettingsRepository.getCameraSettings();
    if (cameraSettings != null) {
      cameraSettingsRepository.setResolutionSettingSupported(cameraSettings, resolutionSetting);
    }

  }

  private void checkCamera2FrameRateSupported(Range<Integer>[] fpsRange)
          throws CameraAccessException {
    HashMap<Integer, Boolean> frameRateMap = new HashMap<>();
    boolean frameRate24FpsSupported = false;
    boolean frameRate25FpsSupported = false;
    boolean frameRate30FpsSupported = false;
    String defaultFrameRate = Constants.DEFAULT_CAMERA_SETTING_FRAME_RATE;
    Range<Integer> fps24 = new Range<>(24, 24);
    Range<Integer> fps25 = new Range<>(25, 25);
    Range<Integer> fps30 = new Range<>(30, 30);

    for(Range<Integer> fps: fpsRange) {
      if (fps.equals(fps24)) {
        frameRate24FpsSupported = true;
      } else {
        if (fps.equals(fps25)) {
          frameRate25FpsSupported = true;
        } else {
          if (fps.equals(fps30)) {
            frameRate30FpsSupported = true;
          }
        }
      }
    }

    frameRateMap.put(CAMERA_SETTING_FRAME_RATE_24_ID, frameRate24FpsSupported);
    frameRateMap.put(CAMERA_SETTING_FRAME_RATE_25_ID, frameRate25FpsSupported);
    frameRateMap.put(CAMERA_SETTING_FRAME_RATE_30_ID, frameRate30FpsSupported);

    FrameRateSetting frameRateSetting = new FrameRateSetting(defaultFrameRate, frameRateMap);
    cameraSettings = cameraSettingsRepository.getCameraSettings();
    if (cameraSettings != null) {
      cameraSettingsRepository.setFrameRateSettingSupported(cameraSettings, frameRateSetting);
    }
  }

  public void init() {
    runSyncAdapterHelper.runSyncAdapterPeriodically();
    if (amIAVerticalApp || !userAuth0Helper.isLogged()) {
      initAppView.screenOrientationPortrait();
    } else {
      initAppView.screenOrientationLandscape();
    }
  }

  private void setupAds() {
    if (showAds) {
      initAppView.initializeAdMob();
    }
  }

  public void checkAppOutOfDateToContinue() {
    if (isAppOutOfDate) {
      initAppView.showDialogOutOfDate();
    } else {
      initAppView.appContinueWorkflow();
    }
  }

  public void setNavigation() {
    if (!vimojoPlatformAvailable) {
      initAppView.navigate(RecordCamera2Activity.class);
      return;
    }
    checkLogin();
  }

  protected void checkLogin() {
    if (userAuth0Helper.isLogged()) {
      userEventTracker.trackUserLoggedIn(true);
      initAppView.navigate(RecordCamera2Activity.class);
    } else {
      initAppView.navigateToRegisterLogin();
    }
  }

  public void trackUserProfileGeneralTraits() {
    userEventTracker.trackUserProfileGeneralTraits();
  }

  public void trackAppStartupProperties(boolean firstTime) {
    userEventTracker.trackAppStartupProperties(firstTime);
  }

  public void trackUserProfile(String androidId) {
    userEventTracker.trackUserProfile(androidId);
  }

  public void trackCreatedSuperProperty() {
    userEventTracker.trackCreatedSuperProperty();
  }

  public void trackAppStartup(String initState) {
    userEventTracker.trackAppStartup(initState);
  }

  public void onFirstTimeRun(String androidId) {
    checkPrehistericUser();
    saveFirstTimeData();
    trackAppStartupProperties(true);
    trackUserProfile(androidId);
    trackCreatedSuperProperty();
    checkCamera2FrameRateAndResolutionSupported();
  }

  public void checkPrehistericUser() { // TODO(jliarte): 28/08/18 make private
    boolean preHisteric = sharedPreferences.getBoolean(ConfigPreferences.PREHISTERIC_USER, false);

    String firstRunFromMPTracker = userEventTracker.getUserFirstRun();
    if (!preHisteric
            && !firstRunFromMPTracker.equals("")
            && firstRunWasBeforeSaasLaunchDate(firstRunFromMPTracker)) {
      sharedPreferences.edit().putBoolean(ConfigPreferences.PREHISTERIC_USER, true).apply();
      trackPrehistericUser();
    }
  }

  private boolean firstRunWasBeforeSaasLaunchDate(String firstRunFromMPTracker) {
    boolean wasBefore = false;
    Calendar firstRun = Calendar.getInstance();
    Calendar saasLaunchDate = Calendar.getInstance();

    try {
      saasLaunchDate.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(Constants.SAAS_LAUNCH_DATE));
      firstRun.setTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(firstRunFromMPTracker));
      return firstRun.before(saasLaunchDate);
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return wasBefore;
  }

  private void trackPrehistericUser() {
    userEventTracker.trackPrehistoricUser();
  }

  private void saveFirstTimeData() {
    int firstInstalledVersion = sharedPreferences.getInt(ConfigPreferences.FIRST_APP_VERSION, -1);
    if (firstInstalledVersion == -1) {
      sharedPreferences.edit().putInt(ConfigPreferences.FIRST_APP_VERSION,
              BuildConfig.VERSION_CODE).apply();
    }
    String firstRun = sharedPreferences.getString(ConfigPreferences.FIRST_APP_RUN, "");
    if (firstRun.equals("")) {
      sharedPreferences.edit().putString(ConfigPreferences.FIRST_APP_RUN,
              DateUtils.getDateRightNow()).apply();
    }
  }

  public void onAppUpgraded(String androidId) {
    checkPrehistericUser();
    trackAppStartupProperties(false);
    trackUserProfile(androidId);
    // Repeat this method for security, if user delete app data miss this configs.
    checkCamera2FrameRateAndResolutionSupported();
  }


}
