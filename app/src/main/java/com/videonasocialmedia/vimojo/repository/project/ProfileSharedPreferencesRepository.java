package com.videonasocialmedia.vimojo.repository.project;

import android.content.Context;
import android.content.SharedPreferences;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;

/**
 * Created by alvaro on 24/10/16.
 */

public class ProfileSharedPreferencesRepository implements ProfileRepository {
  public static final VideoQuality.Quality DEFAULT_VIDEO_QUALITY = VideoQuality.Quality.LOW;
  public static final int DEFAULT_VIDEO_QUALITY_NAME = R.string.low_quality_name;
  public static final VideoFrameRate.FrameRate DEFAULT_VIDEO_FRAME_RATE = VideoFrameRate.FrameRate.FPS30;
  public static final int DEFAULT_VIDEO_FRAME_RATE_NAME = R.string.high_frame_rate_name;
  private final SharedPreferences sharedPreferences;
  private final Context context;

  public ProfileSharedPreferencesRepository(SharedPreferences preferences, Context context) {
    this.sharedPreferences = preferences;
    this.context = context;
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
    String resolution = sharedPreferences.getString(ConfigPreferences.KEY_LIST_PREFERENCES_RESOLUTION,
        context.getString(R.string.good_resolution_name));
    if (sharedPreferences.getBoolean(ConfigPreferences.BACK_CAMERA_720P_SUPPORTED, false)) {
      if (resolution.compareTo(context.getString(R.string.low_resolution_name)) == 0) {
        return VideoResolution.Resolution.HD720;
      }
    }
    if (sharedPreferences.getBoolean(ConfigPreferences.BACK_CAMERA_1080P_SUPPORTED, false)) {
      if (resolution.compareTo(context.getString(R.string.good_resolution_name)) == 0) {
        return VideoResolution.Resolution.HD1080;
      }
    }
    if (sharedPreferences.getBoolean(ConfigPreferences.BACK_CAMERA_2160P_SUPPORTED, false)) {
      if (resolution.compareTo(context.getString(R.string.high_resolution_name)) == 0) {
        return VideoResolution.Resolution.HD4K;
      }
    }
    // default 1080p. We suppose that 720p is the minimum supported, 1080p not is always presented if all phones,ex Videona MotoG.
    if (sharedPreferences.getBoolean(ConfigPreferences.BACK_CAMERA_1080P_SUPPORTED, false)) {
      return VideoResolution.Resolution.HD1080;
    } else {
      return VideoResolution.Resolution.HD720;
    }
  }

  private VideoQuality.Quality getQualityFromPreferenceSettings() {
    String quality = sharedPreferences.getString(ConfigPreferences.KEY_LIST_PREFERENCES_QUALITY,
        context.getString(DEFAULT_VIDEO_QUALITY_NAME));
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
    String frameRate = sharedPreferences.getString(ConfigPreferences.KEY_LIST_PREFERENCES_FRAME_RATE,
        context.getString(DEFAULT_VIDEO_FRAME_RATE_NAME));
    if (sharedPreferences.getBoolean(ConfigPreferences.CAMERA_FRAME_RATE_24FPS_SUPPORTED, false)) {
      if (frameRate.compareTo(context.getString(R.string.low_frame_rate_name)) == 0) {
        return VideoFrameRate.FrameRate.FPS24;
      }
    }
    if (sharedPreferences.getBoolean(ConfigPreferences.CAMERA_FRAME_RATE_25FPS_SUPPORTED, false)) {
      if (frameRate.compareTo(context.getString(R.string.good_frame_rate_name)) == 0) {
        return VideoFrameRate.FrameRate.FPS25;
      }
    }
    if (sharedPreferences.getBoolean(ConfigPreferences.CAMERA_FRAME_RATE_30FPS_SUPPORTED, false)) {
      if (frameRate.compareTo(context.getString(R.string.high_frame_rate_name)) == 0) {
        return VideoFrameRate.FrameRate.FPS30;
      }
    }
    // default 30 fps, standard
    return DEFAULT_VIDEO_FRAME_RATE;
  }

}
