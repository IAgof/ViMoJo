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

    public void separateVideo(Video initialVideo, int positionInAdapter, int timeMs) {

        Video endVideo = new Video(initialVideo);

        initialVideo.setFileStopTime(timeMs);
        initialVideo.getFileStartTime();
        initialVideo.setIsSplit(true);

        endVideo.setFileStartTime(timeMs);
        endVideo.setFileStopTime(initialVideo.getFileStopTime());
        endVideo.setIsSplit(true);


        addVideoToProjectUseCase.addVideoToProjectAtPosition(endVideo, positionInAdapter + 1);

        trimVideoSplit(initialVideo,initialVideo.getFileStartTime(), timeMs);
        trimVideoSplit(endVideo,timeMs,initialVideo.getFileStopTime());



    }

    public void trimVideoSplit(final Video videoToEdit, final int startTimeMs, final int finishTimeMs) {
        Context appContext = VideonaApplication.getAppContext();
        Intent trimServiceIntent = new Intent(appContext, TrimBackgroundService.class);

        trimServiceIntent.putExtra("startTimeMs",startTimeMs);
        trimServiceIntent.putExtra("finishTimeMs", finishTimeMs);




    }




}
