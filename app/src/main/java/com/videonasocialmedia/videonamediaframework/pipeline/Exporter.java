package com.videonasocialmedia.videonamediaframework.pipeline;

import com.videonasocialmedia.videonamediaframework.model.media.Video;

/**
 * Created by jca on 27/5/15.
 */
public interface Exporter {
    void export();

    /**
     * Created by jca on 27/5/15.
     */
    interface OnExportEndedListener {
        void onExportError(String error);

        void onExportSuccess(Video video);
    }
}
