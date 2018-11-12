/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.trim.presentation.mvp.presenters;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.videonamediaframework.model.VMComposition;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;
import com.videonasocialmedia.vimojo.asset.domain.usecase.UpdateMedia;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateComposition;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.trim.domain.ModifyVideoDurationUseCase;
import com.videonasocialmedia.vimojo.trim.presentation.mvp.views.TrimView;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.view.BackgroundExecutor;
import com.videonasocialmedia.vimojo.vimojoapiclient.CompositionApiClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by jliarte on 10/06/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class TrimPreviewPresenterTest {
    @Mock private Context mockedContext;
    @Mock private TrimView mockedTrimView;
    @Mock private SharedPreferences mockedSharedPreferences;
    @Mock private UserEventTracker mockedUserEventTracker;
    @Mock ModifyVideoDurationUseCase mockedModifyVideoDurationUseCase;
    @Mock ProjectInstanceCache mockedProjectInstanceCache;
    @Mock CompositionApiClient mockedCompositionApiClient;
    private Project currentProject;
    @Mock private UpdateMedia mockedUpdateMedia;
    @Mock private UpdateComposition mockedUpdateComposition;
    private boolean amIAVerticalApp;
    @Mock BackgroundExecutor mockedBackgroundExecutor;
    @Mock private ListenableFuture mockedListenableFuture;

    @Before
    public void injectMocks() throws IllegalItemOnTrack {
        MockitoAnnotations.initMocks(this);
        setAProjectWithSomeVideo();
        when(mockedProjectInstanceCache.getCurrentProject()).thenReturn(currentProject);
    }

    @Test
    public void constructorSetsUserTracker() {
        UserEventTracker userEventTracker = UserEventTracker.getInstance();
        TrimPreviewPresenter trimPreviewPresenter = new TrimPreviewPresenter(
                mockedContext, mockedTrimView, mockedSharedPreferences, userEventTracker,
                mockedModifyVideoDurationUseCase, mockedProjectInstanceCache, mockedUpdateMedia,
                mockedUpdateComposition, amIAVerticalApp, mockedBackgroundExecutor);

        assertThat(trimPreviewPresenter.userEventTracker, is(userEventTracker));
    }

    @Test
    public void updatePresenterSetsCurrentProject() {
        TrimPreviewPresenter trimPreviewPresenter = getTrimPreviewPresenter();
        int videoIndexOnTrack = 0;

        trimPreviewPresenter.updatePresenter(videoIndexOnTrack);

        assertThat(trimPreviewPresenter.currentProject, is(currentProject));
    }


    @Test
    public void setTrimCallsTracking() {
        TrimPreviewPresenter trimPreviewPresenter = getTrimPreviewPresenter();

        trimPreviewPresenter.trackVideoTrimmed();

        verify(mockedUserEventTracker).trackClipTrimmed(currentProject);
    }

    @Test
    public void setTrimCallsUseCase() {
        TrimPreviewPresenter spyTrimPreviewPresenter = getTrimPreviewPresenter();
        int videoIndexOnTrack = 0;
        Video video = (Video) currentProject.getMediaTrack().getItems().get(0);
        spyTrimPreviewPresenter.updatePresenter(videoIndexOnTrack);
        int startTime = video.getStartTime();
        int finishTime = video.getStopTime();

        spyTrimPreviewPresenter.setTrim();

        verify(mockedModifyVideoDurationUseCase).trimVideo(video, startTime, finishTime,
            currentProject);
    }

    @Test
    public void updatePresenterInitTrimmingTags() {
        TrimPreviewPresenter trimPreviewPresenter = getTrimPreviewPresenter();
        int videoIndexOnTrack = 0;
        Video video = (Video) currentProject.getMediaTrack().getItems().get(0);

        trimPreviewPresenter.updatePresenter(videoIndexOnTrack);

        verify(mockedTrimView).updateStartTrimmingRangeSeekBar(0);
        verify(mockedTrimView).updateFinishTrimmingRangeSeekBar(video.getDuration()
            / Constants.MS_CORRECTION_FACTOR);
        verify(mockedTrimView).showTrimBar(video.getDuration());
        verify(mockedTrimView).refreshDurationTag(video.getDuration());
    }

    @Test
    public void updatePresenterAttachView() {
        TrimPreviewPresenter trimPreviewPresenter = getTrimPreviewPresenter();
        int videoIndexOnTrack = 0;

        trimPreviewPresenter.updatePresenter(videoIndexOnTrack);

        verify(mockedTrimView).attachView(mockedContext);
    }

    @Test
    public void updatePresenterSetAspectRatioVerticalIfIsAVerticalApp() {
        TrimPreviewPresenter spyTrimPreviewPresenter = spy(getTrimPreviewPresenter());
        int videoIndexOnTrack = 0;
        spyTrimPreviewPresenter.amIVerticalApp = true;

        spyTrimPreviewPresenter.updatePresenter(videoIndexOnTrack);

        verify(mockedTrimView)
            .setAspectRatioVerticalVideos(Constants.DEFAULT_PLAYER_HEIGHT_VERTICAL_MODE);
    }

    @Test
    public void updatePresenterInitSingleComposition() {
        TrimPreviewPresenter trimPreviewPresenter = getTrimPreviewPresenter();
        int videoIndexOnTrack = 0;

        trimPreviewPresenter.updatePresenter(videoIndexOnTrack);

        verify(mockedTrimView).initSingleClip(any(VMComposition.class), eq(videoIndexOnTrack));
    }

    @Test
    public void pausePresenterDetachPlayerView() {
        TrimPreviewPresenter trimPreviewPresenter = getTrimPreviewPresenter();

        trimPreviewPresenter.pausePresenter();

        verify(mockedTrimView).detachView();
    }

    @Test
    public void advanceBackwardStartTrimmingCallsUpdateStartTrimmingRangeSeekBarAndPlayer() {
        TrimPreviewPresenter presenter = getTrimPreviewPresenter();
        int advancePrecision = 600; //ms

        presenter.advanceBackwardStartTrimming(advancePrecision);

        verify(mockedTrimView).updateStartTrimmingRangeSeekBar(anyFloat());
        verify(mockedTrimView).refreshDurationTag(anyInt());
        verify(mockedTrimView).seekTo(anyInt());
    }

    @Test
    public void advanceForwardStartTrimmingCallsUpdateStartTrimmingRangeSeekBarAndPlayer() {
        TrimPreviewPresenter spyTrimPreviewPresenter = spy(getTrimPreviewPresenter());
        spyTrimPreviewPresenter.startTimeMs = 500;
        spyTrimPreviewPresenter.finishTimeMs = 1200;
        int advancePrecision = 600; //ms

        spyTrimPreviewPresenter.advanceForwardStartTrimming(advancePrecision);

        verify(mockedTrimView).updateStartTrimmingRangeSeekBar(anyFloat());
        verify(mockedTrimView).refreshDurationTag(anyInt());
        verify(mockedTrimView).seekTo(anyInt());
    }

    @Test
    public void advanceForwardStartTrimmingAdjustProperlyWithMinTrimOffset() {
        TrimPreviewPresenter spyTrimPreviewPresenter = spy(getTrimPreviewPresenter());
        int videoIndexOnTrack = 0;
        spyTrimPreviewPresenter.updatePresenter(videoIndexOnTrack);
        int advancePrecision = 600; //ms
        spyTrimPreviewPresenter.startTimeMs = 500;
        spyTrimPreviewPresenter.finishTimeMs = 1000;
        float startTimeMsAdjusted = spyTrimPreviewPresenter.finishTimeMs
            - spyTrimPreviewPresenter.MIN_TRIM_OFFSET_MS;

        spyTrimPreviewPresenter.advanceForwardStartTrimming(advancePrecision);

        verify(mockedTrimView).updateStartTrimmingRangeSeekBar(startTimeMsAdjusted
            / Constants.MS_CORRECTION_FACTOR);
    }

    @Test
    public void advanceBackwardEndTrimmingCallsUpdateFinishTrimmingRangeSeekBarAndPlayer() {
        TrimPreviewPresenter spyTrimPreviewPresenter = spy(getTrimPreviewPresenter());
        spyTrimPreviewPresenter.startTimeMs = 500;
        spyTrimPreviewPresenter.finishTimeMs = 1200;
        int advancePrecision = 600; //ms

        spyTrimPreviewPresenter.advanceBackwardEndTrimming(advancePrecision);

        verify(mockedTrimView).updateFinishTrimmingRangeSeekBar(anyFloat());
        verify(mockedTrimView).refreshDurationTag(anyInt());
        verify(mockedTrimView).seekTo(anyInt());
    }

    @Test
    public void advanceBackwardEndTrimmingAdjustProperlyWithMinTrimOffset() {
        TrimPreviewPresenter spyTrimPreviewPresenter = spy(getTrimPreviewPresenter());
        spyTrimPreviewPresenter.startTimeMs = 500;
        spyTrimPreviewPresenter.finishTimeMs = 1200;
        int advancePrecision = 600; //ms
        float finishTimeMsAdjusted = spyTrimPreviewPresenter.startTimeMs
            + spyTrimPreviewPresenter.MIN_TRIM_OFFSET_MS;

        spyTrimPreviewPresenter.advanceBackwardEndTrimming(advancePrecision);

        verify(mockedTrimView).updateFinishTrimmingRangeSeekBar(finishTimeMsAdjusted
            / Constants.MS_CORRECTION_FACTOR);
    }

    @Test
    public void advanceForwardEndTrimmingCallsUpdateFinishTrimmingRangeSeekBarAndPlayer() {
        TrimPreviewPresenter spyTrimPreviewPresenter = spy(getTrimPreviewPresenter());
        spyTrimPreviewPresenter.startTimeMs = 500;
        spyTrimPreviewPresenter.finishTimeMs = 1200;
        int advancePrecision = 600; //ms

        spyTrimPreviewPresenter.advanceBackwardEndTrimming(advancePrecision);

        verify(mockedTrimView).updateFinishTrimmingRangeSeekBar(anyFloat());
        verify(mockedTrimView).refreshDurationTag(anyInt());
        verify(mockedTrimView).seekTo(anyInt());
    }

    @Test
    public void onRangeSeekBarChangedUpdateTrimmingTagsAndPlayer() {
        TrimPreviewPresenter spyTrimPreviewPresenter = spy(getTrimPreviewPresenter());
        int startTimeMs = 500;
        int finishTimeMs = 1500;
        spyTrimPreviewPresenter.startTimeMs = startTimeMs;
        spyTrimPreviewPresenter.finishTimeMs = finishTimeMs;
        float maxValue = 0.75f;
        float minValue = 0.25f;

        spyTrimPreviewPresenter.onRangeSeekBarChanged(minValue, maxValue);

        verify(mockedTrimView).refreshDurationTag(anyInt());
        verify(mockedTrimView).seekTo(anyInt());
    }

    @Test
    public void setupActivityViewsCallsUpdateViewToThemeDarkIfThemeIsDark() {
        TrimPreviewPresenter trimPreviewPresenter = getTrimPreviewPresenter();
        when(mockedSharedPreferences.getBoolean(ConfigPreferences.THEME_APP_DARK,
            com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_THEME_DARK_STATE)).thenReturn(true);

        trimPreviewPresenter.updateViewsAccordingTheme();

        verify(mockedTrimView).updateViewToThemeDark();
    }

    @NonNull
    private TrimPreviewPresenter getTrimPreviewPresenter() {
        TrimPreviewPresenter trimPreviewPresenter = new TrimPreviewPresenter(
                mockedContext, mockedTrimView, mockedSharedPreferences, mockedUserEventTracker,
                mockedModifyVideoDurationUseCase, mockedProjectInstanceCache, mockedUpdateMedia,
                mockedUpdateComposition, amIAVerticalApp, mockedBackgroundExecutor);
        trimPreviewPresenter.currentProject = currentProject;
        return trimPreviewPresenter;
    }

    private void setAProjectWithSomeVideo() throws IllegalItemOnTrack {
        Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
                VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
        List<String> productType = new ArrayList<>();
        ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
        currentProject = new Project(projectInfo, "/path", "private/path", compositionProfile);
        Video video = new Video("some/path", Video.DEFAULT_VOLUME);
        currentProject.getMediaTrack().insertItem(video);
    }
}
