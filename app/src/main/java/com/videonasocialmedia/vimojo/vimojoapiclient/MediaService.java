/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.vimojoapiclient;

/**
 * Created by jliarte on 18/7/18.
 */

import com.videonasocialmedia.vimojo.vimojoapiclient.model.MediaDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Class describing vimojo API media services.
 */
public interface MediaService {
  @POST("project/{projectId}/composition/{compositionId}/media")
  @Headers("Content-Type: application/json")
  Call<MediaDto> addMedia(
          @Path("projectId") String projectId, @Path("compositionId") String compositionId,
          @Body MediaDto mediaDto);

  @GET("media/{mediaId}")
  @Headers("Content-Type: application/json")
  Call<MediaDto> getById(@Path("mediaId") String id);

  @DELETE("media/{mediaId}")
  @Headers("Content-Type: application/json")
  Call<MediaDto> remove(@Path("mediaId") String uuid);

  @PUT("media/{mediaId}")
  @Headers("Content-Type: application/json")
  Call<MediaDto> update(@Path("mediaId") String uuid, @Body MediaDto mediaDto);
}
