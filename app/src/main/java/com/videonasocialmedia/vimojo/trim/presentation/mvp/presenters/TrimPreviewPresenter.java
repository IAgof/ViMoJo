/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.trim.presentation.mvp.presenters;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.trim.domain.ModifyVideoDurationUseCase;
import com.videonasocialmedia.vimojo.trim.presentation.mvp.views.TrimView;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.videonasocialmedia.vimojo.trim.presentation.views.activity.VideoTrimActivity.MS_CORRECTION_FACTOR;

/**
 * Created by vlf on 7/7/15.
 */
public class TrimPreviewPresenter implements OnVideosRetrieved {

    /**
     * LOG_TAG
     */
    private final String LOG_TAG = getClass().getSimpleName();

    private Video videoToEdit;

    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    private ModifyVideoDurationUseCase modifyVideoDurationUseCase;

    // View reference. We use as a WeakReference
    // because the Activity could be destroyed at any time
    // and we don't want to create a memory leak
    //private WeakReference<TrimView> trimView;
    private TrimView trimView;
    public UserEventTracker userEventTracker;
    public Project currentProject;

    @Inject
    public TrimPreviewPresenter(TrimView trimView, UserEventTracker userEventTracker,
                                GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
                                ModifyVideoDurationUseCase modifyVideoDurationUseCase) {
        //this.trimView = new WeakReference<>(trimView);
        this.trimView = trimView;
        this.currentProject = loadCurrentProject();
        this.userEventTracker = userEventTracker;
        this.getMediaListFromProjectUseCase = getMediaListFromProjectUseCase;
        this.modifyVideoDurationUseCase = modifyVideoDurationUseCase;
    }

    private Project loadCurrentProject() {
        // TODO(jliarte): this should make use of a repository or use case to load the Project
        return Project.getInstance(null, null,null, null);
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
        VideonaFormat videoFormat = currentProject.getVMComposition().getVideoFormat();

        // TODO:(alvaro.martinez) 22/02/17 This drawable saved in app or sdk?
        Drawable drawableFadeTransitionVideo =
            ContextCompat.getDrawable(VimojoApplication.getAppContext(),
                R.drawable.alpha_transition_white);

        modifyVideoDurationUseCase.trimVideo(drawableFadeTransitionVideo, videoToEdit, videoFormat,
                startTimeMs, finishTimeMs,
                currentProject.getProjectPathIntermediateFileAudioFade());

        trackVideoTrimmed();
    }

    public void trackVideoTrimmed() {
        userEventTracker.trackClipTrimmed(currentProject);
    }

    public void advanceBackwardStartTrimming(int advancePrecision, int startTimeMs) {
        float adjustSeekBarMinPosition = (float) (startTimeMs - advancePrecision)
            / MS_CORRECTION_FACTOR;
        trimView.updateStartTrimmingRangeSeekBar(Math.max(0, adjustSeekBarMinPosition));
    }

    public void advanceForwardStartTrimming(int advancePrecision, int startTimeMs) {
        float adjustSeekBarMinPosition = (float) (startTimeMs + advancePrecision)
            / MS_CORRECTION_FACTOR;
        trimView.updateStartTrimmingRangeSeekBar(adjustSeekBarMinPosition);
    }

    public void advanceBackwardEndTrimming(int advancePrecision, int finishTimeMs) {
        float adjustSeekBarMaxPosition = (float) (finishTimeMs - advancePrecision)
            / MS_CORRECTION_FACTOR;
        trimView.updateFinishTrimmingRangeSeekBar(adjustSeekBarMaxPosition);
    }

    public void advanceForwardEndTrimming(int advancePrecision, int finishTimeMs) {
        float adjustSeekBarMaxPosition = (float) (finishTimeMs + advancePrecision)
            / MS_CORRECTION_FACTOR;
        float maxRangeSeekBarValue = (float) videoToEdit.getFileDuration() / MS_CORRECTION_FACTOR;
        if (adjustSeekBarMaxPosition > maxRangeSeekBarValue) {
            adjustSeekBarMaxPosition = maxRangeSeekBarValue;
        }
        trimView.updateFinishTrimmingRangeSeekBar(Math.min(maxRangeSeekBarValue,
            adjustSeekBarMaxPosition));
    }
}



