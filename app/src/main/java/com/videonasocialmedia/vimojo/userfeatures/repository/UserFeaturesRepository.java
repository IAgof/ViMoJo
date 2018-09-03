/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.userfeatures.repository;

/**
 * Created by alvaro on 30/8/18.
 */

import com.videonasocialmedia.vimojo.repository.DeletePolicy;
import com.videonasocialmedia.vimojo.repository.Specification;
import com.videonasocialmedia.vimojo.repository.VimojoRepository;
import com.videonasocialmedia.vimojo.userfeatures.domain.model.UserFeatures;
import com.videonasocialmedia.vimojo.userfeatures.repository.datasource.UserFeaturesApi;
import com.videonasocialmedia.vimojo.userfeatures.repository.datasource.UserFeaturesLocal;
import com.videonasocialmedia.vimojo.userfeatures.repository.datasource.UserFeaturesMemoryCache;

import java.util.List;

import javax.inject.Inject;

/**
 * Repository for providing {@link UserFeatures} via repository pattern
 *
 * <p>This class handles saving and retrieving {@link UserFeatures}s from different data sources and merge
 * UserFeatures provided by them for returning results. This also handles asset retrieval from backend and
 * local storage, implicit policy local first</p>
 */
public class UserFeaturesRepository extends VimojoRepository<UserFeatures> {

  private final UserFeaturesApi apiDataSource;
  private final UserFeaturesLocal localDataSource;
  private final UserFeaturesMemoryCache memoryCacheDataSource;

  @Inject
  public UserFeaturesRepository(UserFeaturesApi userFeaturesApiDataSource,
                                UserFeaturesLocal userFeaturesLocalDataSource,
                                UserFeaturesMemoryCache userFeaturesMemoryCacheDataSource) {
    this.apiDataSource = userFeaturesApiDataSource;
    this.localDataSource = userFeaturesLocalDataSource;
    this.memoryCacheDataSource = userFeaturesMemoryCacheDataSource;
  }

  @Override
  public void add(UserFeatures item) {
    // 30/8/18 empty implementation. User features is not be added.
  }

  @Override
  public void add(Iterable<UserFeatures> items) {
    // 30/8/18 empty implementation. User features can not be iterated, only one UserFeature.
  }

  @Override
  public void update(UserFeatures item) {
    // Only add local user features, from backend to app
    localDataSource.update(item);
  }

  @Override
  public void remove(UserFeatures item) {
    // 30/8/18 empty implementation. User features can not be removed.
  }

  @Override
  public void remove(Specification specification) {
    // 30/8/18 empty implementation. User features can not be removed.
  }

  @Override
  public List<UserFeatures> query(Specification specification) {
    // 30/8/18 empty implementation. User features can not be listed, only one UserFeature.
    return null;
  }

  @Override
  public UserFeatures getById(String userId) {
    // Policy check first api data source
    UserFeatures apiFeatures = apiDataSource.getById(userId);
    if (apiFeatures != null) {
      return apiFeatures;
    }
    return getCurrentUserFeatures();
  }

  @Override
  public void remove(UserFeatures item, DeletePolicy policy) {
    // 30/8/18 empty implementation. User features can not be removed.
  }

  public UserFeatures getCurrentUserFeatures() {
    UserFeatures memoryCacheFeatures = memoryCacheDataSource.getCurrent();
    if( memoryCacheFeatures != null) {
      return memoryCacheFeatures;
    }
    UserFeatures localFeatures = localDataSource.get();
    return localFeatures;
  }

  public void setCurrentUserFeatures(UserFeatures userFeatures) {
    memoryCacheDataSource.setCurrent(userFeatures);
  }
}
