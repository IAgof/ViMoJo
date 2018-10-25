/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.vimojoapiclient;

/**
 * Created by jliarte on 18/7/18.
 */

import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.MediaDto;

import java.io.IOException;

import javax.inject.Inject;

import retrofit2.Response;

/**
 * Api client for media service.
 * <p>
 * <p>Handles Media vimojo API calls.</p>
 */
public class MediaApiClient extends VimojoApiClient {
  @Inject public MediaApiClient() {
  }

  private MediaService getMediaService(String accessToken) {
    return getService(MediaService.class, accessToken);
  }

//  public MediaDto addMedia(MediaDto mediaDto, String accessToken) throws VimojoApiException {
//    MediaService mediaService = getService(MediaService.class, accessToken);
//    try {
//      // TODO(jliarte): 11/07/18 set cut dto project, owner is set in backend
//      //                         set from Project (new entity)
//      String projectId = "defaultProject";
//      String compositionId = mediaDto.compositionId;
//      Response<MediaDto> response =
//              mediaService.addMedia(projectId, compositionId, mediaDto).execute();
//      if (response.isSuccessful()) {
//        return response.body();
//      } else {
//        parseError(response);
//      }
//    } catch (IOException ioException) {
//      if (BuildConfig.DEBUG) {
//        ioException.printStackTrace();
//      }
//      throw new VimojoApiException(-1, VimojoApiException.NETWORK_ERROR);
//    }
//    return null; // TODO(jliarte): 18/07/18 we should either return a MediaDto or throw error
//  }

  public MediaDto getById(String id, String accessToken) throws VimojoApiException {
    try {
      Response<MediaDto> response = getMediaService(accessToken).getById(id).execute();
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
    return null; // TODO(jliarte): 18/07/18 we should either return a MediaDto or throw error
  }

  public void remove(MediaDto mediaDto, String accessToken) throws VimojoApiException {
    try {
      Response<MediaDto> response =
              getMediaService(accessToken).remove(mediaDto.getUuid()).execute();
      if (response.isSuccessful()) {
        return;
      } else {
        parseError(response);
      }
    } catch (IOException ioException) {
      if (BuildConfig.DEBUG) {
        ioException.printStackTrace();
      }
      throw new VimojoApiException(-1, VimojoApiException.NETWORK_ERROR);
    }
  }

  public MediaDto update(MediaDto mediaDto, String accessToken) throws VimojoApiException {
    try {
      Response<MediaDto> response =
              getMediaService(accessToken).update(mediaDto.getUuid(), mediaDto).execute();
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
    throw new VimojoApiException(); // TODO(jliarte): 19/07/18 default unknown error - check when this path is reached
  }
}
