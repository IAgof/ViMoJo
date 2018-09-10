package com.videonasocialmedia.vimojo.repository.project;

import android.support.annotation.NonNull;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettings;
import com.videonasocialmedia.vimojo.cameraSettings.model.FrameRateSetting;
import com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting;
import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraSettingsDataSource;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.util.HashMap;

import static com.videonasocialmedia.vimojo.cameraSettings.model.FrameRateSetting.CAMERA_SETTING_FRAME_RATE_24_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.FrameRateSetting.CAMERA_SETTING_FRAME_RATE_25_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.FrameRateSetting.CAMERA_SETTING_FRAME_RATE_30_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting.CAMERA_SETTING_RESOLUTION_1080_BACK_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting.CAMERA_SETTING_RESOLUTION_1080_FRONT_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting.CAMERA_SETTING_RESOLUTION_2160_BACK_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting.CAMERA_SETTING_RESOLUTION_2160_FRONT_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting.CAMERA_SETTING_RESOLUTION_720_BACK_ID;
import static com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting.CAMERA_SETTING_RESOLUTION_720_FRONT_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_CAMERA_SETTINGS_CAMERA_ID_SELECTED;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_CAMERA_SETTINGS_CAMERA_ID_SELECTED_VERTICAL_APP;

/**
 * Created by jliarte on 28/11/17.
 */

/**
 * {@link ProfileRepository} implementation to build a {@link Profile} from camera settings
 * chosen by user and stored in device.
 *
 * This mapping between {@link Profile} and {@link CameraSettings} is made because app now need that
 * all video clips in a {@link com.videonasocialmedia.videonamediaframework.model.VMComposition}
 * have the same video settings as final exported video.
 */
public class ProfileRepositoryFromCameraSettings implements ProfileRepository {
  public static final VideoQuality.Quality DEFAULT_VIDEO_QUALITY = VideoQuality.Quality.LOW;
  public static final VideoFrameRate.FrameRate DEFAULT_VIDEO_FRAME_RATE = VideoFrameRate
          .FrameRate.FPS30;
  private final CameraSettingsDataSource cameraSettingsRepository;
  private HashMap<String, VideoQuality.Quality> videoQualityMap;
  private HashMap<String, VideoFrameRate.FrameRate> frameRateMap;
  private HashMap<String, VideoResolution.Resolution> resolutionMap;

  public ProfileRepositoryFromCameraSettings(CameraSettingsDataSource cameraSettingsRepository,
                                             boolean amIAVerticalApp, boolean showCameraPro,
                                             String defaultResolutionSetting) {
    this.cameraSettingsRepository = cameraSettingsRepository;
    int defaultCameraIdSelected = DEFAULT_CAMERA_SETTINGS_CAMERA_ID_SELECTED;
    if (amIAVerticalApp) {
      defaultCameraIdSelected = DEFAULT_CAMERA_SETTINGS_CAMERA_ID_SELECTED_VERTICAL_APP;
    }
    if (this.cameraSettingsRepository.getCameraSettings() == null) {
      createDefaultCameraSettings(defaultCameraIdSelected, showCameraPro, defaultResolutionSetting);
    }
    setupVideoQualityMap();
    setupFrameRateMap();
    setupResolutionMap();
  }

  private void setupResolutionMap() {
    resolutionMap = new HashMap<>();
    resolutionMap.put(ResolutionSetting.CAMERA_SETTING_RESOLUTION_H_720,
            VideoResolution.Resolution.HD720);
    resolutionMap.put(ResolutionSetting.CAMERA_SETTING_RESOLUTION_H_1080,
            VideoResolution.Resolution.HD1080);
    resolutionMap.put(ResolutionSetting.CAMERA_SETTING_RESOLUTION_H_2160,
            VideoResolution.Resolution.HD4K);
    resolutionMap.put(ResolutionSetting.CAMERA_SETTING_RESOLUTION_V_720,
        VideoResolution.Resolution.V_720P);
    resolutionMap.put(ResolutionSetting.CAMERA_SETTING_RESOLUTION_V_1080,
        VideoResolution.Resolution.V_1080P);
    resolutionMap.put(ResolutionSetting.CAMERA_SETTING_RESOLUTION_V_2160,
        VideoResolution.Resolution.V_4K);
  }

  private void setupFrameRateMap() {
    frameRateMap = new HashMap<>();
    frameRateMap.put(FrameRateSetting.CAMERA_SETTING_FRAME_RATE_24, VideoFrameRate.FrameRate.FPS24);
    frameRateMap.put(FrameRateSetting.CAMERA_SETTING_FRAME_RATE_25, VideoFrameRate.FrameRate.FPS25);
    frameRateMap.put(FrameRateSetting.CAMERA_SETTING_FRAME_RATE_30, VideoFrameRate.FrameRate.FPS30);
  }

  private void setupVideoQualityMap() {
    videoQualityMap = new HashMap<>();
    videoQualityMap.put(CameraSettings.CAMERA_SETTING_QUALITY_16, VideoQuality.Quality.LOW);
    videoQualityMap.put(CameraSettings.CAMERA_SETTING_QUALITY_25, VideoQuality.Quality.GOOD);
    videoQualityMap.put(CameraSettings.CAMERA_SETTING_QUALITY_50, VideoQuality.Quality.HIGH);
  }

  // TODO(jliarte): 29/11/17 seems not to be responsibility of this repo, check for suitable class
  private void createDefaultCameraSettings(int defaultCameraIdSelected, boolean showCameraPro,
                                           String defaultResolutionSetting) {
    HashMap<Integer, Boolean> resolutionsSupportedMap = new HashMap<>();
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_720_BACK_ID, true);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_1080_BACK_ID, false);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_2160_BACK_ID, false);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_720_FRONT_ID, true);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_1080_FRONT_ID, false);
    resolutionsSupportedMap.put(CAMERA_SETTING_RESOLUTION_2160_FRONT_ID, false);

    ResolutionSetting resolutionSetting = new ResolutionSetting(defaultResolutionSetting,
        resolutionsSupportedMap);

    HashMap<Integer, Boolean> frameRateSupportedMap = new HashMap<>();
    frameRateSupportedMap.put(CAMERA_SETTING_FRAME_RATE_24_ID, false);
    frameRateSupportedMap.put(CAMERA_SETTING_FRAME_RATE_25_ID, false);
    frameRateSupportedMap.put(CAMERA_SETTING_FRAME_RATE_30_ID, true);
    FrameRateSetting frameRateSetting = new FrameRateSetting(
            Constants.DEFAULT_CAMERA_SETTING_FRAME_RATE, frameRateSupportedMap);

    String quality = Constants.DEFAULT_CAMERA_SETTING_QUALITY;
    String interfaceSelected;
    if (showCameraPro) {
      interfaceSelected = Constants.DEFAULT_CAMERA_SETTING_INTERFACE_SELECTED;
    } else {
      interfaceSelected = Constants.CAMERA_SETTING_INTERFACE_BASIC;
    }
    CameraSettings defaultCameraSettings = new CameraSettings(resolutionSetting,
            frameRateSetting, quality, interfaceSelected, defaultCameraIdSelected);
    cameraSettingsRepository.createCameraSetting(defaultCameraSettings);
  }

  @Override
  public Profile getCurrentProfile(boolean isVerticalMode) {
    Profile currentProfileInstance = buildProfileFromCameraSettings(isVerticalMode);
    return currentProfileInstance;
  }

  @NonNull
  /**
   * Build composition profile from current camera settings.
   *
   * Currently camera settings are tied to composition profile. As by 2011/11/28
   * {@link com.videonasocialmedia.videonamediaframework.pipeline.VMCompositionExportSession} does
   * not transcode composition videos to match composition profile, we force videos added to
   * composition to have the same settings than the composition profile.
   * This is the reason why here we build composition profile from stored camera settings
   */
  private Profile buildProfileFromCameraSettings(boolean isVerticalMode) {
    VideoResolution.Resolution resolution = getResolutionFromSettings(isVerticalMode);
    VideoQuality.Quality quality = getQualityFromSettings();
    VideoFrameRate.FrameRate frameRate = getFrameRateFromSettings();
    // TODO(jliarte): 28/11/17 check if we have to ask repo for it
    return new Profile(resolution, quality, frameRate);
  }

  private VideoResolution.Resolution getResolutionFromSettings(boolean isVerticalMode) {
    ResolutionSetting resolutionSetting = cameraSettingsRepository.getCameraSettings()
            .getResolutionSetting();
    VideoResolution.Resolution resolution = resolutionMap.get(resolutionSetting.getResolution());
    if (resolutionSetting.isSupportedByDeviceBackFacingCamera() && resolution != null) {
      return resolution;
    } else {
      // default 1080p. We suppose that 720p is the minimum supported, 1080p not is always presented if all phones, eg. Videona MotoG.
      if (resolutionSetting.deviceSupports(CAMERA_SETTING_RESOLUTION_1080_BACK_ID)) {
        return isVerticalMode ? VideoResolution.Resolution.V_1080P
            : VideoResolution.Resolution.HD1080;
      } else {
        return isVerticalMode ? VideoResolution.Resolution.V_720P
            : VideoResolution.Resolution.HD720;
      }
    }
  }

  private VideoQuality.Quality getQualityFromSettings() {
    CameraSettings cameraSettings = cameraSettingsRepository.getCameraSettings();
    VideoQuality.Quality videoQuality = videoQualityMap.get(cameraSettings.getQuality());
    if (videoQuality == null) {
      videoQuality = DEFAULT_VIDEO_QUALITY;
    }
    return videoQuality;
  }

  private VideoFrameRate.FrameRate getFrameRateFromSettings() {
    FrameRateSetting frameRateSetting = cameraSettingsRepository.getCameraSettings()
            .getFrameRateSetting();
    VideoFrameRate.FrameRate frameRate = frameRateMap.get(frameRateSetting.getFrameRate());
    if (frameRateSetting.isSupportedByDevice() && frameRate != null) {
      return frameRate;
    } else {
      return DEFAULT_VIDEO_FRAME_RATE;
    }
  }
}
