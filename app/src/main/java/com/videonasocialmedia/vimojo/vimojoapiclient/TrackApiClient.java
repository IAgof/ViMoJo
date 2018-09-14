package com.videonasocialmedia.vimojo.vimojoapiclient;

/**
 * Created by jliarte on 14/09/18.
 */

import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.TrackDto;

import java.io.IOException;

import javax.inject.Inject;

import retrofit2.Response;

/**
 * Api client for track service.
 *
 * <p>Handles Track vimojo API calls.</p>
 */
public class TrackApiClient extends VimojoApiClient {
  @Inject public TrackApiClient() {
  }

  private TrackService getTrackService(String accessToken) {
    return getService(TrackService.class, accessToken);
  }

  public TrackDto updateTrack(TrackDto trackDto, String accessToken) throws VimojoApiException {
    try {
      Response<TrackDto> response =
              getTrackService(accessToken).update(trackDto.getUuid(), trackDto).execute();
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

  public TrackDto remove(String id, String accessToken) throws VimojoApiException {
    try {
      Response<TrackDto> response =
              getTrackService(accessToken).remove(id).execute();
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
