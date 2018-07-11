/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.vimojoapiclient;

import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.cut.domain.model.Project;

import java.io.IOException;

import retrofit2.Response;

/**
 * Created by alvaro on 22/6/18.
 */

public class CompositionApiClient extends VimojoApiClient {

  public Project uploadComposition(Project currentProject) throws VimojoApiException {

   // Gson gson = new Gson();
   // String projectJson = gson.toJson(currentProject);

    CompositionService compositionService = getService(CompositionService.class);
    //Project requestBody = currentProject;
    try {
      Response<Project> response = compositionService.uploadComposition("hola").execute();
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
