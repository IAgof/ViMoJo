package com.videonasocialmedia.vimojo.vimojoapiclient;

/**
 * Created by jliarte on 11/01/18.
 */

import com.videonasocialmedia.vimojo.vimojoapiclient.model.AuthTokenRequest;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.RegisterRequest;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.AuthToken;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.UserDto;

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
  Call<UserDto> register(@Body RegisterRequest requestBody);

  @POST("login")
  @Headers("Content-Type: application/json")
  Call<AuthToken> getAuthToken(@Body AuthTokenRequest requestBody);
}
