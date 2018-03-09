/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.sync;

import android.app.NotificationChannel;
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
  private static final String NOTIFICATION_CHANNEL_ID = "notification_channel_upload_videos";
  private static final String NOTIFICATION_GROUP_ID = "video_uploads";
  private static final int NOTIFICATION_BUNDLE_SUMMARY_ID = 0;
  private static final String LOG_TAG = UploadNotification.class.getSimpleName();
  private final Context context;
  private final int successNotificationId = R.drawable.notification_success_small;
  private final int errorNotificationId = R.drawable.notification_error_small;

  public UploadNotification(Context context) {
    this.context = context;
  }

  private NotificationCompat.Builder getBuilder(NotificationManager notificationManager) {
    NotificationCompat.Builder builder = new NotificationCompat
            .Builder(context, NOTIFICATION_CHANNEL_ID).setGroup(NOTIFICATION_GROUP_ID);
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      String name = "Vimojo";
      int importance = NotificationManager.IMPORTANCE_DEFAULT;
      NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
              name, importance);
      notificationManager.createNotificationChannel(channel);
      return builder.setChannelId(NOTIFICATION_CHANNEL_ID);
    }
    return builder;
  }

  private NotificationManager getNotificationManager() {
    return (NotificationManager) context.
            getSystemService(Context.NOTIFICATION_SERVICE);
  }

  private void showBundleSummary() {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
      Log.d(LOG_TAG, "Showing summary notification");
      NotificationManager notificationManager = getNotificationManager();
      NotificationCompat.Builder notificationBuilder = getBuilder(notificationManager)
              .setGroupSummary(true); // (jliarte): 9/03/18 setGroupSummary just for the parent notification in bundled notifications!
      notificationBuilder.setSmallIcon(R.drawable.notification_uploading_small);
      notificationBuilder.setContentTitle("Platform uploads");
      notificationManager.notify(NOTIFICATION_BUNDLE_SUMMARY_ID, notificationBuilder.build());
    }
  }

  public void startInfiniteProgressNotification(int notificationUploadId, int iconNotificationId,
                                                String uploadingVideo) {
    showBundleSummary();
    Log.d(LOG_TAG, "Starting notification id " + notificationUploadId);
    NotificationManager notificationManager = getNotificationManager();
    NotificationCompat.Builder notificationBuilder = getBuilder(notificationManager);
    notificationBuilder.setSmallIcon(iconNotificationId);
    notificationBuilder.setContentTitle(uploadingVideo);
    notificationBuilder.setProgress(0, 0, true);
    notificationManager.notify(notificationUploadId, notificationBuilder.build());
  }

  public void finishNotification(int notificationUploadId, String result, String videoTitle,
                                 boolean success) {
    Log.d(LOG_TAG, "Finishing notification id " + notificationUploadId);
    NotificationManager notificationManager = getNotificationManager();
    NotificationCompat.Builder notificationBuilder = getBuilder(notificationManager);
    notificationBuilder.setSmallIcon(success ? successNotificationId : errorNotificationId);
    notificationBuilder.setContentTitle(context.getString(R.string.upload_video_completed));
    String message = result + " " + videoTitle;
    notificationBuilder.setContentText(message);
    notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
    notificationBuilder.setProgress(0, 0, false);
    notificationManager.notify(notificationUploadId, notificationBuilder.build());
  }

  public void errorNetworkNotification(int notificationUploadId) {
    NotificationManager notificationManager = getNotificationManager();
    NotificationCompat.Builder notificationBuilder = getBuilder(notificationManager);
    notificationBuilder.setSmallIcon(R.drawable.notification_error_small);
    notificationBuilder.setContentTitle(context.getString(R.string.error_uploading_video));
    notificationBuilder.setContentText(context.getString(R.string.upload_video_network_error));
    notificationBuilder.setStyle(new NotificationCompat.BigTextStyle()
            .bigText(context.getString(R.string.upload_video_network_error)));
    notificationBuilder.setProgress(0, 0, false);
    notificationManager.notify(notificationUploadId, notificationBuilder.build());
  }

  public void cancelNotification(int notificationUploadId) {
    NotificationManager notificationManager = getNotificationManager();
    notificationManager.cancel(notificationUploadId);
  }

  public void errorUnauthorizedUploadingVideos(int notificationUploadId) {
    NotificationManager notificationManager = getNotificationManager();
    NotificationCompat.Builder notificationBuilder = getBuilder(notificationManager);
    notificationBuilder.setSmallIcon(errorNotificationId);
    notificationBuilder.setContentTitle(context.getString(R.string.error_uploading_video));
    notificationBuilder.setContentText(context.getString(R.string.upload_video_unauthorization_upload_error));
    notificationBuilder.setStyle(new NotificationCompat.BigTextStyle()
            .bigText(context.getString(R.string.upload_video_unauthorization_upload_error)));
    notificationBuilder.setProgress(0, 0, false);
    notificationManager.notify(notificationUploadId, notificationBuilder.build());
  }

  public void errorFileNotFound(int notificationUploadId, VideoUpload videoUpload) {
    NotificationManager notificationManager = getNotificationManager();
    NotificationCompat.Builder notificationBuilder = getBuilder(notificationManager);
    notificationBuilder.setSmallIcon(errorNotificationId);
    notificationBuilder.setContentTitle(context.getString(R.string.error_uploading_video));
    notificationBuilder.setContentText(videoUpload.getTitle() + " " +
        context.getString(R.string.upload_video_file_not_found));
    notificationBuilder.setStyle(new NotificationCompat.BigTextStyle()
            .bigText(videoUpload.getTitle() + " "
                    + context.getString(R.string.upload_video_file_not_found)));
    notificationBuilder.setProgress(0, 0, false);
    notificationManager.notify(notificationUploadId, notificationBuilder.build());
  }
}