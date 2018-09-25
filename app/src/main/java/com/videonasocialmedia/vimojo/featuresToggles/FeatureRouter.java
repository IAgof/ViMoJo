package com.videonasocialmedia.vimojo.featuresToggles;

/**
 * Created by jliarte on 3/09/18.
 */

import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.featuresToggles.domain.model.FeatureToggle;
import com.videonasocialmedia.vimojo.featuresToggles.repository.FeatureRepository;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.util.HashMap;

import javax.inject.Inject;

import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_CAMERA_PRO;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_CLOUD_BACKUP;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_FORCE_WATERMARK;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_FTP;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_SELECT_FRAME_RATE;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_SELECT_RESOLUTION;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_SHOW_ADS;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_VIMOJO_STORE;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_VOICE_OVER;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_WATERMARK;

/**
 * Feature router for holding vimojo feature toggles
 */
class FeatureRouter {
  private final FeatureRepository featureRepository;
  private HashMap<String, FeatureToggle> defaultMap;
  private HashMap<String, FeatureToggle> releaseMap;

  @Inject
  public FeatureRouter(FeatureRepository featureRepository) {
    initDeafults();
    initReleaseToggles();
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
    defaultMap.put(Constants.USER_FEATURE_FTP_PUBLISHING,
            new FeatureToggle(Constants.USER_FEATURE_FTP_PUBLISHING, DEFAULT_FTP));
    defaultMap.put(Constants.FEATURE_ADS_ENABLED,
            new FeatureToggle(Constants.FEATURE_ADS_ENABLED, DEFAULT_SHOW_ADS));
    defaultMap.put(Constants.USER_FEATURE_VOICE_OVER,
            new FeatureToggle(Constants.USER_FEATURE_VOICE_OVER, DEFAULT_VOICE_OVER));
    defaultMap.put(Constants.USER_FEATURE_CAMERA_PRO,
        new FeatureToggle(Constants.USER_FEATURE_CAMERA_PRO, DEFAULT_CAMERA_PRO));
    defaultMap.put(Constants.USER_FEATURE_SELECT_FRAME_RATE,
        new FeatureToggle(Constants.USER_FEATURE_SELECT_FRAME_RATE, DEFAULT_SELECT_FRAME_RATE));
    defaultMap.put(Constants.USER_FEATURE_SELECT_RESOLUTION,
        new FeatureToggle(Constants.USER_FEATURE_SELECT_RESOLUTION, DEFAULT_SELECT_RESOLUTION));
    defaultMap.put(Constants.USER_FEATURE_CLOUD_BACKUP,
        new FeatureToggle(Constants.USER_FEATURE_CLOUD_BACKUP, DEFAULT_CLOUD_BACKUP));
  }

  private void initReleaseToggles() {
    releaseMap = new HashMap<>();
    releaseMap.put(Constants.FEATURE_AVTRANSITIONS,
            new FeatureToggle(Constants.FEATURE_AVTRANSITIONS, BuildConfig.FEATURE_AVTRANSTITION));
    releaseMap.put(Constants.FEATURE_RECORD_AUDIO_GAIN,
        new FeatureToggle(Constants.FEATURE_RECORD_AUDIO_GAIN,
            BuildConfig.FEATURE_RECORD_AUDIO_GAIN));
    releaseMap.put(Constants.FEATURE_SHARE_SHOW_SOCIAL_NETWORKS,
        new FeatureToggle(Constants.FEATURE_SHARE_SHOW_SOCIAL_NETWORKS,
            BuildConfig.FEATURE_SHARE_SHOW_SOCIAL_NETWORKS));
    releaseMap.put(Constants.FEATURE_SHOW_MORE_APPS,
        new FeatureToggle(Constants.FEATURE_SHOW_MORE_APPS,
            BuildConfig.FEATURE_SHOW_MORE_APPS));
    releaseMap.put(Constants.FEATURE_SHOW_TUTORIALS,
        new FeatureToggle(Constants.FEATURE_SHOW_TUTORIALS, BuildConfig.FEATURE_SHOW_TUTORIALS));
    releaseMap.put(Constants.FEATURE_VERTICAL_VIDEOS,
        new FeatureToggle(Constants.FEATURE_VERTICAL_VIDEOS, BuildConfig.FEATURE_VERTICAL_VIDEOS));
    releaseMap.put(Constants.FEATURE_OUT_OF_DATE,
        new FeatureToggle(Constants.FEATURE_OUT_OF_DATE, BuildConfig.FEATURE_OUT_OF_DATE));
    releaseMap.put(Constants.FEATURE_VIMOJO_PLATFORM,
        new FeatureToggle(Constants.FEATURE_VIMOJO_PLATFORM, BuildConfig.FEATURE_VIMOJO_PLATFORM));
  }

  public boolean isEnabled(String feature) {
    FeatureToggle featureToggle = this.getFeatureToggleById(feature);
    return featureToggle != null && featureToggle.isEnabled();
  }

  private FeatureToggle getFeatureToggleById(String feature) {
    if (releaseMap.get(feature) != null) {
      return releaseMap.get(feature);
    } else {
      FeatureToggle featureToggle = featureRepository.getById(feature);
      if (featureToggle != null) {
        return featureToggle;
      } else {
        return defaultMap.get(feature);
      }
    }
  }

}
