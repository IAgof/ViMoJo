/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.composition.domain.usecase;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.repository.datasource.ProjectRealmDataSource;
import com.videonasocialmedia.vimojo.model.entities.editor.ProductType;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.model.sources.ProductTypeProvider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by alvaro on 7/9/18.
 */

public class SetCompositionInfoTest {

  @InjectMocks
  SetCompositionInfo injectedSetCompositionInfoUseCase;
  @Mock
  ProjectRealmDataSource mockedProjectRepository;
  private Project currentProject;

  @Before
  public void injectDoubles() {
    MockitoAnnotations.initMocks(this);
    getAProject();
    when(mockedProjectRepository.getLastModifiedProject()).thenReturn(currentProject);
  }

  @Test
  public void testSetResolutionSetProfileResolution() {
    String newTitle = "newTitle";
    String newDescription = "newDescription";
    List<String> newProductTypeList = new ArrayList<>();
    newProductTypeList.add(ProductTypeProvider.Types.INTERVIEW.name());

    injectedSetCompositionInfoUseCase.setCompositionInfo(currentProject, newTitle, newDescription,
        newProductTypeList);

    assertThat("Title is set", currentProject.getProjectInfo().getTitle(),
        is(newTitle));
    assertThat("Description is set", currentProject.getProjectInfo().getDescription(),
        is(newDescription));
    assertThat("Product type list is set",
        currentProject.getProjectInfo().getProductTypeList(), is(newProductTypeList));
  }

  public void getAProject() {
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
        VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, "/path","private/path", profile);
  }
}
