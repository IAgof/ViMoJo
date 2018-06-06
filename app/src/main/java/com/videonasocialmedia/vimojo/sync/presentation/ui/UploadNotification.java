/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.sync.presentation.ui;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.sync.model.VideoUpload;

/**
 * Created by alvaro on 15/2/18.
 *
 * Class to manage uploads notification
 * Create notification by id
 * Start, update, finish, pause, cancel notification
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
      String name = BuildConfig.FLAVOR;
      int importance = NotificationManager.IMPORTANCE_DEFAULT;
      NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
              name, importance);
      notificationManager.createNotificationChannel(channel);
      return builder.setChannelId(NOTIFICATION_CHANNEL_ID);
    }
    return builder;
  }

  private NotificationManager getNotificationManager() {
    return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
  }

  private void showBundleSummary(int smallIconNotificationId) {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
      Log.d(LOG_TAG, "Showing summary notification");
      NotificationManager notificationManager = getNotificationManager();
      NotificationCompat.Builder notificationBuilder = getBuilder(notificationManager)
              .setGroupSummary(true); // (jliarte): 9/03/18 setGroupSummary just for the parent notification in bundled notifications!
      notificationBuilder.setSmallIcon(smallIconNotificationId);
      notificationBuilder.setContentTitle(context.getString(R.string.upload_bundle_summary_title));
      notificationManager.notify(NOTIFICATION_BUNDLE_SUMMARY_ID, notificationBuilder.build());
    }
  }

  public void startInfiniteProgressNotification(int notificationUploadId, int iconNotificationId,
                                                String uploadingVideo,
                                                PendingIntent cancelUploadPendingIntent,
                                                PendingIntent pauseUploadPendingIntent) {
    showBundleSummary(R.drawable.notification_uploading_small);
    Log.d(LOG_TAG, "Starting notification id " + notificationUploadId);
    NotificationManager notificationManager = getNotificationManager();
    NotificationCompat.Builder notificationBuilder = getBuilder(notificationManager);
    notificationBuilder.setSmallIcon(iconNotificationId);
    notificationBuilder.setContentTitle(uploadingVideo);
    notificationBuilder.setProgress(0, 0, true);
    notificationBuilder.addAction(R.drawable.activity_edit_common_icon_cancel, "CANCEL",
        cancelUploadPendingIntent);
    notificationBuilder.addAction(R.drawable.activity_edit_common_icon_cancel, "PAUSE",
        pauseUploadPendingIntent);
    notificationManager.notify(notificationUploadId, notificationBuilder.build());
  }

  public void finishNotification(int notificationUploadId, String result, String videoTitle,
                                 boolean success, String userId) {
    Log.d(LOG_TAG, "Finishing notification id " + notificationUploadId);

    Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
    // TODO: 5/6/18 Create BuildConfig URL_PLATFORM_BASE
    String URL_PLATFORM_BASE = "http://www.vimojo.co";
    String URL_PLATFORM_BASE_FILTER = URL_PLATFORM_BASE.substring(0,
        URL_PLATFORM_BASE.lastIndexOf("/") + 1) ;
    String URL_USER_PLATFORM = URL_PLATFORM_BASE_FILTER + "/user/" + userId + "/videos";
    notificationIntent.setData(Uri.parse(URL_USER_PLATFORM));
    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
        notificationIntent, 0);

    int smallIconNotificationId = success ? successNotificationId : errorNotificationId;
    showBundleSummary(smallIconNotificationId);
    NotificationManager notificationManager = getNotificationManager();
    NotificationCompat.Builder notificationBuilder = getBuilder(notificationManager);
    notificationBuilder.setSmallIcon(smallIconNotificationId);
    notificationBuilder.setContentTitle(context.getString(R.string.upload_video_completed));
    String message = result + " " + videoTitle;
    notificationBuilder.setContentText(message);
    notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
    notificationBuilder.setProgress(0, 0, false);
    notificationBuilder.setContentIntent(pendingIntent);
    notificationManager.notify(notificationUploadId, notificationBuilder.build());
  }

  public void errorNetworkNotification(int notificationUploadId) {
    showBundleSummary(R.drawable.notification_error_small);
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

  public void cancelNotification(int notificationUploadId, String title) {
    showBundleSummary(R.drawable.notification_error_small);
    NotificationManager notificationManager = getNotificationManager();
    NotificationCompat.Builder notificationBuilder = getBuilder(notificationManager);
    notificationBuilder.setSmallIcon(R.drawable.notification_error_small);
    notificationBuilder.setContentTitle(context.getString(R.string.cancel_uploading_video));
    notificationBuilder.setContentText(title);
    notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(title));
    notificationBuilder.setProgress(0, 0, false);
    notificationManager.cancel(notificationUploadId);
    notificationManager.notify(notificationUploadId, notificationBuilder.build());
  }

  public void pauseNotification(int notificationUploadId, String title) {
    // TODO: 5/6/18 Add pause upload icon
    showBundleSummary(R.drawable.notification_error_small);
    NotificationManager notificationManager = getNotificationManager();
    NotificationCompat.Builder notificationBuilder = getBuilder(notificationManager);
    notificationBuilder.setSmallIcon(R.drawable.notification_error_small);
    notificationBuilder.setContentTitle(context.getString(R.string.cancel_uploading_video));
    notificationBuilder.setContentText(title);
    notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(title));
    notificationBuilder.setProgress(0, 0, false);
    notificationManager.cancel(notificationUploadId);
    notificationManager.notify(notificationUploadId, notificationBuilder.build());
  }

  public void errorUnauthorizedUploadingVideos(int notificationUploadId) {
    showBundleSummary(R.drawable.notification_error_small);
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
    showBundleSummary(R.drawable.notification_error_small);
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

  public void setProgress(int percentage) {
    NotificationManager notificationManager = getNotificationManager();
    NotificationCompat.Builder notificationBuilder = getBuilder(notificationManager);
    notificationBuilder.setProgress(100, percentage, false);
  }
}
