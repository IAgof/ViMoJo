package com.videonasocialmedia.vimojo.cut.repository.datasource.mapper;

import android.support.annotation.NonNull;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.track.AudioTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.vimojo.model.entities.editor.LastVideoExported;
import com.videonasocialmedia.vimojo.cut.domain.model.Project;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.repository.Mapper;
import com.videonasocialmedia.vimojo.repository.music.datasource.RealmMusic;
import com.videonasocialmedia.vimojo.repository.music.datasource.mapper.RealmMusicToMusicMapper;
import com.videonasocialmedia.vimojo.cut.repository.datasource.RealmProject;
import com.videonasocialmedia.vimojo.repository.track.datasource.RealmTrack;
import com.videonasocialmedia.vimojo.repository.track.datasource.mapper.RealmTrackToTrackMapper;
import com.videonasocialmedia.vimojo.repository.video.datasource.RealmVideo;
import com.videonasocialmedia.vimojo.repository.video.datasource.mapper.RealmVideoToVideoMapper;
import com.videonasocialmedia.vimojo.utils.Constants;

import static com.videonasocialmedia.videonamediaframework.model.Constants.*;


/**
 * Created by jliarte on 20/10/16.
 */

public class RealmProjectToProjectMapper implements Mapper<RealmProject, Project> {
  private RealmVideoToVideoMapper toVideoMapper = new RealmVideoToVideoMapper();
  private RealmMusicToMusicMapper toMusicMapper = new RealmMusicToMusicMapper();
  private RealmTrackToTrackMapper toTrackMapper = new RealmTrackToTrackMapper();

  public RealmProjectToProjectMapper() {
  }

  @Override
  public Project map(RealmProject realmProject) {
    try {
      Project project = mapProject(realmProject);
      setProjectTracks(project, realmProject);
      setProjectVideos(project, realmProject);
      setProjectLastVideoExported(project, realmProject);
      setProjectMusic(project, realmProject);
      return project;
    } catch (Exception exception) {
      return null;
    }
  }

  @NonNull
  private Profile mapProfile(RealmProject realmProject) {
    VideoResolution.Resolution resolution =
            VideoResolution.Resolution.valueOf(realmProject.resolution);
    VideoQuality.Quality quality = VideoQuality.Quality.valueOf(realmProject.quality);
    VideoFrameRate.FrameRate frameRate = VideoFrameRate.FrameRate.valueOf(realmProject.frameRate);

    return new Profile(resolution, quality, frameRate);
  }

  @NonNull
  private Project mapProject(RealmProject realmProject){
    ProjectInfo projectInfo = new ProjectInfo(realmProject.title, realmProject.description,
        realmProject.productTypeList);
    Project currentProject = new Project(projectInfo, Constants.PATH_APP,
        Constants.PATH_APP_ANDROID, mapProfile(realmProject));
    currentProject.setProjectPath(realmProject.projectPath);
    currentProject.setUuid(realmProject.uuid);
    currentProject.updateDateOfModification(realmProject.lastModification);
    currentProject.setDuration(realmProject.duration);
    currentProject.getVMComposition().setAudioFadeTransitionActivated(
            realmProject.isAudioFadeTransitionActivated);
    currentProject.getVMComposition().setVideoFadeTransitionActivated(
            realmProject.isVideoFadeTransitionActivated);
    currentProject.setWatermarkActivated(realmProject.isWatermarkActivated);

    return currentProject;
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
    if (realmProject.dateLastVideoExported != null && realmProject.pathLastVideoExported != null) {
      LastVideoExported lastVideoExported = new LastVideoExported(
          realmProject.pathLastVideoExported,realmProject.dateLastVideoExported);
      project.setLastVideoExported(lastVideoExported);
    }
  }

  private void setProjectTracks(Project project, RealmProject realmProject) {
    project.getAudioTracks().clear();
    for (RealmTrack realmTrack : realmProject.tracks) {
      switch (realmTrack.id) {
        case INDEX_MEDIA_TRACK:
          project.setMediaTrack((MediaTrack) toTrackMapper.map(realmTrack));
          break;
        case INDEX_AUDIO_TRACK_MUSIC:
          project.getAudioTracks().add(INDEX_AUDIO_TRACK_MUSIC,
              (AudioTrack) toTrackMapper.map(realmTrack));
        case INDEX_AUDIO_TRACK_VOICE_OVER:
          project.getAudioTracks().add(INDEX_AUDIO_TRACK_VOICE_OVER,
               (AudioTrack) toTrackMapper.map(realmTrack));
          break;
      }
    }
  }

  private void setProjectMusic(Project project, RealmProject realmProject) {
    for (RealmMusic realmMusic : realmProject.musics) {
      try {
        if (isAVoiceOver(realmMusic)) {
          project.getAudioTracks()
              .get(INDEX_AUDIO_TRACK_VOICE_OVER).insertItem(toMusicMapper.map(realmMusic));
        } else {
          project.getAudioTracks()
              .get(INDEX_AUDIO_TRACK_MUSIC).insertItem(toMusicMapper.map(realmMusic));
        }
      } catch (IllegalItemOnTrack illegalItemOnTrack) {
        illegalItemOnTrack.printStackTrace();
      }
    }
  }

  private boolean isAVoiceOver(RealmMusic realmMusic) {
    return realmMusic.title.compareTo(Constants.MUSIC_AUDIO_VOICEOVER_TITLE) == 0;
  }
}
