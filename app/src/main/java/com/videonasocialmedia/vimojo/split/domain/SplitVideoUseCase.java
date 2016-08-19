package com.videonasocialmedia.vimojo.split.domain;

import android.content.Context;
import android.content.Intent;

import com.videonasocialmedia.vimojo.VimojoApplication;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.trim.domain.TrimBackgroundService;

/**
 * Created by ruth on 4/08/16.
 */

public class SplitVideoUseCase {

    public void separateVideo(Video initialVideo, int positionInAdapter, int splitTimeMs) {

        splitTimeMs += initialVideo.getStartTime();

        Video endVideo = new Video(initialVideo);
        endVideo.setStartTime(splitTimeMs);
        endVideo.setStopTime(initialVideo.getStopTime());
        initialVideo.setStopTime(splitTimeMs);

        AddVideoToProjectUseCase addVideoToProjectUseCase = new AddVideoToProjectUseCase();
        addVideoToProjectUseCase.addVideoToProjectAtPosition(endVideo, positionInAdapter + 1);

        trimVideoSplit(initialVideo, initialVideo.getStartTime(), initialVideo.getStopTime());
        trimVideoSplit(endVideo, endVideo.getStartTime(), endVideo.getStopTime());
    }

    public void trimVideoSplit(Video videoToEdit, final int startTimeMs, final int finishTimeMs) {

        Context appContext = VimojoApplication.getAppContext();
        Intent trimServiceIntent = new Intent(appContext, TrimBackgroundService.class);
        trimServiceIntent.putExtra("videoId", videoToEdit.getIdentifier());
        trimServiceIntent.putExtra("startTimeMs", startTimeMs);
        trimServiceIntent.putExtra("finishTimeMs", finishTimeMs);
        appContext.startService(trimServiceIntent);
    }
}
