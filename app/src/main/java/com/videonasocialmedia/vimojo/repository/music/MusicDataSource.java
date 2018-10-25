package com.videonasocialmedia.vimojo.repository.music;

import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.vimojo.repository.datasource.DataSource;

import java.util.List;

/**
 * Created by alvaro on 12/04/17.
 */

public interface MusicDataSource extends DataSource<Music> {
  void update(Music item);
  List<Music> getAllMusics();
  void removeAllMusics();
}
