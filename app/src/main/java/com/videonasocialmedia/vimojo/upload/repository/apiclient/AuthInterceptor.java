package com.videonasocialmedia.vimojo.upload.repository.apiclient;

import com.videonasocialmedia.vimojo.upload.model.Token;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by alvaro on 28/11/17.
 */

public class AuthInterceptor implements Interceptor {

  Token token;

  public AuthInterceptor(Token token) {
    this.token = token;
  }

  @Override
  public Response intercept(Chain chain) throws IOException {
    Request original = chain.request();
    Request.Builder requestBuilder = original.newBuilder()
        .header("Authorization", token.getToken())
        .method(original.method(), original.body());
    Request request = requestBuilder.build();
    return chain.proceed(request);
  }
}
