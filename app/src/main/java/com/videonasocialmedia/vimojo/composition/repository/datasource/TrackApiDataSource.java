package com.videonasocialmedia.vimojo.composition.repository.datasource;

/**
 * Created by jliarte on 14/09/18.
 */

import android.util.Log;

import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.auth0.GetUserId;
import com.videonasocialmedia.vimojo.auth0.UserAuth0Helper;
import com.videonasocialmedia.vimojo.composition.repository.datasource.mapper.TrackToTrackDtoMapper;
import com.videonasocialmedia.vimojo.repository.Specification;
import com.videonasocialmedia.vimojo.repository.datasource.ApiDataSource;
import com.videonasocialmedia.vimojo.repository.datasource.BackgroundScheduler;
import com.videonasocialmedia.vimojo.vimojoapiclient.VimojoApiException;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.TrackDto;
import com.videonasocialmedia.vimojo.vimojoapiclient.TrackApiClient;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

/**
 * API DataSource for traks. Provide remote persistence of {@link Track} using vimojo API
 * via {@link TrackDto} class.
 */
public class TrackApiDataSource extends ApiDataSource<Track> {
  private static final String LOG_TAG = TrackApiDataSource.class.getSimpleName();
  private TrackToTrackDtoMapper mapper;
  private TrackApiClient trackApiClient;

  @Inject
  protected TrackApiDataSource(UserAuth0Helper userAuth0Helper, GetUserId getUserId,
                               BackgroundScheduler backgroundScheduler,
                               TrackToTrackDtoMapper mapper, TrackApiClient trackApiClient) {
    super(userAuth0Helper, getUserId, backgroundScheduler);
    this.mapper = mapper;
    this.trackApiClient = trackApiClient;
  }

  @Override
  public void add(Track item) {

  }

  @Override
  public void add(Iterable<Track> items) {

  }

  @Override
  public void update(Track item) {
    TrackDto trackDto = mapper.map(item);
    schedule(() -> {
      updateTrackDto(item, trackDto);
      return null;
    });
  }

  private TrackDto updateTrackDto(Track item, TrackDto trackDto)
          throws ExecutionException, InterruptedException, VimojoApiException {
    String accessToken = getApiAccessToken().get().getAccessToken();
    String userId = getUserId();
    TrackDto updatedTrack = this.trackApiClient.updateTrack(trackDto, accessToken);
    Log.d(LOG_TAG, "Composition updated with platform!");
    return updatedTrack;
  }

  @Override
  public void remove(Track item) {
    String itemId = item.getUuid();
    schedule(() -> {
      removeTrackById(itemId);
      return null;
    });
  }

  private TrackDto removeTrackById(String itemId) throws ExecutionException,
          InterruptedException, VimojoApiException {
    String accessToken = getApiAccessToken().get().getAccessToken();
    return this.trackApiClient.remove(itemId, accessToken);
  }

  @Override
  public void remove(Specification specification) {

  }

  @Override
  public List<Track> query(Specification specification) {
    return null;
  }

  @Override
  public Track getById(String id) {
    return null;
  }
}
