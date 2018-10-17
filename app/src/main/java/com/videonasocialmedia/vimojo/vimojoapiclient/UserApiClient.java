package com.videonasocialmedia.vimojo.vimojoapiclient;

/**
 * Created by jliarte on 8/02/18.
 */


import com.videonasocialmedia.vimojo.vimojoapiclient.model.UserDto;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.UserId;

import java.io.IOException;
import java.util.HashMap;

import javax.inject.Inject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

/**
 * Api client for user service.
 * <p>
 * <p>Handles user vimojo API calls.</p>
 */
public class UserApiClient extends VimojoApiClient {

  @Inject
  public UserApiClient() {
  }

  /**
   * Make a user auth call to get user info
   *
   * @param authToken valid authToken
   * @param id        unique identification of user
   * @return the user response of the platform service
   * @throws VimojoApiException if an error has occurred in the call.
   */
  public UserDto getUser(String authToken, String id) throws VimojoApiException {
    UserService userService = getService(UserService.class, authToken);
    try {
      Response<UserDto> response = userService.getUser(id).execute();
      if (response.isSuccessful()) {
        return response.body();
      } else {
        parseError(response);
      }
    } catch (IOException ioException) {
      throw new VimojoApiException(-1, VimojoApiException.NETWORK_ERROR);
    }
    return null;
  }

  public UserId getUserId(String token, boolean prehisteric) throws VimojoApiException {
    UserService userService = getService(UserService.class, token);
    try {
      Response<UserId> response = userService.getUserId(String.valueOf(prehisteric)).execute();
      if (response.isSuccessful()) {
        return response.body();
      } else {
        parseError(response);
      }
    } catch (IOException e) {
      e.printStackTrace();
      throw new VimojoApiException(-1, VimojoApiException.NETWORK_ERROR);
    }
    return null;
  }
}
