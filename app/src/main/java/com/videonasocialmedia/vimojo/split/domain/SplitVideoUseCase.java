package com.videonasocialmedia.vimojo.split.domain;

import android.content.Context;
import android.content.Intent;
import com.videonasocialmedia.vimojo.VideonaApplication;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.trim.domain.TrimBackgroundService;

/**
 * Created by ruth on 4/08/16.
 */

public class SplitVideoUseCase {

    AddVideoToProjectUseCase addVideoToProjectUseCase;

    public void separateVideo(Video initialVideo, int positionInAdapter, int splitTimeMs) {

        Video endVideo = new Video(initialVideo);

        initialVideo.setFileStopTime(splitTimeMs);
        initialVideo.setIsSplit(true);
        endVideo.setFileStartTime(splitTimeMs);
        endVideo.setFileStopTime(initialVideo.getFileStopTime());
        endVideo.setIsSplit(true);

        addVideoToProjectUseCase.addVideoToProjectAtPosition(endVideo, positionInAdapter + 1);

        trimVideoSplit(initialVideo,initialVideo.getFileStartTime(), splitTimeMs);
        trimVideoSplit(endVideo,splitTimeMs,initialVideo.getFileStopTime());
    }

    public void trimVideoSplit(final Video videoToEdit, final int startTimeMs, final int finishTimeMs) {

        Context appContext = VideonaApplication.getAppContext();
        Intent trimServiceIntent = new Intent(appContext, TrimBackgroundService.class);
        trimServiceIntent.putExtra("startTimeMs",startTimeMs);
        trimServiceIntent.putExtra("finishTimeMs", finishTimeMs);
        appContext.startService(trimServiceIntent);
    }
}
