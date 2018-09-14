package com.videonasocialmedia.vimojo.asset.repository.datasource;

/**
 * Created by jliarte on 18/07/18.
 */

import android.util.Log;

import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.vimojo.asset.domain.model.Asset;
import com.videonasocialmedia.vimojo.asset.repository.datasource.mapper.MediaToMediaDtoMapper;
import com.videonasocialmedia.vimojo.auth0.GetUserId;
import com.videonasocialmedia.vimojo.auth0.UserAuth0Helper;
import com.videonasocialmedia.vimojo.repository.Specification;
import com.videonasocialmedia.vimojo.repository.datasource.ApiDataSource;
import com.videonasocialmedia.vimojo.repository.datasource.BackgroundScheduler;
import com.videonasocialmedia.vimojo.vimojoapiclient.MediaApiClient;
import com.videonasocialmedia.vimojo.vimojoapiclient.VimojoApiException;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.MediaDto;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

/**
 * API DataSource for media. Provide remote persistence of {@link Media} using vimojo API
 * via {@link MediaDto} class.
 */
public class MediaApiDataSource extends ApiDataSource<Media> {
  private static final String LOG_TAG = MediaApiDataSource.class.getSimpleName();
  private final MediaApiClient mediaApiClient;
  private final AssetApiDataSource assetApiDataSource;
  private MediaToMediaDtoMapper mapper = new MediaToMediaDtoMapper();

  @Inject
  protected MediaApiDataSource(UserAuth0Helper userAuth0Helper, MediaApiClient mediaApiClient,
                               AssetApiDataSource assetApiDataSource,
                               GetUserId getUserId, BackgroundScheduler backgroundScheduler) {
    super(userAuth0Helper, getUserId, backgroundScheduler);
    this.mediaApiClient = mediaApiClient;
    this.assetApiDataSource = assetApiDataSource;
  }

  @Override
  public void add(Media item) {
    MediaDto mediaDtoToUpdate = mapper.map(item);
    schedule(() -> {
      updateMediaDto(item, mediaDtoToUpdate); // TODO(jliarte): 13/09/18 update to addMediaDto when uuid collissions are handled in backend and routes are different
      return null;
    });
  }

  @Override
  public void add(Iterable items) {
  }

  @Override
  public void update(Media item) {
    MediaDto mediaDtoToUpdate = mapper.map(item);
    schedule(() -> {
      updateMediaDto(item, mediaDtoToUpdate);
      return null;
    });
  }

  private void updateMediaDto(Media item, MediaDto mediaDtoToUpdate)
          throws ExecutionException, InterruptedException, VimojoApiException {
    String accessToken = getApiAccessToken().get().getAccessToken();
    MediaDto mediaDto = mediaApiClient.update(mediaDtoToUpdate, accessToken);
    // TODO(jliarte): 23/07/18 set asset by asset hash
    addOrUpdateAsset(item, mediaDto);
  }

  private void addOrUpdateAsset(Media media, MediaDto mediaDto) {
    // TODO(jliarte): 23/07/18 get project
    Asset asset = new Asset("confihack", media);
    asset.type = mediaDto.getMediaType();
    assetApiDataSource.update(asset);
  }

  @Override
  public void remove(Media item) {
    MediaDto mediaDto = mapper.map(item);
    schedule(() -> {
      removeMediaDto(mediaDto);
      return null;
    });
  }

  private void removeMediaDto(MediaDto mediaDto)
          throws ExecutionException, InterruptedException, VimojoApiException {
    String accessToken = getApiAccessToken().get().getAccessToken();
    mediaApiClient.remove(mediaDto, accessToken);
  }

  @Override
  public void remove(Specification specification) {

  }

  @Override
  public List query(Specification specification) {
    return null;
  }

  @Override
  public Media getById(String id) {
    try {
      String accessToken = getApiAccessToken().get().getAccessToken();
      MediaDto mediaDto = mediaApiClient.getById(id, accessToken);
      return mapper.reverseMap(mediaDto);
    } catch (InterruptedException | ExecutionException e) {
      // TODO(jliarte): 12/07/18 manage this error - unable to get access token
      Log.d(LOG_TAG, "media api data source get - unable to get access token");
      e.printStackTrace();
    } catch (VimojoApiException e) {
      e.printStackTrace();
    }
    return null; // TODO(jliarte): 20/07/18 check this path
  }

  public Media assignAssetIdToMedia(String mediaId, String assetId) {
    try {
      String accessToken = getApiAccessToken().get().getAccessToken();
      MediaDto mediaDto = mediaApiClient.getById(mediaId, accessToken);
      mediaDto.assetId = assetId;
      MediaDto res = mediaApiClient.update(mediaDto, accessToken);
      return mapper.reverseMap(res);
    } catch (InterruptedException | ExecutionException e) {
      // TODO(jliarte): 12/07/18 manage this error - unable to get access token
      Log.d(LOG_TAG, "media api data source assign assetId to media - unable to get access token");
      e.printStackTrace();
    } catch (VimojoApiException e) {
      e.printStackTrace();
    }
    return null; // TODO(jliarte): 20/07/18 check this path
  }

}
