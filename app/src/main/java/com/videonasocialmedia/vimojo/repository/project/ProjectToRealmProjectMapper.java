package com.videonasocialmedia.vimojo.repository.project;

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.repository.Mapper;
import com.videonasocialmedia.vimojo.repository.music.MusicToRealmMusicMapper;
import com.videonasocialmedia.vimojo.repository.track.TrackToRealmTrackMapper;
import com.videonasocialmedia.vimojo.repository.video.VideoToRealmVideoMapper;

/**
 * Created by jliarte on 21/10/16.
 */

public class ProjectToRealmProjectMapper implements Mapper<Project, RealmProject> {
  protected VideoToRealmVideoMapper toRealmVideoMapper = new VideoToRealmVideoMapper();
  protected TrackToRealmTrackMapper toRealmTrackMapper = new TrackToRealmTrackMapper();
  protected MusicToRealmMusicMapper toReamMusicMapper = new MusicToRealmMusicMapper();

  @Override
  public RealmProject map(Project project) {
    if (project.getProfile() == null) {
      return null;
    }
    RealmProject realmProject = new RealmProject(project.getUuid(), project.getTitle(),
            project.getLastModification(), project.getProjectPath(),
            project.getProfile().getQuality().name(), project.getProfile().getResolution().name(),
            project.getProfile().getFrameRate().name(), project.getDuration(),
            project.isAudioFadeTransitionActivated(), project.isVideoFadeTransitionActivated(),
            project.hasWatermark());

    if (project.hasVideoExported()) {
      realmProject.pathLastVideoExported = project.getPathLastVideoExported();
      realmProject.dateLastVideoExported = project.getDateLastVideoExported();
    }

    for (Media video : project.getMediaTrack().getItems()) {
      realmProject.videos.add(toRealmVideoMapper.map((Video) video));
    }

    for (Media music : project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACKS_MUSIC).getItems()) {
      realmProject.musics.add(toReamMusicMapper.map((Music) music));
    }

    for (Media music : project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACKS_VOICE_OVER)
        .getItems()) {
      realmProject.musics.add(toReamMusicMapper.map((Music) music));
    }

    realmProject.tracks.add(toRealmTrackMapper.map(project.getMediaTrack()));
    realmProject.tracks.add(toRealmTrackMapper.map(project.getAudioTracks()
        .get(Constants.INDEX_AUDIO_TRACKS_MUSIC)));
    realmProject.tracks.add(toRealmTrackMapper.map(project.getAudioTracks()
        .get(Constants.INDEX_AUDIO_TRACKS_VOICE_OVER)));

     return realmProject;
  }
}
