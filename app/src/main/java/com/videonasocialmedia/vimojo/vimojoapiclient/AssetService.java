/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.vimojoapiclient;

import com.videonasocialmedia.vimojo.vimojoapiclient.model.Asset;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

/**
 * Created by alvaro on 21/6/18.
 */

public abstract class AssetService {

  @Multipart
  @POST("asset")
  abstract Call<Asset> uploadAsset(
      // TODO(jliarte): 8/02/18 check if we can model the request body into a vimojoapiclient.model
      @PartMap() Map<String, RequestBody> partMap,
      @Part MultipartBody.Part file
  );
}
