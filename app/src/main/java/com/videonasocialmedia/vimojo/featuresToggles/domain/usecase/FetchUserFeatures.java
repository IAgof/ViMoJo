package com.videonasocialmedia.vimojo.featuresToggles.domain.usecase;

/**
 * Created by jliarte on 3/09/18.
 */

import com.videonasocialmedia.vimojo.featuresToggles.repository.FeatureRepository;

import javax.inject.Inject;

/**
 * Use case for calling fetch on {@link FeatureRepository}
 */
public class FetchUserFeatures {
  private FeatureRepository featureRepository;

  @Inject
  public FetchUserFeatures(FeatureRepository featureRepository) {
    this.featureRepository = featureRepository;
  }

  public void fetch() {
    featureRepository.fetch();
  }
}
