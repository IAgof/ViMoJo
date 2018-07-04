package com.videonasocialmedia.vimojo.vimojoapiclient;

/**
 * Created by alvaro on 25/01/18.
 */

import com.videonasocialmedia.vimojo.vimojoapiclient.model.User;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.UserId;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Class describing user services.
 */
public interface UserService {
  @GET("user/{id}")
  Call<User> getUser(@Path("id") String id);

  @GET("user/getId")
  Call<UserId> getUserId();
}
