package com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.presenters;

import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.galleryprojects.domain.UpdateTitleProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.views.DetailProjectView;

/**
 * Created by alvaro on 19/12/16.
 */

public class DetailProjectPresenter {

  private DetailProjectView detailProjectView;
  private Project currentProject;
  private UpdateTitleProjectUseCase updateTitleProjectUseCase;

  public DetailProjectPresenter(DetailProjectView detailProjectView, UpdateTitleProjectUseCase
                                updateTitleProjectUseCase){
    this.detailProjectView = detailProjectView;
    this.updateTitleProjectUseCase = updateTitleProjectUseCase;
    currentProject = Project.getInstance(null,null,null);
  }

  public void init(){

    if(currentProject.getVMComposition().hasVideos()) {
      Video firstVideo = (Video) currentProject.getMediaTrack().getItems().get(0);
      String path = firstVideo.getIconPath() != null
          ? firstVideo.getIconPath() : firstVideo.getMediaPath();
      detailProjectView.showDetailProjectThumb(path);
    }

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
}
