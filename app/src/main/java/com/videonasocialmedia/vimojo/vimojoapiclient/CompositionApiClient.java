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
import com.videonasocialmedia.vimojo.vimojoapiclient.model.CompositionDto;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.CompositionQuery;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  private CompositionService getCompositionService(String accessToken) {
    return getService(CompositionService.class, accessToken);
  }

  public CompositionDto addComposition(CompositionDto compositionDto, String accessToken)
          throws VimojoApiException {
    CompositionService compositionService = getCompositionService(accessToken);
    try {
      // TODO(jliarte): 11/07/18 set composition dto project, owner is set in backend
      //                         set from Project (new entity)
      String projectId = "defaultProject";
      Response<CompositionDto> response =
              compositionService.addComposition(projectId, compositionDto).execute();
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
    return null; // TODO(jliarte): 18/07/18 we should either return a compositionDto or throw an exception!
  }

  public CompositionDto updateComposition(CompositionDto compositionDto, String accessToken)
          throws VimojoApiException {
    CompositionService compositionService = getCompositionService(accessToken);
    try {
      // TODO(jliarte): 11/07/18 set composition dto project, owner is set in backend
      //                         set from Project (new entity)
      String projectId = "defaultProject";
      Response<CompositionDto> response = compositionService
              .updateComposition(projectId, compositionDto.getUuid(), compositionDto).execute();
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
    return null; // TODO(jliarte): 18/07/18 we should either return a compositionDto or throw an exception!
  }

  public List<CompositionDto> getAll(String accessToken) throws VimojoApiException {
    CompositionQuery query = CompositionQuery.Builder.create()
            .withOrderBy("modification_date", false).withCascade(true).build();
    return getAll(query, accessToken);
  }

  private List<CompositionDto> getAll(CompositionQuery query, String accessToken)
          throws VimojoApiException {
    CompositionService compositionService = getCompositionService(accessToken);
    try {
      // TODO(jliarte): 11/07/18 set composition dto project
      String projectId = "defaultProject";
      Map<String, Object> queryAsMap = query.toMap();
      Response<List<CompositionDto>> response = compositionService
              .getAll(projectId, queryAsMap).execute();
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
    return Collections.emptyList(); // TODO(jliarte): 18/07/18 we should either return a list of compositionDto or throw an exception!
  }

  public CompositionDto get(String id, String accessToken) throws VimojoApiException {
    CompositionService compositionService = getCompositionService(accessToken);
    try {
      // TODO(jliarte): 11/07/18 set composition dto project
      String projectId = "defaultProject";
      Map<String, Object> query = new HashMap<>();
      query.put("cascade", "true"); // TODO(jliarte): 8/08/18 extract param?
      Response<CompositionDto> response = compositionService
              .get(projectId, id, query).execute();
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
    return null; // TODO(jliarte): 18/07/18 we should either return a compositionDto or throw an exception!
  }
}
