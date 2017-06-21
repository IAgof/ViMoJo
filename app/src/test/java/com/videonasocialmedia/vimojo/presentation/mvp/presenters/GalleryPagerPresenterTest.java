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
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.UpdateVideoResolutionToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.video.UpdateVideoRepositoryUseCase;
import com.videonasocialmedia.vimojo.domain.editor.LaunchTranscoderAddAVTransitionsUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideoFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.main.VimojoTestApplication;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.mvp.views.GalleryPagerView;
import com.videonasocialmedia.vimojo.test.shadows.ShadowMultiDex;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by alvaro on 22/03/17.
 */
@RunWith(RobolectricTestRunner.class)
@Config(application = VimojoTestApplication.class, constants = BuildConfig.class, sdk = 21,
        shadows = {ShadowMultiDex.class}, packageName = "com.videonasocialmedia.vimojo.debug")
public class GalleryPagerPresenterTest {
  @Mock AddVideoToProjectUseCase mockedAddVideoToProjectUseCase;
  @Mock OnLaunchAVTransitionTempFileListener mockedLaunchAVTransitionTempFileListener;
  @Mock Video mockedVideo;
  @Mock GalleryPagerView mockedGalleryPagerView;
  @Mock UpdateVideoRepositoryUseCase mockedUpdateVideoRepositoryUseCase;
  @Mock LaunchTranscoderAddAVTransitionsUseCase mockedLaunchTranscoderAddAVTransitionsUseCase;
  @Mock GetVideoFormatFromCurrentProjectUseCase mockedGetVideonaFormatFromCurrentProjectUseCase;
  @Mock private MediaMetadataRetriever mockedMetadataRetriever;
  @Mock private UpdateVideoResolutionToProjectUseCase mockedUpdateProjectResolution;
  @Mock Context mockedContext;
  @Mock private SharedPreferences mockedSharedPreferences;
  @Mock private SharedPreferences.Editor mockedPreferencesEditor;

  @Before
  public void injectMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void clearProject() {
    Project.getInstance(null, null, null).clear();
  }

  @Test
  public void constructorSetsCurrentProject() {
    GalleryPagerPresenter galleryPagerPresenter = getGalleryPresenter();
    Project project = getAProject();

    assertThat(galleryPagerPresenter.currentProject, is(project));
  }

  @Test
  public void videoToLaunchAVTransitionTempFileUpdateVideoTempPath(){
    getAProject().clear();
    Project project = getAProject();
    project.setAudioFadeTransitionActivated(true);
    String path = "media/path";
    assertThat("Audio transition is activated", project.isAudioFadeTransitionActivated(), is(true));
    Video video = new Video(path, Video.DEFAULT_VOLUME);

    GalleryPagerPresenter galleryPagerPresenter = getGalleryPresenter();

    String tempPath = video.getTempPath();

    galleryPagerPresenter.videoToLaunchAVTransitionTempFile(video,
        project.getProjectPathIntermediateFileAudioFade());

    assertNotEquals("Update tempPath ", tempPath, video.getTempPath());
  }

  @Test
  public void updateProfileForEmptyProjectChangeProjectResolutionIfNoVideos() {
    GalleryPagerPresenter galleryPagerPresenter = getGalleryPresenter();
    galleryPagerPresenter.metadataRetriever = mockedMetadataRetriever;
    VideoResolution videoResolution720 = new VideoResolution(VideoResolution.Resolution.HD720);
    Profile profile = new Profile(VideoResolution.Resolution.HD1080, VideoQuality.Quality.GOOD,
            VideoFrameRate.FrameRate.FPS30);
    Project project = new Project("newproject", "root/path", profile);
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

    galleryPagerPresenter.updateProfileForEmptyProject(project, videoList);

    ArgumentCaptor<VideoResolution.Resolution> resolutionCaptor =
            ArgumentCaptor.forClass(VideoResolution.Resolution.class);
    verify(mockedUpdateProjectResolution).updateResolution(resolutionCaptor.capture());
    VideoResolution.Resolution resolutionCaptorValue = resolutionCaptor.getValue();
    assertThat(resolutionCaptorValue, is(VideoResolution.Resolution.HD720));

    verify(mockedPreferencesEditor).putString(ConfigPreferences.KEY_LIST_PREFERENCES_RESOLUTION,
            preferenceResolutionString);
  }

  @Test
  public void updateProfileForEmptyProjectDoesNothingIfUnknownResolution() {
    GalleryPagerPresenter galleryPagerPresenter = getGalleryPresenter();
    galleryPagerPresenter.metadataRetriever = mockedMetadataRetriever;
    Profile profile = new Profile(VideoResolution.Resolution.HD1080, VideoQuality.Quality.GOOD,
            VideoFrameRate.FrameRate.FPS30);
    Project project = new Project("newproject", "root/path", profile);
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

    verify(mockedUpdateProjectResolution, never())
            .updateResolution(Mockito.any(VideoResolution.Resolution.class));
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
    Project project = getAProject();
    Video video1 = new Video("video1", Video.DEFAULT_VOLUME);
    Video video2 = new Video("video2", Video.DEFAULT_VOLUME);
    project.getVMComposition().getMediaTrack().insertItem(video1);
    assertThat(project.getVMComposition().getMediaTrack().getItems().size(), not(0));
    List<Video> videoList = Collections.singletonList(video2);

    galleryPagerPresenter.updateProfileForEmptyProject(project, videoList);

    verify(mockedUpdateProjectResolution, never())
            .updateResolution(Mockito.any(VideoResolution.Resolution.class));
  }

  @Test
  public void updateProfileForEmptyProjectDoesNothingIfNoSelectedVideos() {
    GalleryPagerPresenter galleryPagerPresenter = getGalleryPresenter();
    galleryPagerPresenter.metadataRetriever = mockedMetadataRetriever;
    VideoResolution videoResolution720 = new VideoResolution(VideoResolution.Resolution.HD720);
    doReturn(String.valueOf(videoResolution720.getWidth()))
            .when(mockedMetadataRetriever)
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
    Project project = getAProject();
    List<Video> videoList = Collections.emptyList();

    galleryPagerPresenter.updateProfileForEmptyProject(project, videoList);

    verify(mockedUpdateProjectResolution, never())
            .updateResolution(Mockito.any(VideoResolution.Resolution.class));
  }

  private GalleryPagerPresenter getGalleryPresenter() {
    return new GalleryPagerPresenter(mockedGalleryPagerView, mockedContext,
            mockedAddVideoToProjectUseCase, mockedUpdateVideoRepositoryUseCase,
            mockedGetVideonaFormatFromCurrentProjectUseCase,
            mockedLaunchTranscoderAddAVTransitionsUseCase, mockedUpdateProjectResolution,
            mockedSharedPreferences);
  }

  public Project getAProject() {
    return Project.getInstance("title", "/path", Profile.getInstance(VideoResolution.Resolution.HD720,
        VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25));
  }
}
