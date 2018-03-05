package com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.presenters;

import android.view.View;
import android.widget.RelativeLayout;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.views.DetailProjectView;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by alvaro on 19/12/16.
 */

public class DetailProjectPresenter {

  private DetailProjectView detailProjectView;
  private Project currentProject;
  private ProjectRepository projectRepository;
  private HashMap<ProjectInfo.ProductType, Boolean> productTypeCheckedIdsMap;
  private boolean[] checkedProductTypes;

  @Inject
  public DetailProjectPresenter(DetailProjectView detailProjectView,
                                ProjectRepository projectRepository) {
    this.detailProjectView = detailProjectView;
    this.projectRepository = projectRepository;
    this.currentProject = loadCurrentProject();
  }

  public Project loadCurrentProject() {
    return Project.getInstance(null, null, null, null);
  }

  public void init() {
    initProductTypeIdsMap(currentProject.getProjectInfo());
    initMultipleChoiceProductTypes();
    detailProjectView.showTitleProject(currentProject.getProjectInfo().getTitle());
    detailProjectView.showDescriptionProject(currentProject.getProjectInfo().getDescription());
    detailProjectView.showProductTypeSelected(currentProject.getProjectInfo().getProductTypeList());
    double projectSizeMb = currentProject.getProjectSizeMbVideoToExport();
    double formatProjectSizeMb = Math.round(projectSizeMb * 100.0) / 100.0;
    double bitRateMbps = currentProject.getProfile().getVideoQuality().getVideoBitRate() * 0.000001;
    detailProjectView.showDetailProjectInfo(
        currentProject.getDuration(),
        formatProjectSizeMb,
        currentProject.getProfile().getVideoResolution().getWidth(),
        bitRateMbps,
        currentProject.getProfile().getVideoFrameRate().getFrameRate());
  }

  private void initProductTypeIdsMap(ProjectInfo projectInfo) {
    productTypeCheckedIdsMap = new HashMap<>();
    productTypeCheckedIdsMap.put(ProjectInfo.ProductType.DIRECT_FAILURE,
        projectInfo.isDirectFalseTypeSelected());
    productTypeCheckedIdsMap.put(ProjectInfo.ProductType.RAW_VIDEOS,
        projectInfo.isRawVideoTypeSelected());
    productTypeCheckedIdsMap.put(ProjectInfo.ProductType.SPOOLERS,
        projectInfo.isSpoolTypeSelected());
    productTypeCheckedIdsMap.put(ProjectInfo.ProductType.TOTAL,
        projectInfo.isTotalTypeSelected());
    productTypeCheckedIdsMap.put(ProjectInfo.ProductType.GRAPHIC,
        projectInfo.isGraphicTypeSelected());
    productTypeCheckedIdsMap.put(ProjectInfo.ProductType.PIECE,
        projectInfo.isPieceTypeSelected());
  }

   private void initMultipleChoiceProductTypes() {
      // Boolean array for initial selected items
      checkedProductTypes = new boolean[]{
          productTypeCheckedIdsMap.get(ProjectInfo.ProductType.DIRECT_FAILURE),
          productTypeCheckedIdsMap.get(ProjectInfo.ProductType.RAW_VIDEOS),
          productTypeCheckedIdsMap.get(ProjectInfo.ProductType.SPOOLERS),
          productTypeCheckedIdsMap.get(ProjectInfo.ProductType.TOTAL),
          productTypeCheckedIdsMap.get(ProjectInfo.ProductType.GRAPHIC),
          productTypeCheckedIdsMap.get(ProjectInfo.ProductType.PIECE)
      };
    }

  public void titleClicked() {
    detailProjectView.showAcceptTitleButton();
  }

  public void titleAccepted() {
    detailProjectView.hideAcceptTitleButton();
  }

  public void descriptionClicked() {
    detailProjectView.showAcceptDescriptionButton();
  }

  public void descriptionAccepted() {
    detailProjectView.hideAcceptDescriptionButton();
  }

  public void detailsExpand(RelativeLayout layoutDetailsInfo) {
    if (layoutDetailsInfo.getVisibility() == View.VISIBLE) {
      detailProjectView.shrinkDetailsInfo();
    } else {
      detailProjectView.expandDetailsInfo();
    }
  }

  public void setProjectInfo(String projectTitle, String projectDescription,
                             List<String> projectInfoProductTypeSelected) {
    projectRepository.setProjectInfo(currentProject, projectTitle, projectDescription,
        projectInfoProductTypeSelected);
  }

  public void addProductTypeSelected(ProjectInfo.ProductType addProductType) {
    productTypeCheckedIdsMap.put(addProductType, true);
  }

  public void removeProductTypeSelected(ProjectInfo.ProductType removeProductType) {
    productTypeCheckedIdsMap.put(removeProductType, false);
  }

  public void onClickProductTypes() {
    detailProjectView.showProductTypeMultipleDialog(checkedProductTypes);
  }
}
