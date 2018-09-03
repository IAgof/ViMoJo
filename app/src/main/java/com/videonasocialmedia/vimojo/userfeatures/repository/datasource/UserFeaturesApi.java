/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.userfeatures.repository.datasource;

/**
 * Created by alvaro on 30/8/18.
 */

import com.videonasocialmedia.vimojo.auth0.GetUserId;
import com.videonasocialmedia.vimojo.auth0.UserAuth0Helper;
import com.videonasocialmedia.vimojo.repository.Specification;
import com.videonasocialmedia.vimojo.repository.datasource.ApiDataSource;
import com.videonasocialmedia.vimojo.userfeatures.domain.model.UserFeatures;
import com.videonasocialmedia.vimojo.userfeatures.repository.datasource.mapper.UserFeaturesToUserFeaturesDtoMapper;
import com.videonasocialmedia.vimojo.vimojoapiclient.UserFeaturesApiClient;
import com.videonasocialmedia.vimojo.vimojoapiclient.VimojoApiException;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.UserFeaturesDto;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

/**
 * API DataSource for user features. Provide remote persistence of UserFeatures using vimojo API
 * via {@link UserFeaturesDto} class.
 */
public class UserFeaturesApi extends ApiDataSource<UserFeatures> {

  private UserFeaturesApiClient userFeaturesApiClient;
  private UserFeaturesToUserFeaturesDtoMapper mapper;

  @Inject
  protected UserFeaturesApi(UserAuth0Helper userAuth0Helper, GetUserId getUserId,
                            UserFeaturesApiClient userFeaturesApiClient,
                            UserFeaturesToUserFeaturesDtoMapper mapper) {
    super(userAuth0Helper, getUserId);
    this.userFeaturesApiClient = userFeaturesApiClient;
    this.mapper = mapper;
  }

  @Override
  public void add(UserFeatures item) {
    // 30/8/18 empty implementation. User features can not be added from app.
  }

  @Override
  public void add(Iterable<UserFeatures> items) {
    // 30/8/18 empty implementation. User features can not be added from app.
  }

  @Override
  public void update(UserFeatures item) {
    // 30/8/18 empty implementation. User features can not be updated from app.
  }

  @Override
  public void remove(UserFeatures item) {
    // 30/8/18 empty implementation. User features can not be remove from app.
  }

  @Override
  public void remove(Specification specification) {
    // 30/8/18 empty implementation. User features can not be updated from app.
  }

  @Override
  public List<UserFeatures> query(Specification specification) {
    // 30/8/18 empty implementation. User features can not be query list, UserFeatures is a unique class.
    return null;
  }

  @Override
  public UserFeatures getById(String id) {
    try {
      String accessToken = getApiAccessToken().get().getAccessToken();
      UserFeaturesDto userFeaturesDto = this.userFeaturesApiClient.getUserFeatures(accessToken, id);
      return mapper.reverseMap(userFeaturesDto);
    } catch (VimojoApiException apiError) {
      processApiError(apiError);
    } catch (InterruptedException | ExecutionException e) {
      // TODO(jliarte): 12/07/18 manage this error
      e.printStackTrace();
    }
    return null;
  }
}
