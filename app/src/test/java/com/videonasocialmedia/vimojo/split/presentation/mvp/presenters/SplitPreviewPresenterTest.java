/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */
package com.videonasocialmedia.vimojo.split.presentation.mvp.presenters;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.videonasocialmedia.videonamediaframework.model.VMComposition;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.asset.domain.usecase.UpdateMedia;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateComposition;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.main.VimojoTestApplication;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.split.domain.OnSplitVideoListener;
import com.videonasocialmedia.vimojo.split.domain.SplitVideoUseCase;
import com.videonasocialmedia.vimojo.split.presentation.mvp.views.SplitView;
import com.videonasocialmedia.vimojo.test.shadows.JobManager;
import com.videonasocialmedia.vimojo.test.shadows.MediaMetadataRetrieverShadow;
import com.videonasocialmedia.vimojo.test.shadows.ShadowMultiDex;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.view.BackgroundExecutor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by jliarte on 10/06/16.
 */
// TODO(jliarte): 15/06/16 I need to use robolectric here as Video is being copied in the presenter
@RunWith(RobolectricTestRunner.class)
@Config(application = VimojoTestApplication.class, constants = BuildConfig.class, sdk = 21,
        shadows = {ShadowMultiDex.class, JobManager.class})
public class SplitPreviewPresenterTest {
    @Mock private Context mockedContext;
    @Mock private SplitView mockedSplitView;
    @Mock private SharedPreferences mockedSharedPreferences;
    @Mock private UserEventTracker mockedUserEventTracker;
    @Mock private SplitVideoUseCase mockedSplitVideoUseCase;
    @Mock UpdateComposition mockedUpdateComposition;
    @Mock private UpdateMedia mockedUpdateMedia;
    @Mock ProjectInstanceCache mockedProjectInstanceCache;
    private Project currentProject;
    private boolean amIAVerticalApp;
    @Mock BackgroundExecutor mockedBackgroundExecutor;

    @Before
    public void injectMocks() throws IllegalItemOnTrack {
        MockitoAnnotations.initMocks(this);
        setAProjectWithSomeVideo();
        when(mockedProjectInstanceCache.getCurrentProject()).thenReturn(currentProject);
    }

    @Test
    public void constructorSetsUserTracker() {
        UserEventTracker userEventTracker = UserEventTracker.getInstance();
        SplitPreviewPresenter presenter = new SplitPreviewPresenter(mockedContext,
                mockedSplitView, mockedSharedPreferences, userEventTracker, mockedSplitVideoUseCase,
                mockedUpdateComposition, mockedUpdateMedia, mockedProjectInstanceCache,
                amIAVerticalApp, mockedBackgroundExecutor);

        assertThat(presenter.userEventTracker, is(userEventTracker));
    }

    @Test
    public void updatePresenterSetsCurrentProject() {
        SplitPreviewPresenter presenter = getSplitPreviewPresenter();
        int videoIndexOnTrack = 0;

        presenter.updatePresenter(videoIndexOnTrack);

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
    public void advanceForwardEndSplittingUpdateSplitSeekbarAndRefreshTimeTag(){
        SplitPreviewPresenter presenter = getSplitPreviewPresenter();
        int videoIndexOnTrack = 0;
        presenter.updatePresenter(videoIndexOnTrack);
        int advancePlayerPrecision = 1;

        presenter.advanceForwardEndSplitting(advancePlayerPrecision);

        verify(mockedSplitView).updateSplitSeekbar(anyInt());
        verify(mockedSplitView).refreshTimeTag(anyInt());
    }

    @Test
    public void advanceForwardEndSplittingSeekToPlayer(){
        SplitPreviewPresenter presenter = getSplitPreviewPresenter();
        int videoIndexOnTrack = 0;
        presenter.updatePresenter(videoIndexOnTrack);
        int advancePlayerPrecision = 1;

        presenter.advanceForwardEndSplitting(advancePlayerPrecision);

        verify(mockedSplitView).seekTo(anyInt());
    }

    @Test
    public void advanceBackwardStartSplittingUpdateSplitSeekbarAndRefresthTimeTag(){
        SplitPreviewPresenter presenter = getSplitPreviewPresenter();
        int videoIndexOnTrack = 0;
        presenter.updatePresenter(videoIndexOnTrack);
        int advancePlayerPrecision = 1;

        presenter.advanceBackwardStartSplitting(advancePlayerPrecision);

        verify(mockedSplitView).updateSplitSeekbar(anyInt());
        verify(mockedSplitView).refreshTimeTag(anyInt());
    }

    @Test
    public void advanceBackwardStartSplittingSeekToPlayer(){
        SplitPreviewPresenter presenter = getSplitPreviewPresenter();
        int videoIndexOnTrack = 0;
        presenter.updatePresenter(videoIndexOnTrack);
        int advancePlayerPrecision = 1;

        presenter.advanceBackwardStartSplitting(advancePlayerPrecision);

        verify(mockedSplitView).seekTo(anyInt());
    }

    @Test
    public void onSeekBarChangedRefreshTimeTag() {
        SplitPreviewPresenter presenter = getSplitPreviewPresenter();
        int videoIndexOnTrack = 0;
        presenter.updatePresenter(videoIndexOnTrack);
        int progress = 50;

        presenter.onSeekBarChanged(50);

        verify(mockedSplitView).refreshTimeTag(progress);
    }

    @Test
    public void onSeekBarChangedSeekToPlayer() {
        SplitPreviewPresenter presenter = getSplitPreviewPresenter();
        int videoIndexOnTrack = 0;
        presenter.updatePresenter(videoIndexOnTrack);
        int progress = 50;
        Video video = (Video) currentProject.getMediaTrack().getItems().get(0);

        presenter.onSeekBarChanged(50);

        verify(mockedSplitView).seekTo(video.getStartTime() + progress);
    }

    @Test
    public void updatePresenterAttachPlayerView() {
        SplitPreviewPresenter presenter = getSplitPreviewPresenter();
        int videoIndexOnTrack = 0;

        presenter.updatePresenter(videoIndexOnTrack);

        verify(mockedSplitView).attachView(mockedContext);
    }

    @Test
    public void updatePresenterInitSingleComposition() {
        SplitPreviewPresenter presenter = getSplitPreviewPresenter();
        int videoIndexOnTrack = 0;

        presenter.updatePresenter(videoIndexOnTrack);

        verify(mockedSplitView).initSingleClip(any(VMComposition.class), eq(videoIndexOnTrack));
    }

    @Test
    public void pausePresenterDetachPlayerView() {
        SplitPreviewPresenter presenter = getSplitPreviewPresenter();

        presenter.pausePresenter();

        verify(mockedSplitView).detachView();
    }

    @Test
    public void splitVideoCallsUseCase() {
        SplitPreviewPresenter spyPresenter = spy(getSplitPreviewPresenter());
        int videoIndexOnTrack = 0;
        int splitTimeMs = 400;
        when(mockedBackgroundExecutor.submit(any(Runnable.class))).then(new Answer<Runnable>() {
            @Override
            public Runnable answer(InvocationOnMock invocation) throws Throwable {
                Runnable runnable = invocation.getArgument(0);
                runnable.run();
                return null;
            }
        });
        spyPresenter.updatePresenter(videoIndexOnTrack);
        spyPresenter.advanceForwardEndSplitting(splitTimeMs);
        Video video = (Video) currentProject.getMediaTrack().getItems().get(0);

        spyPresenter.splitVideo();

        verify(mockedSplitVideoUseCase).splitVideo(eq(currentProject), eq(video),
            eq(videoIndexOnTrack), eq(splitTimeMs), any(OnSplitVideoListener.class));
    }

    @NonNull
    private SplitPreviewPresenter getSplitPreviewPresenter() {
        SplitPreviewPresenter splitPreviewPresenter = new SplitPreviewPresenter(mockedContext,
            mockedSplitView, mockedSharedPreferences, mockedUserEventTracker,
            mockedSplitVideoUseCase, mockedUpdateComposition, mockedUpdateMedia,
            mockedProjectInstanceCache, amIAVerticalApp, mockedBackgroundExecutor);
        splitPreviewPresenter.currentProject = currentProject;
        return splitPreviewPresenter;
    }

    public void setAProjectWithSomeVideo() throws IllegalItemOnTrack {
        Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
                VideoFrameRate.FrameRate.FPS25);
        List<String> productType = new ArrayList<>();
        ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
        currentProject = new Project(projectInfo, "/path", "private/path", compositionProfile);
        Video video = new Video("some/path", Video.DEFAULT_VOLUME);
        currentProject.getMediaTrack().insertItem(video);
    }
}
