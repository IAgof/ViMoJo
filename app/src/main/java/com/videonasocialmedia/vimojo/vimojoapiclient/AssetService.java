/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.vimojoapiclient;

/**
 * Created by alvaro on 21/6/18.
 */

import com.videonasocialmedia.vimojo.vimojoapiclient.model.AssetDto;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

/**
 * Class describing asset services in vimojo platform.
 */
public interface AssetService {
  @Multipart
  @POST("project/{projectId}/asset")
  Call<AssetDto> addAsset(
      @Path("projectId") String projectId,
      // TODO(jliarte): 8/02/18 check if we can model the request body into a vimojoapiclient.model
      @PartMap() Map<String, RequestBody> partMap,
      @Part MultipartBody.Part file
  );
}
