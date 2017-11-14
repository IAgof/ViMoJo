package com.videonasocialmedia.vimojo.repository.project;

import android.content.Context;
import android.content.SharedPreferences;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.record.model.CameraPreferences;
import com.videonasocialmedia.vimojo.record.model.FrameRatePreference;
import com.videonasocialmedia.vimojo.record.model.ResolutionPreference;
import com.videonasocialmedia.vimojo.repository.camera.CameraRepository;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;

/**
 * Created by alvaro on 24/10/16.
 */

public class ProfileCameraPreferencesRepository implements ProfileRepository {
  public static final VideoQuality.Quality DEFAULT_VIDEO_QUALITY = VideoQuality.Quality.LOW;
  public static final VideoFrameRate.FrameRate DEFAULT_VIDEO_FRAME_RATE = VideoFrameRate.FrameRate.FPS30;
  private final Context context;
  private final CameraRepository cameraRepository;

  public ProfileCameraPreferencesRepository(CameraRepository cameraRepository, Context context) {
    this.context = context;
    this.cameraRepository = cameraRepository;
  }

  @Override
  public Profile getCurrentProfile() {
    VideoResolution.Resolution resolution = getResolutionFromPreferencesSetting();
    VideoQuality.Quality quality = getQualityFromPreferenceSettings();
    VideoFrameRate.FrameRate frameRate = getFrameRateFromPreferenceSettings();

    Profile currentProfileInstance = Profile.getInstance(resolution, quality, frameRate);
    currentProfileInstance.setResolution(resolution);
    currentProfileInstance.setQuality(quality);
    currentProfileInstance.setFrameRate(frameRate);
    return currentProfileInstance;
  }

  private VideoResolution.Resolution getResolutionFromPreferencesSetting() {
    ResolutionPreference resolutionPreference = cameraRepository.getCameraPreferences()
        .getResolutionPreference();
    String resolution = resolutionPreference.getResolution();
    if(resolutionPreference.isResolutionBack720pSupported())
      if (resolution.compareTo(context.getString(R.string.low_resolution_name)) == 0) {
        return VideoResolution.Resolution.HD720;
      }
    if(resolutionPreference.isResolutionBack1080pSupported()) {
      if (resolution.compareTo(context.getString(R.string.good_resolution_name)) == 0) {
        return VideoResolution.Resolution.HD1080;
      }
    }
    if(resolutionPreference.isResolutionBack2160pSupported()) {
      if (resolution.compareTo(context.getString(R.string.high_resolution_name)) == 0) {
        return VideoResolution.Resolution.HD4K;
      }
    }
    // default 1080p. We suppose that 720p is the minimum supported, 1080p not is always presented if all phones,ex Videona MotoG.
    if (resolutionPreference.isResolutionBack1080pSupported()) {
      return VideoResolution.Resolution.HD1080;
    } else {
      return VideoResolution.Resolution.HD720;
    }
  }

  private VideoQuality.Quality getQualityFromPreferenceSettings() {
    CameraPreferences cameraPreferences = cameraRepository.getCameraPreferences();
    String quality = cameraPreferences.getQuality();
    if (quality.compareTo(context.getString(R.string.low_quality_name)) == 0) {
      return VideoQuality.Quality.LOW;
    }
    if (quality.compareTo(context.getString(R.string.good_quality_name)) == 0) {
      return VideoQuality.Quality.GOOD;
    }
    if (quality.compareTo(context.getString(R.string.high_quality_name)) == 0) {
      return VideoQuality.Quality.HIGH;
    }
    // default
    return DEFAULT_VIDEO_QUALITY;
  }

  private VideoFrameRate.FrameRate getFrameRateFromPreferenceSettings() {
    FrameRatePreference frameRatePreference = cameraRepository.getCameraPreferences()
        .getFrameRatePreference();
    String frameRate = frameRatePreference.getFrameRate();
    if (frameRatePreference.isFrameRate24FpsSupported()) {
      if (frameRate.compareTo(context.getString(R.string.low_frame_rate_name)) == 0) {
        return VideoFrameRate.FrameRate.FPS24;
      }
    }
    if (frameRatePreference.isFrameRate25FpsSupported()) {
      if (frameRate.compareTo(context.getString(R.string.good_frame_rate_name)) == 0) {
        return VideoFrameRate.FrameRate.FPS25;
      }
    }
    if (frameRatePreference.isFrameRate30FpsSupported()) {
      if (frameRate.compareTo(context.getString(R.string.high_frame_rate_name)) == 0) {
        return VideoFrameRate.FrameRate.FPS30;
      }
    }
    // default 30 fps, standard
    return DEFAULT_VIDEO_FRAME_RATE;
  }

}
