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

import com.videonasocialmedia.vimojo.repository.DeletePolicy;
import com.videonasocialmedia.vimojo.repository.ReadPolicy;
import com.videonasocialmedia.vimojo.repository.Specification;
import com.videonasocialmedia.vimojo.repository.VimojoRepository;
import com.videonasocialmedia.vimojo.featuresToggles.domain.model.FeatureToggle;
import com.videonasocialmedia.vimojo.featuresToggles.repository.datasource.FeatureApiDataSource;
import com.videonasocialmedia.vimojo.featuresToggles.repository.datasource.FeatureInMemoryDataSource;
import com.videonasocialmedia.vimojo.featuresToggles.repository.datasource.FeatureSharedPreferencesDataSource;

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

  @Inject
  public FeatureRepository(
          FeatureApiDataSource featureApiDataSourceDataSource,
          FeatureSharedPreferencesDataSource featureSharedPreferencesDataSourceDataSource,
          FeatureInMemoryDataSource featureCacheDataSourceDataSource) {
    this.remoteDataSource = featureApiDataSourceDataSource;
    this.localDataSource = featureSharedPreferencesDataSourceDataSource;
    this.cacheDataSource = featureCacheDataSourceDataSource;
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
      featureToggle = remoteDataSource.getById(userId);
      localDataSource.update(featureToggle);
      cacheDataSource.update(featureToggle);
    }

    return featureToggle;
//
//    remoteDataSource.getById(userId);
//    this.getById(userId, ReadPolicy.API_ONLY);
//
//    switch (readPolicy) {
//      case API_ONLY:
//        return this.remoteDataSource.getById(userId);
//      case LOCAL_ONLY:
//        return this.localDataSource.getById(userId);
//      case CACHE_ONLY:
//        return this.cacheDataSource.getById(userId);
//      case READ_ALL:
//        default:
//          // TODO(jliarte): 3/09/18 implement cache first data policy
//          UserFeatures userFeatures = cacheDataSource.getById(userId);
//          if (userFeatures == null) {
//            userFeatures = getFromLocal(userId);
//            cacheDataSource.update(userFeatures);
//          }
//          return userFeatures;
//    }
  }
//
//  private UserFeatures getFromLocal(String userId) {
//    UserFeatures userFeatures = localDataSource.getById(userId);
//    if (userFeatures == null) {
//      userFeatures = getFromRemote(userId);
//      localDataSource.update(userFeatures);
//    }
//    return userFeatures;
//  }

//  private UserFeatures getFromRemote(String userId) {
//    return remoteDataSource.getById(userId);
//  }

  public List<FeatureToggle> fetch() {
    return this.getAll(ReadPolicy.API_ONLY);
    // (jliarte): 3/09/18 local data sources already populated in getById method
//    populateLocalDS(userFeatures);
//    populateCacheDS(userFeatures);

//    Object userFeatures = this.getById(userId, ReadPolicy.API_ONLY);
//    userFeatures = getById.getUserFeatures(userId);
//    setMemoryCache.setUserFeatures(userFeatures);
//    saveLocal.saveUserFeatures(userFeatures);
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
      features = remoteDataSource.getAll();
      // TODO(jliarte): 4/09/18 shall we empty DSs before calling update?
      localDataSource.update(features);
      cacheDataSource.update(features);
    }

    return features;
  }
}
