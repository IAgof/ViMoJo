/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.trim.presentation.mvp.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RadioButton;

import com.crashlytics.android.Crashlytics;
import com.videonasocialmedia.videonamediaframework.model.VMComposition;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.utils.ElementChangedListener;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;
import com.videonasocialmedia.vimojo.asset.domain.usecase.UpdateMedia;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateComposition;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.trim.domain.ModifyVideoDurationUseCase;
import com.videonasocialmedia.vimojo.trim.domain.TrimResultCallback;
import com.videonasocialmedia.vimojo.trim.presentation.mvp.views.TrimView;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.view.BackgroundExecutor;
import com.videonasocialmedia.vimojo.view.VimojoPresenter;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Named;

import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_PLAYER_HEIGHT_VERTICAL_MODE;
import static com.videonasocialmedia.vimojo.utils.Constants.MS_CORRECTION_FACTOR;

/**
 * Created by vlf on 7/7/15.
 */
public class TrimPreviewPresenter extends VimojoPresenter implements ElementChangedListener {
    private final String LOG_TAG = getClass().getSimpleName();
    private final ProjectInstanceCache projectInstanceCache;
    private Video videoToEdit;
    private ModifyVideoDurationUseCase modifyVideoDurationUseCase;
    private UpdateMedia updateMedia;
    private UpdateComposition updateComposition;
    private final Context context;
    // View reference. We use as a WeakReference
    // because the Activity could be destroyed at any time
    // and we don't want to create a memory leak
    //private WeakReference<TrimView> trimView;
    private TrimView trimView;
    private VideonaPlayer videonaPlayerView;
    private SharedPreferences sharedPreferences;
    protected UserEventTracker userEventTracker;
    protected Project currentProject;
    private int videoIndexOnTrack;
    protected boolean amIVerticalApp;
    private Executor backgroundExecutor = Executors.newSingleThreadScheduledExecutor(); // TODO(jliarte): 13/09/18 explore the use of a background thread pool for all the app
    protected int startTimeMs;
    protected int finishTimeMs;
    protected final int MIN_TRIM_OFFSET_MS = 350;
    private int videoDuration;

    @Inject
    public TrimPreviewPresenter(
        Context context, TrimView trimView, VideonaPlayer videonaPlayerView,
        SharedPreferences sharedPreferences, UserEventTracker userEventTracker,
        ModifyVideoDurationUseCase modifyVideoDurationUseCase,
        ProjectInstanceCache projectInstanceCache, UpdateMedia updateMedia,
        UpdateComposition updateComposition, @Named("amIAVerticalApp") boolean amIAVerticalApp,
        BackgroundExecutor backgroundExecutor) {
        super(backgroundExecutor, userEventTracker);
        this.context = context;
        this.trimView = trimView;
        this.videonaPlayerView = videonaPlayerView;
        this.sharedPreferences = sharedPreferences;
        this.userEventTracker = userEventTracker;
        this.modifyVideoDurationUseCase = modifyVideoDurationUseCase;
        this.projectInstanceCache = projectInstanceCache;
        this.updateMedia = updateMedia;
        this.updateComposition = updateComposition;
        this.amIVerticalApp = amIAVerticalApp;
    }

    public void updatePresenter(int videoIndexOnTrack) {
        this.videoIndexOnTrack = videoIndexOnTrack;
        this.currentProject = projectInstanceCache.getCurrentProject();
        currentProject.addListener(this);
        videonaPlayerView.attachView(context);
        loadProjectVideo();
        if (amIVerticalApp) {
            videonaPlayerView
                .setAspectRatioVerticalVideos(DEFAULT_PLAYER_HEIGHT_VERTICAL_MODE);
        }
    }

    public void removePresenter() {
        videonaPlayerView.detachView();
    }

    private void loadProjectVideo() {
        videoToEdit = (Video) currentProject.getVMComposition().getMediaTrack().getItems()
            .get(videoIndexOnTrack);
        VMComposition vmCompositionCopy = null;
        try {
            vmCompositionCopy = new VMComposition(currentProject.getVMComposition());
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            illegalItemOnTrack.printStackTrace();
            Crashlytics.log("Error getting copy VMComposition " + illegalItemOnTrack);
        }
        Video videoCopy = (Video) vmCompositionCopy.getMediaTrack().getItems().get(videoIndexOnTrack);
        videonaPlayerView.initSingleClip(vmCompositionCopy, videoIndexOnTrack);
        trimView.refreshDurationTag(videoCopy.getDuration());
        videoDuration = videoCopy.getStopTime() - videoCopy.getStartTime();
        startTimeMs = 0;
        finishTimeMs = videoDuration;
        trimView.showTrimBar(videoDuration);
        trimView.updateStartTrimmingRangeSeekBar(0);
        trimView.updateFinishTrimmingRangeSeekBar(videoCopy.getDuration() / Constants.MS_CORRECTION_FACTOR);
    }

    public void setTrim() {
        addCallback(modifyVideoDurationUseCase
                .trimVideo(videoToEdit, startTimeMs, finishTimeMs, currentProject),
            new TrimResultCallback(videoToEdit, currentProject, updateMedia, updateComposition));
        trackVideoTrimmed();
        trimView.navigateTo(EditActivity.class, videoIndexOnTrack);
    }

    void trackVideoTrimmed() {
        userEventTracker.trackClipTrimmed(currentProject);
    }

    public void advanceBackwardStartTrimming(int advancePrecision) {
        startTimeMs = startTimeMs - advancePrecision;
        if (startTimeMs < 0) {
            startTimeMs = 0;
        }
        trimView.updateStartTrimmingRangeSeekBar(startTimeMs / MS_CORRECTION_FACTOR);
        trimView.refreshDurationTag(finishTimeMs - startTimeMs);
        videonaPlayerView.seekTo(startTimeMs);
    }

    public void advanceForwardStartTrimming(int advancePrecision) {
        if (((finishTimeMs - startTimeMs)) <= MIN_TRIM_OFFSET_MS) {
            return;
        }
        startTimeMs = startTimeMs + advancePrecision;
        if (Math.abs(finishTimeMs - startTimeMs) < MIN_TRIM_OFFSET_MS) {
            startTimeMs = finishTimeMs - MIN_TRIM_OFFSET_MS ;
        }
        trimView.updateStartTrimmingRangeSeekBar(startTimeMs / MS_CORRECTION_FACTOR);
        trimView.refreshDurationTag(finishTimeMs - startTimeMs);
        videonaPlayerView.seekTo(startTimeMs);
    }

    public void advanceBackwardEndTrimming(int advancePrecision) {
        if ((finishTimeMs - startTimeMs)  <= MIN_TRIM_OFFSET_MS) {
            return;
        }
        finishTimeMs = finishTimeMs - advancePrecision;
        if (finishTimeMs - startTimeMs < MIN_TRIM_OFFSET_MS) {
            finishTimeMs = startTimeMs + MIN_TRIM_OFFSET_MS;
        }
        trimView.updateFinishTrimmingRangeSeekBar(finishTimeMs / MS_CORRECTION_FACTOR);
        trimView.refreshDurationTag(finishTimeMs - startTimeMs);
        videonaPlayerView.seekTo(finishTimeMs);
    }

    public void advanceForwardEndTrimming(int advancePrecision) {
        finishTimeMs = finishTimeMs + advancePrecision;
        if (finishTimeMs > videoDuration) {
            finishTimeMs = videoDuration;
        }
        trimView.updateFinishTrimmingRangeSeekBar(finishTimeMs / MS_CORRECTION_FACTOR);
        trimView.refreshDurationTag(finishTimeMs - startTimeMs);
        videonaPlayerView.seekTo(finishTimeMs);
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

    public void onRangeSeekBarChanged(Object minValue, Object maxValue) {
        videonaPlayerView.pausePreview();
        float minValueFloat = (float) minValue;
        float maxValueFloat = (float) maxValue;
        if (isRangeSeekBarLessThanMinTrimOffset(minValueFloat, maxValueFloat)) {
            if (startTimeMs != (int) ( minValueFloat * Constants.MS_CORRECTION_FACTOR)) {
                startTimeMs = finishTimeMs - MIN_TRIM_OFFSET_MS;
                trimView.updateStartTrimmingRangeSeekBar(startTimeMs / MS_CORRECTION_FACTOR);
                trimView.refreshDurationTag(finishTimeMs - startTimeMs);
                videonaPlayerView.seekTo(startTimeMs);
                return;
            }
            if (finishTimeMs != (int) ( maxValueFloat * Constants.MS_CORRECTION_FACTOR)) {
                finishTimeMs = startTimeMs + MIN_TRIM_OFFSET_MS;
                trimView.updateFinishTrimmingRangeSeekBar(finishTimeMs / MS_CORRECTION_FACTOR);
                trimView.refreshDurationTag(finishTimeMs - startTimeMs);
                videonaPlayerView.seekTo(finishTimeMs);
                return;
            }
        }
        if (startTimeMs != (int) ( minValueFloat * Constants.MS_CORRECTION_FACTOR)) {
            startTimeMs = (int) (minValueFloat * Constants.MS_CORRECTION_FACTOR);
            videonaPlayerView.seekTo(startTimeMs);
        } else {
            if (finishTimeMs != (int) (maxValueFloat * Constants.MS_CORRECTION_FACTOR)) {
                finishTimeMs = (int) (maxValueFloat * Constants.MS_CORRECTION_FACTOR);
                videonaPlayerView.seekTo(finishTimeMs);
            }
        }
        trimView.refreshDurationTag(finishTimeMs - startTimeMs);
    }

    private boolean isRangeSeekBarLessThanMinTrimOffset(float minValueFloat, float maxValueFloat) {
        return Math.abs(maxValueFloat - minValueFloat) <= Constants.MIN_TRIM_OFFSET;
    }

    public void cancelTrim() {
        trimView.navigateTo(EditActivity.class, videoIndexOnTrack);
    }
}