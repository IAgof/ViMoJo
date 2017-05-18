package com.videonasocialmedia.vimojo.split.domain;

import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnAddMediaFinishedListener;

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

    public void splitVideo(final Video initialVideo, int positionInAdapter, int splitTimeMs,
                           final OnSplitVideoListener listener) {
        splitTimeMs += initialVideo.getStartTime();

        final Video endVideo = new Video(initialVideo);
        endVideo.setStartTime(splitTimeMs);
        endVideo.setStopTime(initialVideo.getStopTime());
        initialVideo.setStopTime(splitTimeMs);

        addVideoToProjectUseCase.addVideoToProjectAtPosition(endVideo, positionInAdapter + 1,
            new OnAddMediaFinishedListener() {
            @Override
            public void onAddMediaItemToTrackError() {
                listener.showErrorSplittingVideo();
            }

            @Override
            public void onAddMediaItemToTrackSuccess(Media media) {
                listener.trimVideo(initialVideo, initialVideo.getStartTime(), initialVideo.getStopTime());
                listener.trimVideo(endVideo, endVideo.getStartTime(), endVideo.getStopTime());
            }
        });

    }
}
