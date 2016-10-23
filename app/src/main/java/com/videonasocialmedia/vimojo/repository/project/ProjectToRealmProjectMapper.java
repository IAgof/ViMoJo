package com.videonasocialmedia.vimojo.repository.project;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Media;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.repository.Mapper;
import com.videonasocialmedia.vimojo.repository.video.VideoToRealmVideoMapper;

/**
 * Created by jliarte on 21/10/16.
 */

public class ProjectToRealmProjectMapper implements Mapper<Project, RealmProject> {
  protected VideoToRealmVideoMapper toRealmVideoMapper = new VideoToRealmVideoMapper();

  @Override
  public RealmProject map(Project project) {
    if (project.getProfile() == null) {
      return null;
    }
    RealmProject realmProject = new RealmProject(project.getTitle(), project.getProjectPath(),
            project.getProfile().getQuality().name(), project.getProfile().getResolution().name());
    if (project.hasMusic()) {
      realmProject.musicTitle = project.getMusic().getMusicTitle();
      realmProject.musicVolume = project.getMusic().getVolume();
    }
    for (Media video : project.getMediaTrack().getItems()) {
      realmProject.videos.add(toRealmVideoMapper.map((Video) video));
    }
     return realmProject;
  }
}
