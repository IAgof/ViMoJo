package com.videonasocialmedia.vimojo.ftp.presentation.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.ftp.FtpClient;
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

    public void startUpload(String videoPath, String ftpSelected) {
        //String host= "82.223.76.148

        String host=null;
        String user=null;
        String password=null;
        String editedVideoDestination= null;

        Context appContext = VimojoApplication.getAppContext();
        sharedPreferences =
                appContext.getSharedPreferences(ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
                        Context.MODE_PRIVATE);
        switch (ftpSelected){
            case ConfigPreferences.FTP1:
                host = sharedPreferences.getString(ConfigPreferences.HOST, null);
                user = sharedPreferences.getString(ConfigPreferences.USERNAME_FTP, null);
                password = sharedPreferences.getString(ConfigPreferences.PASSWORD_FTP, null);
                editedVideoDestination = sharedPreferences.getString(ConfigPreferences.EDITED_VIDEO_DESTINATION, null);
                break;
            case ConfigPreferences.FTP2:
                host = sharedPreferences.getString(ConfigPreferences.HOST_FTP2, null);
                user = sharedPreferences.getString(ConfigPreferences.USERNAME_FTP2, null);
                password = sharedPreferences.getString(ConfigPreferences.PASSWORD_FTP2, null);
                editedVideoDestination = sharedPreferences.getString(ConfigPreferences.EDITED_VIDEO_DESTINATION_FTP2, null);
                break;
        }

        if ((host == null) || (user == null) || (password == null)) {
            view.showErrorMessage(R.string.credentialFTPNull);
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
    public void onErrorFinished(int errorCode) {

        switch (errorCode) {
            case FtpClient.FTPClientException.FTP_ERROR_FILE_NOT_FOUND:
                view.showErrorMessage(R.string.fileError);
                break;
            case FtpClient.FTPClientException.FTP_ERROR_HOST_UNREACHABLE:
                view.showErrorMessage(R.string.connectionFTPServerError);
                break;
            case FtpClient.FTPClientException.FTP_ERROR_UNAUTHORIZED:
                view.showErrorMessage(R.string.loginError);
                break;
            case FtpClient.FTPClientException.FTP_ERROR_IO:
                view.showErrorMessage(R.string.shareError);


        }

    }

    @Override
    public void onProgressUpdated(int progress) {
        view.setNotificationProgress(progress);
    }
}
