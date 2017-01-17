package com.videonasocialmedia.vimojo.repository.project;

import android.support.annotation.NonNull;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.vimojo.model.entities.editor.LastVideoExported;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.repository.Mapper;
import com.videonasocialmedia.vimojo.repository.video.RealmVideo;
import com.videonasocialmedia.vimojo.repository.video.RealmVideoToVideoMapper;
import com.videonasocialmedia.vimojo.sources.MusicSource;

/**
 * Created by jliarte on 20/10/16.
 */

public class RealmProjectToProjectMapper implements Mapper<RealmProject, Project> {

  private MusicSource musicSource = new MusicSource();
  private RealmVideoToVideoMapper toVideoMapper = new RealmVideoToVideoMapper();

  @Override
  public Project map(RealmProject realmProject) {
    try {
      Profile profile = mapProfile(realmProject);
      Project project = new Project(realmProject.title, null, profile);
      project.setProjectPath(realmProject.projectPath);
      project.setAudioFadeTransitionActivated(realmProject.isAudioFadeTransitionActivated);
      project.setVideoFadeTransitionActivated(realmProject.isVideoFadeTransitionActivated);

      Project project = mapProject(realmProject);
      setProjectMusic(project, realmProject);
      setProjectVideos(project, realmProject);
      setProjectLastVideoExported(project, realmProject);
      return project;
    } catch (Exception exception) {
      return null;
    }
  }


  @NonNull
  private Profile mapProfile(RealmProject realmProject) {
//    if (realmProject.)
    VideoResolution.Resolution resolution = VideoResolution.Resolution.valueOf(realmProject.resolution);
    VideoQuality.Quality quality = VideoQuality.Quality.valueOf(realmProject.quality);
    VideoFrameRate.FrameRate frameRate = VideoFrameRate.FrameRate.valueOf(realmProject.frameRate);

    return new Profile(resolution, quality, frameRate);
  }

  @NonNull
  private Project mapProject(RealmProject realmProject){
    Project currentProject = new Project(realmProject.title, realmProject.projectPath,
        mapProfile(realmProject));
    currentProject.setProjectPath(realmProject.projectPath);
    currentProject.setUuid(realmProject.uuid);
    currentProject.setLastModification(realmProject.lastModification);
    currentProject.setDuration(realmProject.duration);

    return currentProject;
  }

  private void setProjectMusic(Project project, RealmProject realmProject) {
    if (realmProject.musicTitle != null) {
      Music music = new MusicSource().getMusicByTitle(project.getProjectPathIntermediateFiles(),
          realmProject.musicTitle);
      music.setVolume(realmProject.musicVolume);
      try {
        project.getAudioTracks().get(0).insertItemAt(0, music);
      } catch (IllegalItemOnTrack illegalItemOnTrack) {
        illegalItemOnTrack.printStackTrace();
      }
    }
  }

  private void setProjectVideos(Project project, RealmProject realmProject) {
    // TODO(jliarte): 22/10/16 sort videos by order in project
//    realmProject.videos.sort("")
    for (RealmVideo realmVideo : realmProject.videos) {
      try {
        project.getMediaTrack().insertItem(toVideoMapper.map(realmVideo));
      } catch (IllegalItemOnTrack illegalItemOnTrack) {
        illegalItemOnTrack.printStackTrace();
      }
    }
  }

  private void setProjectLastVideoExported(Project project, RealmProject realmProject) {
    if(realmProject.dateLastVideoExported != null && realmProject.pathLastVideoExported != null){
      LastVideoExported lastVideoExported = new LastVideoExported(
          realmProject.pathLastVideoExported,realmProject.dateLastVideoExported);
      project.setLastVideoExported(lastVideoExported);
    }

  }

}
