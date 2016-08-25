package com.videonasocialmedia.vimojo.ftp.domain;

/**
 *
 */
public interface ProgressListener {
    void onSuccessFinished();
    void onErrorFinished(int errorCode);
    void onProgressUpdated(int progress);
}
