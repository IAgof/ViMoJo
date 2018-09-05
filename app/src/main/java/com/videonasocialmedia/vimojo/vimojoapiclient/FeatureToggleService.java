/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.vimojoapiclient;

/**
 * Created by alvaro on 30/8/18.
 */

import com.videonasocialmedia.vimojo.vimojoapiclient.model.FeatureToggleDto;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Class describing feature toggle vimojo API services.
 */
public interface FeatureToggleService {
  @GET("user/{userId}/feature")
  Call<List<FeatureToggleDto>> getUserFeatures(@Path("userId") String userId);

  @GET("user/{userId}/feature")
  Call<List<FeatureToggleDto>> getUserFeature(@Path("userId") String userId,
                                        @QueryMap Map<String, Object> query);
}
