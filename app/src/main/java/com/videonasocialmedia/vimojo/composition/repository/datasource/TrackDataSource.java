package com.videonasocialmedia.vimojo.composition.repository.datasource;

import com.videonasocialmedia.videonamediaframework.model.media.track.AudioTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.repository.datasource.DataSource;

import java.util.List;

/**
 * Created by alvaro on 10/04/17.
 */

public interface TrackDataSource extends DataSource<Track> {
  void update(Track item);
  List<Track> getAllTracks();
  void removeAllTracks();
  MediaTrack getMediaTrack();
  AudioTrack getMusicTrack();
  AudioTrack getVoiceOverTrack();
}
