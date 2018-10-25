package com.videonasocialmedia.vimojo.composition.domain.usecase;

/**
 * Created by jliarte on 17/08/18.
 */

import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;

import java.util.List;

import javax.inject.Inject;

/**
 * Use case for setting composition info details (title, description and product type list.
 */
public class SetCompositionInfo {
  @Inject
  public SetCompositionInfo() {
  }

  public void setCompositionInfo(Project currentProject, String title, String description,
                                 List<String> productTypeList) {
    ProjectInfo projectInfo = currentProject.getProjectInfo();
    projectInfo.setTitle(title);
    projectInfo.setDescription(description);
    projectInfo.setProductTypeList(productTypeList);
  }
}
