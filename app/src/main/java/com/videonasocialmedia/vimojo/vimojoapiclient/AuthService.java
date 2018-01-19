package com.videonasocialmedia.vimojo.vimojoapiclient;

/**
 * Created by jliarte on 11/01/18.
 */

import com.videonasocialmedia.vimojo.auth.repository.model.AuthTokenRequest;
import com.videonasocialmedia.vimojo.auth.repository.model.RegisterRequest;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.AuthToken;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Class describing auth services.
 */
public interface AuthService {
  @POST("user/")
  @Headers("Content-Type: application/json")
  Call<User> register(@Body RegisterRequest requestBody);

  @POST("login")
  @Headers("Content-Type: application/json")
  Call<AuthToken> getAuthToken(@Body AuthTokenRequest requestBody);
}
