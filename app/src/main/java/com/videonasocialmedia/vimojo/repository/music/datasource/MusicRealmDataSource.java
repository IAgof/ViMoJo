package com.videonasocialmedia.vimojo.repository.music.datasource;

/**
 * Created by alvaro on 12/04/17.
 */

import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.vimojo.repository.Mapper;
import com.videonasocialmedia.vimojo.repository.Specification;
import com.videonasocialmedia.vimojo.repository.music.MusicDataSource;
import com.videonasocialmedia.vimojo.repository.music.datasource.mapper.MusicToRealmMusicMapper;
import com.videonasocialmedia.vimojo.repository.music.datasource.mapper.RealmMusicToMusicMapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Realm DataSource for music. Provide local persistance of {@link Music} using Realm
 * via {@link RealmMusic} class.
 */
public class MusicRealmDataSource implements MusicDataSource {
  protected Mapper<RealmMusic, Music> toMusicMapper;
  protected Mapper<Music, RealmMusic> toRealmMusicMapper;

  @Inject
  public MusicRealmDataSource() {
    this.toMusicMapper = new RealmMusicToMusicMapper();
    this.toRealmMusicMapper = new MusicToRealmMusicMapper();
  }

  @Override
  public void add(final Music item) {
    Realm.getDefaultInstance().executeTransaction(realm -> {
      RealmMusic realmMusic = toRealmMusicMapper.map(item);
      realm.copyToRealm(realmMusic);
    });
    Realm.getDefaultInstance().close();
  }

  @Override
  public void add(Iterable<Music> items) {

  }

  @Override
  public void remove(final Music item) {
    Realm.getDefaultInstance().executeTransaction(realm -> {
      RealmResults<RealmMusic> result = realm.where(RealmMusic.class).
          equalTo("uuid", item.getUuid()).findAll();
      result.deleteAllFromRealm();
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
  public Music getById(String id) {
    Realm realm = Realm.getDefaultInstance();
    RealmMusic result = realm.where(RealmMusic.class).
            equalTo("uuid", id).findFirst();
    return toMusicMapper.map(result);
  }

  @Override
  public void update(final Music item) {
    Realm.getDefaultInstance().executeTransaction(
            realm -> realm.copyToRealmOrUpdate(toRealmMusicMapper.map(item)));
  }

  @Override
  public List<Music> getAllMusics() {
    Realm realm = Realm.getDefaultInstance();
    RealmResults<RealmMusic> realmResults = realm.where(RealmMusic.class).findAll();
    List<Music> musicList = new ArrayList<>();
    for(RealmMusic realmMusic: realmResults) {
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
