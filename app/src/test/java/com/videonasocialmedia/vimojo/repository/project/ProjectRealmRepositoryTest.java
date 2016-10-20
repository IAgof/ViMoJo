package com.videonasocialmedia.vimojo.repository.project;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by jliarte on 20/10/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class ProjectRealmRepositoryTest {
  @Test
  public void testGetCurrentProjectReturnsANewProjectIfThereIsNoCurrentProject() {
    ProjectRepository repo = new ProjectRealmRepository();
    Project defaultEmptyProject = Project.getInstance(null, null, null);

    Project project = repo.getCurrentProject();

    assertThat(project, is(defaultEmptyProject));
  }

//  @Test
//  public void
}