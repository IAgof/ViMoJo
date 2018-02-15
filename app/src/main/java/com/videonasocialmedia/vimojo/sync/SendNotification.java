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

import java.util.ArrayList;
import java.util.List;

import static com.videonasocialmedia.vimojo.share.presentation.views.activity.ShareActivity.NOTIFICATION_UPLOAD_COMPLETE_ID;

/**
 * Created by alvaro on 15/2/18.
 */

public class SendNotification {

  private final Context context;
  private NotificationCompat.Builder notificationBuilder;
  private NotificationManager notificationManager;
  private List<String> videoResults;
  private boolean isNotificationShowed = false;
  String NOTIFICATION_CHANNEL_ID = "notification_channel_upload_videos";

  public SendNotification(Context context) {
    this.context = context;
    videoResults = new ArrayList<>();
  }

  public void sendInfiniteProgressNotification(int iconNotificationId, String uploadingVideo) {
    String text = uploadingVideo + " 1/1";
    NotificationCompat.InboxStyle inboxStyle =
        new NotificationCompat.InboxStyle();
    inboxStyle.setBigContentTitle(text);
    notificationBuilder =  new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(iconNotificationId)
            .setContentTitle(context.getString(R.string.upload_to_server))
            .setContentText(text)
            .setStyle(inboxStyle);
            //.setStyle(new NotificationCompat.BigTextStyle().bigText(text));

    // Sets an activity indicator for an operation of indeterminate length
    notificationBuilder.setProgress(0, 0, true);

    notificationManager =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    notificationManager.notify(NOTIFICATION_UPLOAD_COMPLETE_ID, notificationBuilder.build());

    isNotificationShowed = true;
  }

  public void updateNotificationVideoAdded(String message, int sizeQueue) {
    new Thread(
        new Runnable() {
          @Override
          public void run() {
            String text = message + " 1/" + sizeQueue;
            notificationBuilder.setContentText(text);
            //notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(text));
            notificationManager.notify(NOTIFICATION_UPLOAD_COMPLETE_ID, notificationBuilder.build());
          }
        }
    ).start();
  }

  public void appendResultNotification(String message, int sizeQueue, String result,
                                       String title) {
    new Thread(
        new Runnable() {
          @Override
          public void run() {
            videoResults.add(title + " " + result);
            String text = message + " 1/" + sizeQueue;
            notificationBuilder.setContentText(text);
            notificationManager.notify(NOTIFICATION_UPLOAD_COMPLETE_ID, notificationBuilder.build());
          }
        }
    ).start();
  }

  public void finishNotification(int iconNotificationId, String result) {
    new Thread(
        new Runnable() {
          @Override
          public void run() {
            notificationBuilder.setSmallIcon(iconNotificationId);
            // When the loop is finished, updates the notification
            NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
            inboxStyle.setBigContentTitle(result + ":");
            for(String line: videoResults) {
              inboxStyle.addLine(line);
            }
            // Removes the progress bar
            notificationBuilder.setProgress(0, 0, false);
            notificationBuilder.setStyle(inboxStyle);
            notificationManager.notify(NOTIFICATION_UPLOAD_COMPLETE_ID, notificationBuilder.build());

            isNotificationShowed = false;
            videoResults.clear();
          }
        }
    ).start();
  }

  public boolean isNotificationShowed() {
    return isNotificationShowed;
  }
}


