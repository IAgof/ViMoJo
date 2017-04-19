package com.videonasocialmedia.vimojo.repository.music;

import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.vimojo.repository.Mapper;
import com.videonasocialmedia.vimojo.repository.Specification;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by alvaro on 12/04/17.
 */

public class MusicRealmRepository implements MusicRepository {

  protected Mapper<RealmMusic, Music> toMusicMapper;
  protected Mapper<Music, RealmMusic> toRealmMusicMapper;

  public MusicRealmRepository(){
    this.toMusicMapper = new RealmMusicToMusicMapper();
    this.toRealmMusicMapper = new MusicToRealmMusicMapper();
  }

  @Override
  public void add(final Music item) {
    final Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        RealmMusic realmMusic = toRealmMusicMapper.map(item);
        realm.copyToRealm(realmMusic);
      }
    });
    realm.close();
  }

  @Override
  public void add(Iterable<Music> items) {

  }

  @Override
  public void remove(final Music item) {
    Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        RealmResults<RealmMusic> result = realm.where(RealmMusic.class).
            equalTo("uuid", item.getUuid()).findAll();
        result.deleteAllFromRealm();
      }
    });
  }

  @Override
  public void remove(Specification specification) {

  }

  @Override
  public List<Music> query(Specification specification) {
    return null;
  }

  @Override
  public void update(final Music item) {
    Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        realm.copyToRealmOrUpdate(toRealmMusicMapper.map(item));
      }
    });
  }

  @Override
  public List<Music> getAllMusics() {
    Realm realm = Realm.getDefaultInstance();
    RealmResults<RealmMusic> realmResults = realm.where(RealmMusic.class).findAll();
    List<Music> musicList = new ArrayList<>();
    for(RealmMusic realmMusic: realmResults){
      musicList.add(toMusicMapper.map(realm.copyFromRealm(realmMusic)));
    }
    return musicList;
  }

  @Override
  public void removeAllMusics() {
    final Realm realm = Realm.getDefaultInstance();
    realm.delete(RealmMusic.class);
  }
}
