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
import com.videonasocialmedia.vimojo.vimojoapiclient.model.UserFeaturesDto;

import java.io.IOException;

import javax.inject.Inject;

import retrofit2.Response;


/**
 * Api client for user features service.
 * <p>
 * <p>Handles user vimojo API calls.</p>
 */
public class UserFeaturesApiClient extends VimojoApiClient {

  @Inject
  public UserFeaturesApiClient() {

  }

  /**
   * Make a user auth call to get user info
   *
   * @param authToken valid authToken
   * @param id        unique identification of user
   * @return the user response of the platform service
   * @throws VimojoApiException if an error has occurred in the call.
   */
  public UserFeaturesDto getUserFeatures(String authToken, String id) throws VimojoApiException {
    UserFeaturesService userFeaturesService = getService(UserFeaturesService.class, authToken);
    try {
      Response<UserFeaturesDto> response = userFeaturesService.getUserFeatures(id).execute();
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
}
