package com.videonasocialmedia.vimojo.domain.editor;

import com.videonasocialmedia.camera.utils.VideoCameraFormat;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.vimojo.export.domain.GetVideoFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by alvaro on 5/09/16.
 */
public class GetVideoTranscoderFormatFromCurrentProjectUseCaseTest {

  private Project currentProject;
  @Mock ProjectRepository mockedProjectRepository;

  @Before
  public void setUp() throws Exception {
    // FIXME: tests are not independent as Project keeps state between tests
    MockitoAnnotations.initMocks(this);
    getAProject();
    when(mockedProjectRepository.getLastModifiedProject()).thenReturn(currentProject);
  }

  // TODO:(alvaro.martinez) 24/10/16 Profile.setQuality(null), Profile.setResolution(null)
  // Profile.setFrameRate(null) throw NPE
  @Ignore
  @Test
  public void getVideoTranscoderFormatFromCurrentProjectReturnsDefaultFormatIfANullValueInProfile() {
    currentProject.getProfile().setQuality(null);
    GetVideoFormatFromCurrentProjectUseCase useCase =
        new GetVideoFormatFromCurrentProjectUseCase(mockedProjectRepository);
    VideonaFormat defaultVideoTranscoderFormat = new VideonaFormat();

    VideoCameraFormat videoTranscoderFormat =
            useCase.getVideoRecordedFormatFromCurrentProjectUseCase(currentProject);

    assertThat(videoTranscoderFormat.getVideoBitrate(),
            is(defaultVideoTranscoderFormat.getVideoBitrate()));
    assertThat(videoTranscoderFormat.getVideoHeight(),
            is(defaultVideoTranscoderFormat.getVideoHeight()));
    assertThat(videoTranscoderFormat.getVideoWidth(),
            is(defaultVideoTranscoderFormat.getVideoWidth()));
  }

  @Test
  public void getVideoTranscoderFormatFromCurrentProjectReturnsFormatWithProfileValues() {

    GetVideoFormatFromCurrentProjectUseCase useCase =
        new GetVideoFormatFromCurrentProjectUseCase(mockedProjectRepository);

    VideonaFormat videonaFormat = useCase.getVideonaFormatFromCurrentProject(currentProject);

    assertThat("videoBitRate", 50 * 1000 * 1000, is(videonaFormat.getVideoBitrate()));
    assertThat("videoWidth", 1280, is(videonaFormat.getVideoWidth()));
    assertThat("videoHeight", 720, is(videonaFormat.getVideoHeight()));

  }

  private void getAProject() {
    String rootPath = "project/root/path";
    String privatePath = "private/path";
    Profile compositionProfile = new Profile(VideoResolution.Resolution.H_720P,
            VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, rootPath, privatePath, compositionProfile);
  }

}
