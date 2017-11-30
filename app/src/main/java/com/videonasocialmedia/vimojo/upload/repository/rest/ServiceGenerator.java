package com.videonasocialmedia.vimojo.upload.repository.rest;

import com.videonasocialmedia.vimojo.upload.model.Token;
import com.videonasocialmedia.vimojo.upload.repository.apiclient.AuthInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by alvaro on 28/11/17.
 */

public class ServiceGenerator {

  private OkHttpClient.Builder httpClientBuilder;
  private Retrofit.Builder retrofitBuilder;

  /**
   * Creates a ServiceGenerator with a default url
   */
  public ServiceGenerator() {
    this(null);
  }

  /**
   * Creates a ServiceGenerator
   *
   * @param ApiBaseUrl the url of the API
   */
  public ServiceGenerator(String ApiBaseUrl) {
    httpClientBuilder = new OkHttpClient.Builder();
    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    // set your desired log level
    logging.setLevel(HttpLoggingInterceptor.Level.BODY);
    httpClientBuilder.addInterceptor(logging);
    httpClientBuilder.connectTimeout(90, TimeUnit.SECONDS);
    httpClientBuilder.readTimeout(90, TimeUnit.SECONDS);
    retrofitBuilder = new Retrofit.Builder()
          .baseUrl(ApiBaseUrl)
          .addConverterFactory(GsonConverterFactory.create());
  }

  public <T> T generateService(Class<T> serviceClass) {

    OkHttpClient okClient = httpClientBuilder.build();

    Retrofit retrofit = retrofitBuilder.client(okClient).build();
    return retrofit.create(serviceClass);
  }

  public <T> T generateService(Class<T> serviceClass, final Token token) {
    if (token != null) {
      AuthInterceptor authInterceptor = new AuthInterceptor(token);
      httpClientBuilder.addInterceptor(authInterceptor);
    }

    OkHttpClient okClient = httpClientBuilder.build();

    Retrofit retrofit = retrofitBuilder.client(okClient).build();
    return retrofit.create(serviceClass);
  }

}
