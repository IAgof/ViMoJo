package com.videonasocialmedia.vimojo.domain.editor;

import com.videonasocialmedia.transcoder.format.VideonaFormat;
import com.videonasocialmedia.vimojo.export.domain.GetVideonaFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Profile;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoFrameRate;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoQuality;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoResolution;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by alvaro on 5/09/16.
 */
public class GetVideonaFormatFromCurrentProjectUseCaseTest {

  @Before
  public void setUp() throws Exception {
    // FIXME: tests are not independent as Project keeps state between tests
    Project.getInstance(null, null, null).clear();
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void constructorSetsProjectInstance() {
    Project currentProject = getAProject();

    GetVideonaFormatFromCurrentProjectUseCase useCase =
        new GetVideonaFormatFromCurrentProjectUseCase();

    assertThat("Project field set after construction", useCase.project, is(currentProject));
  }

  // TODO:(alvaro.martinez) 24/10/16 Profile.setQuality(null), Profile.setResolution(null)
  // Profile.setFrameRate(null) throw NPE
  @Ignore
  @Test
  public void getVideonaFormatFromCurrentProjectReturnsDefaultFormatIfANullValueInProfile() {
    Project currentProject = getAProject();
    currentProject.getProfile().setQuality(null);
    GetVideonaFormatFromCurrentProjectUseCase useCase =
        new GetVideonaFormatFromCurrentProjectUseCase();
    VideonaFormat defaultVideonaFormat = new VideonaFormat();

    VideonaFormat videonaFormat = useCase.getVideonaFormatFromCurrentProject();

    assertThat(videonaFormat.getVideoBitrate(), is(defaultVideonaFormat.getVideoBitrate()));
    assertThat(videonaFormat.getVideoHeight(), is(defaultVideonaFormat.getVideoHeight()));
    assertThat(videonaFormat.getVideoWidth(), is(defaultVideonaFormat.getVideoWidth()));
  }

  @Test
  public void getVideonaFormatFromCurrentProjectReturnsFormatWithProfileValues() {
    Project currentProject = getAProject();
    GetVideonaFormatFromCurrentProjectUseCase useCase =
        new GetVideonaFormatFromCurrentProjectUseCase();

    VideonaFormat videonaFormat = useCase.getVideonaFormatFromCurrentProject();

    assertThat("videoBitRate", 10 * 1000 * 1000, is(videonaFormat.getVideoBitrate()));
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
