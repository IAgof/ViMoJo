package com.videonasocialmedia.vimojo.vimojoapiclient;

/**
 * Created by jliarte on 8/02/18.
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.VimojoApiError;
import com.videonasocialmedia.vimojo.vimojoapiclient.rest.ServiceGenerator;
import java.io.IOException;
import retrofit2.Response;

/**
 * Parent Vimojo Api client with common methods for all vimojo api clients.
 */
class VimojoApiClient {
  private static final int INVALID_AUTH_CODE = 401;

  public VimojoApiClient() {

  }

  <T> T getService(Class<T> serviceClass) {
    return new ServiceGenerator(BuildConfig.API_BASE_URL).generateService(serviceClass);
  }

  <T> T getService(Class<T> serviceClass, String authToken) {
    return new ServiceGenerator(BuildConfig.API_BASE_URL).generateService(serviceClass, authToken);
  }

  <T> void parseError(Response<T> response) throws VimojoApiException {
    String apiErrorCode = "unknown error";
    int httpCode = response.code();
    if (response.errorBody() != null) {
      Gson gson = new GsonBuilder().create();
      try {
        VimojoApiError vimojoApiError = gson.fromJson(response.errorBody().string(),
                VimojoApiError.class);
        if (vimojoApiError != null && vimojoApiError.getError() != null
                && !vimojoApiError.getError().equals("")) {
          apiErrorCode = vimojoApiError.getError();
        }
      } catch (IOException ioException) {
        if (BuildConfig.DEBUG) {
          // TODO(jliarte): 12/01/18 check for occurrences
          ioException.printStackTrace();
        }
      } catch (JsonSyntaxException syntaxException) {
        if (BuildConfig.DEBUG) {
          // TODO(jliarte): should be managed as throw new VimojoApiException(httpCode, apiErrorCode);
          // but for now we let it go to capture 401 errors
          syntaxException.printStackTrace();
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
