/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.userfeatures.repository.datasource;

/**
 * Created by alvaro on 30/8/18.
 */

import android.content.SharedPreferences;

import com.videonasocialmedia.vimojo.repository.Specification;
import com.videonasocialmedia.vimojo.repository.datasource.DataSource;
import com.videonasocialmedia.vimojo.userfeatures.domain.model.UserFeatures;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;

import java.util.List;

import javax.inject.Inject;

import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_FORCE_WATERMARK;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_FTP;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_SHOW_ADS;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_VIMOJO_PLATFORM;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_VIMOJO_STORE;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_VOICE_OVER;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_WATERMARK;


/**
 * Local DataSource for user features. Provide local persistence of UserFeatures using
 * SharedPreferences.
 */
public class UserFeaturesLocal implements DataSource<UserFeatures> {

  private final SharedPreferences sharedPreferences;

  @Inject
  public UserFeaturesLocal(SharedPreferences sharedPreferences) {
    this.sharedPreferences = sharedPreferences;
  }

  @Override
  public void add(UserFeatures item) {

  }

  @Override
  public void add(Iterable<UserFeatures> items) {
    // 30/8/18 empty implementation. User features can not be iterated, only one UserFeature.
  }

  @Override
  public void update(UserFeatures item) {
    if (item.isFtp()) {
      sharedPreferences.edit().putBoolean(ConfigPreferences.USER_FEATURES_FTP, true).apply();
    }
    if (item.isForceWatermark()) {
      sharedPreferences.edit().putBoolean(ConfigPreferences.USER_FEATURES_FORCE_WATERMARK, true)
          .apply();
    }
    if (item.isShowAds()) {
      sharedPreferences.edit().putBoolean(ConfigPreferences.USER_FEATURES_SHOW_ADS, true).apply();
    }
    if (item.isVimojoPlatform()) {
      sharedPreferences.edit().putBoolean(ConfigPreferences.USER_FEATURES_VIMOJO_PLATFORM, true)
          .apply();
    }
    if (item.isVimojoStore()) {
      sharedPreferences.edit().putBoolean(ConfigPreferences.USER_FEATURES_VIMOJO_STORE, true)
          .apply();
    }
    if (item.isVoiceOver()) {
      sharedPreferences.edit().putBoolean(ConfigPreferences.USER_FEATURES_VOICE_OVER, true).apply();
    }
    if (item.isWatermark()) {
      sharedPreferences.edit().putBoolean(ConfigPreferences.USER_FEATURES_WATERMARK, true).apply();
    }
  }

  @Override
  public void remove(UserFeatures item) {
    // 30/8/18 empty implementation. User features can not be removed.
  }

  @Override
  public void remove(Specification specification) {
    // 30/8/18 empty implementation. User features can not be removed.
  }

  @Override
  public List<UserFeatures> query(Specification specification) {
    // 30/8/18 empty implementation. User features can not be listed, only one UserFeature.
    return null;
  }

  @Override
  public UserFeatures getById(String id) {
    // 30/8/18 empty implementation. User features has not id.
    return null;
  }

  public UserFeatures get() {
    boolean forceWatermark = sharedPreferences
        .getBoolean(ConfigPreferences.USER_FEATURES_FORCE_WATERMARK, DEFAULT_FORCE_WATERMARK);
    boolean ftp = sharedPreferences
        .getBoolean(ConfigPreferences.USER_FEATURES_FTP, DEFAULT_FTP);
    boolean showAds = sharedPreferences
        .getBoolean(ConfigPreferences.USER_FEATURES_SHOW_ADS, DEFAULT_SHOW_ADS);
    boolean vimojoPlatform = sharedPreferences
        .getBoolean(ConfigPreferences.USER_FEATURES_VIMOJO_PLATFORM, DEFAULT_VIMOJO_PLATFORM);
    boolean vimojoStore = sharedPreferences
        .getBoolean(ConfigPreferences.USER_FEATURES_VIMOJO_STORE, DEFAULT_VIMOJO_STORE);
    boolean voiceOver = sharedPreferences
        .getBoolean(ConfigPreferences.USER_FEATURES_VOICE_OVER, DEFAULT_VOICE_OVER);
    boolean watermark = sharedPreferences
        .getBoolean(ConfigPreferences.USER_FEATURES_WATERMARK, DEFAULT_WATERMARK);

    return new UserFeatures(forceWatermark, ftp, showAds, vimojoPlatform, vimojoStore, voiceOver,
        watermark);
  }
}
