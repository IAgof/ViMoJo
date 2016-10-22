package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.support.annotation.NonNull;

import com.videonasocialmedia.vimojo.model.entities.editor.Profile;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoQuality;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoResolution;
import com.videonasocialmedia.vimojo.presentation.mvp.views.InitAppView;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by jliarte on 22/10/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class InitAppPresenterTest {
  @Mock ProjectRepository mockedProjectRepo;

  @InjectMocks InitAppPresenter injectedPresenter;

  @Before
  public void initDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void clearProject() {
    Project.getInstance(null, null, null).clear();
  }

  @Test
  public void startLoadingProjectCallsGetCurrentProjectIfInstanceIsNull() {
    assert Project.INSTANCE == null;

    injectedPresenter.startLoadingProject("root/path");

    verify(mockedProjectRepo).getCurrentProject();
  }

  @Test
  public void startLoadingProjectDoesNotCallGetCurrentProjectIfNonNullInstance() {
    Project project = Project.INSTANCE = new Project(null, null, null);

    injectedPresenter.startLoadingProject("root/path");

    verify(mockedProjectRepo, never()).getCurrentProject();
    assertThat(Project.getInstance(null, null, null), is(project));
  }

  @Test
  public void startLoadingProjectSetsProjectInstanceToCurrentProjectRetrieved() {
    assert Project.INSTANCE == null;
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.EXCELLENT,
            -1, Profile.ProfileType.pro);
    Project currentProject = new Project("current project", "current/path", profile);
    doReturn(currentProject).when(mockedProjectRepo).getCurrentProject();

    injectedPresenter.startLoadingProject("root/path");

    assertThat(Project.getInstance(null, null, null), is(currentProject));
  }

  @Test
  public void startLoadingProjectSaveOrUpdateProjectInstance() {
    assert Project.INSTANCE == null;
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.EXCELLENT,
            -1, Profile.ProfileType.pro);
    Project currentProject = new Project("current project", "current/path", profile);
    doReturn(currentProject).when(mockedProjectRepo).getCurrentProject();

    injectedPresenter.startLoadingProject("root/path");

    verify(mockedProjectRepo).update(currentProject);
  }

  @NonNull
  private InitAppView getInitAppView() {
    return new InitAppView() {
      @Override
      public void navigate(Class<?> cls) {

      }
    };
  }
}