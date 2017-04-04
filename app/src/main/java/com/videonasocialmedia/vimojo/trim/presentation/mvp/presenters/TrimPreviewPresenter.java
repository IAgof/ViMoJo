/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.trim.presentation.mvp.presenters;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.videonasocialmedia.transcoder.video.format.VideoTranscoderFormat;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelperListener;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.domain.video.UpdateVideoRepositoryUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideoFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.trim.domain.ModifyVideoDurationUseCase;
import com.videonasocialmedia.vimojo.trim.presentation.mvp.views.TrimView;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by vlf on 7/7/15.
 */
public class TrimPreviewPresenter implements OnVideosRetrieved, TranscoderHelperListener {

    /**
     * LOG_TAG
     */
    private final String LOG_TAG = getClass().getSimpleName();

    private Video videoToEdit;

    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    private ModifyVideoDurationUseCase modifyVideoDurationUseCase;
    private UpdateVideoRepositoryUseCase updateVideoRepositoryUseCase;

    // View reference. We use as a WeakReference
    // because the Activity could be destroyed at any time
    // and we don't want to create a memory leak
    //private WeakReference<TrimView> trimView;
    private TrimView trimView;
    public UserEventTracker userEventTracker;
    public Project currentProject;

    @Inject
    public TrimPreviewPresenter(TrimView trimView,
                                UserEventTracker userEventTracker,
                                GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
                                ModifyVideoDurationUseCase
                                    modifyVideoDurationUseCase,
                                UpdateVideoRepositoryUseCase
                                        updateVideoRepositoryUseCase) {
        //this.trimView = new WeakReference<>(trimView);
        this.trimView = trimView;
        this.currentProject = loadCurrentProject();
        this.userEventTracker = userEventTracker;
        this.getMediaListFromProjectUseCase = getMediaListFromProjectUseCase;
        this.modifyVideoDurationUseCase = modifyVideoDurationUseCase;
        this.updateVideoRepositoryUseCase = updateVideoRepositoryUseCase;
    }

    private Project loadCurrentProject() {
        // TODO(jliarte): this should make use of a repository or use case to load the Project
        return Project.getInstance(null, null, null);
    }

    public void init(int videoToTrimIndex) {
        List<Media> videoList = getMediaListFromProjectUseCase.getMediaListFromProject();
        if (videoList != null) {
            ArrayList<Video> v = new ArrayList<>();
            videoToEdit = (Video) videoList.get(videoToTrimIndex);
            v.add(videoToEdit);
            onVideosRetrieved(v);
        }
    }

    @Override
    public void onVideosRetrieved(List<Video> videoList) {
        trimView.showPreview(videoList);
        Video video = videoList.get(0);
        trimView.showTrimBar(video.getStartTime(), video.getStopTime(), video.getFileDuration());
    }

    private void showTimeTags(Video video) {
        trimView.refreshDurationTag(video.getDuration());
        trimView.refreshStartTimeTag(video.getStartTime());
        trimView.refreshStopTimeTag(video.getStopTime());
    }

    @Override
    public void onNoVideosRetrieved() {
        trimView.showError("No videos");
    }

    public void setTrim(int startTimeMs, int finishTimeMs) {
        VideoTranscoderFormat videoFormat = currentProject.getVMComposition().getVideoFormat();

        // TODO:(alvaro.martinez) 22/02/17 This drawable saved in app or sdk?
        Drawable drawableFadeTransitionVideo =
            ContextCompat.getDrawable(VimojoApplication.getAppContext(),
                R.drawable.alpha_transition_white);

        modifyVideoDurationUseCase.trimVideo(drawableFadeTransitionVideo, videoToEdit, videoFormat,
                startTimeMs, finishTimeMs, currentProject.getProjectPathIntermediateFileAudioFade(),
            this);

        trackVideoTrimmed();
    }

    public void trackVideoTrimmed() {
        userEventTracker.trackClipTrimmed(currentProject);
    }

    public void updateVideoTrim(int videoIndexOnTrack, int startTimeMs, int finishTimeMs) {
        List<Media> videoList = getMediaListFromProjectUseCase.getMediaListFromProject();
        if (videoList != null) {
            ArrayList<Video> v = new ArrayList<>();
            videoToEdit = (Video) videoList.get(videoIndexOnTrack);
            v.add(videoToEdit);
            onVideosRetrieved(v);
            trimView.showPreview(v);
            Video video = v.get(0);
            trimView.showTrimBar(startTimeMs, finishTimeMs, video.getDuration());
        }
    }

    @Override
    public void onSuccessTranscoding(Video video) {
        updateVideoRepositoryUseCase.updateVideo(video);
    }

    @Override
    public void onErrorTranscoding(Video video, String message) {
        Log.d(LOG_TAG, "onErrorTranscoding " + video.getTempPath() + " - " + message);
        if(video.getNumTriesToExportVideo() < Constants.MAX_NUM_TRIES_TO_EXPORT_VIDEO){
            video.increaseNumTriesToExportVideo();
            setTrim(video.getStartTime(), video.getStopTime());
        } else {
            //trimView.showError(message);
        }
    }
}



