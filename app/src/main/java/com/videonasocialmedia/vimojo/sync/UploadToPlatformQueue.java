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

import android.content.Context;
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

/**
 * Class to unify video uploads to platform.
 * Create/init ObjectQueue, add objects and launchQueue.
 * FIFO, atomic ObjectQueue.
 */
public class UploadToPlatformQueue {
  private final String LOG_TAG = UploadToPlatformQueue.class.getCanonicalName();
  private final Context context;
  private final VideoApiClient videoApiClient;
  private MoshiConverter converter;
  private SendNotification sendNotification;

  public UploadToPlatformQueue(Context context) {
    this.context = context;
    sendNotification = new SendNotification(context);
    videoApiClient = new VideoApiClient();
  }

  protected ObjectQueue<VideoUpload> getQueue() {
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
    ObjectQueue<VideoUpload> queue = ObjectQueue.create(queueFile, converter);

    return queue;
  }

  public void addVideoToUpload(VideoUpload videoUpload) throws IOException {
    ObjectQueue<VideoUpload> queue = getQueue();
    queue.add(videoUpload);
    if(isNotificationShowed(queue)) {
      Log.d(LOG_TAG, "updateNotification");
      sendNotification.updateNotificationVideoAdded(context.getString(R.string.uploading_video),
          queue.size());
    }
  }

  protected boolean isNotificationShowed(ObjectQueue<VideoUpload> queue) {
    return queue.size() > 0 && sendNotification.isNotificationShowed();
  }

  public void launchQueueVideoUploads() {
    Log.d(LOG_TAG, "launchNotification");
    sendNotification.sendInfiniteProgressNotification(R.drawable.notification_uploading_small,
        context.getString(R.string.uploading_video));
    ObjectQueue<VideoUpload> queue = getQueue();
    Iterator<VideoUpload> iterator = queue.iterator();
    VideoUpload element = iterator.next();
    String title = element.getTitle();
    Video video = process(element);
    if (video != null) {
      removeHeadElement(getQueue());
      Log.d(LOG_TAG, "appendSuccessNotification");
      sendNotification.appendResultNotification(context.getString(R.string.uploading_video),
          queue.size(), context.getString(R.string.upload_video_completed), title);
      if (getQueue().isEmpty()) {
        Log.d(LOG_TAG, "finishNotification");
        sendNotification.finishNotification(R.drawable.notification_success_small,
            context.getString(R.string.upload_video_completed));
      }
    } else {
      element.incrementNumTries();
      if (element.getNumTries() > VideoUpload.MAX_NUM_TRIES_UPLOAD) {
        removeHeadElement(getQueue());
        Log.d(LOG_TAG, "appendErrorNotification");
        sendNotification.appendResultNotification(context.getString(R.string.uploading_video),
            queue.size(), context.getString(R.string.upload_video_error), title);
        if (getQueue().isEmpty())
          Log.d(LOG_TAG, "finishNotification");
        sendNotification.finishNotification(R.drawable.notification_error_small,
            context.getString(R.string.upload_video_error));
      } else {
        launchQueueVideoUploads();
      }
    }
  }

  protected void removeHeadElement(ObjectQueue<VideoUpload> queue) {
    try {
      queue.remove();
    } catch (IOException ioException) {
      ioException.printStackTrace();
      Log.d(LOG_TAG, ioException.getMessage());
      Crashlytics.log("Error removing queue video to upload");
      Crashlytics.logException(ioException);
    }
  }


  protected Video process(VideoUpload videoUpload) {
    try {
      return videoApiClient.uploadVideo(videoUpload);
    } catch (VimojoApiException vimojoApiException) {
      vimojoApiException.printStackTrace();
      Log.d(LOG_TAG, vimojoApiException.getMessage());
      Crashlytics.log("Error vimojoApiException uploading video");
      Crashlytics.logException(vimojoApiException);
    }
    return null;
  }

}
