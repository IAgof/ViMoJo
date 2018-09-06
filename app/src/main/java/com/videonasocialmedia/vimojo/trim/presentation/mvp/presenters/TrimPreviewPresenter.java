/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.trim.presentation.mvp.presenters;

import android.content.SharedPreferences;
import android.widget.RadioButton;

import com.videonasocialmedia.videonamediaframework.model.media.utils.ElementChangedListener;

import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateComposition;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.trim.domain.ModifyVideoDurationUseCase;
import com.videonasocialmedia.vimojo.trim.presentation.mvp.views.TrimView;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.view.VimojoPresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import static com.videonasocialmedia.vimojo.utils.Constants.MIN_TRIM_OFFSET;
import static com.videonasocialmedia.vimojo.utils.Constants.MS_CORRECTION_FACTOR;

/**
 * Created by vlf on 7/7/15.
 */
public class TrimPreviewPresenter extends VimojoPresenter implements OnVideosRetrieved,
    ElementChangedListener {
    private final String LOG_TAG = getClass().getSimpleName();
    private final ProjectInstanceCache projectInstanceCache;

    private Video videoToEdit;

    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    private ModifyVideoDurationUseCase modifyVideoDurationUseCase;

    // View reference. We use as a WeakReference
    // because the Activity could be destroyed at any time
    // and we don't want to create a memory leak
    //private WeakReference<TrimView> trimView;
    private TrimView trimView;
    private SharedPreferences sharedPreferences;
    protected UserEventTracker userEventTracker;
    protected Project currentProject;
    private int videoToTrimIndex;
    private UpdateComposition updateComposition;
    private boolean amIVerticalApp;

    @Inject
    public TrimPreviewPresenter(
            TrimView trimView, SharedPreferences sharedPreferences,
            UserEventTracker userEventTracker,
            GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
            ModifyVideoDurationUseCase modifyVideoDurationUseCase,
            ProjectInstanceCache projectInstanceCache, UpdateComposition updateComposition,
            @Named("amIAVerticalApp") boolean amIAVerticalApp) {
        this.trimView = trimView;
        this.sharedPreferences = sharedPreferences;
        this.userEventTracker = userEventTracker;
        this.getMediaListFromProjectUseCase = getMediaListFromProjectUseCase;
        this.modifyVideoDurationUseCase = modifyVideoDurationUseCase;
        this.projectInstanceCache = projectInstanceCache;
        this.updateComposition = updateComposition;
        this.amIVerticalApp = amIAVerticalApp;
    }

    public void init(int videoToTrimIndex) {
        this.videoToTrimIndex = videoToTrimIndex;
    }

    public void updatePresenter() {
        this.currentProject = projectInstanceCache.getCurrentProject();
        currentProject.addListener(this);
        List<Media> videoList = getMediaListFromProjectUseCase
                .getMediaListFromProject(currentProject);
        if (videoList != null) {
            ArrayList<Video> v = new ArrayList<>();
            videoToEdit = (Video) videoList.get(videoToTrimIndex);
            v.add(videoToEdit);
            onVideosRetrieved(v);
        }
        if (amIVerticalApp) {
            trimView.setAspectRatioVerticalVideos();
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
        // TODO(jliarte): 18/07/18 deal with this case for updating project and videos
        executeUseCaseCall(() -> updateComposition.updateComposition(currentProject));
    }

    void trackVideoTrimmed() {
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

    public void setupActivityViews() {
        updateViewsAccordingTheme();
    }

    public void updateRadioButtonsWithTheme(RadioButton radioButtonLow,
                                            RadioButton radioButtonMedium,
                                            RadioButton radioButtonHigh) {
        updateRadioButtonAccordingTheme(radioButtonLow);
        updateRadioButtonAccordingTheme(radioButtonMedium);
        updateRadioButtonAccordingTheme(radioButtonHigh);
    }

    void updateViewsAccordingTheme() {
        if (isThemeDarkActivated()) {
            trimView.updateViewToThemeDark();
        } else {
            trimView.updateViewToThemeLight();
        }
    }

    private void updateRadioButtonAccordingTheme(RadioButton buttonNoSelected) {
        if (isThemeDarkActivated()) {
            trimView.updateRadioButtonToThemeDark(buttonNoSelected);
        } else {
            trimView.updateRadioButtonToThemeLight(buttonNoSelected);
        }
    }

    private boolean isThemeDarkActivated() {
        return sharedPreferences.getBoolean(ConfigPreferences.THEME_APP_DARK,
            com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_THEME_DARK_STATE);
    }
}