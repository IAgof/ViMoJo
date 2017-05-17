package com.videonasocialmedia.vimojo.domain.project;

import com.videonasocialmedia.vimojo.repository.track.TrackRepository;

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
