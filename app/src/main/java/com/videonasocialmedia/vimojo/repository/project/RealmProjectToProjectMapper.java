package com.videonasocialmedia.vimojo.repository.project;

import android.support.annotation.NonNull;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.track.AudioTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.model.entities.editor.LastVideoExported;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.repository.Mapper;
import com.videonasocialmedia.vimojo.repository.music.RealmMusic;
import com.videonasocialmedia.vimojo.repository.music.RealmMusicToMusicMapper;
import com.videonasocialmedia.vimojo.repository.track.RealmTrack;
import com.videonasocialmedia.vimojo.repository.video.RealmVideo;
import com.videonasocialmedia.vimojo.repository.video.RealmVideoToVideoMapper;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import static com.videonasocialmedia.videonamediaframework.model.Constants.*;


/**
 * Created by jliarte on 20/10/16.
 */

public class RealmProjectToProjectMapper implements Mapper<RealmProject, Project> {
  private RealmVideoToVideoMapper toVideoMapper = new RealmVideoToVideoMapper();
  private RealmMusicToMusicMapper toMusicMapper = new RealmMusicToMusicMapper();

  public RealmProjectToProjectMapper() {
  }

  @Override
  public Project map(RealmProject realmProject) {
    try {
      Project project = mapProject(realmProject);
      setProjectVideos(project, realmProject);
      setProjectLastVideoExported(project, realmProject);
      //setProjectTracks(project, realmProject);
      setProjectMusic(project, realmProject);
      return project;
    } catch (Exception exception) {
      return null;
    }
  }


  @NonNull
  private Profile mapProfile(RealmProject realmProject) {
    VideoResolution.Resolution resolution = VideoResolution.Resolution.valueOf(realmProject.resolution);
    VideoQuality.Quality quality = VideoQuality.Quality.valueOf(realmProject.quality);
    VideoFrameRate.FrameRate frameRate = VideoFrameRate.FrameRate.valueOf(realmProject.frameRate);

    return new Profile(resolution, quality, frameRate);
  }

  @NonNull
  private List<Track> mapTrack(RealmProject realmProject){
    List<Track> trackList = new ArrayList<>();
    trackList.add(new Track(com.videonasocialmedia.videonamediaframework.model.
        Constants.INDEX_MEDIA_TRACK,realmProject.volumeVideoTrack, realmProject.muteVideoTrack,
        false,0));
    trackList.add(new Track(com.videonasocialmedia.videonamediaframework.model.
        Constants.INDEX_AUDIO_TRACK_MUSIC,realmProject.volumeMusicTrack,
        realmProject.muteMusicTrack, false, realmProject.positionMusicTrack));
    trackList.add(new Track(com.videonasocialmedia.videonamediaframework.model.
        Constants.INDEX_AUDIO_TRACK_VOICE_OVER,realmProject.volumeVoiceOverTrack,
        realmProject.muteVoiceOverTrack, false, realmProject.positionVoiceOverTrack));
    return trackList;
  }

  @NonNull
  private Project mapProject(RealmProject realmProject){
    Project currentProject = new Project(realmProject.title, Constants.PATH_APP,
        mapProfile(realmProject), mapTrack(realmProject));
    currentProject.setProjectPath(realmProject.projectPath);
    currentProject.setUuid(realmProject.uuid);
    currentProject.setLastModification(realmProject.lastModification);
    currentProject.setDuration(realmProject.duration);
    currentProject.setAudioFadeTransitionActivated(realmProject.isAudioFadeTransitionActivated);
    currentProject.setVideoFadeTransitionActivated(realmProject.isVideoFadeTransitionActivated);
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
    if(realmProject.dateLastVideoExported != null && realmProject.pathLastVideoExported != null){
      LastVideoExported lastVideoExported = new LastVideoExported(
          realmProject.pathLastVideoExported,realmProject.dateLastVideoExported);
      project.setLastVideoExported(lastVideoExported);
    }
  }

  /*private void setProjectTracks(Project project, RealmProject realmProject) {
    for (RealmTrack realmTrack : realmProject.tracks) {
      switch (realmTrack.id) {
        case INDEX_MEDIA_TRACK:
          setTrackParams(project.getMediaTrack(), realmTrack);
          break;
        case INDEX_AUDIO_TRACK_MUSIC:
          setTrackParams(project.getAudioTracks().get(INDEX_AUDIO_TRACK_MUSIC), realmTrack);
          break;
        case INDEX_AUDIO_TRACK_VOICE_OVER:
          setTrackParams(project.getAudioTracks().get(INDEX_AUDIO_TRACK_VOICE_OVER), realmTrack);
          break;
      }
    }
  }*/

  private void setTrackParams(Track track, RealmTrack realmTrack) {
    if (realmTrack != null) {
      track.setVolume(realmTrack.volume);
      track.setSolo(realmTrack.solo);
      track.setMute(realmTrack.mute);
      track.setPosition(realmTrack.position);
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
