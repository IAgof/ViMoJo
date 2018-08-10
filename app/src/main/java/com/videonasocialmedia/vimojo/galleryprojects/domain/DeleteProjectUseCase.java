package com.videonasocialmedia.vimojo.galleryprojects.domain;

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.track.AudioTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.repository.music.MusicDataSource;
import com.videonasocialmedia.vimojo.composition.repository.ProjectRepository;
import com.videonasocialmedia.vimojo.composition.repository.datasource.TrackDataSource;
import com.videonasocialmedia.vimojo.asset.repository.datasource.VideoDataSource;
import com.videonasocialmedia.vimojo.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.inject.Inject;

/**
 * Created by alvaro on 14/12/16.
 */

public class DeleteProjectUseCase {
  protected ProjectRepository projectRepository;
  protected VideoDataSource videoRepository;
  private MusicDataSource musicRepository;
  private TrackDataSource trackRepository;

  @Inject
  public DeleteProjectUseCase(ProjectRepository projectRepository, VideoDataSource videoRepository,
                              MusicDataSource musicRepository, TrackDataSource trackRepository) {
    this.projectRepository = projectRepository;
    this.videoRepository = videoRepository;
    this.musicRepository = musicRepository;
    this.trackRepository = trackRepository;
  }

  public void delete(Project project) {
    Track videoTrack = project.getMediaTrack();
    LinkedList<Media> videoList = videoTrack.getItems();
    for (Media media : videoList) {
      videoRepository.remove((Video) media);
    }
    trackRepository.remove(videoTrack);

    ArrayList<AudioTrack> audioTracks = project.getAudioTracks();
    if (audioTracks != null && audioTracks.size() > 0) {
      // TODO(jliarte): 9/08/18 FIXME: sometimes audioTracks is empty
      Track musicTrack = audioTracks.get(Constants.INDEX_AUDIO_TRACK_MUSIC);
      LinkedList<Media> musicList = musicTrack.getItems();
      for (Media media: musicList){
        musicRepository.remove((Music) media);
      }
      trackRepository.remove(musicTrack);
    }

    if (project.hasVoiceOver()) {
      Track voiceOverTrack = project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER);
      LinkedList<Media> voiceOverList = voiceOverTrack.getItems();
      for (Media media : voiceOverList) {
        musicRepository.remove((Music) media);
      }
      trackRepository.remove(voiceOverTrack);
    }

    projectRepository.remove(project);

    FileUtils.deleteDirectory(new File(project.getProjectPath()));
  }
}
