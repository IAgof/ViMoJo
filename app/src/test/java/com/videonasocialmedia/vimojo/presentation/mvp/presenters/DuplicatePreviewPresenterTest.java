package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.media.MediaMetadataRetriever;
import android.support.annotation.NonNull;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Video;

import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.presentation.mvp.views.DuplicateView;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;


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
public class DuplicatePreviewPresenterTest {
    @Mock private DuplicateView mockedDuplicateView;
    @Mock private UserEventTracker mockedUserEventTracker;
    @Mock private AddVideoToProjectUseCase mockedAddVideoToProjectUseCase;
    @Mock GetMediaListFromProjectUseCase mockedGetMediaListFromProjectUseCase;
    @Mock ProjectInstanceCache mockedProjectInstanceCache;

    // TODO(jliarte): 13/06/16 Decouple Video entity from android
    @Mock(name="retriever") MediaMetadataRetriever mockedMediaMetadataRetriever;
    @Mock private Video mockedVideo;
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
        DuplicatePreviewPresenter duplicatePreviewPresenter =
                new DuplicatePreviewPresenter(mockedDuplicateView, userEventTracker,
                    mockedAddVideoToProjectUseCase, mockedGetMediaListFromProjectUseCase,
                    mockedProjectInstanceCache);

        assertThat(duplicatePreviewPresenter.userEventTracker, is(userEventTracker));
    }

    @Test
    public void updatePresenterSetsCurrentProject() {
        DuplicatePreviewPresenter duplicatePreviewPresenter =
            getDuplicatePreviewPresenter();
        duplicatePreviewPresenter.updatePresenter();

        assertThat(duplicatePreviewPresenter.currentProject, is(currentProject));
    }

    @Test
    //@Config(manifest="../app/AndroidManifest.xml", shadows = {MediaMetadataRetrieverShadow.class})
    public void duplicateVideoCallsTracking() throws IllegalItemOnTrack {
        Video video = new Video("/media/path", Video.DEFAULT_VOLUME);
        int numCopies = 3;
        DuplicatePreviewPresenter duplicatePreviewPresenter =
            getDuplicatePreviewPresenter();
        duplicatePreviewPresenter = Mockito.spy(duplicatePreviewPresenter);
        doReturn(video).when(duplicatePreviewPresenter).getVideoCopy();

        /**
         * Exception accesing in getFileDuration as MediaMetadataRetriever.extractMetadata returns
         * null using a custom shadow instead
         */
        duplicatePreviewPresenter.duplicateVideo(0, numCopies);

        Mockito.verify(mockedUserEventTracker).trackClipDuplicated(numCopies, currentProject);
    }


    @NonNull
    public DuplicatePreviewPresenter getDuplicatePreviewPresenter() {
        DuplicatePreviewPresenter duplicatePreviewPresenter = new DuplicatePreviewPresenter(mockedDuplicateView, mockedUserEventTracker,
            mockedAddVideoToProjectUseCase, mockedGetMediaListFromProjectUseCase,
            mockedProjectInstanceCache);
        duplicatePreviewPresenter.currentProject = currentProject;
        return  duplicatePreviewPresenter;
    }

    public void setAProject() {
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
