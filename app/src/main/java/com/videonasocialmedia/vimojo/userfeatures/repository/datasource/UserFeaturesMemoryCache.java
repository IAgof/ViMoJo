/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.userfeatures.repository.datasource;

/**
 * Created by alvaro on 31/8/18.
 */

import com.videonasocialmedia.vimojo.repository.datasource.MemoryCacheDataSource;
import com.videonasocialmedia.vimojo.userfeatures.domain.model.UserFeatures;
import com.videonasocialmedia.vimojo.utils.Constants;

import javax.inject.Inject;

import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_FORCE_WATERMARK;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_SHOW_ADS;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_VIMOJO_PLATFORM;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_VIMOJO_STORE;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_VOICE_OVER;
import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_WATERMARK;

/**
 *  MemoryCache DataSource for user features. Provide cache persistence of UserFeatures using,
 *  object in app.
 */
public class UserFeaturesMemoryCache implements MemoryCacheDataSource<UserFeatures> {

  private UserFeatures userFeatures;

  @Inject
  public UserFeaturesMemoryCache() {

  }

  @Override
  public UserFeatures getCurrent() {
    return userFeatures;
  }

  @Override
  public void setCurrent(UserFeatures userFeatures) {
    this.userFeatures = userFeatures;
  }
}
