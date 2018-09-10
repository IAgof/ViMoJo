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

import com.birbit.android.jobqueue.JobManager;
import com.videonasocialmedia.vimojo.auth0.GetUserId;
import com.videonasocialmedia.vimojo.auth0.UserAuth0Helper;
import com.videonasocialmedia.vimojo.repository.Specification;
import com.videonasocialmedia.vimojo.repository.datasource.ApiDataSource;
import com.videonasocialmedia.vimojo.featuresToggles.domain.model.FeatureToggle;
import com.videonasocialmedia.vimojo.featuresToggles.repository.datasource.mapper.FeatureToggleToFeatureToggleDtoMapper;
import com.videonasocialmedia.vimojo.vimojoapiclient.FeatureToggleApiClient;
import com.videonasocialmedia.vimojo.vimojoapiclient.VimojoApiException;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.FeatureToggleDto;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.UserFeaturesDto;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

/**
 * API DataSource for user features. Provide remote persistence of UserFeatures using vimojo API
 * via {@link UserFeaturesDto} class.
 */
public class FeatureApiDataSource extends ApiDataSource<FeatureToggle> {
  private FeatureToggleApiClient featureToggleApiClient;
  private FeatureToggleToFeatureToggleDtoMapper mapper;

  @Inject
  protected FeatureApiDataSource(UserAuth0Helper userAuth0Helper, GetUserId getUserId,
                                 FeatureToggleApiClient featureToggleApiClient,
                                 FeatureToggleToFeatureToggleDtoMapper mapper,
                                 JobManager jobManager) {
    super(userAuth0Helper, getUserId, jobManager);
    this.featureToggleApiClient = featureToggleApiClient;
    this.mapper = mapper;
  }

  @Override
  public void add(FeatureToggle item) {
    // 30/8/18 empty implementation. User features can not be added from app.
  }

  @Override
  public void add(Iterable<FeatureToggle> items) {
    // 30/8/18 empty implementation. User features can not be added from app.
  }

  @Override
  public void update(FeatureToggle item) {
    // 30/8/18 empty implementation. User features can not be updated from app.
  }

  @Override
  public void remove(FeatureToggle item) {
    // 30/8/18 empty implementation. User features can not be remove from app.
  }

  @Override
  public void remove(Specification specification) {
    // 30/8/18 empty implementation. User features can not be updated from app.
  }

  @Override
  public List<FeatureToggle> query(Specification specification) {
    // 30/8/18 empty implementation. User features can not be query list, UserFeatures is a unique class.
    return null;
  }

  @Override
  public FeatureToggle getById(String id) {
    try {
      String accessToken = getApiAccessToken().get().getAccessToken();
      String userId = getUserId();
      FeatureToggleDto featureToggleDto = this.featureToggleApiClient.getUserFeature(accessToken, userId, id);
      return mapper.reverseMap(featureToggleDto);
    } catch (VimojoApiException apiError) {
      processApiError(apiError);
    } catch (InterruptedException | ExecutionException e) {
      // TODO(jliarte): 12/07/18 manage this error
      e.printStackTrace();
    }
    return null;
  }

  // TODO(jliarte): 4/09/18 pull member up
  public List<FeatureToggle> getAll() {
    try {
      String accessToken = getApiAccessToken().get().getAccessToken();
      String userId = getUserId();
      List<FeatureToggleDto> features = this.featureToggleApiClient.getAll(accessToken, userId);
      return (List<FeatureToggle>) mapper.reverseMap(features);
    } catch (VimojoApiException apiError) {
      processApiError(apiError);
    } catch (InterruptedException | ExecutionException e) {
      // TODO(jliarte): 12/07/18 manage this error
      e.printStackTrace();
    }
    return Collections.emptyList();
  }
}
