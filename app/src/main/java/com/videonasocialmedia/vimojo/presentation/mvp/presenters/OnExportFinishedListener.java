/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import com.videonasocialmedia.videonamediaframework.model.media.Video;

public interface OnExportFinishedListener {
    void onExportError(String error);
    void onExportSuccess(Video video);
}
