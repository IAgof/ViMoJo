package com.videonasocialmedia.vimojo.ftp.presentation.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.videonasocialmedia.vimojo.VideonaApplication;
import com.videonasocialmedia.vimojo.ftp.domain.FtpController;
import com.videonasocialmedia.vimojo.ftp.domain.ProgressListener;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;

/**
 *
 */
public class FtpPresenter implements ProgressListener {

    FtpUploaderView view;
    SharedPreferences sharedPreferences;

    public void onCreate(FtpUploaderView view) {
        this.view = view;


    }

    public void startUpload(String videoPath) {
        //String host = BuildConfig.FTP_HOST;// TODO(javi.cabanas): 29/6/16 fetch host from settings
        //String host= "82.223.76.148
        Context appContext = VideonaApplication.getAppContext();
        sharedPreferences =
                appContext.getSharedPreferences(ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
                        Context.MODE_PRIVATE);

        String host = sharedPreferences.getString(ConfigPreferences.HOST, null);
        String user = sharedPreferences.getString(ConfigPreferences.USERNAMEFTP, null);
        String password = sharedPreferences.getString(ConfigPreferences.PASSWORDFTP, null);
        String editedVideoDestination = sharedPreferences.getString(ConfigPreferences.EDITED_VIDEO_DESTINATION, null);
        if ((host == null) || (user == null) || (password == null)) {
            view.showErrorMessage("FTP connection data not set. Please configure FTP in settings.");
            return;
        }
        FtpController ftpController = new FtpController();
        //"videonaftp", "passv1d30n4"
        ftpController.uploadVideo(host, user, password, videoPath, editedVideoDestination, this);
        view.showNotification(true);
    }

    @Override
    public void onSuccessFinished() {
        view.setNotificationProgress(200);
    }

    @Override
    public void onErrorFinished() {

    }

    @Override
    public void onProgressUpdated(int progress) {
        view.setNotificationProgress(progress);
    }
}
