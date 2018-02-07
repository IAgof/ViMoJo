package com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.presenters;

import android.view.View;
import android.widget.RelativeLayout;

import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.galleryprojects.domain.UpdateTitleProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.views.DetailProjectView;

import javax.inject.Inject;

/**
 * Created by alvaro on 19/12/16.
 */

public class DetailProjectPresenter {

  private DetailProjectView detailProjectView;
  private Project currentProject;
  private UpdateTitleProjectUseCase updateTitleProjectUseCase;

  @Inject
  public DetailProjectPresenter(DetailProjectView detailProjectView, UpdateTitleProjectUseCase
                                updateTitleProjectUseCase){
    this.detailProjectView = detailProjectView;
    this.updateTitleProjectUseCase = updateTitleProjectUseCase;
    currentProject = Project.getInstance(null,null,null,null);
  }

  public void init(){

    detailProjectView.showTitleProject(currentProject.getTitle());

    double projectSizeMb = currentProject.getProjectSizeMbVideoToExport();
    double formatProjectSizeMb = Math.round(projectSizeMb * 100.0) / 100.0;

    double bitRateMbps = currentProject.getProfile().getVideoQuality().getVideoBitRate()*0.000001;

    detailProjectView.showDetailProjectInfo(
        currentProject.getDuration(),
        formatProjectSizeMb,
        currentProject.getProfile().getVideoResolution().getWidth(),
        bitRateMbps,
        currentProject.getProfile().getVideoFrameRate().getFrameRate());
  }

  public void setTitleProject(String textFromEditText) {
    updateTitleProjectUseCase.setTitle(currentProject, textFromEditText);
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
    if(layoutDetailsInfo.getVisibility() == View.VISIBLE) {
      detailProjectView.shrinkDetailsInfo();
    } else {
      detailProjectView.expandDetailsInfo();
    }
  }
}
