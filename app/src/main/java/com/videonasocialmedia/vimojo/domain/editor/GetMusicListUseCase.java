package com.videonasocialmedia.vimojo.domain.editor;

import com.videonasocialmedia.vimojo.model.entities.editor.media.Music;
import com.videonasocialmedia.vimojo.sources.MusicSource;

import java.util.List;

/**
 *
 */
public class GetMusicListUseCase {

    public List<Music> getAppMusic() {
        return new MusicSource().retrieveLocalMusic();
    }
}
