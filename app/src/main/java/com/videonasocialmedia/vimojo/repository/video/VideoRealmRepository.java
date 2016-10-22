package com.videonasocialmedia.vimojo.repository.video;

import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.repository.Mapper;
import com.videonasocialmedia.vimojo.repository.Specification;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmModel;
import io.realm.RealmResults;

/**
 * Created by Alejandro on 21/10/16.
 */

public class VideoRealmRepository implements  VideoRepository {
    private Mapper<VideoRealm,Video> toVideoMapper;
    private final RealmConfiguration realmConfiguration;

    public VideoRealmRepository(RealmConfiguration realmConfiguration) {
        this.realmConfiguration = realmConfiguration;

        this.toVideoMapper = new RealmVideoToVideoMapper();

    }

    @Override
    public RealmResults<VideoRealm> getVideos() {
        ArrayList<Video> videoList = new ArrayList<Video>();

        final Realm realm = Realm.getInstance(realmConfiguration);
        RealmResults<VideoRealm> realmResults = realm.where(VideoRealm.class).findAll();

        return realmResults;
    }

    @Override
    public void add(final Video item) {
        final Realm realm = Realm.getInstance(realmConfiguration);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                final VideoRealm videoRealm = realm.createObject(VideoRealm.class);
                videoRealm.VIDEO_FOLDER_PATH = item.getMediaPath();
                videoRealm.fileDuration = item.getFileDuration();
                videoRealm.tempPath = item.getTempPath();
                videoRealm.clipText = item.getClipText();
                videoRealm.clipTextPosition = item.getClipTextPosition();
                videoRealm.isTextToVideoAdded = item.isTextToVideoAdded();
                videoRealm.isTrimmedVideo = item.isTrimmedVideo();
                videoRealm.stopTime = item.getStopTime();
                videoRealm.startTime = item.getStartTime();
            }
        });

        realm.close();
    }

    @Override
    public void removeAllVideos() {
        final Realm realm = Realm.getInstance(realmConfiguration);

        realm.delete((Class<? extends RealmModel>) VideoRealm.class);
    }

    @Override
    public void add(Iterable<Video> items) {

    }

    @Override
    public void update(Video item) {

    }

    @Override
    public void remove(Video item) {

    }

    @Override
    public void remove(Specification specification) {

    }

    @Override
    public List<Video> query(Specification specification) {
        return null;
    }
}
