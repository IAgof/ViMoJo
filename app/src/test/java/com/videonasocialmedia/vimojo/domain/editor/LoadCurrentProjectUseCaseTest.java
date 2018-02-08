package com.videonasocialmedia.vimojo.domain.editor;

import android.os.Environment;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.reflect.Whitebox.setInternalState;

/**
 * Created by jliarte on 23/10/16.
 */
//@RunWith(MockitoJUnitRunner.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest({Environment.class})
public class LoadCurrentProjectUseCaseTest {
  @Mock
  ProjectRepository mockedProjectRepository;
  @InjectMocks
  LoadCurrentProjectUseCase injectedUseCase;

  @Before
  public void injectDoubles() {
    MockitoAnnotations.initMocks(this);

    mockStatic(Environment.class);
    when(Environment.getExternalStorageState()).thenReturn("mounted");
    setInternalState(Environment.class, "DIRECTORY_DCIM", "DCIM");

    when(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM))
        .thenReturn(new File("DCIM/Vimojo/.temp"));

  }

  @Before
  public void clearProjectInstance() {
    Project instance = Project.INSTANCE;
    if (instance != null) {
      instance.clear();
    }
  }

  @Test
  public void loadCurrentProjectInjectsProjectRepositoryCurrentProjectIntoProjectInstanceIfNull() {
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
            VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    Project currentProject = new Project(projectInfo, "root/path", "private/path", profile);
    assert Project.INSTANCE == null;
    doReturn(currentProject).when(mockedProjectRepository).getCurrentProject();

    Project retrievedProject = injectedUseCase.loadCurrentProject();

    verify(mockedProjectRepository).getCurrentProject();
    assertThat(retrievedProject, is(currentProject));
    assertThat(Project.INSTANCE, is(currentProject));
  }

  @Test
  public void loadCurrentProjectDoesNotChangeNonNullProjectInstance() {
    Project currentProject = Project.getInstance(null, null, null, null);
    assert Project.INSTANCE != null;

    Project retrievedProject = injectedUseCase.loadCurrentProject();

    verify(mockedProjectRepository, never()).getCurrentProject();
    assertThat(retrievedProject, is(currentProject));
    assertThat(Project.INSTANCE, is(currentProject));
  }
}