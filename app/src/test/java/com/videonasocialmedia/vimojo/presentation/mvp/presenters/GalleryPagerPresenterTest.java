package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.content.Context;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.video.UpdateVideoRepositoryUseCase;
import com.videonasocialmedia.vimojo.domain.editor.LaunchTranscoderAddAVTransitionsUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.mvp.views.GalleryPagerView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by alvaro on 22/03/17.
 */

public class GalleryPagerPresenterTest {

  @Mock AddVideoToProjectUseCase mockedAddVideoToProjectUseCase;
  @Mock OnLaunchAVTransitionTempFileListener mockedLaunchAVTransitionTempFileListener;
  @Mock Video mockedVideo;
  @Mock GalleryPagerView mockedGalleryPagerView;
  @Mock UpdateVideoRepositoryUseCase mockedUpdateVideoRepositoryUseCase;
  @Mock LaunchTranscoderAddAVTransitionsUseCase mockedLaunchTranscoderAddAVTransitionsUseCase;
  @Mock Context mockedContext;

  private GalleryPagerPresenter galleryPagerPresenter;

  @Before
  public void injectMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void constructorSetsCurrentProject() {

    galleryPagerPresenter = new GalleryPagerPresenter(mockedGalleryPagerView,
        mockedAddVideoToProjectUseCase,mockedUpdateVideoRepositoryUseCase,
        mockedLaunchTranscoderAddAVTransitionsUseCase, mockedContext);
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

    galleryPagerPresenter = new GalleryPagerPresenter(mockedGalleryPagerView,
        mockedAddVideoToProjectUseCase,mockedUpdateVideoRepositoryUseCase,
        mockedLaunchTranscoderAddAVTransitionsUseCase, mockedContext);

    Video video = new Video(path);
    String tempPath = video.getTempPath();

    galleryPagerPresenter.videoToLaunchAVTransitionTempFile(video,
        project.getProjectPathIntermediateFileAudioFade());

    assertNotEquals("Update tempPath ", tempPath, video.getTempPath());
  }

  public Project getAProject() {
    return Project.getInstance("title", "/path", Profile.getInstance(VideoResolution.Resolution.HD720,
        VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25));
  }
}
