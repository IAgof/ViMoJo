package com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.presenters;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.composition.domain.usecase.SetCompositionInfo;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateComposition;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.model.entities.editor.ProductType;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.views.DetailProjectView;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.model.sources.ProductTypeProvider;
import com.videonasocialmedia.vimojo.composition.repository.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.view.VimojoPresenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by alvaro on 19/12/16.
 */

public class DetailProjectPresenter extends VimojoPresenter {
  private final Context context;
  private DetailProjectView detailProjectView;
  private UserEventTracker userEventTracker;
  private ProjectRepository projectRepository;
  private final ProjectInstanceCache projectInstanceCache;
  private Project currentProject;
  private HashMap<Integer, Boolean> productTypeCheckedIdsMap;
  private boolean[] checkedProductTypes;
  private List<String> productTypesTitles = new ArrayList<>();;

  private final int LIVE_ON_TAPE_ID = 0;
  private final int B_ROLL_ID = 1;
  private final int NAT_VO_ID = 2;
  private final int INTERVIEW_ID = 3;
  private final int GRAPHICS_ID = 4;
  private final int PIECE_ID = 5;
  private UpdateComposition updateComposition;
  private SetCompositionInfo setCompositionInfo;

  @Inject
  public DetailProjectPresenter(
          Context context, DetailProjectView detailProjectView, UserEventTracker userEventTracker,
          ProjectRepository projectRepository, ProjectInstanceCache projectInstanceCache,
          UpdateComposition updateComposition, SetCompositionInfo setCompositionInfo) {
    this.context = context;
    this.detailProjectView = detailProjectView;
    this.userEventTracker = userEventTracker;
    this.projectRepository = projectRepository;
    this.projectInstanceCache = projectInstanceCache;
    this.updateComposition = updateComposition;
    this.setCompositionInfo = setCompositionInfo;
  }

  public void init() {
    this.currentProject = projectInstanceCache.getCurrentProject();
    initProductTypeIdsMap(currentProject.getProjectInfo());
    initMultipleChoiceProductTypes();
    detailProjectView.showTitleProject(currentProject.getProjectInfo().getTitle());
    detailProjectView.showDescriptionProject(currentProject.getProjectInfo().getDescription());
    List<String> productTypeList = currentProject.getProjectInfo().getProductTypeList();
    detailProjectView.showProductTypeSelected(productTypeList);
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

  public void updatePresenter() {
    this.currentProject = projectInstanceCache.getCurrentProject();
  }

  private void initProductTypeIdsMap(ProjectInfo projectInfo) {
    productTypeCheckedIdsMap = new HashMap<>();
    productTypeCheckedIdsMap.put(LIVE_ON_TAPE_ID,
        projectInfo.getProductTypeList().contains(ProductTypeProvider.Types.LIVE_ON_TAPE.name()));
    productTypeCheckedIdsMap.put(B_ROLL_ID,
        projectInfo.getProductTypeList().contains(ProductTypeProvider.Types.B_ROLL.name()));
    productTypeCheckedIdsMap.put(NAT_VO_ID,
        projectInfo.getProductTypeList().contains(ProductTypeProvider.Types.NAT_VO.name()));
    productTypeCheckedIdsMap.put(INTERVIEW_ID,
        projectInfo.getProductTypeList().contains(ProductTypeProvider.Types.INTERVIEW.name()));
    productTypeCheckedIdsMap.put(GRAPHICS_ID,
        projectInfo.getProductTypeList().contains(ProductTypeProvider.Types.GRAPHICS.name()));
    productTypeCheckedIdsMap.put(PIECE_ID,
        projectInfo.getProductTypeList().contains(ProductTypeProvider.Types.PIECE.name()));
  }

   private void initMultipleChoiceProductTypes() {
      // Boolean array for initial selected items
      checkedProductTypes = new boolean[]{
          productTypeCheckedIdsMap.get(LIVE_ON_TAPE_ID),
          productTypeCheckedIdsMap.get(B_ROLL_ID),
          productTypeCheckedIdsMap.get(NAT_VO_ID),
          productTypeCheckedIdsMap.get(INTERVIEW_ID),
          productTypeCheckedIdsMap.get(GRAPHICS_ID),
          productTypeCheckedIdsMap.get(PIECE_ID)
      };

     productTypesTitles.add(context.getString(R.string.detail_project_product_type_live_on_tape));
     productTypesTitles.add(context.getString(R.string.detail_project_product_type_b_roll));
     productTypesTitles.add(context.getString(R.string.detail_project_product_type_nat_vo));
     productTypesTitles.add(context.getString(R.string.detail_project_product_type_interview));
     productTypesTitles.add(context.getString(R.string.detail_project_product_type_graphic));
     productTypesTitles.add(context.getString(R.string.detail_project_product_type_piece));
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
    userEventTracker.trackProjectInfo(currentProject);
    setCompositionInfo.setCompositionInfo(currentProject, projectTitle, projectDescription,
        getProjectInfoProductTypeSelectedInOrder(projectInfoProductTypeSelected));
    executeUseCaseCall(() -> updateComposition.updateComposition(currentProject));
  }

  // Product list is added/removed item by item without order. Needed sort list to show it properly in activity.
  private List<String> getProjectInfoProductTypeSelectedInOrder(List<String>
                                                          projectInfoArrayListProductTypeSelected) {
    List<ProductType> sortProjectInfoProductTypeList = new ArrayList<>();
    for (String productTypeString : projectInfoArrayListProductTypeSelected) {
      for (ProductType productType : ProductTypeProvider.getProductTypeList()) {
        if (productTypeString.equals(productType.getName())) {
          sortProjectInfoProductTypeList.add(productType);
        }
      }
    }
    Collections.sort(sortProjectInfoProductTypeList);
    List<String> sortProjectInfoArrayListProductType = new ArrayList<>();
    for (ProductType productType : sortProjectInfoProductTypeList) {
      sortProjectInfoArrayListProductType.add(productType.getName());
    }
    return sortProjectInfoArrayListProductType;
  }

  public void addProductTypeSelected(int position) {
    ProductType productType = getProductTypeFromPosition(position);
    productTypeCheckedIdsMap.put(productType.getPosition(), true);
    detailProjectView.addSelectedProductType(productType.getName());
  }

  public void removeProductTypeSelected(int position) {
    ProductType productType = getProductTypeFromPosition(position);
    productTypeCheckedIdsMap.put(productType.getPosition(), false);
    detailProjectView.removeSelectedProductType(productType.getName());
  }

  public void onClickProductTypes() {
    detailProjectView.showProductTypeMultipleDialog(checkedProductTypes, productTypesTitles);
  }

  private ProductType getProductTypeFromPosition(int position) {
    List<ProductType> productTypeList = ProductTypeProvider.getProductTypeList();
    return productTypeList.get(position);
  }

  public List<String> convertToStringProductTypeListValues(List<String> productTypeList) {
    List<String> productTypeConverted = new ArrayList<>();
    List<ProductType> productTypeListProvider = ProductTypeProvider.getProductTypeList();
    for(String productTypeName: productTypeList) {
      for(ProductType productType: productTypeListProvider) {
        if(productTypeName.equals(productType.getName())) {
          productTypeConverted.add(productTypesTitles.get(productType.getPosition()));
        }
      }
    }
    return productTypeConverted;
  }

}
