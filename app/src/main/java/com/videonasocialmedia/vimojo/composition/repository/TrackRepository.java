package com.videonasocialmedia.vimojo.composition.repository;

/**
 * Created by jliarte on 14/09/18.
 */

import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.composition.repository.datasource.TrackApiDataSource;
import com.videonasocialmedia.vimojo.composition.repository.datasource.TrackRealmDataSource;
import com.videonasocialmedia.vimojo.repository.DeletePolicy;
import com.videonasocialmedia.vimojo.repository.ReadPolicy;
import com.videonasocialmedia.vimojo.repository.Specification;
import com.videonasocialmedia.vimojo.repository.VimojoRepository;

import java.util.List;

import javax.inject.Inject;

/**
 * Repository for providing {@link Track} via repository pattern.
 *
 * <p>This class handles saving and retrieving {@link Track} from different data sources and merge
 * tracks provided by them for returning results.</p>
 */
public class TrackRepository extends VimojoRepository<Track> {
  private TrackRealmDataSource trackLocalDataSource;
  private TrackApiDataSource trackRemoteDataSource;

  @Inject
  public TrackRepository(TrackRealmDataSource trackLocalDataSource,
                         TrackApiDataSource trackRemoteDataSource) {
    this.trackLocalDataSource = trackLocalDataSource;
    this.trackRemoteDataSource = trackRemoteDataSource;
  }

  @Override
  public void add(Track item) {

  }

  @Override
  public void add(Iterable<Track> items) {

  }

  @Override
  public void update(Track item) {
    trackLocalDataSource.update(item);
    trackRemoteDataSource.update(item);
  }

  @Override
  public void remove(Track item) {
    trackLocalDataSource.remove(item);
    trackRemoteDataSource.remove(item);
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

  @Override
  public Track getById(String id, ReadPolicy readPolicy) {
    return null;
  }

  @Override
  public void remove(Track item, DeletePolicy policy) {

  }
}
