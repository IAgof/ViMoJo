package com.videonasocialmedia.vimojo.split.domain;

import com.videonasocialmedia.videonamediaframework.model.media.Video;

/**
 * Created by alvaro on 5/09/16.
 */
public interface OnSplitVideoListener {
    void onSuccessSplittingVideo(Video initialVideo, Video endVideo);
    void showErrorSplittingVideo();
}
