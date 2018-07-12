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
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.CompositionDto;

import java.io.IOException;

import javax.inject.Inject;

import retrofit2.Response;

/**
 * Api client for composition/cut service.
 * <p>
 * <p>Handles composition/cut vimojo API calls.</p>
 */
public class CompositionApiClient extends VimojoApiClient {
  @Inject public CompositionApiClient() {
  }
  // TODO(jliarte): 11/07/18 check if rename

  public CompositionDto addComposition(CompositionDto compositionDto, String accessToken) throws VimojoApiException {
   // Gson gson = new Gson();
   // String projectJson = gson.toJson(currentProject);

    CompositionService compositionService = getService(CompositionService.class, accessToken);
    try {
      // TODO(jliarte): 11/07/18 set cut dto project, owner is set in backend
      //                         set from Project (new entity)
      String projectId = "defaultProject";
      Response<CompositionDto> response = compositionService.addComposition(projectId, compositionDto).execute();
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

  // TODO(jliarte): 11/07/18 implement this
  public Project updateComposition(Project currentProject) {
    return null;
  }
}
