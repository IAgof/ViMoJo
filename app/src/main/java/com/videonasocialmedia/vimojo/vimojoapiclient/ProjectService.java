/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.vimojoapiclient;


import com.videonasocialmedia.vimojo.vimojoapiclient.model.Project;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by alvaro on 21/6/18.
 */

public interface ProjectService {
  @GET("project/{userId}")
  Call<Project> getMyProjects(@Path("userId") String userId);

  @GET("project")
  Call<List<Project>> getProjects();

  /**
  @GET("project/{projectId}")
  Call<List<Composition>> getProjectsById(@Path("projectId") String projectId);
  */
}
