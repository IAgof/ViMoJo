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
import android.util.Log;

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
  public static final String NOTIFICATION_GROUP_ID = "video_uploads";
  public static final int NOTIFICATION_BUNDLE_SUMMARY_ID = 0;
  private static final String LOG_TAG = UploadNotification.class.getSimpleName();
  private final Context context;
  private NotificationManager notificationManager;
  private int successNotificationId = R.drawable.notification_success_small;
  private int errorNotificationId = R.drawable.notification_error_small;

  public UploadNotification(Context context) {
    this.context = context;
  }

  private NotificationCompat.Builder getBuilder() {
    return new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setGroup(NOTIFICATION_GROUP_ID);
  }

  public void showBundleSummary() {
    Log.d(LOG_TAG, "Showing summary notification");
    NotificationCompat.Builder notificationBuilder = getBuilder().setGroupSummary(true);
    notificationBuilder.setSmallIcon(R.drawable.notification_uploading_small);
    notificationBuilder.setContentTitle("Platform uploads");
    notificationManager =
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(NOTIFICATION_BUNDLE_SUMMARY_ID, notificationBuilder.build());
  }

  public void startInfiniteProgressNotification(int notificationUploadId, int iconNotificationId,
                                                String uploadingVideo) {
    showBundleSummary();
    Log.d(LOG_TAG, "Starting notification id " + notificationUploadId);
    NotificationCompat.Builder notificationBuilder = getBuilder();
    notificationBuilder.setSmallIcon(iconNotificationId);
    notificationBuilder.setContentTitle(uploadingVideo);
    notificationBuilder.setProgress(0, 0, true);
    notificationManager =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(notificationUploadId, notificationBuilder.build());
  }

  public void finishNotification(int notificationUploadId, String result, String videoTitle,
                                 boolean success) {
    Log.d(LOG_TAG, "Finishing notification id " + notificationUploadId);
    NotificationCompat.Builder notificationBuilder = getBuilder();
    notificationBuilder.setSmallIcon(success ? successNotificationId : errorNotificationId);
    notificationBuilder.setContentTitle(context.getString(R.string.upload_video_completed));
    String message = result + " " + videoTitle;
    notificationBuilder.setContentText(message);
    notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
    notificationBuilder.setProgress(0, 0, false);
    notificationManager.notify(notificationUploadId, notificationBuilder.build());
  }

  public void errorNetworkNotification(int notificationUploadId) {
    NotificationCompat.Builder notificationBuilder = getBuilder();
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
    NotificationCompat.Builder notificationBuilder = getBuilder();
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
    NotificationCompat.Builder notificationBuilder = getBuilder();
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