/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.sync;

/**
 * Created by alvaro on 6/2/18.
 */

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.squareup.moshi.Moshi;
import com.squareup.tape2.ObjectQueue;
import com.squareup.tape2.QueueFile;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.vimojoapiclient.VideoApiClient;
import com.videonasocialmedia.vimojo.vimojoapiclient.VimojoApiException;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.Video;
import com.videonasocialmedia.vimojo.sync.model.VideoUpload;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import static com.videonasocialmedia.vimojo.share.presentation.views.activity.ShareActivity.NOTIFICATION_UPLOAD_COMPLETE_ID;

/**
 * Class to unify video uploads to platform.
 * Create/init ObjectQueue, add objects and launchQueue.
 * FIFO, atomic ObjectQueue.
 */
public class UploadToPlatformQueue {
  private final String LOG_TAG = UploadToPlatformQueue.class.getCanonicalName();
  private ObjectQueue<VideoUpload> queue;
  private final Context context;
  private NotificationCompat.Builder notificationBuilder;
  private NotificationManager notificationManager;
  private MoshiConverter converter;

  public UploadToPlatformQueue(Context context) {
    this.context = context;
  }

  private void initQueue(Context context) {
    String uploadQUEUE = "QueueUploads_" + BuildConfig.FLAVOR;
    File file = new File(context.getFilesDir(), uploadQUEUE);
    QueueFile queueFile = null;
    try {
      queueFile = new QueueFile.Builder(file).build();
    } catch (IOException ioException) {
      ioException.printStackTrace();
      Log.d(LOG_TAG, ioException.getMessage());
      Crashlytics.log("Error launching queue video to upload");
      Crashlytics.logException(ioException);
    }
    Moshi moshi = new Moshi.Builder().build();
    converter = new MoshiConverter(moshi, VideoUpload.class);
    // A persistent ObjectQueue.
    queue = ObjectQueue.create(queueFile, converter);
  }

  public void addVideoToUpload(VideoUpload videoUpload) throws IOException {
    initQueue(context);
    queue.add(videoUpload);
    if(queue.size() == 1) {
      launchQueueVideoUploads();
    }
  }

  public void launchQueueVideoUploads() {
    initQueue(context);
    Iterator<VideoUpload> iterator = queue.iterator();
    Log.d(LOG_TAG, "queue size " + queue.size());
    int idIndex = 1;
    int queueSize = queue.size();
    if(queue.size() > 0) {
      sendInfiniteProgressNotification(R.drawable.notification_uploading_small,
          context.getString(R.string.uploading_video), idIndex, queueSize);
    }
    while (iterator.hasNext()) {
      VideoUpload element = iterator.next();
      Log.d(LOG_TAG, "numTries " + element.getNumTries());
      Log.d(LOG_TAG, "process " + element.getMediaPath());
      Video video = process(element);
      if (video != null) {
        Log.d(LOG_TAG, "video " + video.toString());
        Log.d(LOG_TAG, "remove " + queue.size());
        updateNotification(R.drawable.notification_success_small, element.getTitle(), idIndex,
            context.getString(R.string.upload_video_completed));
        idIndex++;
        iterator.remove();
      } else {
        element.incrementNumTries();
        Log.d(LOG_TAG, "incrementNumTries " + element.getNumTries());
        if (element.getNumTries() > VideoUpload.MAX_NUM_TRIES_UPLOAD) {
          updateNotification(R.drawable.notification_error_small, element.getTitle(), idIndex,
              context.getString(R.string.upload_video_error));
          idIndex++;
          iterator.remove();
        }
      }
    }
  }

  private void sendInfiniteProgressNotification(int iconNotificationId, String uploadingVideo,
                                                int idOrder, int sizeQueue) {

    String text = uploadingVideo + " " + idOrder + "/" + sizeQueue;
    notificationBuilder =
        (NotificationCompat.Builder) new NotificationCompat.Builder(context)
            .setSmallIcon(iconNotificationId)
            .setContentTitle(context.getString(R.string.upload_to_server))
            .setContentText(text);

    // Sets an activity indicator for an operation of indeterminate length
    notificationBuilder.setProgress(0, 0, true);

    notificationManager =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    notificationManager.notify(NOTIFICATION_UPLOAD_COMPLETE_ID, notificationBuilder.build());
  }

  private void updateNotification(int iconNotificationId, String title, int idOrder, String result) {

    // Start a lengthy operation in a background thread
    new Thread(
        new Runnable() {
          @Override
          public void run() {
            if(idOrder > queue.size()) {
              String text = result + " " + idOrder + "/" + queue.size();
              notificationBuilder.setContentText(text);
            } else {
              notificationBuilder.setSmallIcon(iconNotificationId);
              // When the loop is finished, updates the notification
              String text = result + " " + title;
              notificationBuilder.setContentText(text);
              // Removes the progress bar
              notificationBuilder.setProgress(0, 0, false);
            }
            notificationManager.notify(NOTIFICATION_UPLOAD_COMPLETE_ID, notificationBuilder.build());
          }
        }
      // Starts the thread by calling the run() method in its Runnable
    ).start();
  }

  private Video process(VideoUpload videoUpload) {
    try {
      VideoApiClient videoApiClient = new VideoApiClient();
      return videoApiClient.uploadVideo(videoUpload);
    } catch (VimojoApiException e) {
      e.printStackTrace();
    }
    return null;
  }

}
