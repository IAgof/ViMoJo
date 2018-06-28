package com.videonasocialmedia.vimojo.vimojoapiclient;

/**
 * Created by jliarte on 8/02/18.
 */

import android.content.Context;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.storage.SecureCredentialsManager;
import com.auth0.android.authentication.storage.SharedPreferencesStorage;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.User;

import java.io.IOException;

import javax.inject.Inject;

import retrofit2.Response;

/**
 * Api client for user service.
 * <p>
 * <p>Handles user details calls.</p>
 */
public class UserApiClient extends VimojoApiClient {

  private Auth0 account;
  private AuthenticationAPIClient authenticator;
  private SecureCredentialsManager manager;

  public UserApiClient(Context context) {
    account = new Auth0(context);
    //Configure the account in OIDC conformant mode
    account.setOIDCConformant(true);
    authenticator = new AuthenticationAPIClient(account);
    manager = new SecureCredentialsManager(context, authenticator,
        new SharedPreferencesStorage(context));
  }

  /**
   * Make a user auth call to get user info
   *
   * @param authToken valid authToken
   * @param id        unique identification of user
   * @return the user response of the platform service
   * @throws VimojoApiException if an error has occurred in the call.
   */
  public User getUser(String authToken, String id) throws VimojoApiException {
    UserService userService = getService(UserService.class, authToken);
    try {
      Response<User> response = userService.getUser(id).execute();
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

  public String getUserId(String token) throws VimojoApiException {
    UserService userService = getService(UserService.class, token);
    try {
      Response<String> response = userService.getUserId(token).execute();
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

  public void signOut() {
    manager.clearCredentials();
  }

  public boolean isLogged() {
    return manager.hasValidCredentials();
  }

  public AuthenticationAPIClient getAuthenticator() {
    return authenticator;
  }

  public SecureCredentialsManager getManager() {
    return manager;
  }
}
