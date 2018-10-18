package com.videonasocialmedia.vimojo.repository.music.datasource.mapper;

import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.vimojo.repository.Mapper;
import com.videonasocialmedia.vimojo.repository.music.datasource.RealmMusic;

/**
 * Created by alvaro on 12/04/17.
 */

public class RealmMusicToMusicMapper implements Mapper<RealmMusic,Music> {
  @Override
  public Music map(RealmMusic realmMusic) {
    Music music = new Music(realmMusic.musicPath, realmMusic.volume, realmMusic.duration);
    music.setUuid(realmMusic.uuid);
    music.setMusicTitle(realmMusic.title);
    music.setMusicAuthor(realmMusic.author);
    music.setIconResourceId(realmMusic.iconResourceId);
    return music;
  }
}
