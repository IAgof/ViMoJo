package com.videonasocialmedia.vimojo.vimojoapiclient;

/**
 * Created by jliarte on 8/01/18.
 */

import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.AuthTokenRequest;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.RegisterRequest;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.AuthToken;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.UserDto;

import retrofit2.Response;

import java.io.IOException;

/**
 * Api client for user authentication.
 *
 * <p>Handles user sign in and register calls.</p>
 */
public class AuthApiClient extends VimojoApiClient {
  public static final String REGISTER_ERROR_MISSING_REQUEST_PARAMETERS =
          "Unable to register, no user or email provided";
  public static final String REGISTER_ERROR_USER_ALREADY_EXISTS = "User already exists";
  public static final String REGISTER_ERROR_INTERNAL_SERVER_ERROR = "Unable to add the user";
  public static final String SIGNIN_ERROR_PASSWORD_MISSING =
          "Unable to login, no password provided";
  public static final String SIGNIN_ERROR_USER_MISSING = "Unable to login, no user provided";
  public static final String SIGNIN_ERROR_USER_NOT_FOUND = "Unable to find user";
  public static final String SIGNIN_ERROR_WRONG_PASSWORD = "Password does not match";
  public static final String SIGNIN_ERROR_INTERNAL_SERVER_ERROR = "Error checking password";

  /**
   * Make a user register call to users plaftform service.
   *
   * @param username user name for user account.
   * @param email email for user account. This will be the account identificator.
   * @param password password for user account.
   * @param checkBoxAcceptTermChecked user acceptance of privacy and policy terms.
   * @return the user response of the platform service
   * @throws VimojoApiException if an error has occurred in the call.
   */
  public UserDto register(String username, String email, String password, boolean checkBoxAcceptTermChecked)
          throws VimojoApiException {
    AuthService authService = getService(AuthService.class);
    RegisterRequest requestBody = new RegisterRequest(username, email, password,
        checkBoxAcceptTermChecked);
    try {
      Response<UserDto> response = authService.register(requestBody).execute();
      if (response.isSuccessful()) {
        return response.body();
      } else {
        parseError(response);
      }
    } catch (IOException ioException) {
      if (BuildConfig.DEBUG) {
        ioException.printStackTrace();
      }
      throw new VimojoApiException(-1, VimojoApiException.NETWORK_ERROR);
    }
    return null;
  }

  /**
   * Make a user auth call to users auth service.
   *
   * @param email email for user account. This will be the account identificator.
   * @param password password for user account.
   * @return the auth token response of the platform service for using in protected service calls.
   * @throws VimojoApiException if an error has occurred in the call.
   */
  public AuthToken signIn(String email, String password) throws VimojoApiException {
    AuthService authService = getService(AuthService.class);
    AuthTokenRequest requestBody = new AuthTokenRequest(email, password);
    try {
      Response<AuthToken> response = authService.getAuthToken(requestBody).execute();
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

}
