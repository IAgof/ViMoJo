/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.featuresToggles.repository.datasource;

/**
 * Created by alvaro on 30/8/18.
 */

import android.content.SharedPreferences;

import com.videonasocialmedia.vimojo.repository.Specification;
import com.videonasocialmedia.vimojo.repository.datasource.DataSource;
import com.videonasocialmedia.vimojo.featuresToggles.domain.model.FeatureToggle;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Local DataSource for {@link FeatureToggle}. Provide local persistence of {@link FeatureToggle}
 * using {@link SharedPreferences}.
 */
public class FeatureSharedPreferencesDataSource implements DataSource<FeatureToggle> {
  private static final String DESCRIPTION_SUFFIX = "_description";
  private final SharedPreferences featuresSharedPreferences;

  @Inject
  public FeatureSharedPreferencesDataSource(
          @Named("featureToggleSharedPreferences") SharedPreferences featuresSharedPreferences) {
    this.featuresSharedPreferences = featuresSharedPreferences;
  }

  @Override
  public void add(FeatureToggle item) {
  }

  @Override
  public void add(Iterable<FeatureToggle> items) {
  }

  @Override
  public void update(FeatureToggle item) {
    if (item != null) {
      featuresSharedPreferences.edit().putBoolean(item.getName(), item.isEnabled())
              .putString(item.getName() + DESCRIPTION_SUFFIX, item.getDescription()).apply();
    }
  }

  @Override
  public void remove(FeatureToggle item) {
  }

  @Override
  public void remove(Specification specification) {
  }

  @Override
  public List<FeatureToggle> query(Specification specification) {
    // TODO(jliarte): 4/09/18 implement as needed
    return null;
  }

  @Override
  public FeatureToggle getById(String id) {
    if (featuresSharedPreferences.getAll().containsKey(id)) {
      // TODO(jliarte): 4/09/18 create a mapper??
      FeatureToggle featureToggle =
              new FeatureToggle(id, featuresSharedPreferences.getBoolean(id, false));
      featureToggle.description =
              featuresSharedPreferences.getString(id + DESCRIPTION_SUFFIX, "");
      return featureToggle;
    } else {
      return null;
    }
  }

  // TODO(jliarte): 4/09/18 pull member up
  public void update(List<FeatureToggle> features) {
    for (FeatureToggle featureToggle : features) {
      this.update(featureToggle);
    }
  }

  // TODO(jliarte): 4/09/18 pull member up
  public List<FeatureToggle> getAll() {
    ArrayList featureToggles = new ArrayList<FeatureToggle>();
    Set<String> sharedPreferencesMapKeys = featuresSharedPreferences.getAll().keySet();
    for (String key : sharedPreferencesMapKeys) {
      if (!key.endsWith(DESCRIPTION_SUFFIX)) {
        featureToggles.add(this.getById(key));
      }
    }
    return featureToggles;
  }
}
