package com.videonasocialmedia.vimojo.vimojoapiclient.auth;

/**
 * Created by jliarte on 8/01/18.
 */

import com.google.gson.Gson;

import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.auth.repository.model.AuthTokenRequest;
import com.videonasocialmedia.vimojo.auth.repository.model.RegisterRequest;
import com.videonasocialmedia.vimojo.vimojoapiclient.AuthService;
import com.videonasocialmedia.vimojo.vimojoapiclient.VimojoApiException;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.AuthToken;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.User;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.VimojoApiError;
import com.videonasocialmedia.vimojo.vimojoapiclient.rest.ServiceGenerator;

import retrofit2.Response;

import java.io.IOException;

/**
 * Api client for user authentication.
 *
 * <p>Handles user sign in and register calls.</p>
 */
public class VimojoUserAuthenticator {
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
  // TODO(jliarte): 12/01/18 consider moving
  private static final int INVALID_AUTH_CODE = 401;

  /**
   * Make a user register call to users plaftform service.
   *
   * @param email email for user account. This will be the account identificator.
   * @param password password for user account.
   * @param checkBoxAcceptTermChecked user acceptance of privacy and policy terms.
   * @return the user response of the platform service
   * @throws VimojoApiException if an error has occurred in the call.
   */
  public User register(String email, String password, boolean checkBoxAcceptTermChecked)
          throws VimojoApiException {
    AuthService authService = new ServiceGenerator(BuildConfig.API_BASE_URL)
            .generateService(AuthService.class);
    RegisterRequest requestBody = new RegisterRequest(email, password, checkBoxAcceptTermChecked);
    try {
      Response<User> response = authService.register(requestBody).execute();
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
    AuthService authService = new ServiceGenerator(BuildConfig.API_BASE_URL)
            .generateService(AuthService.class);
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

  private <T> void parseError(Response<T> response) throws VimojoApiException {
    String apiErrorCode = "unknown error";
    int httpCode = response.code();
    if (response.errorBody() != null) {
      Gson gson = new Gson();
      try {
        String errorBody = response.errorBody().string();
        VimojoApiError apiError = gson.fromJson(errorBody, VimojoApiError.class);
        if (apiError.getError() != null && !apiError.getError().equals("")) {
          apiErrorCode = apiError.getError();
        }
      } catch (IOException ioException) {
        if (BuildConfig.DEBUG) {
          // TODO(jliarte): 12/01/18 check for occurrences
          ioException.printStackTrace();
        }
      }
      if (httpCode == INVALID_AUTH_CODE) {
        //        throw new VimojoAuthApiException(execute.code(), apiErrorCode);
        throw new VimojoApiException(httpCode, VimojoApiException.UNAUTHORIZED);
      } else {
        throw new VimojoApiException(httpCode, apiErrorCode);
      }
    }
  }

}
