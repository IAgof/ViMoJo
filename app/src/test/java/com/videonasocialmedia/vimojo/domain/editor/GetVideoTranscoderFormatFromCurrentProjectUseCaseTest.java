package com.videonasocialmedia.vimojo.domain.editor;

import com.videonasocialmedia.camera.utils.VideoCameraFormat;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.vimojo.export.domain.GetVideoFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by alvaro on 5/09/16.
 */
public class GetVideoTranscoderFormatFromCurrentProjectUseCaseTest {

  @Before
  public void setUp() throws Exception {
    // FIXME: tests are not independent as Project keeps state between tests
    Project.getInstance(null, null, null).clear();
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void constructorSetsProjectInstance() {
    Project currentProject = getAProject();

    GetVideoFormatFromCurrentProjectUseCase useCase =
        new GetVideoFormatFromCurrentProjectUseCase();

    assertThat("Project field set after construction", useCase.project, is(currentProject));
  }

  // TODO:(alvaro.martinez) 24/10/16 Profile.setQuality(null), Profile.setResolution(null)
  // Profile.setFrameRate(null) throw NPE
  @Ignore
  @Test
  public void getVideoTranscoderFormatFromCurrentProjectReturnsDefaultFormatIfANullValueInProfile() {
    Project currentProject = getAProject();
    currentProject.getProfile().setQuality(null);
    GetVideoFormatFromCurrentProjectUseCase useCase =
        new GetVideoFormatFromCurrentProjectUseCase();
    VideonaFormat defaultVideoTranscoderFormat = new VideonaFormat();

    VideoCameraFormat videoTranscoderFormat = useCase.getVideoRecordedFormatFromCurrentProjectUseCase();

    assertThat(videoTranscoderFormat.getVideoBitrate(), is(defaultVideoTranscoderFormat.getVideoBitrate()));
    assertThat(videoTranscoderFormat.getVideoHeight(), is(defaultVideoTranscoderFormat.getVideoHeight()));
    assertThat(videoTranscoderFormat.getVideoWidth(), is(defaultVideoTranscoderFormat.getVideoWidth()));
  }

  @Test
  public void getVideoTranscoderFormatFromCurrentProjectReturnsFormatWithProfileValues() {

    Project currentProject = getAProject();
    GetVideonaFormatFromCurrentProjectUseCase useCase =
        new GetVideonaFormatFromCurrentProjectUseCase();

    VideonaFormat videonaFormat = useCase.getVideonaFormatFromCurrentProject();

    assertThat("videoBitRate", 50 * 1000 * 1000, is(videonaFormat.getVideoBitrate()));
    assertThat("videoWidth", 1280, is(videonaFormat.getVideoWidth()));
    assertThat("videoHeight", 720, is(videonaFormat.getVideoHeight()));

  }

  private Project getAProject() {
    String title = "project title";
    String rootPath = "project/root/path";
    Profile profile = Profile.getInstance(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
        VideoFrameRate.FrameRate.FPS25);
    return Project.getInstance(title, rootPath, profile);
  }

}
