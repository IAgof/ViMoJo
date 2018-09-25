/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.featuresToggles.repository;

/**
 * Created by alvaro on 30/8/18.
 */

import com.videonasocialmedia.vimojo.featuresToggles.domain.model.FeatureToggle;
import com.videonasocialmedia.vimojo.featuresToggles.repository.datasource.FeatureApiDataSource;
import com.videonasocialmedia.vimojo.featuresToggles.repository.datasource.FeatureInMemoryDataSource;
import com.videonasocialmedia.vimojo.featuresToggles.repository.datasource.FeatureSharedPreferencesDataSource;
import com.videonasocialmedia.vimojo.repository.DeletePolicy;
import com.videonasocialmedia.vimojo.repository.ReadPolicy;
import com.videonasocialmedia.vimojo.repository.Specification;
import com.videonasocialmedia.vimojo.repository.VimojoRepository;
import com.videonasocialmedia.vimojo.repository.datasource.BackgroundScheduler;

import java.util.List;

import javax.inject.Inject;

/**
 * Repository for providing {@link FeatureToggle} via repository pattern
 *
 * <p>This class handles saving and retrieving {@link FeatureToggle}s from different data sources
 * with local first data policy, and merge {@link FeatureToggle} provided by them for returning
 * results.</p>
 */
public class FeatureRepository extends VimojoRepository<FeatureToggle> {
  private final FeatureApiDataSource remoteDataSource;
  private final FeatureSharedPreferencesDataSource localDataSource;
  private final FeatureInMemoryDataSource cacheDataSource;
  private final BackgroundScheduler backgroundScheduler;

  @Inject
  public FeatureRepository(
          FeatureApiDataSource featureApiDataSourceDataSource,
          FeatureSharedPreferencesDataSource featureSharedPreferencesDataSourceDataSource,
          FeatureInMemoryDataSource featureCacheDataSourceDataSource,
          BackgroundScheduler backgroundScheduler) {
    this.remoteDataSource = featureApiDataSourceDataSource;
    this.localDataSource = featureSharedPreferencesDataSourceDataSource;
    this.cacheDataSource = featureCacheDataSourceDataSource;
    this.backgroundScheduler = backgroundScheduler;
  }

  @Override
  public void add(FeatureToggle item) {
    // 30/8/18 empty implementation. Application only get/list/query FeatureToggles
  }

  @Override
  public void add(Iterable<FeatureToggle> items) {
    // 30/8/18 empty implementation. Application only get/list/query FeatureToggles
    // TODO(jliarte): 4/09/18 make this repo just "ReadableVimojoRepository"
  }

  @Override
  public void update(FeatureToggle item) {
    // 30/8/18 empty implementation. Application only get/list/query FeatureToggles
    // TODO(jliarte): 4/09/18 make this repo just "ReadableVimojoRepository"
  }

  @Override
  public void remove(FeatureToggle item) {
    // 30/8/18 empty implementation. Application only get/list/query FeatureToggles
    // TODO(jliarte): 4/09/18 make this repo just "ReadableVimojoRepository"
  }

  @Override
  public void remove(Specification specification) {
    // 30/8/18 empty implementation. Application only get/list/query FeatureToggles
    // TODO(jliarte): 4/09/18 make this repo just "ReadableVimojoRepository"
  }

  @Override
  public List<FeatureToggle> query(Specification specification) {
    // TODO(jliarte): 4/09/18 implement as needed
    return null;
  }

  @Override
  public FeatureToggle getById(String id) {
    return this.getById(id, ReadPolicy.READ_ALL);
  }

  @Override
  public void remove(FeatureToggle item, DeletePolicy policy) {
    // 30/8/18 empty implementation. Application only get/list/query FeatureToggles
    // TODO(jliarte): 4/09/18 make this repo just "ReadableVimojoRepository"
  }

  @Override
  public FeatureToggle getById(String userId, ReadPolicy readPolicy) {
    FeatureToggle featureToggle = null;
    if (readPolicy.useLocal()) {
      featureToggle = cacheDataSource.getById(userId);
      if (featureToggle == null) {
        featureToggle = localDataSource.getById(userId);
        cacheDataSource.update(featureToggle);
      }
    }
    if (featureToggle == null && readPolicy.useRemote()) {
      backgroundScheduler.schedule(() -> {
        FeatureToggle apifeatureToggle = remoteDataSource.getById(userId);
        localDataSource.update(apifeatureToggle);
        cacheDataSource.update(apifeatureToggle);
        return apifeatureToggle;
      });
    }
    return featureToggle;
  }

  public List<FeatureToggle> fetch() {
    return this.getAll(ReadPolicy.API_ONLY);
  }

  // TODO(jliarte): 4/09/18 pull member up
  public List<FeatureToggle> getAll(ReadPolicy readPolicy) {
    List<FeatureToggle> features = null;
    if (readPolicy.useLocal()) {
      features = cacheDataSource.getAll();
      if (features == null) {
        features = localDataSource.getAll();
        cacheDataSource.update(features);
      }
    }
    if (features == null && readPolicy.useRemote()) {
      backgroundScheduler.schedule(() -> {
        List<FeatureToggle> apiFeatures = remoteDataSource.getAll();
        cacheDataSource.update(apiFeatures);
        localDataSource.update(apiFeatures);
        return apiFeatures;
      });
    }
    return features;
  }
}
