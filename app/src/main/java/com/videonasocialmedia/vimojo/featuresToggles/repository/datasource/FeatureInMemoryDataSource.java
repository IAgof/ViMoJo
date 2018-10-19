/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.featuresToggles.repository.datasource;

/**
 * Created by alvaro on 31/8/18.
 */

import com.videonasocialmedia.vimojo.repository.Specification;
import com.videonasocialmedia.vimojo.repository.datasource.InMemoryDataSource;
import com.videonasocialmedia.vimojo.featuresToggles.domain.model.FeatureToggle;

import java.util.List;

import javax.inject.Inject;

/**
 *  MemoryCache DataSource for user features. Provide cache persistence of UserFeatures using,
 *  object in app.
 */
public class FeatureInMemoryDataSource extends InMemoryDataSource<String, FeatureToggle> {
  @Inject public FeatureInMemoryDataSource() {
  }

  @Override
  public void remove(Specification specification) {

  }

  @Override
  public List<FeatureToggle> query(Specification specification) {
    return null;
  }

  // TODO(jliarte): 4/09/18 pull member up
  public void update(List<FeatureToggle> features) {
    for (FeatureToggle featureToggle : features) {
      this.update(featureToggle);
    }
  }
}
