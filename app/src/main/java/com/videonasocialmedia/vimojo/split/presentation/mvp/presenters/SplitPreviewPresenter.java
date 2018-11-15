/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.split.presentation.mvp.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RadioButton;

import com.crashlytics.android.Crashlytics;
import com.videonasocialmedia.videonamediaframework.model.VMComposition;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.utils.ElementChangedListener;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Video;

import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.split.domain.VideoAndCompositionUpdaterOnSplitSuccess;
import com.videonasocialmedia.vimojo.split.presentation.mvp.views.SplitView;
import com.videonasocialmedia.vimojo.split.domain.SplitVideoUseCase;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.view.BackgroundExecutor;
import com.videonasocialmedia.vimojo.view.VimojoPresenter;

import javax.inject.Inject;
import javax.inject.Named;

import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_PLAYER_HEIGHT_VERTICAL_MODE;

/**
 * Created by vlf on 7/7/15.
 */
public class SplitPreviewPresenter extends VimojoPresenter implements ElementChangedListener {

    private final String LOG_TAG = getClass().getSimpleName();
    private final ProjectInstanceCache projectInstanceCache;
    private Context context;
    private SplitVideoUseCase splitVideoUseCase;
    private Video videoToEdit;
    private SplitView splitView;
    private SharedPreferences sharedPreferences;
    protected UserEventTracker userEventTracker;
    protected Project currentProject;
    private int maxSeekBarSplit;
    private int videoIndexOnTrack;
    private boolean amIAVerticalApp;

    private int currentSplitPosition = 0;

    @Inject
    public SplitPreviewPresenter(Context context, SplitView splitView,
                                 SharedPreferences sharedPreferences,
                                 UserEventTracker userEventTracker,
                                 SplitVideoUseCase splitVideoUseCase,
                                 ProjectInstanceCache projectInstanceCache,
                                 @Named("amIAVerticalApp") boolean amIAVerticalApp,
                                 BackgroundExecutor backgroundExecutor) {
        super(backgroundExecutor, userEventTracker);
        this.context = context;
        this.splitView = splitView;
        this.sharedPreferences = sharedPreferences;
        this.userEventTracker = userEventTracker;
        this.splitVideoUseCase = splitVideoUseCase;
        this.projectInstanceCache = projectInstanceCache;
        this.amIAVerticalApp = amIAVerticalApp;
    }

    public void updatePresenter(int videoIndexOnTrack) {
        this.videoIndexOnTrack = videoIndexOnTrack;
        currentProject = projectInstanceCache.getCurrentProject();
        currentProject.addListener(this);
        splitView.attachView(context);
        loadProjectVideo();
        if (amIAVerticalApp) {
            splitView.setAspectRatioVerticalVideos(DEFAULT_PLAYER_HEIGHT_VERTICAL_MODE);
        }
    }

    public void pausePresenter() {
        splitView.detachView();
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
        splitView.initSingleClip(vmCompositionCopy, videoIndexOnTrack);
        maxSeekBarSplit =  videoCopy.getStopTime() - videoCopy.getStartTime();
        splitView.initSplitView(maxSeekBarSplit);
        splitView.setVideonaPlayerListener();
    }

    public void splitVideo() {
        // TODO(jliarte): 18/07/18 deal with this case for updating project and videos
        executeUseCaseCall(() -> splitVideoUseCase
                .splitVideo(currentProject, videoToEdit, videoIndexOnTrack, currentSplitPosition,
                        new VideoAndCompositionUpdaterOnSplitSuccess(currentProject)));
        trackSplitVideo();
        splitView.navigateTo(EditActivity.class, videoIndexOnTrack);
    }

    protected void trackSplitVideo() {
        userEventTracker.trackClipSplitted(currentProject);
    }

    public void advanceBackwardStartSplitting(int advancePlayerPrecision) {
        if (currentSplitPosition > advancePlayerPrecision) {
            currentSplitPosition = currentSplitPosition - advancePlayerPrecision;
        }
        splitView.updateSplitSeekbar(currentSplitPosition);
        splitView.refreshTimeTag(currentSplitPosition);
        splitView.seekTo(currentSplitPosition);
    }

    public void advanceForwardEndSplitting(int advancePlayerPrecision) {
        currentSplitPosition = currentSplitPosition + advancePlayerPrecision;
        splitView.updateSplitSeekbar(Math.min(maxSeekBarSplit, currentSplitPosition));
        splitView.refreshTimeTag(currentSplitPosition);
        splitView.seekTo(currentSplitPosition);
    }

    @Override
    public void onObjectUpdated() {
        splitView.updateProject();
    }

    public void onSeekBarChanged(int progress) {
        currentSplitPosition = progress;
        splitView.seekTo(progress);
        splitView.refreshTimeTag(progress);
    }

    public void cancelSplit() {
        splitView.navigateTo(EditActivity.class, videoIndexOnTrack);
    }

    public void setupActivityViews() {
        updateViewsAccordingTheme();
    }

    private void updateViewsAccordingTheme() {
        if (isThemeDarkActivated()) {
            splitView.updateViewToThemeDark();
        } else {
            splitView.updateViewToThemeLight();
        }
    }

    public void updateRadioButtonsWithTheme(RadioButton radioButtonLow,
                                            RadioButton radioButtonMedium,
                                            RadioButton radioButtonHigh) {
        updateRadioButtonAccordingTheme(radioButtonLow);
        updateRadioButtonAccordingTheme(radioButtonMedium);
        updateRadioButtonAccordingTheme(radioButtonHigh);
    }

    private void updateRadioButtonAccordingTheme(RadioButton buttonNoSelected) {
        if (isThemeDarkActivated()) {
            splitView.updateRadioButtonToThemeDark(buttonNoSelected);
        } else {
            splitView.updateRadioButtonToThemeLight(buttonNoSelected);
        }
    }

    private boolean isThemeDarkActivated() {
        return sharedPreferences.getBoolean(ConfigPreferences.THEME_APP_DARK,
            com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_THEME_DARK_STATE);
    }
}
