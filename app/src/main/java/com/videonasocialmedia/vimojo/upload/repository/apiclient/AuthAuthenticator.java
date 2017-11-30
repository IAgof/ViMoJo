package com.videonasocialmedia.vimojo.upload.repository.apiclient;

import com.videonasocialmedia.vimojo.upload.repository.localsource.CachedToken;
import com.videonasocialmedia.vimojo.upload.repository.rest.ServiceGenerator;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Created by alvaro on 28/11/17.
 */

public class AuthAuthenticator implements Authenticator {
  @Override
  public Request authenticate(Route route, Response response) throws IOException {
    VimojoApi vimojoApi = new ServiceGenerator().generateService(VimojoApi.class);
    // Token newToken= authClient.refreshToken();
    Request.Builder builder = response.request().newBuilder();
    if (!CachedToken.hasToken()) {
      // TODO(javi.cabanas): 15/6/16 refresh token
      //builder.addHeader("Authorization", "fakeToken");
    } else {
      builder.addHeader("Authorization", CachedToken.getToken().getToken());
    }
    return builder.build();
  }
}

