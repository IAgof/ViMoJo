package com.videonasocialmedia.vimojo.domain.project;

import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.repository.track.TrackRepository;

import java.util.List;

/**
 * Created by alvaro on 12/04/17.
 */

public class GetTracksInProjectUseCase {

  private TrackRepository trackRepository;

  public GetTracksInProjectUseCase(TrackRepository trackRepository){
    this.trackRepository = trackRepository;
  }

  public void getTracksInProject(GetTracksInProjectCallback listener){
    listener.onTracksRetrieved(trackRepository.getAllTracks());
  }
}
