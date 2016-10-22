package com.videonasocialmedia.vimojo.repository.video;

import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.repository.Mapper;

/**
 * Created by Alejandro on 21/10/16.
 */

public class RealmVideoToVideoMapper implements Mapper<RealmVideo,Video> {
    @Override
    public Video map(RealmVideo realmVideo) {
        Video video = new Video(realmVideo.mediaPath);
        video.setIdentifier(realmVideo.identifier);
        video.setPosition(realmVideo.position);
        video.setClipText(realmVideo.clipText);
        video.setClipTextPosition(realmVideo.clipTextPosition);
        video.setTextToVideoAdded(realmVideo.isTextToVideoAdded);
        video.setTrimmedVideo(realmVideo.isTrimmedVideo);
        video.setStartTime(realmVideo.startTime);
        video.setStopTime(realmVideo.stopTime);
        return video;
    }
}
