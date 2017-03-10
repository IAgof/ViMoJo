package com.videonasocialmedia.vimojo.repository.video;

import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.repository.Mapper;

/**
 * Created by Alejandro on 21/10/16.
 */

public class RealmVideoToVideoMapper implements Mapper<RealmVideo,Video> {
    @Override
    public Video map(RealmVideo realmVideo) {
        Video video = new Video(realmVideo.mediaPath, realmVideo.volume);
        video.setUuid(realmVideo.uuid);
        video.tempPath = realmVideo.tempPath;
        video.setTempPathFinished(realmVideo.isTempPathFinished);
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
