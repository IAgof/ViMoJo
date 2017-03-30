package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.content.Context;
import android.media.MediaMetadataRetriever;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.video.UpdateVideoRepositoryUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideonaFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Video;

import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.split.domain.SplitVideoUseCase;
import com.videonasocialmedia.vimojo.split.presentation.mvp.presenters.SplitPreviewPresenter;
import com.videonasocialmedia.vimojo.split.presentation.mvp.views.SplitView;
import com.videonasocialmedia.vimojo.test.shadows.MediaMetadataRetrieverShadow;
import com.videonasocialmedia.vimojo.trim.domain.ModifyVideoDurationUseCase;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

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
    @Mock ModifyVideoDurationUseCase mockedModifyVideoDurationUseCase;
    @Mock GetVideonaFormatFromCurrentProjectUseCase mockedGetVideonaFormatFromCurrentProjectUseCase;
    @Mock UpdateVideoRepositoryUseCase mockedUpdateVideoRepositoryUseCase;

    @Mock Context mockedContext;

    @Before
    public void injectMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
        Project.getInstance(null, null, null).clear();
    }

    @Test
    public void constructorSetsUserTracker() {
        UserEventTracker userEventTracker = UserEventTracker.getInstance(mockedMixpanelAPI);
        SplitPreviewPresenter presenter = new SplitPreviewPresenter(mockedSplitView,
            userEventTracker, mockedContext, mockedSplitVideoUseCase,
            mockedGetMediaListFromProjectUseCase, mockedModifyVideoDurationUseCase,
            mockedGetVideonaFormatFromCurrentProjectUseCase, mockedUpdateVideoRepositoryUseCase);

        assertThat(presenter.userEventTracker, is(userEventTracker));
    }

    @Test
    public void constructorSetsCurrentProject() {
        Project videonaProject = getAProject();
        SplitPreviewPresenter presenter = new SplitPreviewPresenter(mockedSplitView,
            mockedUserEventTracker, mockedContext, mockedSplitVideoUseCase,
            mockedGetMediaListFromProjectUseCase, mockedModifyVideoDurationUseCase,
            mockedGetVideonaFormatFromCurrentProjectUseCase, mockedUpdateVideoRepositoryUseCase);

        assertThat(presenter.currentProject, is(videonaProject));
    }

    @Test
    @Config(shadows = {MediaMetadataRetrieverShadow.class})
    public void splitVideoCallsUserTracking() {
        SplitPreviewPresenter presenter = new SplitPreviewPresenter(mockedSplitView,
            mockedUserEventTracker, mockedContext, mockedSplitVideoUseCase,
            mockedGetMediaListFromProjectUseCase, mockedModifyVideoDurationUseCase,
            mockedGetVideonaFormatFromCurrentProjectUseCase, mockedUpdateVideoRepositoryUseCase);
        Project videonaProject = getAProject();

       // presenter.splitVideo(injectedVideo, 0, 10);
        presenter.trackSplitVideo();

        Mockito.verify(mockedUserEventTracker).trackClipSplitted(videonaProject);
    }

    public Project getAProject() {
        return Project.getInstance("title", "/path",
                Profile.getInstance(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
                        VideoFrameRate.FrameRate.FPS25));
    }
}
