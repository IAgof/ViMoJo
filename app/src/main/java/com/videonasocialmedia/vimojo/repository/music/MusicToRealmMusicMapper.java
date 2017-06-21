package com.videonasocialmedia.vimojo.repository.music;

import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.vimojo.repository.Mapper;

/**
 * Created by alvaro on 12/04/17.
 */

public class MusicToRealmMusicMapper implements Mapper<Music, RealmMusic> {
  @Override
  public RealmMusic map(Music music) {
    RealmMusic realmMusic = new RealmMusic(music.getUuid(), music.getMediaPath(),
        music.getMusicTitle(), music.getAuthor(), music.getIconResourceId(), music.getDuration(),
        music.getVolume());
    return realmMusic;
  }
}
