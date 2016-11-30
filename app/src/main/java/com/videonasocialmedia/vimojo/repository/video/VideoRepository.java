package com.videonasocialmedia.vimojo.repository.video;

import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.repository.Repository;
import com.videonasocialmedia.vimojo.repository.project.RealmProject;

import io.realm.RealmResults;

/**
 * Created by Alejandro on 21/10/16.
 */

public interface VideoRepository extends Repository<Video>{
    void update(Video item, RealmProject realmProject);
    RealmResults<RealmVideo> getVideos();
    void removeAllVideos();
}
