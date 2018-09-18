/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */
package com.videonasocialmedia.vimojo.split.presentation.mvp.presenters;


import android.support.annotation.NonNull;

import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateComposition;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.split.domain.SplitVideoUseCase;
import com.videonasocialmedia.vimojo.split.presentation.mvp.views.SplitView;
import com.videonasocialmedia.vimojo.test.shadows.MediaMetadataRetrieverShadow;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by jliarte on 10/06/16.
 */
// TODO(jliarte): 15/06/16 I need to use robolectric here as Video is being copied in the presenter
@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class SplitPreviewPresenterTest {
    @Mock private SplitView mockedSplitView;
    @Mock private UserEventTracker mockedUserEventTracker;
    @Mock private SplitVideoUseCase mockedSplitVideoUseCase;
    @Mock GetMediaListFromProjectUseCase mockedGetMediaListFromProjectUseCase;
    @Mock ProjectInstanceCache mockedProjectInstanceCache;
    private Project currentProject;
    List<Media> videoList = new ArrayList<>();
    @Mock UpdateComposition mockedUpdateComposition;
    private boolean amIAVerticalApp;

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
        SplitPreviewPresenter presenter = new SplitPreviewPresenter(
                mockedSplitView, userEventTracker, mockedSplitVideoUseCase,
                mockedGetMediaListFromProjectUseCase, mockedProjectInstanceCache,
                mockedUpdateComposition, amIAVerticalApp);

        assertThat(presenter.userEventTracker, is(userEventTracker));
    }

    @Test
    public void updatePresenterSetsCurrentProject() {
        SplitPreviewPresenter presenter = getSplitPreviewPresenter();

        presenter.updatePresenter();

        assertThat(presenter.currentProject, is(currentProject));
    }

    @Test
    @Config(shadows = {MediaMetadataRetrieverShadow.class})
    public void splitVideoCallsUserTracking() {
        SplitPreviewPresenter presenter = getSplitPreviewPresenter();

        presenter.trackSplitVideo();

        verify(mockedUserEventTracker).trackClipSplitted(currentProject);
    }

    @Test
    public void advanceForwardEndSplittingUpdateSplitSeekbar(){
        SplitPreviewPresenter presenter = getSplitPreviewPresenter();
        presenter.updatePresenter();
        int advancePlayerPrecision = 1;
        int currentSplitPosition = 2;

        presenter.advanceForwardEndSplitting(advancePlayerPrecision, currentSplitPosition);

        verify(mockedSplitView).updateSplitSeekbar(anyInt());
    }

    @Test
    public void advanceBackwardStartSplittingUpdateSplitSeekbar(){
        SplitPreviewPresenter presenter = getSplitPreviewPresenter();
        presenter.updatePresenter();
        int advancePlayerPrecision = 1;
        int currentSplitPosition = 2;

        presenter.advanceBackwardStartSplitting(advancePlayerPrecision, currentSplitPosition);

        verify(mockedSplitView).updateSplitSeekbar(anyInt());
    }

    @NonNull
    private SplitPreviewPresenter getSplitPreviewPresenter() {
        SplitPreviewPresenter splitPreviewPresenter = new SplitPreviewPresenter(
                mockedSplitView, mockedUserEventTracker, mockedSplitVideoUseCase,
                mockedGetMediaListFromProjectUseCase, mockedProjectInstanceCache,
                mockedUpdateComposition, amIAVerticalApp);
        splitPreviewPresenter.currentProject = currentProject;
        return splitPreviewPresenter;
    }

    public void setAProject() {
        Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
                VideoFrameRate.FrameRate.FPS25);
        List<String> productType = new ArrayList<>();
        ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
        currentProject = new Project(projectInfo, "/path", "private/path", compositionProfile);
    }

    public void getAVideoList(){
        Video video = new Video("media/path", Video.DEFAULT_VOLUME);
        videoList.add(video);
    }
}
