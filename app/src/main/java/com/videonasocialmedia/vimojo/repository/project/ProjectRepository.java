package com.videonasocialmedia.vimojo.repository.project;

/**
 * Created by jliarte on 20/10/16.
 */

import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.cut.domain.model.Project;
import com.videonasocialmedia.vimojo.repository.Repository;

import java.util.List;

/**
 * Repository for saving Projects (will be renamed to Cuts!!)
 */
public interface ProjectRepository extends Repository<Project> {
  // TODO(jliarte): 11/07/18 rename to CutRepository
  void updateWithDate(Project item, String date);

  Project getLastModifiedProject();

  List<Project> getListProjectsByLastModificationDescending();

  void updateResolution(Project project, VideoResolution.Resolution videoResolution);

  void updateFrameRate(Project project, VideoFrameRate.FrameRate videoFrameRate);

  void updateQuality(Project project, VideoQuality.Quality videoQuality);

  void setWatermarkActivated(Project project, boolean isChecked);

  void setProjectInfo(Project project, String projectTitle, String projectDescription,
                      List<String> productTypesListSelected);
}
