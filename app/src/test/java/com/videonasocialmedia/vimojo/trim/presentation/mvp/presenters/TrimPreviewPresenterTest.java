/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.trim.presentation.mvp.presenters;


import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.trim.domain.ModifyVideoDurationUseCase;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;

import com.videonasocialmedia.vimojo.trim.presentation.mvp.views.TrimView;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by jliarte on 10/06/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class TrimPreviewPresenterTest {
    @Mock private TrimView mockedTrimView;
    @Mock private SharedPreferences mockedSharedPreferences;
    @Mock private UserEventTracker mockedUserEventTracker;
    @Mock GetMediaListFromProjectUseCase mockedGetMediaListFromProjectUseCase;
    @Mock ModifyVideoDurationUseCase mockedModifyVideoDurationUseCase;
    @Mock ProjectInstanceCache mockedProjectInstanceCache;
    private Project currentProject;
    List<Media> videoList = new ArrayList<>();

    @Before
    public void injectMocks() {
        MockitoAnnotations.initMocks(this);
        setAProject();
        when(mockedProjectInstanceCache.getCurrentProject()).thenReturn(currentProject);
        getAVideoList();
        when(mockedGetMediaListFromProjectUseCase.getMediaListFromProject(currentProject))
            .thenReturn(videoList);
    }

    @Test
    public void constructorSetsUserTracker() {
        UserEventTracker userEventTracker = UserEventTracker.getInstance();
        TrimPreviewPresenter trimPreviewPresenter = new TrimPreviewPresenter(mockedTrimView,
            mockedSharedPreferences, userEventTracker, mockedGetMediaListFromProjectUseCase,
            mockedModifyVideoDurationUseCase, mockedProjectInstanceCache);

        assertThat(trimPreviewPresenter.userEventTracker, is(userEventTracker));
    }

    @Test
    public void updatePresenterSetsCurrentProject() {
        TrimPreviewPresenter trimPreviewPresenter = getTrimPreviewPresenter();

        trimPreviewPresenter.updatePresenter();

        assertThat(trimPreviewPresenter.currentProject, is(currentProject));
    }


    @Test
    public void setTrimCallsTracking() {
        TrimPreviewPresenter trimPreviewPresenter = getTrimPreviewPresenter();

        trimPreviewPresenter.trackVideoTrimmed();

        verify(mockedUserEventTracker).trackClipTrimmed(currentProject);
    }

    @Test
    public void setTrimUpdateVideoTimes() {

    }

    @Test
    public void advanceBackwardStartTrimmingCallsUpdateStartTrimmingRangeSeekBar() {
        TrimPreviewPresenter presenter = getTrimPreviewPresenter();
        int advancePrecision = 600; //ms
        int startTimeMs = 1200; //ms

        presenter.advanceBackwardStartTrimming(advancePrecision, startTimeMs);

        verify(mockedTrimView).updateStartTrimmingRangeSeekBar(anyFloat());
    }

    @Test
    public void advanceForwardStartTrimmingCallsUpdateStartTrimmingRangeSeekBar() {
        TrimPreviewPresenter presenter = getTrimPreviewPresenter();
        int advancePrecision = 600; //ms
        int startTimeMs = 1200; //ms
        int finishTimeMs = 2400; //ms

        presenter.advanceForwardStartTrimming(advancePrecision, startTimeMs, finishTimeMs);

        verify(mockedTrimView).updateStartTrimmingRangeSeekBar(anyFloat());
    }

    @Test
    public void advanceForwardStartTrimmingAdjustProperlyWithMinTrimOffset() {
        TrimPreviewPresenter presenter = getTrimPreviewPresenter();
        int advancePrecision = 600; //ms
        int startTimeMs = 1200; //ms
        int finishTimeMs = 1800; //ms
        int MIN_TRIM_OFFSET = 350;
        float MS_CORRECTION_FACTOR = 1000f;
        float startTimeMsAdjusted = 1.4499999f; // Math.min(startTimeMs + advancePrecision, finishTimeMs-MIN_TRIM_OFFSET);

        presenter.advanceForwardStartTrimming(advancePrecision, startTimeMs, finishTimeMs);

        verify(mockedTrimView).updateStartTrimmingRangeSeekBar(startTimeMsAdjusted);
    }

    @Test
    public void advanceBackwardEndTrimmingCallsUpdateFinishTrimmingRangeSeekBar() {
        TrimPreviewPresenter presenter = getTrimPreviewPresenter();
        int advancePrecision = 600; //ms
        int startTimeMs = 1200; //ms
        int finishTimeMs = 2400; //ms

        presenter.advanceBackwardEndTrimming(advancePrecision, startTimeMs, finishTimeMs);

        verify(mockedTrimView).updateFinishTrimmingRangeSeekBar(anyFloat());
    }

    @Test
    public void advanceBackwardEndTrimmingAdjustProperlyWithMinTrimOffset() {
        TrimPreviewPresenter presenter = getTrimPreviewPresenter();
        int advancePrecision = 600; //ms
        int startTimeMs = 1800; //ms
        int finishTimeMs = 2400; //ms
        int MIN_TRIM_OFFSET = 350;
        float MS_CORRECTION_FACTOR = 1000f;
        float finishTimeMsAdjusted = 2.1499999f; // Math.max(startTimeMs + MIN_TRIM_OFFSET, finishTimeMs-advancePrecision);

        presenter.advanceBackwardEndTrimming(advancePrecision, startTimeMs, finishTimeMs);

        verify(mockedTrimView).updateFinishTrimmingRangeSeekBar(finishTimeMsAdjusted);
    }

    @Test
    public void advanceForwardEndTrimmingCallsUpdateFinishTrimmingRangeSeekBar() {
        TrimPreviewPresenter presenter = getTrimPreviewPresenter();
        int advancePrecision = 600; //ms
        int endTimeMs = 1200; //ms
        int finishTimeMs = 2400; //ms

        presenter.advanceBackwardEndTrimming(advancePrecision, endTimeMs, finishTimeMs);

        verify(mockedTrimView).updateFinishTrimmingRangeSeekBar(anyFloat());
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
        TrimPreviewPresenter trimPreviewPresenter = new TrimPreviewPresenter(mockedTrimView, mockedSharedPreferences,
            mockedUserEventTracker, mockedGetMediaListFromProjectUseCase,
            mockedModifyVideoDurationUseCase, mockedProjectInstanceCache);
        trimPreviewPresenter.currentProject = currentProject;
        return trimPreviewPresenter;
    }

    private void setAProject() {
        Profile compositionProfile = new Profile(VideoResolution.Resolution.H_720P,
                VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
        List<String> productType = new ArrayList<>();
        ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
        currentProject = new Project(projectInfo, "/path", "private/path", compositionProfile);
    }

    public void getAVideoList(){
        Video video = new Video("media/path", Video.DEFAULT_VOLUME);
        videoList.add(video);
    }
}
