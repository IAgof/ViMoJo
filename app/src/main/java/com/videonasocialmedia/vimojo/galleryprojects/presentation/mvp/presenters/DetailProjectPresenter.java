package com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.presenters;

import android.content.Context;
import android.text.Editable;
import android.view.View;
import android.widget.RelativeLayout;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.views.DetailProjectView;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by alvaro on 19/12/16.
 */

public class DetailProjectPresenter {

  private final Context context;
  private DetailProjectView detailProjectView;
  private Project currentProject;
  private ProjectRepository projectRepository;

  private List<String> productTypesListSelected = new ArrayList<>();

  private HashMap<ProjectInfo.ProductType, Boolean> productTypeCheckedIdsMap;
  private boolean[] checkedProductTypes;
  private String[] productTypesTitles;

  @Inject
  public DetailProjectPresenter(Context context, DetailProjectView detailProjectView,
                                ProjectRepository projectRepository) {
    this.context = context;
    this.detailProjectView = detailProjectView;
    this.projectRepository = projectRepository;
    currentProject = Project.getInstance(null, null, null, null);
  }

  public void init() {

    initProductTypeIdsMap(currentProject.getProjectInfo());

    initMultipleChoiceProductTypes();

    detailProjectView.showTitleProject(currentProject.getProjectInfo().getTitle());

    detailProjectView.showDescriptionProject(currentProject.getProjectInfo().getDescription());

    detailProjectView.showProductTypeSelected(currentProject.getProjectInfo().getProductTypeList(),
        productTypesTitles);

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

    // String array for alert dialog multi choice items
    productTypesTitles = new String[] {
      context.getString(R.string.detail_project_product_type_direct_failure),
          context.getString(R.string.detail_project_product_type_raw_videos),
          context.getString(R.string.detail_project_product_type_spoolers),
          context.getString(R.string.detail_project_product_type_total),
          context.getString(R.string.detail_project_product_type_graphic),
          context.getString(R.string.detail_project_product_type_piece)
    } ;

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

  public void setDetailProject(Editable title, Editable description) {
    String projectTitle = title.toString();
    String projectDescription = description.toString();

    projectRepository.setProjectInfo(currentProject, projectTitle, projectDescription,
        productTypesListSelected);
  }

  public void addProductTypeSelected(int positionSelected) {
    if (positionSelected > ProjectInfo.ProductType.values().length) {
      return;
    }
    ProjectInfo.ProductType addProductType = getProductTypeFromPosition(positionSelected);
    if (addProductType != null) {
      productTypesListSelected.add(addProductType.name());
      productTypeCheckedIdsMap.put(addProductType, true);
    }
  }

  public void removeProductTypeSelected(int positionSelected) {
    ProjectInfo.ProductType removeProductType = getProductTypeFromPosition(positionSelected);
    if(removeProductType != null) {
      productTypesListSelected.remove(removeProductType.name());
      productTypeCheckedIdsMap.put(removeProductType, false);
    }
  }

  private ProjectInfo.ProductType getProductTypeFromPosition(int position) {
    if (position == 0) {
      return ProjectInfo.ProductType.DIRECT_FAILURE;
    } else {
      if (position == 1) {
        return ProjectInfo.ProductType.RAW_VIDEOS;
      } else {
        if (position == 2) {
          return ProjectInfo.ProductType.SPOOLERS;
        } else {
          if (position == 3) {
            return ProjectInfo.ProductType.TOTAL;
          } else {
            if (position == 4) {
              return ProjectInfo.ProductType.GRAPHIC;
            } else {
              if (position == 5) {
                return ProjectInfo.ProductType.PIECE;
              }
            }
          }
        }
      }
    }
    return null;
  }

  public void onClickProductTypes() {
    detailProjectView.showProductTypeMultipleDialog(productTypesTitles, checkedProductTypes);
  }

}
