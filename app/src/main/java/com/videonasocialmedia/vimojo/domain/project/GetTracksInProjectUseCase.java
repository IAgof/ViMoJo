package com.videonasocialmedia.vimojo.domain.project;

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.track.TrackRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alvaro on 12/04/17.
 */

public class GetTracksInProjectUseCase {

  public GetTracksInProjectUseCase(){
  }

  public void getTracksInProject(GetTracksInProjectCallback listener){
    List<Track> trackList = new ArrayList<>();
    Project project = getCurrentProject();
    Track mediaTrack = project.getMediaTrack();
    trackList.add(mediaTrack);
    if(project.hasMusic()){
      Track musicTrack = project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_MUSIC);
      trackList.add(musicTrack);
    }
    if(project.hasVoiceOver()){
      Track voiceOverTrack = project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER);
      trackList.add(voiceOverTrack);
    }
    if(trackList.size()>0) {
      listener.onTracksRetrieved(trackList);
    }
  }

  public Project getCurrentProject() {
    return Project.getInstance(null, null, null);
  }
}
