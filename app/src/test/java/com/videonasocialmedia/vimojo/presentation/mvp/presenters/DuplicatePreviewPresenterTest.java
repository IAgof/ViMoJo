package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.media.MediaMetadataRetriever;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Video;

import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.presentation.mvp.views.DuplicateView;
import com.videonasocialmedia.vimojo.test.shadows.MediaMetadataRetrieverShadow;
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


import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by jliarte on 10/06/16.
 */
@RunWith(RobolectricTestRunner.class)
//@RunWith(MockitoJUnitRunner.class)
public class DuplicatePreviewPresenterTest {
    @Mock private MixpanelAPI mockedMixpanelAPI;
    @Mock private DuplicateView mockedDuplicateView;
    @Mock private UserEventTracker mockedUserEventTracker;
    @Mock private AddVideoToProjectUseCase
            mockedAddVideoToProjectUseCase;

    @InjectMocks DuplicatePreviewPresenter injectedPresenter;

    // TODO(jliarte): 13/06/16 Decouple Video entity from android
    @Mock(name="retriever") MediaMetadataRetriever mockedMediaMetadataRetriever;
    @Mock private Video mockedVideo;

    @Before
    public void injectMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
        Project.getInstance(null, null, null, null).clear();
    }

    @Test
    public void constructorSetsUserTracker() {
        UserEventTracker userEventTracker = UserEventTracker.getInstance(mockedMixpanelAPI);
        DuplicatePreviewPresenter duplicatePreviewPresenter =
                new DuplicatePreviewPresenter(mockedDuplicateView, userEventTracker,
                        mockedAddVideoToProjectUseCase);

        assertThat(duplicatePreviewPresenter.userEventTracker, is(userEventTracker));
    }

    @Test
    public void constructorSetsCurrentProject() {
        Project videonaProject = getAProject();

        DuplicatePreviewPresenter duplicatePreviewPresenter =
                new DuplicatePreviewPresenter(mockedDuplicateView, mockedUserEventTracker,
                        mockedAddVideoToProjectUseCase);

        assertThat(duplicatePreviewPresenter.currentProject, is(videonaProject));
    }

    @Test
    //@Config(manifest="../app/AndroidManifest.xml", shadows = {MediaMetadataRetrieverShadow.class})
    public void duplicateVideoCallsTracking() throws IllegalItemOnTrack {
        Project videonaProject = getAProject();
        Video video = new Video("/media/path", Video.DEFAULT_VOLUME);
        int numCopies = 3;
        DuplicatePreviewPresenter duplicatePreviewPresenter =
            new DuplicatePreviewPresenter(mockedDuplicateView, mockedUserEventTracker,
                mockedAddVideoToProjectUseCase);
        duplicatePreviewPresenter = Mockito.spy(duplicatePreviewPresenter);
        doReturn(video).when(duplicatePreviewPresenter).getVideoCopy();

        /**
         * Exception accesing in getFileDuration as MediaMetadataRetriever.extractMetadata returns
         * null using a custom shadow instead
         */
        duplicatePreviewPresenter.duplicateVideo(0, numCopies);

        Mockito.verify(mockedUserEventTracker).trackClipDuplicated(numCopies, videonaProject);
    }

    public Project getAProject() {
        Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
                VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
        List<String> productType = new ArrayList<>();
        ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
        return Project.getInstance(projectInfo, "/path", "private/path", compositionProfile);
    }
}
