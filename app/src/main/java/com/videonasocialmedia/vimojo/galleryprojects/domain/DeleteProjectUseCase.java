package com.videonasocialmedia.vimojo.galleryprojects.domain;

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.music.MusicRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.track.TrackRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.vimojo.utils.FileUtils;

import java.io.File;
import java.util.LinkedList;

import javax.inject.Inject;

/**
 * Created by alvaro on 14/12/16.
 */

public class DeleteProjectUseCase {

  protected ProjectRepository projectRepository;
  protected VideoRepository videoRepository;
  protected MusicRepository musicRepository;
  protected TrackRepository trackRepository;

  @Inject
  public DeleteProjectUseCase(ProjectRepository projectRepository, VideoRepository videoRepository,
                              MusicRepository musicRepository, TrackRepository trackRepository){
    this.projectRepository = projectRepository;
    this.videoRepository = videoRepository;
    this.musicRepository = musicRepository;
    this.trackRepository = trackRepository;
  }

  public void delete(Project project){
    Track videoTrack = project.getMediaTrack();
    LinkedList<Media> videoList = videoTrack.getItems();
    for (Media media : videoList) {
      videoRepository.remove((Video) media);
    }
    trackRepository.remove(videoTrack);

    Track musicTrack = project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_MUSIC);
    LinkedList<Media> musicList = musicTrack.getItems();
    for(Media media: musicList){
      musicRepository.remove((Music) media);
    }
    trackRepository.remove(musicTrack);

    if(project.hasVoiceOver()) {
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
