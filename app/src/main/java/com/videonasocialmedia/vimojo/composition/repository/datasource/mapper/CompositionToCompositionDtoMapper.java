package com.videonasocialmedia.vimojo.composition.repository.datasource.mapper;

/**
 * Created by jliarte on 11/07/18.
 */

// TODO(jliarte): 11/07/18 remove this dependency?
import android.text.TextUtils;

import com.videonasocialmedia.videonamediaframework.model.VMComposition;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.track.AudioTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.asset.repository.datasource.mapper.AssetToAssetDtoMapper;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.repository.DataPersistanceType;
import com.videonasocialmedia.vimojo.repository.KarumiMapper;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.CompositionDto;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.MediaDto;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.TrackDto;

import java.util.Date;

import javax.inject.Inject;

import static com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_MUSIC;
import static com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_VOICE_OVER;
import static com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_MEDIA_TRACK;

/**
 * Class to provide model conversions between {@link Project} and {@link CompositionDto}
 */
public class CompositionToCompositionDtoMapper extends KarumiMapper<Project, CompositionDto> {
  private TrackToTrackDtoMapper trackToTrackDtoMapper;
  private AssetToAssetDtoMapper assetToAssetDtoMapper;
  private String rootPath;
  private String privatePath;

  @Inject
  public CompositionToCompositionDtoMapper(TrackToTrackDtoMapper trackToTrackDtoMapper,
                                           AssetToAssetDtoMapper assetToAssetDtoMapper) {
    this.trackToTrackDtoMapper = trackToTrackDtoMapper;
    this.assetToAssetDtoMapper = assetToAssetDtoMapper;
    // TODO(jliarte): 7/08/18 inject to remove Constants dep?
    this.rootPath = com.videonasocialmedia.vimojo.utils.Constants.PATH_APP;
    this.privatePath = com.videonasocialmedia.vimojo.utils.Constants.PATH_APP_ANDROID;
  }

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
            .get(INDEX_AUDIO_TRACK_MUSIC)));
    if (project.hasVoiceOver()) {
      compositionDto.tracks.add(trackToTrackDtoMapper.map(project.getAudioTracks()
              .get(INDEX_AUDIO_TRACK_VOICE_OVER)));
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
  public Project reverseMap(CompositionDto compositionDto) {
    ProjectInfo projectInfo = new ProjectInfo(compositionDto.getTitle(),
            compositionDto.getDescription(), compositionDto.getProductTypeList());
    Project project = new Project(projectInfo, rootPath, privatePath, mapProfile(compositionDto));
    project.setDataPersistanceType(DataPersistanceType.API);
    project.setProjectPath(compositionDto.getProjectPath());
    project.setUuid(compositionDto.getId());
    // TODO(jliarte): 7/08/18 parse date
//    project.updateDateOfModification(compositionDto.getModification_date());
    project.setDuration(compositionDto.getDuration());
    project.getVMComposition().setAudioFadeTransitionActivated(
            compositionDto.isAudioFadeTransitionActivated());
    project.getVMComposition().setVideoFadeTransitionActivated(
            compositionDto.isVideoFadeTransitionActivated());
    project.setWatermarkActivated(compositionDto.isWatermarkActivated());
    project.updateDateOfModification(compositionDto.getModification_date().toString());
    mapCompositionDtoTracks(compositionDto, project);
    mapCompositionDtoAssets(compositionDto, project);
    return project;
  }

  private void mapCompositionDtoAssets(CompositionDto compositionDto, Project project) {
    for (TrackDto trackDto : compositionDto.getTracks()) {
      for (MediaDto mediaDto : trackDto.getMediaItems()) {
        if (mediaDto.getAsset() != null) {
          project.getAssets().put(mediaDto.getId(),
                  assetToAssetDtoMapper.reverseMap(mediaDto.getAsset()));
        }
      }
    }
  }

  private void mapCompositionDtoTracks(CompositionDto compositionDto, Project project) {
    if (compositionDto.getTracks() != null && compositionDto.getTracks().size() > 0) {
      for (TrackDto trackDto : compositionDto.getTracks())
        switch (trackDto.getTrackIndex()) {
          case INDEX_MEDIA_TRACK:
            project.setMediaTrack((MediaTrack) trackToTrackDtoMapper.reverseMap(trackDto));
            break;
          case INDEX_AUDIO_TRACK_MUSIC:
            project.getAudioTracks().add(INDEX_AUDIO_TRACK_MUSIC,
                    (AudioTrack) trackToTrackDtoMapper.reverseMap(trackDto));
          case INDEX_AUDIO_TRACK_VOICE_OVER:
            project.getAudioTracks().add(INDEX_AUDIO_TRACK_VOICE_OVER,
                    (AudioTrack) trackToTrackDtoMapper.reverseMap(trackDto));
            break;
        }
    }
  }

  private Profile mapProfile(CompositionDto compositionDto) {
    VideoResolution.Resolution resolution =
            VideoResolution.Resolution.valueOf(compositionDto.getResolution());
    VideoQuality.Quality quality = VideoQuality.Quality.valueOf(compositionDto.getQuality());
    VideoFrameRate.FrameRate frameRate =
            VideoFrameRate.FrameRate.valueOf(compositionDto.getFrameRate());
    return new Profile(resolution, quality, frameRate);
  }
}
