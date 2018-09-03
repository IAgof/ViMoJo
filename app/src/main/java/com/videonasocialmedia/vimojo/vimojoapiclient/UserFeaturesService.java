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

import com.videonasocialmedia.vimojo.vimojoapiclient.model.UserFeaturesDto;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Class describing user features services.
 */
public interface UserFeaturesService {

  @GET("user/{id}/userfeatures")
  Call<UserFeaturesDto> getUserFeatures(@Path("id") String id);
}
