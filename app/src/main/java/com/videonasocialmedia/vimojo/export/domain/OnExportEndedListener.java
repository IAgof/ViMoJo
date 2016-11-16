package com.videonasocialmedia.vimojo.export.domain;

import com.videonasocialmedia.videonamediaframework.model.media.Video;

/**
 * Created by jca on 27/5/15.
 */
public interface OnExportEndedListener {
    void onExportError(String error);

    void onExportSuccess(Video video);
}
