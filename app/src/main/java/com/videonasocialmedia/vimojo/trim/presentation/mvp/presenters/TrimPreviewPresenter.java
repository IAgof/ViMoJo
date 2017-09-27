/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.trim.presentation.mvp.presenters;

import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
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
import static com.videonasocialmedia.vimojo.utils.Constants.ADVANCE_PLAYER_PRECISION_LOW;

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

    public static final float MIN_TRIM_OFFSET = 0.5f;

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
        trimView.refreshDurationTag(video.getDuration());
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

        modifyVideoDurationUseCase.trimVideo(videoToEdit, startTimeMs, finishTimeMs,
                currentProject);

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
        Log.d(LOG_TAG, "onSuccessTranscoding after trim " + video.getTempPath());
        updateVideoRepositoryUseCase.succesTranscodingVideo(video);
    }

    @Override
    public void onErrorTranscoding(Video video, String message) {
        Log.d(LOG_TAG, "onErrorTranscoding " + video.getTempPath() + " - " + message);
        if (video.getNumTriesToExportVideo() < Constants.MAX_NUM_TRIES_TO_EXPORT_VIDEO) {
            video.increaseNumTriesToExportVideo();
            setTrim(video.getStartTime(), video.getStopTime());
        } else {
            //trimView.showError(message);
            updateVideoRepositoryUseCase.errorTranscodingVideo(video,
                    Constants.ERROR_TRANSCODING_TEMP_FILE_TYPE.TRIM.name());
        }
    }

    public void advanceBackwardStartTrimming(int advancePrecision, int startTimeMs, int finishTimeMs) {
        float adjustSeekBarMinPosition = (float) (startTimeMs - advancePrecision)
            / MS_CORRECTION_FACTOR;
        trimView.updateStartTrimmingRangeSeekBar(Math.max(0, adjustSeekBarMinPosition));
        //trimView.refreshDurationTag(finishTimeMs - startTimeMs);
    }

    public void advanceForwardStartTrimming(int advancePrecision, int startTimeMs, int finishTimeMs) {
        if(finishTimeMs - (startTimeMs + advancePrecision) < MIN_TRIM_OFFSET){
            return;
        }
        float adjustSeekBarMinPosition = (float) (startTimeMs + advancePrecision)
            / MS_CORRECTION_FACTOR;
        if(Math.abs(adjustSeekBarMinPosition - (float) finishTimeMs / MS_CORRECTION_FACTOR) < MIN_TRIM_OFFSET){
            adjustSeekBarMinPosition = ((float) finishTimeMs / MS_CORRECTION_FACTOR) - MIN_TRIM_OFFSET;
        }
        trimView.updateStartTrimmingRangeSeekBar(Math.min(adjustSeekBarMinPosition,
            ((float) finishTimeMs / MS_CORRECTION_FACTOR)));
        //trimView.refreshDurationTag(finishTimeMs - startTimeMs);
    }

    public void advanceBackwardEndTrimming(int advancePrecision, int startTimeMs, int finishTimeMs) {
        if((finishTimeMs - advancePrecision) - startTimeMs < MIN_TRIM_OFFSET){
            return;
        }

        float adjustSeekBarMaxPosition = (float) (finishTimeMs - advancePrecision)
            / MS_CORRECTION_FACTOR;
        if(Math.abs(adjustSeekBarMaxPosition - (float) startTimeMs / MS_CORRECTION_FACTOR) < MIN_TRIM_OFFSET){
            adjustSeekBarMaxPosition = (float) startTimeMs / MS_CORRECTION_FACTOR + MIN_TRIM_OFFSET;
        }
        trimView.updateFinishTrimmingRangeSeekBar(Math.max(adjustSeekBarMaxPosition, ((float) startTimeMs / MS_CORRECTION_FACTOR) - MIN_TRIM_OFFSET));
       // trimView.refreshDurationTag(finishTimeMs - startTimeMs);
    }

    public void advanceForwardEndTrimming(int advancePrecision, int startTimeMs, int finishTimeMs) {
        float adjustSeekBarMaxPosition = (float) (finishTimeMs + advancePrecision)
            / MS_CORRECTION_FACTOR;
        float maxRangeSeekBarValue = (float) videoToEdit.getFileDuration() / MS_CORRECTION_FACTOR;
        if (adjustSeekBarMaxPosition > maxRangeSeekBarValue) {
            adjustSeekBarMaxPosition = maxRangeSeekBarValue;
        }
        trimView.updateFinishTrimmingRangeSeekBar(Math.min(maxRangeSeekBarValue,
            adjustSeekBarMaxPosition));
        //trimView.refreshDurationTag(finishTimeMs - startTimeMs);
    }
}



