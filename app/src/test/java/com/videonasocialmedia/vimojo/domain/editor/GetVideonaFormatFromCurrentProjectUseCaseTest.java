package com.videonasocialmedia.vimojo.domain.editor;

import com.videonasocialmedia.transcoder.format.VideonaFormat;
import com.videonasocialmedia.vimojo.export.domain.GetVideonaFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.OnGetVideonaFormatListener;
import com.videonasocialmedia.vimojo.model.entities.editor.Profile;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoFrameRate;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoQuality;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoResolution;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by alvaro on 5/09/16.
 */
public class GetVideonaFormatFromCurrentProjectUseCaseTest {

  @Mock OnGetVideonaFormatListener mockedGetVideonaFormatListener;
  @InjectMocks
  GetVideonaFormatFromCurrentProjectUseCase injectedUseCase;
  private Project currentProject;
  VideonaFormat videonaFormat;

  @Before
  public void setUp() throws Exception {
    currentProject = getAProject();
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void tearDown() throws Exception {
    // FIXME: tests are not independent as Project keeps state between tests
    Project singletonProject = Project.getInstance(null, null, null);
    singletonProject.clear();
  }


  @Test
  public void constructorSetsProjectInstance() {

    assertThat("Project field set after construction", injectedUseCase.project, is(currentProject));
  }

  @Test
  public void getVideonaFormatFromProjectReturnCorrectFormat() {

    injectedUseCase.getVideonaFormatFromProject(mockedGetVideonaFormatListener);

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
