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

    public void separateVideo(Video initialVideo, int positionInAdapter, int splitTimeMs) {

        Video endVideo = new Video(initialVideo);


        endVideo.setFileStartTime(splitTimeMs);
        endVideo.setFileStopTime(initialVideo.getFileStopTime());
        endVideo.setIsSplit(true);
        initialVideo.setFileStopTime(splitTimeMs);
        initialVideo.setIsSplit(true);

        AddVideoToProjectUseCase addVideoToProjectUseCase = new AddVideoToProjectUseCase();
        addVideoToProjectUseCase.addVideoToProjectAtPosition(endVideo, positionInAdapter + 1);

        trimVideoSplit(initialVideo, initialVideo.getFileStartTime(), initialVideo.getFileStopTime());
        trimVideoSplit(endVideo, endVideo.getFileStartTime(), endVideo.getFileStopTime());
    }

    public void trimVideoSplit(Video videoToEdit, final int startTimeMs, final int finishTimeMs) {

        Context appContext = VideonaApplication.getAppContext();
        Intent trimServiceIntent = new Intent(appContext, TrimBackgroundService.class);
        trimServiceIntent.putExtra("videoId", videoToEdit.getIdentifier());
        trimServiceIntent.putExtra("startTimeMs", startTimeMs);
        trimServiceIntent.putExtra("finishTimeMs", finishTimeMs);
        appContext.startService(trimServiceIntent);
    }
}
