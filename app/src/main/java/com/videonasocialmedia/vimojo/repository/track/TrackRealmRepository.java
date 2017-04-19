package com.videonasocialmedia.vimojo.repository.track;

import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.repository.Mapper;
import com.videonasocialmedia.vimojo.repository.Specification;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by alvaro on 10/04/17.
 */

public class TrackRealmRepository implements TrackRepository{
  protected Mapper<RealmTrack, Track> toTrackMapper;
  protected Mapper<Track, RealmTrack> toRealmTrackMapper;

  public TrackRealmRepository(){
    this.toTrackMapper = new RealmTrackToTrackMapper();
    this.toRealmTrackMapper = new TrackToRealmTrackMapper();
  }

  @Override
  public void add(final Track item) {
    final Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        RealmTrack realmTrack = toRealmTrackMapper.map(item);
        realm.copyToRealm(realmTrack);
      }
    });
    realm.close();
  }

  @Override
  public void add(Iterable<Track> items) {

  }

  @Override
  public void update(final Track item) {
    Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        realm.copyToRealmOrUpdate(toRealmTrackMapper.map(item));
      }
    });
  }

  @Override
  public List<Track> getAllTracks() {
    Realm realm = Realm.getDefaultInstance();
    RealmResults<RealmTrack> realmResults = realm.where(RealmTrack.class).findAll();
    List<Track> trackList = new ArrayList<>();
    for(RealmTrack realmTrack: realmResults){
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
  public void remove(final Track item) {
    Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        RealmResults<RealmTrack> result = realm.where(RealmTrack.class).
            equalTo("uuid", item.getUuid()).findAll();
        result.deleteAllFromRealm();
      }
    });
  }

  @Override
  public void remove(Specification specification) {

  }

  @Override
  public List<Track> query(Specification specification) {
    return null;
  }
}
