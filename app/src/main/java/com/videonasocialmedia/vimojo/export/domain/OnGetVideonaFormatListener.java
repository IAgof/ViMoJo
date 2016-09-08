package com.videonasocialmedia.vimojo.export.domain;

import com.videonasocialmedia.transcoder.format.VideonaFormat;

/**
 * Created by alvaro on 5/09/16.
 */
public interface OnGetVideonaFormatListener {
    public void onVideonaFormat(VideonaFormat videonaFormat);
    public void onVideonaErrorFormat();
}
