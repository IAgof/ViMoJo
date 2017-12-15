/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.trim.presentation.mvp.presenters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.widget.RadioButton;

import com.videonasocialmedia.videonamediaframework.model.media.utils.ElementChangedListener;

import com.videonasocialmedia.vimojo.R;
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

import static com.videonasocialmedia.vimojo.utils.Constants.MIN_TRIM_OFFSET;
import static com.videonasocialmedia.vimojo.utils.Constants.MS_CORRECTION_FACTOR;

/**
 * Created by vlf on 7/7/15.
 */
public class TrimPreviewPresenter implements OnVideosRetrieved, ElementChangedListener {

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
    private Context context;
    public UserEventTracker userEventTracker;
    public Project currentProject;
    private final String THEME_DARK = "dark";

    @Inject
    public TrimPreviewPresenter(TrimView trimView, Context context, UserEventTracker userEventTracker,
                                GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
                                ModifyVideoDurationUseCase modifyVideoDurationUseCase) {
        //this.trimView = new WeakReference<>(trimView);
        this.trimView = trimView;
        this.context = context;
        this.currentProject = loadCurrentProject();
        currentProject.addListener(this);
        this.userEventTracker = userEventTracker;
        this.getMediaListFromProjectUseCase = getMediaListFromProjectUseCase;
        this.modifyVideoDurationUseCase = modifyVideoDurationUseCase;
    }

    private Project loadCurrentProject() {
        // TODO(jliarte): this should make use of a repository or use case to load the Project
        return Project.getInstance(null, null, null, null);
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

    @Override
    public void onNoVideosRetrieved() {
        trimView.showError("No videos");
    }

    public void setTrim(int startTimeMs, int finishTimeMs) {

        modifyVideoDurationUseCase.trimVideo(videoToEdit, startTimeMs, finishTimeMs,
            currentProject);
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

    public void advanceForwardStartTrimming(int advancePrecision, int startTimeMs,
                                            int finishTimeMs) {
        if (((finishTimeMs - startTimeMs) / MS_CORRECTION_FACTOR) <= MIN_TRIM_OFFSET) {
            return;
        }
        float adjustSeekBarMinPosition = (float) (startTimeMs + advancePrecision)
            / MS_CORRECTION_FACTOR;
        if (Math.abs(adjustSeekBarMinPosition - (float) finishTimeMs / MS_CORRECTION_FACTOR)
            < MIN_TRIM_OFFSET) {
            adjustSeekBarMinPosition = ((float) finishTimeMs / MS_CORRECTION_FACTOR)
                - MIN_TRIM_OFFSET;
        }
        trimView.updateStartTrimmingRangeSeekBar(Math.min(adjustSeekBarMinPosition,
            ((float) finishTimeMs / MS_CORRECTION_FACTOR)));
    }

    public void advanceBackwardEndTrimming(int advancePrecision, int startTimeMs,
                                           int finishTimeMs) {
        if (((finishTimeMs - startTimeMs) / MS_CORRECTION_FACTOR) <= MIN_TRIM_OFFSET) {
            return;
        }

        float adjustSeekBarMaxPosition = (float) (finishTimeMs - advancePrecision)
            / MS_CORRECTION_FACTOR;
        if (Math.abs(adjustSeekBarMaxPosition - (float) startTimeMs / MS_CORRECTION_FACTOR)
            < MIN_TRIM_OFFSET) {
            adjustSeekBarMaxPosition = (startTimeMs / MS_CORRECTION_FACTOR) + MIN_TRIM_OFFSET;
        }
        trimView.updateFinishTrimmingRangeSeekBar(Math.max(adjustSeekBarMaxPosition,
            ((float) startTimeMs / MS_CORRECTION_FACTOR) - MIN_TRIM_OFFSET));
    }

    public void advanceForwardEndTrimming(int advancePrecision, int finishTimeMs) {

        float maxRangeSeekBarValue = (float) videoToEdit.getFileDuration() / MS_CORRECTION_FACTOR;
        float adjustSeekBarMaxPosition = Math.min(maxRangeSeekBarValue,
            (float) (finishTimeMs + advancePrecision) / MS_CORRECTION_FACTOR);
        trimView.updateFinishTrimmingRangeSeekBar(adjustSeekBarMaxPosition);
    }

    @Override
    public void onObjectUpdated() {
        trimView.updateProject();
    }


    public void updateColorRadioButtonSelected(RadioButton buttonSelected) {
        trimView.updateColorRadioButtonSelected(buttonSelected);
    }

    public void updateColorRadioButtonNoSelected(RadioButton buttonNoSelected) {
        TypedValue currentTheme = getCurrentTheme();
        if (currentTheme.string.equals(THEME_DARK)) {
            trimView.updateRadioButtonNoSelectedToThemeDark(buttonNoSelected);
        } else {
            trimView.updateRadioButtonNoSelectedToThemeLight(buttonNoSelected);
        }
    }

    public void updateColorButton() {
        TypedValue currentTheme = getCurrentTheme();
        if (currentTheme.string.equals(THEME_DARK)) {
            trimView.updateViewToThemeDark();
        } else {
            trimView.updateViewToThemeLight();
        }
    }

    @NonNull
    public TypedValue getCurrentTheme() {
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.themeName, outValue, true);
        return outValue;
    }
}



