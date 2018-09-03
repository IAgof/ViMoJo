/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.userfeatures.repository.datasource.mapper;

/**
 * Created by alvaro on 30/8/18.
 */

import com.videonasocialmedia.vimojo.repository.KarumiMapper;
import com.videonasocialmedia.vimojo.userfeatures.domain.model.UserFeatures;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.UserFeaturesDto;

import javax.inject.Inject;

/**
 * Class to provide model conversions between {@link UserFeatures} and {@link UserFeaturesDto}
 */
public class UserFeaturesToUserFeaturesDtoMapper extends KarumiMapper<UserFeatures,
    UserFeaturesDto> {

  @Inject
  public UserFeaturesToUserFeaturesDtoMapper() {

  }

  @Override
  public UserFeaturesDto map(UserFeatures userFeatures) {
    UserFeaturesDto userFeaturesDto = new UserFeaturesDto();
    userFeaturesDto.forceWatermark = userFeatures.isForceWatermark();
    userFeaturesDto.ftp = userFeatures.isFtp();
    userFeaturesDto.showAds = userFeatures.isShowAds();
    userFeaturesDto.vimojoPlatform = userFeatures.isVimojoPlatform();
    userFeaturesDto.vimojoStore = userFeatures.isVimojoStore();
    userFeaturesDto.voiceOver = userFeatures.isVoiceOver();
    userFeaturesDto.watermark = userFeatures.isWatermark();
    return userFeaturesDto;
  }

  @Override
  public UserFeatures reverseMap(UserFeaturesDto userFeaturesDto) {
    boolean forceWatermark = userFeaturesDto.forceWatermark;
    boolean ftp = userFeaturesDto.ftp;
    boolean showAds = userFeaturesDto.showAds;
    boolean vimojoPlatform = userFeaturesDto.vimojoPlatform;
    boolean vimojoStore = userFeaturesDto.vimojoStore;
    boolean voiceOver = userFeaturesDto.voiceOver;
    boolean watermark = userFeaturesDto.watermark;
    return new UserFeatures(forceWatermark, ftp, showAds, vimojoPlatform, vimojoStore, voiceOver,
        watermark);
  }
}
