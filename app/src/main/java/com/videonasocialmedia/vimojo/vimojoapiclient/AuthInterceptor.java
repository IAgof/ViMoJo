package com.videonasocialmedia.vimojo.vimojoapiclient;

import com.videonasocialmedia.vimojo.vimojoapiclient.model.AuthToken;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by alvaro on 28/11/17.
 */

/**
 * Class for chaining auth calls for restricted api calls
 */
public class AuthInterceptor implements Interceptor {
  AuthToken authToken;

  public AuthInterceptor(AuthToken authToken) {
    this.authToken = authToken;
  }

  @Override
  public Response intercept(Chain chain) throws IOException {
    Request original = chain.request();
    Request.Builder requestBuilder = original.newBuilder()
        .header("Authorization", authToken.getToken())
        .method(original.method(), original.body());
    Request request = requestBuilder.build();
    return chain.proceed(request);
  }
}
