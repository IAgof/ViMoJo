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

import com.squareup.moshi.Moshi;
import com.squareup.tape2.ObjectQueue;
import com.squareup.tape2.QueueFile;
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

  public UploadToPlatformQueue(Context context) {
    this.context = context;
  }

  private void initQueue(Context context) throws IOException {
    String uploadQUEUE = "QueueUploads";
    File file = new File(context.getFilesDir(), uploadQUEUE);
    QueueFile queueFile = new QueueFile.Builder(file).build();
    Moshi moshi = new Moshi.Builder().build();
    MoshiConverter converter = new MoshiConverter(moshi, VideoUpload.class);
    // A persistent ObjectQueue.
    queue = ObjectQueue.create(queueFile, converter);
  }

  public void addVideoToUpload(VideoUpload videoUpload) throws IOException {
    initQueue(context);
    queue.add(videoUpload);
  }

  public void launchQueueVideoUploads() throws IOException {
    initQueue(context);
    Iterator<VideoUpload> iterator = queue.iterator();
    Log.d(LOG_TAG, "queue size " + queue.size());
    while (iterator.hasNext()) {
      VideoUpload element = iterator.next();
      Log.d(LOG_TAG, "numTries " + element.getNumTries());
      Log.d(LOG_TAG, "process " + element.getMediaPath());
      Video video = process(element);
      if (video != null) {
        Log.d(LOG_TAG, "video " + video.toString());
        Log.d(LOG_TAG, "remove " + queue.size());
        sendSimpleNotification(R.drawable.notification_success_small,
            context.getString(R.string.upload_video_completed));
        iterator.remove();
      } else {
        element.incrementNumTries();
        Log.d(LOG_TAG, "incrementNumTries " + element.getNumTries());
        if (element.getNumTries() > VideoUpload.MAX_NUM_TRIES_UPLOAD) {
          iterator.remove();
          sendSimpleNotification(R.drawable.notification_error_small,
              context.getString(R.string.upload_video_error));
        }
      }
    }
  }

  private void sendSimpleNotification(int iconNotificationId, String result) {
    NotificationCompat.Builder mBuilder =
        (NotificationCompat.Builder) new NotificationCompat.Builder(context)
            .setSmallIcon(iconNotificationId)
            .setContentTitle(context.getString(R.string.upload_to_server))
            .setContentText(result);

    NotificationManager notificationManager =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    notificationManager.notify(NOTIFICATION_UPLOAD_COMPLETE_ID, mBuilder.build());
  }

  private Video process(VideoUpload videoUpload) {
    try {
      VideoApiClient videoApiClient = new VideoApiClient(context);
      return videoApiClient.uploadVideo(videoUpload);
    } catch (VimojoApiException e) {
      e.printStackTrace();
    }
    return null;
  }
}
