package com.videonasocialmedia.vimojo.composition.repository.datasource.mapper;

/**
 * Created by jliarte on 11/07/18.
 */

// TODO(jliarte): 11/07/18 remove this dependency?
import android.text.TextUtils;

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.VMComposition;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.repository.KarumiMapper;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.CompositionDto;

import java.util.Date;

import javax.inject.Inject;

/**
 * Class to provide model conversions between {@link Project} and {@link CompositionDto}
 */
public class CompositionToCompositionDtoMapper extends KarumiMapper<Project, CompositionDto> {
  // TODO(jliarte): 13/07/18 maybe inject?
  TrackToTrackDtoMapper trackToTrackDtoMapper = new TrackToTrackDtoMapper();

  @Override
  public CompositionDto map(Project project) {
    VMComposition vmComposition = project.getVMComposition();
    CompositionDto compositionDto = new CompositionDto();
    compositionDto.id = project.getUuid();
    compositionDto.uuid = project.getUuid();
    if (project.getProjectInfo() != null) {
      mapProjectInfo(project.getProjectInfo(), compositionDto);
    }
    compositionDto.projectPath = project.getProjectPath();
    if (vmComposition.getProfile() != null) {
      mapProjectProfile(vmComposition.getProfile(), compositionDto);
    }
    compositionDto.duration = vmComposition.getDuration();
    compositionDto.isAudioFadeTransitionActivated = vmComposition.isAudioFadeTransitionActivated();
    compositionDto.isVideoFadeTransitionActivated = vmComposition.isVideoFadeTransitionActivated();
    compositionDto.isWatermarkActivated = vmComposition.hasWatermark();
    compositionDto.projectId = "defaultProject";
    compositionDto.date = new Date();
    // TODO(jliarte): 11/07/18 retrieve last updated from realm into Project

    compositionDto.tracks.add(trackToTrackDtoMapper.map(project.getMediaTrack()));
    compositionDto.tracks.add(trackToTrackDtoMapper.map(project.getAudioTracks()
            .get(Constants.INDEX_AUDIO_TRACK_MUSIC)));
    if (project.hasVoiceOver()) {
      compositionDto.tracks.add(trackToTrackDtoMapper.map(project.getAudioTracks()
              .get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER)));
    }

    return compositionDto;
  }

  private void mapProjectProfile(Profile profile, CompositionDto compositionDto) {
    compositionDto.quality = profile.getQuality().name();
    compositionDto.resolution = profile.getResolution().name();
    compositionDto.frameRate = profile.getFrameRate().name();
  }

  private void mapProjectInfo(ProjectInfo projectInfo, CompositionDto compositionDto) {
    compositionDto.title = projectInfo.getTitle();
    compositionDto.description = projectInfo.getDescription();
    if (compositionDto.productType != null && compositionDto.productType.length() > 0) {
      compositionDto.productType = TextUtils.join(",", projectInfo.getProductTypeList());
    }
  }

  @Override
  public Project reverseMap(CompositionDto value) {
    return null;
  }
}
