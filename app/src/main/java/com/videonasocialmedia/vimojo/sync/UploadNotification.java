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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alvaro on 15/2/18.
 *
 * Class to manage uploads notification
 * Create notification by id
 * Start, update, finish, cancel notification
 *
 */

public class UploadNotification {
  public static final int NOTIFICATION_UPLOAD_ID = 001;
  public static final String NOTIFICATION_CHANNEL_ID = "notification_channel_upload_videos";
  private final Context context;
  private NotificationCompat.Builder notificationBuilder;
  private NotificationManager notificationManager;
  private List<String> videoResults;
  private boolean isNotificationShowed = false;
  private int successNotificationId = R.drawable.notification_success_small;
  private int errorNotificationId = R.drawable.notification_error_small;
  private boolean errorUploadingVideo = false;
  private boolean errorNetworkNotification = false;

  public UploadNotification(Context context) {
    this.context = context;
    videoResults = new ArrayList<>();
  }

  public void startInfiniteProgressNotification(int iconNotificationId, String uploadingVideo) {
    NotificationCompat.InboxStyle inboxStyle =
        new NotificationCompat.InboxStyle();
    inboxStyle.addLine(uploadingVideo + " 1/1");
    notificationBuilder =  new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(iconNotificationId)
            .setContentTitle(uploadingVideo)
            .setStyle(inboxStyle);
    notificationBuilder.setProgress(0, 0, true);
    notificationManager =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(NOTIFICATION_UPLOAD_ID, notificationBuilder.build());
    isNotificationShowed = true;
    errorNetworkNotification = false;
  }

  public void updateNotificationVideoAdded(String message, int sizeQueue) {
    new Thread(() -> {
      String text = message + " 1/" + sizeQueue;
        NotificationCompat.InboxStyle inboxStyle =
            new NotificationCompat.InboxStyle();
        inboxStyle.setSummaryText(text);
        for (String line: videoResults) {
          inboxStyle.addLine(line);
        }
        notificationBuilder.setStyle(inboxStyle);
        notificationManager.notify(NOTIFICATION_UPLOAD_ID, notificationBuilder.build());
      }).start();
  }

  public void appendResultNotification(String message, int sizeQueue, String result,
                                       String title, boolean success) {
    new Thread(() -> {
      if (!success) {
        errorUploadingVideo = true;
      }
      videoResults.add(result + " " + title);
      String text = message + " 1/" + sizeQueue;
      NotificationCompat.InboxStyle inboxStyle =
          new NotificationCompat.InboxStyle();
      inboxStyle.setSummaryText(text);
      for (String line: videoResults) {
        inboxStyle.addLine(line);
      }
      notificationBuilder.setStyle(inboxStyle);
      notificationManager.notify(NOTIFICATION_UPLOAD_ID, notificationBuilder.build());
    }).start();
  }

  public void finishNotification(boolean success) {
    new Thread(() -> {
      if (!success) {
        errorUploadingVideo = true;
      }
      notificationBuilder.setSmallIcon(errorUploadingVideo ? errorNotificationId : successNotificationId);
      String title1 = context.getString(R.string.upload_video_completed) + ":";
      NotificationCompat.InboxStyle inboxStyle =
          new NotificationCompat.InboxStyle();
      for (String line: videoResults) {
        inboxStyle.addLine(line);
      }
      String summaryText = String.valueOf(videoResults.size());
      if (videoResults.size() > 1) {
        summaryText = summaryText + " "
            + context.getString(R.string.upload_video_finish_plural);
      } else {
        summaryText = summaryText + " "
            + context.getString(R.string.upload_video_finish_singular);
      }
      inboxStyle.setSummaryText(summaryText);
      notificationBuilder.setContentTitle(title1);
      notificationBuilder.setStyle(inboxStyle);
      notificationBuilder.setProgress(0, 0, false);
      notificationManager.notify(NOTIFICATION_UPLOAD_ID, notificationBuilder.build());
      isNotificationShowed = false;
    }).start();
  }

  public boolean isNotificationShowed() {
    return isNotificationShowed;
  }

  public void errorNetworkNotification() {
    new Thread(() -> {
            notificationBuilder.setSmallIcon(errorNotificationId);
            String title = context.getString(R.string.upload_video_network_error);
            NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
            for(String line: videoResults) {
              inboxStyle.addLine(line);
            }
            String summaryText = String.valueOf(videoResults.size());
            if(videoResults.size() > 1) {
              summaryText = summaryText + " "
                  + context.getString(R.string.upload_video_finish_plural);
            } else {
              summaryText = summaryText + " "
                  + context.getString(R.string.upload_video_finish_singular);
            }
            inboxStyle.setSummaryText(summaryText);
            notificationBuilder.setContentTitle(title);
            notificationBuilder.setStyle(inboxStyle);
            notificationBuilder.setProgress(0, 0, false);
            notificationManager.notify(NOTIFICATION_UPLOAD_ID, notificationBuilder.build());
            errorNetworkNotification = true;
    }).start();
  }

  public void cancelNotification() {
    notificationManager.cancel(NOTIFICATION_UPLOAD_ID);
    isNotificationShowed = false;
  }

  public void errorUnauthorizationUploadingVideos() {
    notificationBuilder.setSmallIcon(errorNotificationId);
    String title = context.getString(R.string.upload_video_unauthorization_upload_error);
    notificationBuilder.setContentTitle(title);
    notificationBuilder.setProgress(0, 0, false);
    notificationManager.notify(NOTIFICATION_UPLOAD_ID, notificationBuilder.build());
  }

  public boolean isShowedErrorNetworkNotification() {
    return errorNetworkNotification;
  }

  public void errorFileNotFound(VideoUpload videoUpload) {
    NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
    style.setSummaryText(context.getString(R.string.error_uploading_video));
//    String title = context.getString(R.string.upload_video_file_not_found);
    style.addLine("Video file for " + videoUpload.getTitle() + " no longer exists!");
    NotificationCompat.Builder errorNotificationBuilder = new NotificationCompat.Builder(
            context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(context.getString(R.string.error_uploading_video))
            .setStyle(style).setSmallIcon(errorNotificationId);
    notificationManager.notify(NOTIFICATION_UPLOAD_ID, errorNotificationBuilder.build());
  }
}