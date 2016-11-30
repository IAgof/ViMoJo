package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import com.videonasocialmedia.videonamediaframework.model.media.Music;

/**
 * Created by jliarte on 31/05/16.
 */
public interface GetMusicFromProjectCallback {
    void onMusicRetrieved(Music music);
}
