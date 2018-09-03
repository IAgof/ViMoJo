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
 * Use Case for retrieving the current {@link UserFeatures} from repository.
 */
public class GetCurrentUserFeatures {

  private UserFeaturesRepository userFeaturesRepository;

  @Inject
  public GetCurrentUserFeatures(UserFeaturesRepository userFeaturesRepository) {
    this.userFeaturesRepository = userFeaturesRepository;
  }

  public UserFeatures get() {
    return userFeaturesRepository.getCurrentUserFeatures();
  }
}
