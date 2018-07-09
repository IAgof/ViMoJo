/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.vimojoapiclient;

import com.videonasocialmedia.vimojo.vimojoapiclient.model.Asset;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

/**
 * Created by alvaro on 21/6/18.
 */

public interface AssetService {

  @Multipart
  @POST("project/{projectId}/asset")
  Call<Asset> uploadAsset(
      @Path("projectId") String projectId,
      // TODO(jliarte): 8/02/18 check if we can model the request body into a vimojoapiclient.model
      @PartMap() Map<String, RequestBody> partMap,
      @Part MultipartBody.Part file
  );

  @GET("project/{projectId}/asset")
  Call<List<Asset>> assetListToDownload();

  @GET
  @Streaming
  Call<ResponseBody> downloadAsset(String pathToDownload, Asset asset);
}
