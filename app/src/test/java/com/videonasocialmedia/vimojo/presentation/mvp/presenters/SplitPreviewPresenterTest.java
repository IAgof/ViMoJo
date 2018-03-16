package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.support.annotation.NonNull;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Video;

import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.vimojo.split.domain.SplitVideoUseCase;
import com.videonasocialmedia.vimojo.split.presentation.mvp.presenters.SplitPreviewPresenter;
import com.videonasocialmedia.vimojo.split.presentation.mvp.views.SplitView;
import com.videonasocialmedia.vimojo.test.shadows.MediaMetadataRetrieverShadow;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by jliarte on 10/06/16.
 */
// TODO(jliarte): 15/06/16 I need to use robolectric here as Video is being copied in the presenter
@RunWith(RobolectricTestRunner.class)
public class SplitPreviewPresenterTest {
    @Mock private SplitView mockedSplitView;
    @Mock private MixpanelAPI mockedMixpanelAPI;
    @Mock private UserEventTracker mockedUserEventTracker;

    // TODO(jliarte): 13/06/16 Decouple Video entity from android
    @Mock(name="retriever") MediaMetadataRetriever mockedMediaMetadataRetriever;
    @InjectMocks Video injectedVideo;
    @Mock private SplitVideoUseCase mockedSplitVideoUseCase;
    @Mock GetMediaListFromProjectUseCase mockedGetMediaListFromProjectUseCase;

    @Mock Context mockedContext;
    @Mock private VideoRepository mockedVideoRepository;
    @Mock private ProjectRepository mockedProjectRepository;
    private Project currentProject;

    @Before
    public void injectMocks() {
        MockitoAnnotations.initMocks(this);
        getAProject();
        when(mockedProjectRepository.getCurrentProject()).thenReturn(currentProject);
    }

    @Test
    public void constructorSetsUserTracker() {
        UserEventTracker userEventTracker = UserEventTracker.getInstance(mockedMixpanelAPI);
        SplitPreviewPresenter presenter = new SplitPreviewPresenter(mockedSplitView,
            userEventTracker, mockedContext, mockedProjectRepository, mockedVideoRepository,
            mockedSplitVideoUseCase, mockedGetMediaListFromProjectUseCase);

        assertThat(presenter.userEventTracker, is(userEventTracker));
    }

    @Test
    public void constructorSetsCurrentProject() {
        SplitPreviewPresenter presenter = getSplitPreviewPresenter();

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
        int advancePlayerPrecision = 1;
        int currentSplitPosition = 2;

        presenter.advanceForwardEndSplitting(advancePlayerPrecision, currentSplitPosition);

        verify(mockedSplitView).updateSplitSeekbar(anyInt());
    }

    @Test
    public void advanceBackwardStartSplittingUpdateSplitSeekbar(){
        SplitPreviewPresenter presenter = getSplitPreviewPresenter();
        int advancePlayerPrecision = 1;
        int currentSplitPosition = 2;

        presenter.advanceBackwardStartSplitting(advancePlayerPrecision, currentSplitPosition);

        verify(mockedSplitView).updateSplitSeekbar(anyInt());
    }

    @NonNull
    private SplitPreviewPresenter getSplitPreviewPresenter() {
        return new SplitPreviewPresenter(mockedSplitView,
            mockedUserEventTracker, mockedContext, mockedProjectRepository, mockedVideoRepository,
            mockedSplitVideoUseCase, mockedGetMediaListFromProjectUseCase);
    }

    public void getAProject() {
        Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
                VideoFrameRate.FrameRate.FPS25);
        List<String> productType = new ArrayList<>();
        ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
        currentProject = new Project(projectInfo, "/path", "private/path", compositionProfile);
    }
}
