package com.videonasocialmedia.vimojo.split.domain;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.asset.repository.MediaRepository;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnAddMediaFinishedListener;
import com.videonasocialmedia.vimojo.trim.domain.ModifyVideoDurationUseCase;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.util.concurrent.ThreadLocalRandom;

import javax.inject.Inject;

import static com.videonasocialmedia.vimojo.utils.Constants.MIN_TRIM_OFFSET;
import static com.videonasocialmedia.vimojo.utils.Constants.MS_CORRECTION_FACTOR;

/**
 * Created by ruth on 4/08/16.
 */

public class SplitVideoUseCase {
    private static final String LOG_TAG = SplitVideoUseCase.class.getSimpleName();
    static final int AUTOSPLIT_MS_RANGE = 10;
    private AddVideoToProjectUseCase addVideoToProjectUseCase;
    private ModifyVideoDurationUseCase modifyVideoDurationUseCase;
    protected Video endVideo;
    private Project currentProject;
    private MediaRepository mediaRepository;

    @Inject
    public SplitVideoUseCase(AddVideoToProjectUseCase addVideoToProjectUseCase,
                             ModifyVideoDurationUseCase modifyVideoDurationUseCase,
                             MediaRepository mediaRepository) {
        this.addVideoToProjectUseCase = addVideoToProjectUseCase;
        this.modifyVideoDurationUseCase = modifyVideoDurationUseCase;
        this.mediaRepository = mediaRepository;
    }

    public void splitVideo(Project currentProject, final Video initialVideo, int positionInAdapter,
                           int splitTimeMs,
                           final OnSplitVideoListener listener) {
        splitTimeMs += initialVideo.getStartTime();
        this.currentProject = currentProject;
        endVideo = new Video(initialVideo);
        endVideo.setStartTime(splitTimeMs);
        endVideo.setStopTime(initialVideo.getStopTime());
        endVideo.setTranscodingTask(initialVideo.getTranscodingTask());
        initialVideo.setStopTime(splitTimeMs);

        addVideoToProjectUseCase.addVideoToProjectAtPosition(currentProject, endVideo,
            positionInAdapter + 1,
            new OnAddMediaFinishedListener() {
            @Override
            public void onAddMediaItemToTrackError() {
                listener.showErrorSplittingVideo();
            }

            @Override
            public void onAddMediaItemToTrackSuccess(Media media) {
                // TODO(jliarte): 19/07/18 handle composition update
                listener.onSuccessSplittingVideo(currentProject, initialVideo, endVideo);
                runTrimTasks(initialVideo, endVideo);
            }
        });
    }

    protected void runTrimTasks(Video initialVideo, Video endVideo) {
        trimVideo(initialVideo, initialVideo.getStartTime(), initialVideo.getStopTime());
        trimVideo(endVideo, endVideo.getStartTime(), endVideo.getStopTime());
    }

    private void trimVideo(Video video, int startTime, int stopTime) {
        VideonaFormat videoFormat = currentProject.getVMComposition().getVideoFormat();
        // TODO(jliarte): 30/10/17 consider not calling Trim UC and manage errors here
        ListenableFuture<Video> result = modifyVideoDurationUseCase
                .trimVideo(video, startTime, stopTime, currentProject);
        FutureCallback<? super Video> trimCallback = new SplitTaskCallback(video, currentProject);
        Futures.addCallback(result, trimCallback);
    }

    private class SplitTaskCallback implements FutureCallback<Video> {
        private final Video video;
        private final Project currentProject;

        private SplitTaskCallback(Video video, Project currentProject) {
            this.video = video;
            this.currentProject = currentProject;
        }

        @Override
        public void onSuccess(Video result) {
            handleTaskSuccess(result);
        }

        @Override
        public void onFailure(@NonNull Throwable t) {
            handleTaskError(video, t.getMessage(), currentProject);
        }
    }

    private void handleTaskSuccess(Video video) {
        Log.d(LOG_TAG, "onSuccessTranscoding after trim in split " + video.getTempPath());
        // TODO(jliarte): 10/09/18 should we move this code to caller?
        video.resetNumTriesToExportVideo();
        video.setTranscodingTempFileFinished(true);
        video.setVideoError(null);
        mediaRepository.update(video); // TODO(jliarte): 13/09/18 this is needed to set success on transcoding. check that this is ran in background!
    }

    void handleTaskError(Video video, String message, Project currentProject) {
        Log.d(LOG_TAG, "onErrorTranscoding " + video.getTempPath() + " - " + message);
        if (video.getNumTriesToExportVideo() < Constants.MAX_NUM_TRIES_TO_EXPORT_VIDEO) {
            video.increaseNumTriesToExportVideo();
            // TODO(jliarte): 23/10/17 modify here trim times
            randomizeSplitTime(video, endVideo);
            mediaRepository.update(video); // TODO(jliarte): 13/09/18 split times change. check this is ran in background!
            mediaRepository.update(endVideo);
            runTrimTasks(video, endVideo);
        } else {
            //trimView.showError(message);
            video.setVideoError(Constants.ERROR_TRANSCODING_TEMP_FILE_TYPE.SPLIT.name());
            video.setTranscodingTempFileFinished(true);
            mediaRepository.update(video); // TODO(jliarte): 13/09/18 needed to set error to video. check that this is ran in background!
        }
    }

    private void randomizeSplitTime(Video video, Video endVideo) {
        int minTrimTime = (int) (MIN_TRIM_OFFSET * MS_CORRECTION_FACTOR);
        int splitTime = video.getStopTime();
        int stopTime = endVideo.getStopTime();
        int randomSplit = splitTime;
        while (randomSplit == splitTime) {
            int minSplit = Math.max(0, splitTime - AUTOSPLIT_MS_RANGE);
            int maxSplit = Math.min(splitTime + AUTOSPLIT_MS_RANGE, stopTime - minTrimTime);
            randomSplit = ThreadLocalRandom.current().nextInt(minSplit, maxSplit + 1);
        }
        video.setStopTime(randomSplit);
        endVideo.setStartTime(randomSplit);
    }
}
