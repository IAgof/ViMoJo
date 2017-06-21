package com.videonasocialmedia.vimojo.repository.track;

import com.videonasocialmedia.videonamediaframework.model.media.track.AudioTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.repository.Repository;

import java.util.List;

/**
 * Created by alvaro on 10/04/17.
 */

public interface TrackRepository extends Repository<Track> {
  void update(Track item);
  List<Track> getAllTracks();
  void removeAllTracks();
  MediaTrack getMediaTrack();
  AudioTrack getMusicTrack();
  AudioTrack getVoiceOverTrack();
}
