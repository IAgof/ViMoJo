/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.userfeatures.domain.usecase;

/**
 * Created by alvaro on 30/8/18.
 */

import com.videonasocialmedia.vimojo.userfeatures.domain.model.UserFeatures;
import com.videonasocialmedia.vimojo.userfeatures.repository.UserFeaturesRepository;

import javax.inject.Inject;


/**
 * Use Case for saving a {@link UserFeatures} into repository.
 */
public class SaveLocalUserFeatures {

  private UserFeaturesRepository userFeaturesRepository;

  @Inject
  public SaveLocalUserFeatures(UserFeaturesRepository userFeaturesRepository) {
    this.userFeaturesRepository = userFeaturesRepository;
  }

  public void saveUserFeatures(UserFeatures userFeatures) {
    userFeaturesRepository.update(userFeatures);
  }
}
