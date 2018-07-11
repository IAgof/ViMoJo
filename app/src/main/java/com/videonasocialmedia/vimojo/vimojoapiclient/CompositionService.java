/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.vimojoapiclient;

import com.videonasocialmedia.vimojo.cut.domain.model.Project;

import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by alvaro on 21/6/18.
 */

/**
 * Class describing cut/composition services.
 */
public interface CompositionService {
  // TODO(jliarte): 11/07/18 check if rename
  /**
  @GET("project/{userId}")
  Call<Project> getMyProjects(@Path("userId") String userId);

  @GET("project")
  Call<List<Project>> getProjects();

  @GET("project/{projectId}")
  Call<List<Composition>> getProjectsById(@Path("projectId") String projectId);
  */

  @POST("project/cut")
  @Headers("Content-Type: application/json")
  Call<Project> addComposition(String currentProject);

}
