package com.videonasocialmedia.vimojo.vimojoapiclient.rest;

import com.videonasocialmedia.vimojo.vimojoapiclient.AuthInterceptor;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.AuthToken;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import retrofit2.Retrofit;
import retrofit2.adapter.guava.GuavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by alvaro on 28/11/17.
 */

public class ServiceGenerator {

  public static final String BEARER_TOKEN = "Bearer";
  private OkHttpClient.Builder httpClientBuilder;
  private Retrofit.Builder retrofitBuilder;

  /**
   * Creates a ServiceGenerator with default url.
   */
  public ServiceGenerator() {
    this(null);
  }

  /**
   * Creates a ServiceGenerator for a given service URL.
   *
   * @param apiBaseUrl the url of the API
   */
  public ServiceGenerator(String apiBaseUrl) {
    httpClientBuilder = new OkHttpClient.Builder();
    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    // set your desired log level
    logging.setLevel(HttpLoggingInterceptor.Level.BODY);
    httpClientBuilder.addInterceptor(logging);
    httpClientBuilder.connectTimeout(90, TimeUnit.SECONDS);
    httpClientBuilder.readTimeout(90, TimeUnit.SECONDS);
    retrofitBuilder = new Retrofit.Builder()
          .baseUrl(apiBaseUrl)
          .addConverterFactory(GsonConverterFactory.create())
          .addCallAdapterFactory(GuavaCallAdapterFactory.create());
  }

  /**
   * Generate a service client for a service class.
   *
   * @param serviceClass class describing the service client to generate.
   * @param <T> the class of described service
   * @return the service instance
   */
  public <T> T generateService(Class<T> serviceClass) {
    OkHttpClient okClient = httpClientBuilder.build();

    Retrofit retrofit = retrofitBuilder.client(okClient).build();
    return retrofit.create(serviceClass);
  }

  /**
   * Generate a service client for a service class that requires user authentication.
   *
   * @param serviceClass class describing the service client to generate.
   * @param authToken the auth token for use in the service calls.
   * @param <T> the class of described service
   * @return the service instance
   */
  public <T> T generateService(Class<T> serviceClass, final String authToken) {
    if (authToken != null) {
      String bearerToken = BEARER_TOKEN + " " + authToken;
      AuthInterceptor authInterceptor = new AuthInterceptor(bearerToken);
      httpClientBuilder.addInterceptor(authInterceptor);
    }

    OkHttpClient okClient = httpClientBuilder.build();

    Retrofit retrofit = retrofitBuilder.client(okClient).build();
    return retrofit.create(serviceClass);
  }

}
