package com.videonasocialmedia.vimojo.repository.Video;

import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.repository.Repository;

import java.util.ArrayList;

import io.realm.RealmResults;

/**
 * Created by Alejandro on 21/10/16.
 */

public interface VideoRepository extends Repository<Video>{
    RealmResults<VideoRealm> getVideos();
    void removeAllVideos();

}
