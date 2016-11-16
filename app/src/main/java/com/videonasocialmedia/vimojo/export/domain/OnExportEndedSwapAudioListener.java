package com.videonasocialmedia.vimojo.export.domain;

/**
 * Created by jca on 27/5/15.
 */
public interface OnExportEndedSwapAudioListener {
    void onExportError(String error);
    void onExportSuccess();
}
