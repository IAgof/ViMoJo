package com.videonasocialmedia.vimojo.ftp.presentation.services;

/**
 *
 */
public interface FtpUploaderView {

    void showNotification(boolean foreground);

    void setNotificationProgress(int progress);

    void hideNotification();

    void showErrorMessage(String message);
}
