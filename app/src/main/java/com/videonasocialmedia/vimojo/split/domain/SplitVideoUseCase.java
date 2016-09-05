package com.videonasocialmedia.vimojo.split.domain;

import android.content.Context;
import android.content.Intent;

import com.videonasocialmedia.vimojo.VimojoApplication;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.export.ExportTempBackgroundService;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.utils.ExportIntentConstants;

/**
 * Created by ruth on 4/08/16.
 */

public class SplitVideoUseCase {

    public void splitVideo(Video initialVideo, int positionInAdapter, int splitTimeMs, OnSplitVideoListener listener) {
        splitTimeMs += initialVideo.getStartTime();

        Video endVideo = new Video(initialVideo);
        endVideo.setStartTime(splitTimeMs);
        endVideo.setStopTime(initialVideo.getStopTime());
        initialVideo.setStopTime(splitTimeMs);

        AddVideoToProjectUseCase addVideoToProjectUseCase = new AddVideoToProjectUseCase();
        addVideoToProjectUseCase.addVideoToProjectAtPosition(endVideo, positionInAdapter + 1);

        listener.trimVideo(initialVideo, initialVideo.getStartTime(), initialVideo.getStopTime());
        listener.trimVideo(endVideo, endVideo.getStartTime(), endVideo.getStopTime());

    }
}
