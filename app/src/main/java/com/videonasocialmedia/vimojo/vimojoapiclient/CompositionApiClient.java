/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.vimojoapiclient;

/**
 * Created by alvaro on 22/6/18.
 */

import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.cut.domain.model.Project;

import java.io.IOException;

import retrofit2.Response;

/**
 * Api client for composition/cut service.
 * <p>
 * <p>Handles composition/cut vimojo API calls.</p>
 */
public class CompositionApiClient extends VimojoApiClient {
  // TODO(jliarte): 11/07/18 check if rename

  public Project addComposition(Project currentProject) throws VimojoApiException {
   // Gson gson = new Gson();
   // String projectJson = gson.toJson(currentProject);

    CompositionService compositionService = getService(CompositionService.class);
    //Project requestBody = currentProject;
    try {
      Response<Project> response = compositionService.addComposition("hola").execute();
      if (response.isSuccessful()) {
        return response.body();
      } else {
        parseError(response);
      }
    } catch (IOException ioException) {
      if (BuildConfig.DEBUG) {
        ioException.printStackTrace();
      }
      throw new VimojoApiException(-1, VimojoApiException.NETWORK_ERROR);
    }
    return null;
  }
}
