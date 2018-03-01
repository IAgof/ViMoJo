/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.sync;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.sync.model.VideoUpload;

/**
 * Created by alvaro on 15/2/18.
 *
 * Class to manage uploads notification
 * Create notification by id
 * Start, update, finish, cancel notification
 *
 */

public class UploadNotification {
  public static final String NOTIFICATION_CHANNEL_ID = "notification_channel_upload_videos";
  private final Context context;
  private NotificationCompat.Builder notificationBuilder;
  private NotificationManager notificationManager;
  private int successNotificationId = R.drawable.notification_success_small;
  private int errorNotificationId = R.drawable.notification_error_small;

  public UploadNotification(Context context) {
    this.context = context;
  }

  public void startInfiniteProgressNotification(int notificationUploadId, int iconNotificationId,
                                                String uploadingVideo) {
    notificationBuilder =  new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(iconNotificationId)
            .setContentTitle(uploadingVideo);
    notificationBuilder.setProgress(0, 0, true);
    notificationManager =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(notificationUploadId, notificationBuilder.build());
  }

  public void finishNotification(int notificationUploadId, String result, String videoTitle,
                                 boolean success) {
    notificationBuilder.setSmallIcon(success ? successNotificationId : errorNotificationId);
    notificationBuilder.setContentTitle(context.getString(R.string.upload_video_completed));
    String message = result + " " + videoTitle;
    notificationBuilder.setContentText(message);
    notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
    notificationBuilder.setProgress(0, 0, false);
    notificationManager.notify(notificationUploadId, notificationBuilder.build());
  }

  public void errorNetworkNotification(int notificationUploadId) {
    notificationBuilder.setSmallIcon(errorNotificationId);
    String title = context.getString(R.string.error_uploading_video);
    String message = context.getString(R.string.upload_video_network_error);
    notificationBuilder.setContentTitle(title);
    notificationBuilder.setContentText(message);
    notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
    notificationBuilder.setProgress(0, 0, false);
    notificationManager.notify(notificationUploadId, notificationBuilder.build());
  }

  public void cancelNotification(int notificationUploadId) {
    notificationManager.cancel(notificationUploadId);
  }

  public void errorUnauthorizationUploadingVideos(int notificationUploadId) {
    notificationBuilder.setSmallIcon(errorNotificationId);
    String title = context.getString(R.string.error_uploading_video);
    String message = context.getString(R.string.upload_video_unauthorization_upload_error);
    notificationBuilder.setContentTitle(title);
    notificationBuilder.setContentText(message);
    notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
    notificationBuilder.setProgress(0, 0, false);
    notificationManager.notify(notificationUploadId, notificationBuilder.build());
  }

  public void errorFileNotFound(int notificationUploadId, VideoUpload videoUpload) {
    notificationBuilder.setSmallIcon(errorNotificationId);
    String title = context.getString(R.string.error_uploading_video);
    String message = videoUpload.getTitle() + " " +
        context.getString(R.string.upload_video_file_not_found);
    notificationBuilder.setContentTitle(title);
    notificationBuilder.setContentText(message);
    notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
    notificationBuilder.setProgress(0, 0, false);
    notificationManager.notify(notificationUploadId, notificationBuilder.build());
  }
}