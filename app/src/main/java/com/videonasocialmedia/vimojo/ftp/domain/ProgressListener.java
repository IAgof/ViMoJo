package com.videonasocialmedia.vimojo.ftp.domain;

/**
 *
 */
public interface ProgressListener {
    void onSuccessFinished();
    void onErrorFinished();
    void onProgressUpdated(int progress);
}
