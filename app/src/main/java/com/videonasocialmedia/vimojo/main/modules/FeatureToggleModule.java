package com.videonasocialmedia.vimojo.main.modules;

/**
 * Created by jliarte on 27/10/16.
 */

import android.content.Context;
import android.content.SharedPreferences;

import com.videonasocialmedia.vimojo.auth0.GetUserId;
import com.videonasocialmedia.vimojo.featuresToggles.FeatureDecisions;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Module for providing feature decisions to classes that require them, based on Feature toggle router
 */
@Module
public class FeatureToggleModule {
  @Provides @Named("userId")
  String provideUserId(GetUserId getUserId) {
    return getUserId.getUserId().getId();
  }

  @Provides @Named("featureToggleSharedPreferences")
  SharedPreferences provideSharedPreferences(Context context) {
    return context.getSharedPreferences(
            ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FEATURE_TOGGLE_LOCAL_DS,
            Context.MODE_PRIVATE);
  }

  @Provides @Named("watermarkIsForced")
  boolean provideForcedWatermarkDecision(FeatureDecisions featureDecisions) {
    return featureDecisions.watermarkIsForced();
  }

  @Provides @Named("showWaterMarkSwitch")
  boolean provideShowWaterMarkSwitchDecision(FeatureDecisions featureDecisions) {
    return featureDecisions.showWaterMarkSwitch();
  }

  @Provides @Named("vimojoStoreAvailable")
  boolean provideVimojoStoreAvailable(FeatureDecisions featureDecisions) {
    // TODO(jliarte): 4/09/18 this is not a decision, is a feature availability
    return featureDecisions.vimojoStoreAvailable();
  }

  @Provides @Named("vimojoPlatformAvailable")
  boolean provideVimojoPlatformAvailable(FeatureDecisions featureDecisions) {
    // TODO(jliarte): 4/09/18 this is not a decision, is a feature availability
    return featureDecisions.vimojoPlatformAvailable();
  }

  @Provides @Named("ftpPublishingAvailable")
  boolean provideFtpPublishingAvailable(FeatureDecisions featureDecisions) {
    // TODO(jliarte): 4/09/18 this is not a decision, is a feature availability
    return featureDecisions.ftpPublishingAvailable();
  }

  @Provides @Named("showAds")
  boolean provideShowAdsDecision(FeatureDecisions featureDecisions) {
    return featureDecisions.showAds();
  }

  @Provides @Named("voiceOverAvailable")
  boolean provideVoiceOverAvailable(FeatureDecisions featureDecisions) {
    // TODO(jliarte): 4/09/18 this is not a decision, is a feature availability
    return featureDecisions.voiceOverAvailable();
  }

  @Provides @Named("hideTransitionPreference")
  boolean provideHideTransitionPreferenceDecision(FeatureDecisions featureDecisions) {
    // TODO(jliarte): 4/09/18 this is not a decision, is a feature availability
    return featureDecisions.hideTransitionPreference();
  }

  @Provides @Named("showCameraProAvailable")
  boolean provideShowCameraProAvailable(FeatureDecisions featureDecisions) {
    // TODO(jliarte): 4/09/18 this is not a decision, is a feature availability
    return featureDecisions.cameraProAvailable();
  }

  @Provides @Named("selectFrameRateAvailable")
  boolean provideSelectFrameRateAvailable(FeatureDecisions featureDecisions) {
    // TODO(jliarte): 4/09/18 this is not a decision, is a feature availability
    return featureDecisions.selectFrameRateAvailable();
  }

  @Provides @Named("selectResolutionAvailable")
  boolean provideSelectResolutionAvailable(FeatureDecisions featureDecisions) {
    // TODO(jliarte): 4/09/18 this is not a decision, is a feature availability
    return featureDecisions.selectResolutionAvailable();
  }

  @Provides @Named("hideRecordAudioGain")
  boolean provideHideRecordAudioGainDecision(FeatureDecisions featureDecisions) {
    // TODO(jliarte): 4/09/18 this is not a decision, is a feature availability
    return featureDecisions.hideRecordAudioGain();
  }

  @Provides @Named("showSocialNetworks")
  boolean provideShowSocialNetworksDecision(FeatureDecisions featureDecisions) {
    // TODO(jliarte): 4/09/18 this is not a decision, is a feature availability
    return featureDecisions.showSocialNetworks();
  }

  @Provides @Named("showMoreAppsPreference")
  boolean provideShowMoreAppsPreference(FeatureDecisions featureDecisions) {
    // TODO(jliarte): 4/09/18 this is not a decision, is a feature availability
    return featureDecisions.showMoreAppsPreference();
  }

  @Provides @Named("hideTutorials")
  boolean provideHideTutorialsDecision(FeatureDecisions featureDecisions) {
    // TODO(jliarte): 4/09/18 this is not a decision, is a feature availability
    return featureDecisions.hideTutorials();
  }

}
