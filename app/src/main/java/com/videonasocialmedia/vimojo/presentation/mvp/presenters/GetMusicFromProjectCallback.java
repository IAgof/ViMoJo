package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import com.videonasocialmedia.vimojo.model.entities.editor.media.Music;

/**
 * Created by jliarte on 31/05/16.
 */
public interface GetMusicFromProjectCallback {
    void onMusicRetrieved(Music music);
    void noMusicOnProject();
}
