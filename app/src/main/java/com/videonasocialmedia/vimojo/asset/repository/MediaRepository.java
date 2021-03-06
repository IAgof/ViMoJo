package com.videonasocialmedia.vimojo.asset.repository;

/**
 * Created by jliarte on 18/07/18.
 */

import android.util.Log;

import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.asset.repository.datasource.MediaApiDataSource;
import com.videonasocialmedia.vimojo.asset.repository.datasource.VideoRealmDataSource;
import com.videonasocialmedia.vimojo.repository.DeletePolicy;
import com.videonasocialmedia.vimojo.repository.ReadPolicy;
import com.videonasocialmedia.vimojo.repository.Specification;
import com.videonasocialmedia.vimojo.repository.VimojoRepository;
import com.videonasocialmedia.vimojo.repository.music.datasource.MusicRealmDataSource;

import java.util.List;

import javax.inject.Inject;

/**
 * Repository for providing {@link Media} via repository pattern
 *
 * <p>This class handles saving and retrieving {@link Media} from different data sources and merge
 * {@link Media} provided by them for returning results.</p>
 * <p>Old Realm*Repositories are now Realm*DataSource</p>
 * <p>Old flows implementation using Realm*DataSources are moved into this repository</p>
 */
public class MediaRepository extends VimojoRepository<Media> {
  private static final String LOG_TAG = MediaRepository.class.getSimpleName();
  private VideoRealmDataSource videoRealmDataSource;
  private MusicRealmDataSource musicRealmDataSource;
  private MediaApiDataSource mediaApiDataSource;
  private final boolean cloudBackupAvailable;

  @Inject
  public MediaRepository(VideoRealmDataSource videoRealmDataSource,
                         MusicRealmDataSource musicRealmDataSource,
                         MediaApiDataSource mediaApiDataSource,
                         boolean cloudBackupAvailable) {
    this.videoRealmDataSource = videoRealmDataSource;
    this.musicRealmDataSource = musicRealmDataSource;
    this.mediaApiDataSource = mediaApiDataSource;
    this.cloudBackupAvailable = cloudBackupAvailable;
  }

  @Override
  public void add(Media item) {
    if (item instanceof Video) {
      videoRealmDataSource.add((Video) item);
    } else if (item instanceof Music) {
      musicRealmDataSource.add((Music) item);
    } else {
      Log.e(LOG_TAG, "Trying to update media that is not video or music " + item.toString());
    }
    if (cloudBackupAvailable) {
      mediaApiDataSource.add(item);
    }
  }

  @Override
  public void add(Iterable<Media> items) {
    for (Media media : items) {
      add(media);
    }
  }

  @Override
  public void update(Media item) {
    if (item instanceof Video) {
      videoRealmDataSource.update((Video) item);
    } else if (item instanceof Music) {
      musicRealmDataSource.update((Music) item);
    } else {
      Log.e(LOG_TAG, "Trying to update media that is not video or music " + item.toString());
    }
    if (cloudBackupAvailable) {
      mediaApiDataSource.update(item);
    }
  }

  @Override
  public void remove(Media item, DeletePolicy policy) {
    if (policy.useLocal()) {
      if (item instanceof Video) {
        videoRealmDataSource.remove((Video) item);
      } else if (item instanceof Music) {
        musicRealmDataSource.remove((Music) item);
      } else {
        Log.e(LOG_TAG, "Trying to remove media that is not video or music " + item.toString());
      }
    }

    if (policy.useRemote() && cloudBackupAvailable) {
      mediaApiDataSource.remove(item);
    }
  }

  @Override
  public Media getById(String id, ReadPolicy readPolicy) {
    return getById(id, ReadPolicy.READ_ALL);
  }

  @Override
  public void remove(Media item) {
    remove(item, DeletePolicy.DELETE_ALL);
  }

  @Override
  public void remove(Specification specification) {

  }

  @Override
  public List<Media> query(Specification specification) {
    return null;
  }

  @Override
  public Media getById(String id) {
    return null;
  }
}
