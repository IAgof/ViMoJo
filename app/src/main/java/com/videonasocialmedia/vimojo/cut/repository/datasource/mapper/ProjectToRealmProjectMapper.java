package com.videonasocialmedia.vimojo.cut.repository.datasource.mapper;

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.vimojo.cut.domain.model.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.repository.Mapper;
import com.videonasocialmedia.vimojo.repository.music.datasource.mapper.MusicToRealmMusicMapper;
import com.videonasocialmedia.vimojo.cut.repository.datasource.RealmProject;
import com.videonasocialmedia.vimojo.repository.track.datasource.mapper.TrackToRealmTrackMapper;
import com.videonasocialmedia.vimojo.repository.video.datasource.mapper.VideoToRealmVideoMapper;

/**
 * Created by jliarte on 21/10/16.
 */

public class ProjectToRealmProjectMapper implements Mapper<Project, RealmProject> {
  protected VideoToRealmVideoMapper toRealmVideoMapper = new VideoToRealmVideoMapper();
  protected TrackToRealmTrackMapper toRealmTrackMapper = new TrackToRealmTrackMapper();
  protected MusicToRealmMusicMapper toRealmMusicMapper = new MusicToRealmMusicMapper();

  @Override
  public RealmProject map(Project project) {
    if (project.getProfile() == null) {
      return null;
    }
    ProjectInfo projectInfo = project.getProjectInfo();

    RealmProject realmProject = new RealmProject(project.getUuid(), projectInfo.getTitle(),
        projectInfo.getDescription(), project.getLastModification(), project.getProjectPath(),
        project.getProfile().getQuality().name(), project.getProfile().getResolution().name(),
        project.getProfile().getFrameRate().name(), project.getDuration(),
        project.getVMComposition().isAudioFadeTransitionActivated(),
        project.getVMComposition().isVideoFadeTransitionActivated(),
        project.hasWatermark());

    if (project.hasVideoExported()) {
      realmProject.pathLastVideoExported = project.getPathLastVideoExported();
      realmProject.dateLastVideoExported = project.getDateLastVideoExported();
    }

    for (Media video : project.getMediaTrack().getItems()) {
      realmProject.videos.add(toRealmVideoMapper.map((Video) video));
    }

    if(project.hasMusic()) {
      for (Media music : project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_MUSIC).getItems()) {
        realmProject.musics.add(toRealmMusicMapper.map((Music) music));
      }
    }

    if(project.hasVoiceOver()) {
      for (Media music : project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER)
          .getItems()) {
        realmProject.musics.add(toRealmMusicMapper.map((Music) music));
      }
    }

    realmProject.tracks.add(toRealmTrackMapper.map(project.getMediaTrack()));
    realmProject.tracks.add(toRealmTrackMapper.map(project.getAudioTracks()
          .get(Constants.INDEX_AUDIO_TRACK_MUSIC)));

    if(project.hasVoiceOver()) {
      realmProject.tracks.add(toRealmTrackMapper.map(project.getAudioTracks()
          .get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER)));
    }

    if(projectInfo.getProductTypeList().size() > 0) {
      for(String productType: projectInfo.getProductTypeList()) {
        realmProject.productTypeList.add(productType);
      }
    }

    return realmProject;
  }
}
