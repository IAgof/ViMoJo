/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.vimojoapiclient;

import com.videonasocialmedia.vimojo.vimojoapiclient.model.CompositionDto;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

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

  @POST("project/{projectId}/composition")
  @Headers("Content-Type: application/json")
  Call<CompositionDto> addComposition(@Path("projectId") String projectId,
                                      @Body CompositionDto compositionDto);

  @PUT("project/{projectId}/composition/{compositionId}")
  @Headers("Content-Type: application/json")
  Call<CompositionDto> updateComposition(@Path("projectId") String projectId,
                                         @Path("compositionId") String compositionId,
                                         @Body CompositionDto compositionDto);

  @GET("project/{projectId}/composition")
  @Headers("Content-Type: application/json")
  Call<List<CompositionDto>> getAll(@Path("projectId") String projectId,
                                    @QueryMap Map<String, Object> compositionFilter);

  @GET("project/{projectId}/composition/{compositionId}")
  @Headers("Content-Type: application/json")
  Call<CompositionDto> get(@Path("projectId") String projectId,
                           @Path("compositionId") String compositionId,
                           @QueryMap Map<String, Object> query);

  @DELETE("project/{projectId}/composition/{compositionId}")
  Call<CompositionDto> remove(@Path("projectId") String projectId,
                              @Path("compositionId") String compositionId,
                              @QueryMap Map<String, Object> query);
}
