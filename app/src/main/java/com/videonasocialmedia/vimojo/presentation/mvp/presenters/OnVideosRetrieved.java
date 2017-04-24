package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import com.videonasocialmedia.videonamediaframework.model.media.Video;

import java.util.List;

/**
 * Created by jca on 18/5/15.
 */
public interface OnVideosRetrieved {

    void onVideosRetrieved(List<Video> videoList);

    void onNoVideosRetrieved();
}
