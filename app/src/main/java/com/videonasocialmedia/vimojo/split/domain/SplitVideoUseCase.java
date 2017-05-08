package com.videonasocialmedia.vimojo.split.domain;

import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.videonamediaframework.model.media.Video;

import javax.inject.Inject;

/**
 * Created by ruth on 4/08/16.
 */

public class SplitVideoUseCase {
    private AddVideoToProjectUseCase addVideoToProjectUseCase;

    @Inject
    public SplitVideoUseCase(AddVideoToProjectUseCase addVideoToProjectUseCase) {
        this.addVideoToProjectUseCase = addVideoToProjectUseCase;
    }

    public void splitVideo(Video initialVideo, int positionInAdapter, int splitTimeMs,
                           OnSplitVideoListener listener) {
        splitTimeMs += initialVideo.getStartTime();

        Video endVideo = new Video(initialVideo);
        endVideo.setStartTime(splitTimeMs);
        endVideo.setStopTime(initialVideo.getStopTime());
        initialVideo.setStopTime(splitTimeMs);

        addVideoToProjectUseCase.addVideoToProjectAtPosition(endVideo, positionInAdapter + 1);

        listener.trimVideo(initialVideo, initialVideo.getStartTime(), initialVideo.getStopTime());
        listener.trimVideo(endVideo, endVideo.getStartTime(), endVideo.getStopTime());
    }
}
