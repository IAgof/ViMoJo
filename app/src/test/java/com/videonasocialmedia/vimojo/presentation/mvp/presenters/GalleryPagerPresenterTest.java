package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.asset.repository.datasource.VideoDataSource;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.domain.usecase.SetCompositionResolution;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateComposition;
import com.videonasocialmedia.vimojo.composition.repository.ProjectRepository;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.ApplyAVTransitionsUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideoFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.importer.helpers.NewClipImporter;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.main.VimojoTestApplication;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.presentation.mvp.views.GalleryPagerView;
import com.videonasocialmedia.vimojo.test.shadows.JobManager;
import com.videonasocialmedia.vimojo.test.shadows.ShadowMultiDex;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.view.BackgroundExecutor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by alvaro on 22/03/17.
 */
@RunWith(RobolectricTestRunner.class)
@Config(application = VimojoTestApplication.class, constants = BuildConfig.class, sdk = 21,
        shadows = {ShadowMultiDex.class, JobManager.class}, packageName = "com.videonasocialmedia.vimojo.debug")
public class GalleryPagerPresenterTest {
  @Mock AddVideoToProjectUseCase mockedAddVideoToProjectUseCase;
  @Mock OnLaunchAVTransitionTempFileListener mockedLaunchAVTransitionTempFileListener;
  @Mock Video mockedVideo;
  @Mock GalleryPagerView mockedGalleryPagerView;
  @Mock
  ApplyAVTransitionsUseCase mockedApplyAVTransitionsUseCase;
  @Mock GetVideoFormatFromCurrentProjectUseCase mockedGetVideonaFormatFromCurrentProjectUseCase;
  @Mock private MediaMetadataRetriever mockedMetadataRetriever;
  @Mock private ProjectRepository mockedProjectRepository;
  @Mock Context mockedContext;
  @Mock private SharedPreferences mockedSharedPreferences;
  @Mock private SharedPreferences.Editor mockedPreferencesEditor;
  @Mock private VideoDataSource mockedVideoRepository;
  @Mock ProjectInstanceCache mockedProjectInstanceCache;
  @Mock UpdateComposition mockedUpdateComposition;
  @Mock SetCompositionResolution mockedSetCompositionResolution;
  private Project currentProject;
  @Mock BackgroundExecutor mockedBackgroundExecutor;
  @Mock UserEventTracker mockedUserEventTracker;
  @Mock NewClipImporter mockedNewClipImporter;
  private boolean showAds;

  @Before
  public void injectMocks() {
    MockitoAnnotations.initMocks(this);
    setAProject();
  }

//  @Test
//  public void videoToLaunchAVTransitionTempFileUpdateVideoTempPath(){
//    getAProject().clear();
//    Project project = getAProject();
//    project.getVMComposition().setAudioFadeTransitionActivated(true);
//    String path = "media/path";
//    assertThat("Audio transition is activated",
//            project.getVMComposition().isAudioFadeTransitionActivated(), is(true));
//    Video video = new Video(path, Video.DEFAULT_VOLUME);
//    GalleryPagerPresenter galleryPagerPresenter = getGalleryPresenter();
//    String tempPath = video.getTempPath();
//
//    galleryPagerPresenter.videoToLaunchAVTransitionTempFile(video,
//        project.getProjectPathIntermediateFileAudioFade());
//
//    assertNotEquals("Update tempPath ", tempPath, video.getTempPath());
//  }

  @Test
  public void updateProfileForEmptyProjectChangeProjectResolutionIfNoVideos() {
    GalleryPagerPresenter galleryPagerPresenter = getGalleryPresenter();
    galleryPagerPresenter.metadataRetriever = mockedMetadataRetriever;
    VideoResolution videoResolution720 = new VideoResolution(VideoResolution.Resolution.HD720);
    Profile profile = new Profile(VideoResolution.Resolution.HD1080, VideoQuality.Quality.GOOD,
            VideoFrameRate.FrameRate.FPS30);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    Project project = new Project(projectInfo, "root/path","private/path", profile);
    Video video1 = new Video("video/1", Video.DEFAULT_VOLUME);
    List<Video> videoList = Collections.singletonList(video1);
    assertThat(project.getVMComposition().getMediaTrack().getItems().size(), is(0));
    doReturn(String.valueOf(videoResolution720.getWidth()))
            .when(mockedMetadataRetriever)
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
    doReturn(mockedPreferencesEditor).when(mockedSharedPreferences).edit();
    String preferenceResolutionString = "720p";
    doReturn(preferenceResolutionString).when(mockedContext)
            .getString(R.string.low_resolution_name);

    when(mockedBackgroundExecutor.submit(any(Runnable.class))).then((Answer<Runnable>) invocation -> {
      Runnable runnable = invocation.getArgument(0);
      runnable.run();
      return null;
    });

    galleryPagerPresenter.updateProfileForEmptyProject(project, videoList);

    ArgumentCaptor<VideoResolution.Resolution> resolutionCaptor =
            ArgumentCaptor.forClass(VideoResolution.Resolution.class);
    verify(mockedSetCompositionResolution).setResolution(any(Project.class),
        resolutionCaptor.capture());
    VideoResolution.Resolution resolutionCaptorValue = resolutionCaptor.getValue();
    assertThat(resolutionCaptorValue, is(VideoResolution.Resolution.HD720));
    verify(mockedPreferencesEditor).putString(ConfigPreferences.KEY_LIST_PREFERENCES_RESOLUTION,
            preferenceResolutionString);
    verify(mockedUpdateComposition).updateComposition(any(Project.class));
  }

  @Test
  public void updateProfileForEmptyProjectDoesNothingIfUnknownResolution() {
    GalleryPagerPresenter galleryPagerPresenter = getGalleryPresenter();
    galleryPagerPresenter.metadataRetriever = mockedMetadataRetriever;
    Profile profile = new Profile(VideoResolution.Resolution.HD1080, VideoQuality.Quality.GOOD,
            VideoFrameRate.FrameRate.FPS30);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    Project project = new Project(projectInfo, "root/path", "private/path", profile);
    Video video1 = new Video("video/1", Video.DEFAULT_VOLUME);
    List<Video> videoList = Collections.singletonList(video1);
    assertThat(project.getVMComposition().getMediaTrack().getItems().size(), is(0));
    doReturn(String.valueOf("124"))
            .when(mockedMetadataRetriever)
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
    doReturn(mockedPreferencesEditor).when(mockedSharedPreferences).edit();
    String preferenceResolutionString = "720p";
    doReturn(preferenceResolutionString).when(mockedContext)
            .getString(R.string.low_resolution_name);

    galleryPagerPresenter.updateProfileForEmptyProject(project, videoList);

    verify(mockedSetCompositionResolution, never())
            .setResolution(Mockito.any(Project.class), Mockito.any(VideoResolution.Resolution.class));
    verify(mockedPreferencesEditor, never())
            .putString(eq(ConfigPreferences.KEY_LIST_PREFERENCES_RESOLUTION),
            Mockito.anyString());
  }


  @Test
  public void updateProfileForEmptyProjectDoesNothingIfVideosInProject() throws IllegalItemOnTrack {
    GalleryPagerPresenter galleryPagerPresenter = getGalleryPresenter();
    galleryPagerPresenter.metadataRetriever = mockedMetadataRetriever;
    VideoResolution videoResolution720 = new VideoResolution(VideoResolution.Resolution.HD720);
    doReturn(String.valueOf(videoResolution720.getWidth()))
            .when(mockedMetadataRetriever)
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
    Video video1 = new Video("video1", Video.DEFAULT_VOLUME);
    Video video2 = new Video("video2", Video.DEFAULT_VOLUME);
    currentProject.getVMComposition().getMediaTrack().insertItem(video1);
    assertThat(currentProject.getVMComposition().getMediaTrack().getItems().size(), not(0));
    List<Video> videoList = Collections.singletonList(video2);

    galleryPagerPresenter.updateProfileForEmptyProject(currentProject, videoList);

    verify(mockedSetCompositionResolution, never()).setResolution(Mockito.any(Project.class),
        Mockito.any(VideoResolution.Resolution.class));
  }

  @Test
  public void updateProfileForEmptyProjectDoesNothingIfNoSelectedVideos() {
    GalleryPagerPresenter galleryPagerPresenter = getGalleryPresenter();
    galleryPagerPresenter.metadataRetriever = mockedMetadataRetriever;
    VideoResolution videoResolution720 = new VideoResolution(VideoResolution.Resolution.HD720);
    doReturn(String.valueOf(videoResolution720.getWidth()))
            .when(mockedMetadataRetriever)
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
    List<Video> videoList = Collections.emptyList();

    galleryPagerPresenter.updateProfileForEmptyProject(currentProject, videoList);

    verify(mockedSetCompositionResolution, never()).setResolution(Mockito.any(Project.class),
        Mockito.any(VideoResolution.Resolution.class));
  }

  private GalleryPagerPresenter getGalleryPresenter() {
    return new GalleryPagerPresenter(
            mockedGalleryPagerView, mockedContext, mockedAddVideoToProjectUseCase,
            mockedApplyAVTransitionsUseCase,
            mockedSharedPreferences,
            mockedProjectInstanceCache, mockedUpdateComposition,
            mockedSetCompositionResolution, mockedNewClipImporter, showAds, mockedBackgroundExecutor,
            mockedUserEventTracker);
  }

  public void setAProject() {
    Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
            VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, "/path", "private/path", compositionProfile);
  }
}
