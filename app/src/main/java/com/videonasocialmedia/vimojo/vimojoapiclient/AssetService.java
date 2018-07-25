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

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

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

  @GET("project/{projectId}/asset")
  Call<List<AssetDto>> getProjectAssets();

  @GET
  @Streaming
  Call<ResponseBody> downloadAssetFile(String pathToDownload, AssetDto asset);

  @GET("asset/{assetId}")
  @Headers("Content-Type: application/json")
  Call<AssetDto> get(@Path("assetId") String id);
}
