package com.videonasocialmedia.vimojo.featuresToggles;

/**
 * Created by jliarte on 3/09/18.
 */

import com.videonasocialmedia.vimojo.utils.Constants;

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

  public boolean showWaterMarkSwitch() {
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
}
