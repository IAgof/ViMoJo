package com.videonasocialmedia.vimojo.featuresToggles;

/**
 * Created by jliarte on 3/09/18.
 */

import com.videonasocialmedia.vimojo.featuresToggles.domain.model.FeatureToggle;
import com.videonasocialmedia.vimojo.featuresToggles.repository.FeatureRepository;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.util.HashMap;

import javax.inject.Inject;

import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_FORCE_WATERMARK;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_FTP;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_SHOW_ADS;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_VIMOJO_PLATFORM;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_VIMOJO_STORE;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_VOICE_OVER;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_WATERMARK;

/**
 * Feature router for holding vimojo feature toggles
 */
class FeatureRouter {
  private final FeatureRepository featureRepository;
  private HashMap<String, FeatureToggle> defaultMap;

  @Inject
  public FeatureRouter(FeatureRepository featureRepository) {
    initDeafults();
    this.featureRepository = featureRepository;
  }

  private void initDeafults() {
    defaultMap = new HashMap<>();
    defaultMap.put(Constants.USER_FEATURE_FORCE_WATERMARK,
            new FeatureToggle(Constants.USER_FEATURE_FORCE_WATERMARK, DEFAULT_FORCE_WATERMARK));
    defaultMap.put(Constants.USER_FEATURE_WATERMARK,
            new FeatureToggle(Constants.USER_FEATURE_WATERMARK, DEFAULT_WATERMARK));
    defaultMap.put(Constants.FEATURE_VIMOJO_STORE,
            new FeatureToggle(Constants.FEATURE_VIMOJO_STORE, DEFAULT_VIMOJO_STORE));
    defaultMap.put(Constants.FEATURE_VIMOJO_PLATFORM,
            new FeatureToggle(Constants.FEATURE_VIMOJO_PLATFORM, DEFAULT_VIMOJO_PLATFORM));
    defaultMap.put(Constants.USER_FEATURE_FTP_PUBLISHING,
            new FeatureToggle(Constants.USER_FEATURE_FTP_PUBLISHING, DEFAULT_FTP));
    defaultMap.put(Constants.FEATURE_ADS_ENABLED,
            new FeatureToggle(Constants.FEATURE_ADS_ENABLED, DEFAULT_SHOW_ADS));
    defaultMap.put(Constants.USER_FEATURE_VOICE_OVER,
            new FeatureToggle(Constants.USER_FEATURE_VOICE_OVER, DEFAULT_VOICE_OVER));
  }

  public boolean isEnabled(String feature) {
    FeatureToggle featureToggle = this.getFeatureToggleById(feature);
    return featureToggle != null && featureToggle.isEnabled();
  }

  private FeatureToggle getFeatureToggleById(String feature) {
    FeatureToggle featureToggle = featureRepository.getById(feature);
    if (featureToggle != null) {
      return featureToggle;
    } else {
      return defaultMap.get(feature);
    }
  }

}