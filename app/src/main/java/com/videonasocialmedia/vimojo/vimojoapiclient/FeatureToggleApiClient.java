/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.vimojoapiclient;

/**
 * Created by alvaro on 30/8/18.
 */
import com.videonasocialmedia.vimojo.vimojoapiclient.model.FeatureToggleDto;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.UserFeaturesDto;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Response;


/**
 * Api client for features toggle service.
 * <p>
 * <p>Handles feature toggle vimojo API calls.</p>
 */
public class FeatureToggleApiClient extends VimojoApiClient {
  @Inject
  public FeatureToggleApiClient() {
  }

  public List<FeatureToggleDto> getAll(String accessToken, String userId) throws VimojoApiException {
    FeatureToggleService featureToggleService = getService(FeatureToggleService.class, accessToken);
    try {
      Response<List<FeatureToggleDto>> response =
              featureToggleService.getUserFeatures(userId).execute();
      if (response.isSuccessful()) {
        return response.body();
      } else {
        parseError(response);
      }
    } catch (IOException ioException) {
      throw new VimojoApiException(-1, VimojoApiException.NETWORK_ERROR);
    }
    return null;
  }

  public FeatureToggleDto getUserFeature(String accessToken, String userId, String featureName)
          throws VimojoApiException {
    FeatureToggleService featureToggleService = getService(FeatureToggleService.class, accessToken);
    try {
      Map<String, Object> query = new HashMap<>();
      query.put("featureName", featureName);
      Response<List<FeatureToggleDto>> response =
              featureToggleService.getUserFeature(userId, query).execute();
      if (response.isSuccessful()) {
        List<FeatureToggleDto> features = response.body();
        if (features.size() == 0) {
          return null;
        }
        return features.get(0);
      } else {
        parseError(response);
      }
    } catch (IOException ioException) {
      throw new VimojoApiException(-1, VimojoApiException.NETWORK_ERROR);
    }
    return null;
  }
}
