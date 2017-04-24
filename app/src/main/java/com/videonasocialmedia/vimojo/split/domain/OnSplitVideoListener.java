package com.videonasocialmedia.vimojo.split.domain;

import com.videonasocialmedia.videonamediaframework.model.media.Video;

/**
 * Created by alvaro on 5/09/16.
 */
public interface OnSplitVideoListener {

    public void trimVideo(Video video, int startTimeMs, int finishTimeMs);
}
