package com.videonasocialmedia.vimojo.repository;

import com.videonasocialmedia.vimojo.asset.repository.datasource.RealmVideo;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by jliarte on 25/07/17.
 */

public interface RealmSpecification extends Specification {
  RealmResults<RealmVideo> toRealmResults(Realm realm);
}
