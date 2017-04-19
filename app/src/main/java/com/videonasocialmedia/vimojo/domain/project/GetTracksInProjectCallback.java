package com.videonasocialmedia.vimojo.domain.project;

import com.videonasocialmedia.videonamediaframework.model.media.track.Track;

import java.util.List;

/**
 * Created by alvaro on 12/04/17.
 */

public interface GetTracksInProjectCallback {
  void onTracksRetrieved(List<Track> trackList);
}
