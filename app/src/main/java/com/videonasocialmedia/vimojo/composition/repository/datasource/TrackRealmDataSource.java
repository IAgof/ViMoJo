package com.videonasocialmedia.vimojo.composition.repository.datasource;

/**
 * Created by alvaro on 10/04/17.
 */

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.track.AudioTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.asset.repository.datasource.VideoRealmDataSource;
import com.videonasocialmedia.vimojo.repository.Mapper;
import com.videonasocialmedia.vimojo.repository.Specification;
import com.videonasocialmedia.vimojo.repository.music.datasource.MusicRealmDataSource;
import com.videonasocialmedia.vimojo.repository.track.datasource.mapper.RealmTrackToTrackMapper;
import com.videonasocialmedia.vimojo.repository.track.datasource.mapper.TrackToRealmTrackMapper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Realm DataSource for tracks. Provide local persistance of {@link Track} using Realm
 * via {@link RealmTrack} class.
 */
public class TrackRealmDataSource implements TrackDataSource {
  protected Mapper<RealmTrack, Track> toTrackMapper;
  protected Mapper<Track, RealmTrack> toRealmTrackMapper;
  private VideoRealmDataSource videoDataSource;
  private MusicRealmDataSource musicDataSource;

  @Inject
  public TrackRealmDataSource(VideoRealmDataSource videoDataSource,
                              MusicRealmDataSource musicDataSource) {
    this.videoDataSource = videoDataSource;
    this.musicDataSource = musicDataSource;
    this.toTrackMapper = new RealmTrackToTrackMapper();
    this.toRealmTrackMapper = new TrackToRealmTrackMapper();
  }

  @Override
  public void add(final Track item) {
    Realm.getDefaultInstance().executeTransaction(realm -> {
      RealmTrack realmTrack = toRealmTrackMapper.map(item);
      realm.copyToRealm(realmTrack);
    });
    Realm.getDefaultInstance().close();
  }

  @Override
  public void add(Iterable<Track> items) {

  }

  @Override
  public void update(final Track item) {
    Realm.getDefaultInstance().executeTransaction(
            realm -> realm.copyToRealmOrUpdate(toRealmTrackMapper.map(item)));
  }

  @Override
  public List<Track> getAllTracks() {
    Realm realm = Realm.getDefaultInstance();
    RealmResults<RealmTrack> realmResults = realm.where(RealmTrack.class).findAll();
    List<Track> trackList = new ArrayList<>();
    for (RealmTrack realmTrack: realmResults) {
      trackList.add(toTrackMapper.map(realm.copyFromRealm(realmTrack)));
    }
    return trackList;
  }

  @Override
  public void removeAllTracks() {
    final Realm realm = Realm.getDefaultInstance();
    realm.delete(RealmTrack.class);
  }

  @Override
  public MediaTrack getMediaTrack() {
    Realm realm = Realm.getDefaultInstance();
    RealmResults<RealmTrack> realmResults = realm.where(RealmTrack.class).findAll();
    MediaTrack mediaTrack = null;
    for (RealmTrack realmTrack: realmResults) {
      if(realmTrack.id == Constants.INDEX_MEDIA_TRACK) {
        mediaTrack = (MediaTrack) toTrackMapper.map(realmTrack);
        break;
      }
    }
    return mediaTrack;
  }

  @Override
  public AudioTrack getMusicTrack() {
    Realm realm = Realm.getDefaultInstance();
    RealmResults<RealmTrack> realmResults = realm.where(RealmTrack.class).findAll();
    AudioTrack musicTrack = null;
    for (RealmTrack realmTrack: realmResults) {
      if(realmTrack.id == Constants.INDEX_AUDIO_TRACK_MUSIC) {
        musicTrack = (AudioTrack) toTrackMapper.map(realmTrack);
        break;
      }
    }
    return musicTrack;
  }

  @Override
  public AudioTrack getVoiceOverTrack() {
    Realm realm = Realm.getDefaultInstance();
    RealmResults<RealmTrack> realmResults = realm.where(RealmTrack.class).findAll();
    AudioTrack voiceOverTrack = null;
    for (RealmTrack realmTrack: realmResults) {
      if(realmTrack.id == Constants.INDEX_AUDIO_TRACK_VOICE_OVER) {
        voiceOverTrack = (AudioTrack) toTrackMapper.map(realmTrack);
        break;
      }
    }
    return voiceOverTrack;
  }

  @Override
  public void remove(final Track track) {
    deleteAllMediasInTrack(track);
    Realm.getDefaultInstance().executeTransaction(realm -> {
      RealmResults<RealmTrack> result = realm.where(RealmTrack.class).
          equalTo("uuid", track.getUuid()).findAll();
      result.deleteAllFromRealm();
    });
  }

  private void deleteAllMediasInTrack(Track track) {
    LinkedList<Media> mediaList = track.getItems();
    for (Media media : mediaList) {
      if (media instanceof Video) {
        videoDataSource.remove((Video) media);
      } else if (media instanceof Music) {
        musicDataSource.remove((Music) media);
      }
    }
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
    Realm realm = Realm.getDefaultInstance();
    RealmTrack result = realm.where(RealmTrack.class).
            equalTo("uuid", id).findFirst();
    return toTrackMapper.map(result);
  }
}
