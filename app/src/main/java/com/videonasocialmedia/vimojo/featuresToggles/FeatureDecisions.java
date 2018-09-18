package com.videonasocialmedia.vimojo.featuresToggles;

/**
 * Created by jliarte on 3/09/18.
 */

import android.content.Context;

import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

/**
 * Class serving as collection point for any feature toggle decision logic.
 */
public class FeatureDecisions {
  private final FeatureRouter features;

  @Inject public FeatureDecisions(FeatureRouter features) {
    this.features = features;
  }

  public boolean watermarkIsForced() {
    return features.isEnabled(Constants.USER_FEATURE_FORCE_WATERMARK);
  }

  public boolean showWatermarkSwitch() {
    return features.isEnabled(Constants.USER_FEATURE_WATERMARK)
            && !features.isEnabled(Constants.USER_FEATURE_FORCE_WATERMARK);
  }

  public boolean vimojoStoreAvailable() {
    return features.isEnabled(Constants.FEATURE_VIMOJO_STORE);
  }

  public boolean vimojoPlatformAvailable() {
    return features.isEnabled(Constants.FEATURE_VIMOJO_PLATFORM);
  }

  public boolean ftpPublishingAvailable() {
    return features.isEnabled(Constants.USER_FEATURE_FTP_PUBLISHING);
  }

  public boolean showAds() {
    return features.isEnabled(Constants.FEATURE_ADS_ENABLED);
  }

  public boolean voiceOverAvailable() {
    return features.isEnabled(Constants.USER_FEATURE_VOICE_OVER);
  }

  public boolean hideTransitionPreference() {
    return !features.isEnabled(Constants.FEATURE_AVTRANSITIONS);
  }

  public boolean cameraProAvailable() {
    return features.isEnabled(Constants.USER_FEATURE_CAMERA_PRO);
  }

  public boolean selectFrameRateAvailable() {
    return features.isEnabled(Constants.USER_FEATURE_SELECT_FRAME_RATE);
  }

  public boolean selectResolutionAvailable() {
    return features.isEnabled(Constants.USER_FEATURE_SELECT_RESOLUTION);
  }

  public boolean hideRecordAudioGain() {
    return !features.isEnabled(Constants.FEATURE_RECORD_AUDIO_GAIN);
  }

  public boolean showSocialNetworks() {
    return features.isEnabled(Constants.FEATURE_SHARE_SHOW_SOCIAL_NETWORKS);
  }

  public boolean showMoreAppsPreference() {
    return features.isEnabled(Constants.FEATURE_SHOW_MORE_APPS);
  }

  public boolean hideTutorials() {
    return !features.isEnabled(Constants.FEATURE_SHOW_TUTORIALS);
  }

  public boolean amIAVerticalApp(){
    return features.isEnabled(Constants.FEATURE_VERTICAL_VIDEOS);
  }

  public String defaultResolutionSetting() {
    if (features.isEnabled(Constants.FEATURE_VERTICAL_VIDEOS)) {
      return ResolutionSetting.CAMERA_SETTING_RESOLUTION_V_720;
    } else {
      return ResolutionSetting.CAMERA_SETTING_RESOLUTION_H_720;
    }
  }

  public VideoResolution.Resolution defaultVideoResolution() {
    if (features.isEnabled(Constants.FEATURE_VERTICAL_VIDEOS)) {
      return VideoResolution.Resolution.V_720P;
    } else {
      return VideoResolution.Resolution.HD720;
    }
  }

  public boolean isAppOutOfDate(Context context) {
    return features.isEnabled(Constants.FEATURE_OUT_OF_DATE) && isBetaAppOutOfDate(context)
        && !BuildConfig.DEBUG;
  }

  private boolean isBetaAppOutOfDate(Context context) {
    Calendar endOfBeta = Calendar.getInstance();
    Calendar today = Calendar.getInstance();

    // TODO:(alvaro.martinez) 8/11/16 get this date from flavor config
    String str= context.getResources().getString(R.string.app_out_of_date);
    Date dateBeta = null;
    try {
      dateBeta = new SimpleDateFormat("yyyy-MM-dd").parse(str);
    } catch (ParseException e) {
      e.printStackTrace();
    }

    endOfBeta.setTime(dateBeta);
    today.setTime(new Date());

    return today.after(endOfBeta);
  }

}
