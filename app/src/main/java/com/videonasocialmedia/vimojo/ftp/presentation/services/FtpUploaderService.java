package com.videonasocialmedia.vimojo.ftp.presentation.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.presentation.views.activity.ShareActivity;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class FtpUploaderService extends Service implements FtpUploaderView {

    private FtpPresenter ftpPresenter;
    private NotificationManager notificationManager;
    private Notification.Builder builder;

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();


    public class LocalBinder extends Binder {
        FtpUploaderService getService() {
            return FtpUploaderService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ftpPresenter = new FtpPresenter();
        ftpPresenter.onCreate(this);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Bundle extras = intent.getExtras();
        final String videoPath = extras.getString("VIDEO_FOLDER_PATH", "no_path");
        builder = prepareNotificationBuilder(videoPath);

        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        ftpPresenter.startUpload(videoPath);

        return START_NOT_STICKY;
    }

    private String getVideoTitle(String videoPath) {
        Pattern pattern = Pattern.compile("\\w+(?:\\.\\w+)*$");
        Matcher matcher = pattern.matcher(videoPath);

        String videoTitle = "";
        if (matcher.find()) {
            videoTitle = matcher.group();
        }
        return videoTitle;
    }

    private Notification.Builder prepareNotificationBuilder(String videoPath) {
        Intent intent= new Intent(this, ShareActivity.class);
        intent.putExtra(Constants.VIDEO_TO_SHARE_PATH,videoPath);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)  // the status icon
                .setTicker(getString(R.string.uploading))  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(getString(R.string.uploading))  // the label of the entry
                .setContentText(getVideoTitle(videoPath))  // the contents of the entry
                .setProgress(100, 0, false)
                .setContentIntent(contentIntent);  // The intent to send when the entry is clicked
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Show a notification while this service is running.
     */
    @Override
    public void showNotification(boolean foreground) {
        // Send the notification.
        //notificationManager.notify(R.string.app_name,  builder.build());
        if (foreground)
            startForeground(R.string.app_name, builder.build());
        else {
            stopForeground(true);
            notificationManager.notify(R.string.app_name, builder.build());
        }
    }

    @Override
    public void setNotificationProgress(int progress) {
        if (progress > 100) {
            builder.setContentText(getString(R.string.uploadComplete));
            builder.setContentTitle(getString(R.string.uploadComplete));
            builder.setProgress(0, 0, false);
            showNotification(false);
        } else {
            builder.setProgress(100, progress, false);
            updateNotification();
        }
    }

    private void updateNotification() {
        notificationManager.notify(R.string.app_name, builder.build());
    }

    @Override
    public void hideNotification() {

    }

    @Override
    public void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

