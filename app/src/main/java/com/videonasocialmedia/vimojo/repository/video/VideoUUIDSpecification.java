package com.videonasocialmedia.vimojo.repository.video;

import com.videonasocialmedia.vimojo.repository.RealmSpecification;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by jliarte on 25/07/17.
 */

public class VideoUUIDSpecification implements RealmSpecification {
  private final String video_uuid;

  public VideoUUIDSpecification(String video_uuid) {
    this.video_uuid = video_uuid;
  }

  @Override
  public RealmResults<RealmVideo> toRealmResults(Realm realm) {
    return realm.where(RealmVideo.class).equalTo("uuid", video_uuid).findAll();
  }
}
