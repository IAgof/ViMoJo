package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.content.Context;
import android.support.annotation.NonNull;

import com.videonasocialmedia.videonamediaframework.model.VMComposition;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateComposition;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.main.VimojoTestApplication;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.presentation.mvp.views.DuplicateView;
import com.videonasocialmedia.vimojo.test.shadows.JobManager;
import com.videonasocialmedia.vimojo.test.shadows.ShadowMultiDex;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.view.BackgroundExecutor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_PLAYER_HEIGHT_VERTICAL_MODE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by jliarte on 10/06/16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(application = VimojoTestApplication.class, constants = BuildConfig.class, sdk = 21,
        shadows = {ShadowMultiDex.class, JobManager.class})
public class DuplicatePreviewPresenterTest {
    @Mock private Context mockedContext;
    @Mock private DuplicateView mockedDuplicateView;
    @Mock private UserEventTracker mockedUserEventTracker;
    @Mock private AddVideoToProjectUseCase mockedAddVideoToProjectUseCase;
    @Mock ProjectInstanceCache mockedProjectInstanceCache;
    @Mock UpdateComposition mockedUpdateComposition;
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
        DuplicatePreviewPresenter duplicatePreviewPresenter =
                new DuplicatePreviewPresenter(mockedContext, mockedDuplicateView,
                    userEventTracker, mockedAddVideoToProjectUseCase,
                    mockedProjectInstanceCache, mockedUpdateComposition, amIAVerticalApp,
                    mockedBackgroundExecutor);

        assertThat(duplicatePreviewPresenter.userEventTracker, is(userEventTracker));
    }

    @Test
    public void updatePresenterSetsCurrentProject() {
        DuplicatePreviewPresenter duplicatePreviewPresenter = getDuplicatePreviewPresenter();
        int videoIndexOnTrack = 0;

        duplicatePreviewPresenter.updatePresenter(videoIndexOnTrack);

        assertThat(duplicatePreviewPresenter.currentProject, is(currentProject));
    }

    @Test
    //@Config(manifest="../app/AndroidManifest.xml", shadows = {MediaMetadataRetrieverShadow.class})
    public void duplicateVideoCallsTracking() throws IllegalItemOnTrack {
        Video video = new Video("/media/path", Video.DEFAULT_VOLUME);
        int numCopies = 3;
        DuplicatePreviewPresenter duplicatePreviewPresenter = getDuplicatePreviewPresenter();
        duplicatePreviewPresenter = Mockito.spy(duplicatePreviewPresenter);
        doReturn(video).when(duplicatePreviewPresenter).getVideoCopy();

        /**
         * Exception accesing in getFileDuration as MediaMetadataRetriever.extractMetadata returns
         * null using a custom shadow instead
         */
        duplicatePreviewPresenter.duplicateVideo(numCopies);

        verify(mockedUserEventTracker).trackClipDuplicated(numCopies, currentProject);
    }

    @Test
    public void updatePresenterAttachPlayerView() {
        DuplicatePreviewPresenter duplicatePreviewPresenter = getDuplicatePreviewPresenter();
        int videoIndexOnTrack = 0;

        duplicatePreviewPresenter.updatePresenter(videoIndexOnTrack);

        verify(mockedDuplicateView).attachView(mockedContext);
    }

    @Test
    public void pausePresenterDetachPlayerView() {
        DuplicatePreviewPresenter duplicatePreviewPresenter = getDuplicatePreviewPresenter();

        duplicatePreviewPresenter.pausePresenter();

        verify(mockedDuplicateView).detachView();
    }

    @Test
    public void updatePresenterInitSingleComposition() {
        DuplicatePreviewPresenter duplicatePreviewPresenter = getDuplicatePreviewPresenter();
        int videoIndexOnTrack = 0;

        duplicatePreviewPresenter.updatePresenter(videoIndexOnTrack);

        verify(mockedDuplicateView).initSingleClip(any(VMComposition.class), eq(videoIndexOnTrack));
    }

    @Test
    public void updatePresenterSetAspectRatioIfIAVerticalApp() {
        DuplicatePreviewPresenter spyDuplicatePreviewPresenter = spy(getDuplicatePreviewPresenter());
        spyDuplicatePreviewPresenter.amIAVerticalApp = true;
        int videoIndexOnTrack = 0;

        spyDuplicatePreviewPresenter.updatePresenter(videoIndexOnTrack);

        verify(mockedDuplicateView)
            .setAspectRatioVerticalVideos(DEFAULT_PLAYER_HEIGHT_VERTICAL_MODE);
    }

    @Test
    public void duplicateVideoCallsUseCase() {
        DuplicatePreviewPresenter spyDuplicatePreviewPresenter = spy(getDuplicatePreviewPresenter());
        int videoIndexOnTrack = 0;
        spyDuplicatePreviewPresenter.updatePresenter(videoIndexOnTrack);
        int numDuplicates = 2;
        Video videoInTrack = (Video) currentProject.getMediaTrack().getItems().get(0);
        when(spyDuplicatePreviewPresenter.getVideoCopy()).thenReturn(videoInTrack);

        spyDuplicatePreviewPresenter.duplicateVideo(numDuplicates);

        verify(mockedAddVideoToProjectUseCase).addVideoToProjectAtPosition(eq(currentProject),
            eq(videoInTrack), eq(videoIndexOnTrack), any(OnAddMediaFinishedListener.class));
    }


    @NonNull
    public DuplicatePreviewPresenter getDuplicatePreviewPresenter() {
        DuplicatePreviewPresenter duplicatePreviewPresenter = new DuplicatePreviewPresenter(
            mockedContext, mockedDuplicateView, mockedUserEventTracker,
            mockedAddVideoToProjectUseCase, mockedProjectInstanceCache, mockedUpdateComposition,
            amIAVerticalApp, mockedBackgroundExecutor);
        duplicatePreviewPresenter.currentProject = currentProject;
        return  duplicatePreviewPresenter;
    }

    public void setAProjectWithSomeVideo() throws IllegalItemOnTrack {
        Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
                VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
        List<String> productType = new ArrayList<>();
        ProjectInfo projectInfo = new ProjectInfo("title", "description",
            productType);
        currentProject = new Project(projectInfo, "/path", "private/path",
            compositionProfile);
        Video video = new Video("some/path", Video.DEFAULT_VOLUME);
        currentProject.getMediaTrack().insertItem(video);
    }
}
