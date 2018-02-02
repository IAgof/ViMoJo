package com.videonasocialmedia.vimojo.vimojoapiclient.auth;

/**
 * Created by jliarte on 8/01/18.
 */

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.vimojoapiclient.UserService;
import com.videonasocialmedia.vimojo.vimojoapiclient.VideoService;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.AuthTokenRequest;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.RegisterRequest;
import com.videonasocialmedia.vimojo.vimojoapiclient.AuthService;
import com.videonasocialmedia.vimojo.vimojoapiclient.VimojoApiException;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.AuthToken;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.User;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.VideoResponse;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.VimojoApiError;
import com.videonasocialmedia.vimojo.vimojoapiclient.rest.ServiceGenerator;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

import java.io.File;
import java.io.IOException;

import static com.videonasocialmedia.vimojo.vimojoapiclient.ApiConstants.MIME_TYPE_VIDEO;
import static com.videonasocialmedia.vimojo.vimojoapiclient.ApiConstants.MULTIPART_NAME_DATA;

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
   * @param username user name for user account.
   * @param email email for user account. This will be the account identificator.
   * @param password password for user account.
   * @param checkBoxAcceptTermChecked user acceptance of privacy and policy terms.
   * @return the user response of the platform service
   * @throws VimojoApiException if an error has occurred in the call.
   */
  public User register(String username, String email, String password, boolean checkBoxAcceptTermChecked)
          throws VimojoApiException {
    AuthService authService = new ServiceGenerator(BuildConfig.API_BASE_URL)
            .generateService(AuthService.class);
    RegisterRequest requestBody = new RegisterRequest(username, email, password,
        checkBoxAcceptTermChecked);
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

  /**
   * Make a user auth call to get user info
   *
   * @param token valid token
   * @param id unique identification of user
   * @return the user response of the platform service
   * @throws VimojoApiException if an error has occurred in the call.
   */
  public User getUser(String token, String id) throws VimojoApiException {

    UserService userService = new ServiceGenerator(BuildConfig.API_BASE_URL)
            .generateService(UserService.class, token);
    try {
      Response<User>  response = userService.getUser(id).execute();
      if(response.isSuccessful()) {
        return response.body();
      } else {
        parseError(response);
      }
    } catch (IOException ioException) {
      throw new VimojoApiException(-1, VimojoApiException.NETWORK_ERROR);
    }
    return null;
  }

  /**
   * Make a upload video call to send video to platform
   *
   * @param authToken valid token
   * @param mediaPath Absolute path, video to upload
   * @param description Description of video
   * @return the video upload response of the platform service
   * @throws VimojoApiException if an error has occurred in the call.
   */
  public VideoResponse uploadVideo(String authToken, String mediaPath, String description)
          throws VimojoApiException{
    // create upload service client
    VideoService videoService = new ServiceGenerator(BuildConfig.API_BASE_URL)
            .generateService(VideoService.class, authToken);

    File file = new File(mediaPath);

    RequestBody requestFile = RequestBody
            .create(okhttp3.MediaType.parse(MIME_TYPE_VIDEO), file);

    // MultipartBody.Part is used to send also the actual file name
    MultipartBody.Part body =
            MultipartBody.Part.createFormData(MULTIPART_NAME_DATA, file.getName(), requestFile);

    // add another part within the multipart request
    RequestBody requestBody =
            RequestBody.create(
                    okhttp3.MultipartBody.FORM, description);

    try {
      Response<VideoResponse> response = videoService.uploadVideo(requestBody, body).execute();
      if(response.isSuccessful()) {
        return response.body();
      } else {
        parseError(response);
      }
    } catch (IOException e) {
      e.printStackTrace();
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
