/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.userfeatures.domain.usecase;

/**
 * Created by alvaro on 31/8/18.
 */

import com.videonasocialmedia.vimojo.userfeatures.domain.model.UserFeatures;
import com.videonasocialmedia.vimojo.userfeatures.repository.UserFeaturesRepository;

import javax.inject.Inject;

/**
 * Use Case for set the currents {@link UserFeatures} from repository.
 */
public class SetMemoryCacheUserFeatures {

  private UserFeaturesRepository userFeaturesRepository;

  @Inject
  SetMemoryCacheUserFeatures(UserFeaturesRepository userFeaturesRepository) {
    this.userFeaturesRepository = userFeaturesRepository;
  }

  public void setUserFeatures(UserFeatures userFeatures) {
    userFeaturesRepository.setCurrentUserFeatures(userFeatures);
  }
}
