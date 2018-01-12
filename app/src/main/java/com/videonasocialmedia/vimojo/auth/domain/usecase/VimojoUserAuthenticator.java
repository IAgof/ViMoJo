package com.videonasocialmedia.vimojo.auth.domain.usecase;

/**
 * Created by jliarte on 8/01/18.
 */

import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.auth.repository.datasource.AuthClient;
import com.videonasocialmedia.vimojo.auth.repository.model.AuthTokenRequest;
import com.videonasocialmedia.vimojo.auth.repository.model.RegisterRequest;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.AuthToken;
import com.videonasocialmedia.vimojo.vimojoapiclient.rest.ServiceGenerator;

import java.io.IOException;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Use case for user authentication.
 * <p>
 * Handles user sign in and register.
 */
public class VimojoUserAuthenticator {
  public enum RegisterErrorCauses {
    NETWORK_ERROR, UNKNOWN_ERROR, USER_ALREADY_EXISTS, INVALID_EMAIL, MISSING_REQUEST_PARAMETERS,
    INVALID_PASSWORD,
  }

  public enum SignInErrorCauses {
    NETWORK_ERROR, CREDENTIALS_EXPIRED, UNKNOWN_ERROR, CREDENTIALS_UNKNOWN
  }

  public boolean userIsLoggedIn() {
    // TODO(jliarte): 11/01/18 implement this method
    return false;
  }

  public void register(String email, String password, boolean checkBoxAcceptTermChecked,
                       final RegisterListener registerListener) {
    AuthClient authClient = new ServiceGenerator(BuildConfig.API_BASE_URL)
            .generateService(AuthClient.class);
    RegisterRequest requestBody = new RegisterRequest(email, password, checkBoxAcceptTermChecked);
    authClient.register(requestBody).enqueue(new Callback<Map<String, String>>() {
      @Override
      public void onResponse(Call<Map<String, String>> call,
                             Response<Map<String, String>> response) {
        if (response.code() != 404) {
          Map<String, String> responseBody = response.body();
          RegisterErrorCauses cause = RegisterErrorCauses.UNKNOWN_ERROR;
          if (responseBody.containsKey("error")) {
            // TODO(jliarte): 12/01/18 map new error causes
            switch (responseBody.get("error")) {
              case "Missing request parameters":
                cause = RegisterErrorCauses.MISSING_REQUEST_PARAMETERS;
                break;
              case "Password too short. Type at least 6 characters":
                cause = RegisterErrorCauses.INVALID_PASSWORD;
                break;
              case "Email not valid":
                cause = RegisterErrorCauses.INVALID_EMAIL;
                break;
              case "User already exists":
                cause = RegisterErrorCauses.USER_ALREADY_EXISTS;
                break;
            }
            registerListener.onRegisterError(cause);
          } else {
            registerListener.onRegisterSuccess();
          }
        } else {
          // (jliarte): 12/01/18 we should never get 404
          registerListener.onRegisterError(RegisterErrorCauses.UNKNOWN_ERROR);
        }
      }

      @Override
      public void onFailure(Call<Map<String, String>> call, Throwable t) {
        registerListener.onRegisterError(RegisterErrorCauses.NETWORK_ERROR);
      }
    });
  }

  public void signIn(String email, String password, final SignInListener signInListener) {
    AuthClient authClient = new ServiceGenerator(BuildConfig.API_BASE_URL)
            .generateService(AuthClient.class);
    AuthTokenRequest requestBody = new AuthTokenRequest(email, password);
    authClient.getAuthToken(requestBody).enqueue(new Callback<AuthToken>() {
      @Override
      public void onResponse(Call<AuthToken> call, Response<AuthToken> response) {
        AuthToken authToken = response.body();
        if (authToken != null) {
          // TODO(jliarte): 11/01/18 do it in the listener?
//          CachedToken.setToken(authToken);
          signInListener.onSignInSuccess(authToken);
        } else {
          SignInErrorCauses error_code = SignInErrorCauses.UNKNOWN_ERROR;
          if (response.code() == 404) {
            try {
              String errorString = response.errorBody().string();
              if (errorString.contains("Unable to find user")) {
                error_code = SignInErrorCauses.CREDENTIALS_UNKNOWN;
              }
            } catch (IOException e) {
            }
          }
          signInListener.onSignInError(error_code);
        }
      }

      @Override
      public void onFailure(Call<AuthToken> call, Throwable t) {
        signInListener.onSignInError(SignInErrorCauses.NETWORK_ERROR);
      }
    });
  }

  public interface RegisterListener {
    void onRegisterError(RegisterErrorCauses registerErrorCauses);

    void onRegisterSuccess();
  }

  public interface SignInListener {
    void onSignInError(SignInErrorCauses signInErrorCauses);

    void onSignInSuccess(AuthToken authToken);
  }
}
