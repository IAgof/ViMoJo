/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.featuresToggles.repository.datasource.mapper;

/**
 * Created by alvaro on 30/8/18.
 */

import com.videonasocialmedia.vimojo.repository.KarumiMapper;
import com.videonasocialmedia.vimojo.featuresToggles.domain.model.FeatureToggle;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.FeatureToggleDto;

import javax.inject.Inject;

/**
 * Class to provide model conversions between {@link FeatureToggle} and {@link FeatureToggleDto}
 */
public class FeatureToggleToFeatureToggleDtoMapper extends KarumiMapper<FeatureToggle,
        FeatureToggleDto> {
  @Inject
  public FeatureToggleToFeatureToggleDtoMapper() {

  }

  @Override
  public FeatureToggleDto map(FeatureToggle featureToggle) {
    FeatureToggleDto featureToggleDto = new FeatureToggleDto();
    featureToggleDto.name = featureToggle.getName();
    featureToggleDto.description = featureToggle.getDescription();
    featureToggleDto.enabled = featureToggle.isEnabled();
    return featureToggleDto;
  }

  @Override
  public FeatureToggle reverseMap(FeatureToggleDto featureToggleDto) {
    if (featureToggleDto == null) {
      return null;
    }
    FeatureToggle featureToggle =
            new FeatureToggle(featureToggleDto.getName(), featureToggleDto.isEnabled());
    featureToggle.description = featureToggleDto.getDescription();
    return featureToggle;
  }
}
