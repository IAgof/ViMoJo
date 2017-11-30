package com.videonasocialmedia.vimojo.repository.project;

import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.Repository;

import java.util.List;

/**
 * Created by jliarte on 20/10/16.
 */

public interface ProjectRepository extends Repository<Project> {
  void updateWithDate(Project item, String date);

  Project getCurrentProject();

  List<Project> getListProjectsByLastModificationDescending();

  void updateResolution(Project project, VideoResolution.Resolution videoResolution);

  void updateFrameRate(Project project, VideoFrameRate.FrameRate videoFrameRate);

  void updateQuality(Project project, VideoQuality.Quality videoQuality);

  void setWatermarkActivated(Project project, boolean isChecked);
}
