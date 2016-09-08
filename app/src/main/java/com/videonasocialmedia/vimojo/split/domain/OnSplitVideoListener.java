package com.videonasocialmedia.vimojo.split.domain;

import com.videonasocialmedia.vimojo.export.ExportTempBackgroundService;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;

/**
 * Created by alvaro on 5/09/16.
 */
public interface OnSplitVideoListener {

    public void trimVideo(Video video, int startTimeMs, int finishTimeMs);
}
