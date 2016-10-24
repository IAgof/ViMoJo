package com.videonasocialmedia.vimojo.repository.project;

import android.support.annotation.NonNull;

import com.videonasocialmedia.vimojo.model.entities.editor.Profile;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Music;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoFrameRate;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoQuality;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoResolution;
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
      setProjectMusic(project, realmProject);
      setProjectVideos(project, realmProject);
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
    VideoFrameRate.FrameRate frameRate = VideoFrameRate.FrameRate.valueOf(realmProject.framerate);

    return new Profile(resolution, quality, frameRate);
  }

  private void setProjectMusic(Project project, RealmProject realmProject) {
    if (realmProject.musicTitle != null) {
      Music music = musicSource.getMusicByTitle(realmProject.musicTitle);
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
}
