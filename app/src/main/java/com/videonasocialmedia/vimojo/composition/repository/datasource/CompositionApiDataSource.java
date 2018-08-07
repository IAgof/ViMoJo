package com.videonasocialmedia.vimojo.composition.repository.datasource;

/**
 * Created by jliarte on 12/07/18.
 */

import android.util.Log;

import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.asset.domain.model.Asset;
import com.videonasocialmedia.vimojo.asset.repository.datasource.AssetApiDataSource;
import com.videonasocialmedia.vimojo.asset.repository.datasource.mapper.AssetToAssetDtoMapper;
import com.videonasocialmedia.vimojo.auth0.UserAuth0Helper;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.repository.datasource.mapper.CompositionToCompositionDtoMapper;
import com.videonasocialmedia.vimojo.repository.Specification;
import com.videonasocialmedia.vimojo.repository.datasource.ApiDataSource;
import com.videonasocialmedia.vimojo.vimojoapiclient.CompositionApiClient;
import com.videonasocialmedia.vimojo.vimojoapiclient.VimojoApiException;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.CompositionDto;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.MediaDto;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.TrackDto;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

/**
 * API DataSource for projects. Provide remote persistence of Projects using vimojo API
 * via {@link com.videonasocialmedia.vimojo.vimojoapiclient.model.ProjectDto} class.
 */
public class CompositionApiDataSource extends ApiDataSource<Project> {
  private static final String LOG_TAG = CompositionApiDataSource.class.getSimpleName();
  private CompositionApiClient compositionApiClient;

  private final CompositionToCompositionDtoMapper mapper = new CompositionToCompositionDtoMapper();
  private final AssetToAssetDtoMapper assetMapper = new AssetToAssetDtoMapper();
  private final AssetApiDataSource assetApiDataSource;

  @Inject
  public CompositionApiDataSource(CompositionApiClient compositionApiClient,
                                  UserAuth0Helper userAuth0Helper,
                                  AssetApiDataSource assetApiDataSource) {
    super(userAuth0Helper);
    this.compositionApiClient = compositionApiClient;
    this.assetApiDataSource = assetApiDataSource;
  }

  @Override
  public void add(Project item) {
    // create composition -> create track -> create media -> (mediaId)link with asset(assetID) <- asset upload
    try {
      String accessToken = getApiAccessToken().get().getAccessToken();
      CompositionDto createdComposition = this.compositionApiClient
              .addComposition(mapper.map(item), accessToken);
      updateTrackMediaAssets(item, createdComposition);
      Log.d(LOG_TAG, "Composition added to platform!");
    } catch (VimojoApiException apiError) {
      processApiError(apiError);
    } catch (InterruptedException | ExecutionException e) {
      // TODO(jliarte): 12/07/18 manage this error
      e.printStackTrace();
    }
  }

  /**
   * Update media assets for compositionDto tracks medias.
   *
   * @param project
   * @param createdComposition
   */
  private void updateTrackMediaAssets(Project project, CompositionDto createdComposition) {
    if (createdComposition.tracks != null && createdComposition.tracks.size() > 0) {
      for (TrackDto trackDto : createdComposition.tracks) {
        if (trackDto.mediaItems != null && trackDto.mediaItems.size() > 0) {
          for (MediaDto mediaDto : trackDto.mediaItems) {
            if (mediaDto.getAssetId() == null) {
              // TODO(jliarte): 18/07/18 set Media id and persist on media repo?
              Media mediaFromProjectTrack = getMediaFromProjectTrack(project, mediaDto.getUuid());
              if (mediaFromProjectTrack != null) {
                // TODO(jliarte): 20/07/18 set project Id
                assetApiDataSource.add(new Asset("confiHack", mediaFromProjectTrack));
              }
            }
          }
        }
      }
    }
  }

  private Media getMediaFromProjectTrack(Project project, String uuid) {
    for (Media media : project.getMediaTrack().getItems()) {
      if (media.getUuid().equals(uuid)) {
        return media;
      }
    }
    for (Track track : project.getAudioTracks()) {
      for (Media media : track.getItems()) {
        if (media.getUuid().equals(uuid)) {
          return media;
        }
      }
    }
    return null;
  }

  @Override
  public void add(Iterable<Project> items) {
    for (Project project : items) {
      add(project);
    }
  }

  @Override
  public void update(Project item) {
    // update composition -> update/create track -> update/create media -> (mediaId)link with asset(assetID) <- asset upload
    try {
      String accessToken = getApiAccessToken().get().getAccessToken();
      CompositionDto createdComposition = this.compositionApiClient
              .updateComposition(mapper.map(item), accessToken);
      updateTrackMediaAssets(item, createdComposition);
      Log.d(LOG_TAG, "Composition added to platform!");
    } catch (VimojoApiException apiError) {
      processApiError(apiError);
    } catch (InterruptedException | ExecutionException e) {
      // TODO(jliarte): 12/07/18 manage this error
      e.printStackTrace();
    }
  }

  @Override
  public void remove(Project item) {

  }

  @Override
  public void remove(Specification specification) {

  }

  @Override
  public List<Project> query(Specification specification) {
    return null;
  }

  @Override
  public Project getById(String id) {
    return null;
  }

  public List<Project> getListProjectsByLastModificationDescending() {
    try {
      String accessToken = getApiAccessToken().get().getAccessToken();
      List<CompositionDto> compositions = this.compositionApiClient
              .getAll(accessToken);
      return (List<Project>) mapper.reverseMap(compositions);
    } catch (VimojoApiException apiError) {
      processApiError(apiError);
    } catch (InterruptedException | ExecutionException e) {
      // TODO(jliarte): 12/07/18 manage this error
      e.printStackTrace();
    }
    return Collections.emptyList();
  }
}
