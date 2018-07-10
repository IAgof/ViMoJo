/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.main.modules;

import android.content.Context;

import com.videonasocialmedia.vimojo.auth0.GetUserId;
import com.videonasocialmedia.vimojo.auth0.UserAuth0Helper;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.sync.AssetUploadQueue;
import com.videonasocialmedia.vimojo.vimojoapiclient.AssetApiClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by alvaro on 28/2/18.
 * Module upload to platform queue
 * Needed for injection queue and add testing.
 */

@Module
public class AssetUploadQueueModule {

  private final Context context;

  public AssetUploadQueueModule(VimojoApplication application) {
    this.context = application;
  }

  @Singleton @Provides
  AssetUploadQueue provideAssetUploadQueue(
          AssetApiClient assetApiClient, UserAuth0Helper userAuth0Helper, GetUserId getUserId) {
    return new AssetUploadQueue(context, assetApiClient, userAuth0Helper, getUserId);
  }

  @Provides
  AssetApiClient providesAssetApiClient() {
    return new AssetApiClient();
  }

  /*@Provides
  GetAuthToken providesGetAuthToken() {
    return new GetAuthToken();
  }*/

}
