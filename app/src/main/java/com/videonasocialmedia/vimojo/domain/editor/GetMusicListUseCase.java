package com.videonasocialmedia.vimojo.domain.editor;

import android.content.Context;

import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.vimojo.sources.MusicSource;

import java.util.List;

import javax.inject.Inject;

/**
 *
 */
public class GetMusicListUseCase {

    private Context context;

    @Inject
    public GetMusicListUseCase(Context context) {
        this.context = context;
    }

    public List<Music> getAppMusic() {
        return new MusicSource(context).retrieveLocalMusic();
    }
}
