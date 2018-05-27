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

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.squareup.moshi.Moshi;
import com.squareup.tape2.ObjectQueue;
import com.squareup.tape2.QueueFile;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.auth.domain.usecase.GetAuthToken;
import com.videonasocialmedia.vimojo.utils.IntentConstants;
import com.videonasocialmedia.vimojo.vimojoapiclient.VideoApiClient;
import com.videonasocialmedia.vimojo.vimojoapiclient.VimojoApiException;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.AuthToken;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.Video;
import com.videonasocialmedia.vimojo.sync.model.VideoUpload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

/**
 * Class to unify video uploads to platform.
 * Create/init ObjectQueue, add objects and launchQueue.
 * FIFO, atomic ObjectQueue.
 */
public class UploadToPlatformQueue {
  private final String LOG_TAG = UploadToPlatformQueue.class.getCanonicalName();
  private final Context context;
  private final VideoApiClient videoApiClient;
  private final GetAuthToken getAuthToken;
  private UploadNotification uploadNotification;

  public UploadToPlatformQueue(Context context, UploadNotification uploadNotification,
                               VideoApiClient videoApiClient, GetAuthToken getAuthToken) {
    this.context = context;
    this.uploadNotification = uploadNotification;
    this.videoApiClient = videoApiClient;
    this.getAuthToken = getAuthToken;
    Log.d(LOG_TAG, "Created sync queue...");
  }

  protected ObjectQueue<VideoUpload> getQueue() {
    Log.d(LOG_TAG, "getting queue...");
    String uploadQUEUE = "QueueUploads_" + BuildConfig.FLAVOR;
    File file = new File(context.getFilesDir(), uploadQUEUE);
    ObjectQueue<VideoUpload> videoUploadObjectQueue = null;
    try {
      QueueFile queueFile = new QueueFile.Builder(file).build();
      Moshi moshi = new Moshi.Builder().build();
      MoshiConverter converter = new MoshiConverter(moshi, VideoUpload.class);
      videoUploadObjectQueue = ObjectQueue.create(queueFile, converter);
    } catch (IOException ioException) {
      ioException.printStackTrace();
      Log.d(LOG_TAG, ioException.getMessage());
      Crashlytics.log("Error creating queue video to upload");
      Crashlytics.logException(ioException);
    }
    Log.d(LOG_TAG, "...returned queue");
    return videoUploadObjectQueue;
  }

  public void addVideoToUpload(VideoUpload videoUpload) throws IOException {
    ObjectQueue<VideoUpload> queue = getQueue();
    queue.add(videoUpload);
  }

  public void processNextQueueItem() {
    Log.d(LOG_TAG, "processNextQueueItem");
    Log.d(LOG_TAG, "startNotification");
    VideoUpload element = getQueue().iterator().next();
    int notificationUploadId = element.getId();
    Intent cancelUploadIntent = new Intent(context, CancelUploadBroadcastReceiver.class);
    cancelUploadIntent.setAction(IntentConstants.ACTION_CANCEL_UPLOAD);
    cancelUploadIntent.putExtra(EXTRA_NOTIFICATION_ID, notificationUploadId);
    PendingIntent cancelUploadPendingIntent =
        PendingIntent.getBroadcast(context, 0, cancelUploadIntent, 0);
    uploadNotification.startInfiniteProgressNotification(notificationUploadId,
            R.drawable.notification_uploading_small, context.getString(R.string.uploading_video),
            cancelUploadPendingIntent);
    AuthToken authToken = getAuthToken.getAuthToken(context);
    try {
      String token = authToken.getToken();
      // TODO(jliarte): 27/02/18 check what to do with plaform response
      Video video = videoApiClient.uploadVideo(token, element);
      removeHeadElement(getQueue());
      Log.d(LOG_TAG, "finishNotification success");
      uploadNotification.finishNotification(notificationUploadId,
          context.getString(R.string.upload_video_success), element.getTitle(), true, authToken.getId());
    } catch (VimojoApiException vimojoApiException) {
      Log.d(LOG_TAG, "vimojoApiException " + vimojoApiException.getApiErrorCode());
      Crashlytics.log("Error process upload vimojoApiException");
      Crashlytics.logException(vimojoApiException);
      switch (vimojoApiException.getApiErrorCode()) {
        case VimojoApiException.UNAUTHORIZED:
          uploadNotification.errorUnauthorizedUploadingVideos(notificationUploadId);
          break;
        case VimojoApiException.NETWORK_ERROR:
          uploadNotification.errorNetworkNotification(notificationUploadId);
          break;
        default:
          retryItemUpload(element, authToken.getId());
      }
    } catch (FileNotFoundException fileNotFoundError) {
      if (BuildConfig.DEBUG) {
        fileNotFoundError.printStackTrace();
      }
      Log.d(LOG_TAG, "File " + element.getMediaPath() + " trying to upload does not exists!");
      uploadNotification.errorFileNotFound(notificationUploadId, element);
      // (jliarte): 27/02/18 Check this error management
      removeHeadElement(getQueue());
    }
  }

  public void cancelUploadByUser() {
    // TODO: 27/5/18 Cancel synchronus retrofit call videoApiClient.uploadVideo Now only works UI, video is been uploading to server.
    ObjectQueue<VideoUpload> queue = getQueue();
    VideoUpload element = getQueue().iterator().next();
    int notificationUploadId = element.getId();
    uploadNotification.cancelNotification(notificationUploadId, element.getTitle());
    removeHeadElement(queue);
    ObjectQueue<VideoUpload> updatedQueue = getQueue();
    if(updatedQueue.iterator().hasNext()) {
      processNextQueueItem();
    }
  }

  protected void retryItemUpload(VideoUpload element, String userId) {
    incrementHeadNumTries(getQueue());
    if (element.getNumTries() > VideoUpload.MAX_NUM_TRIES_UPLOAD) {
      removeHeadElement(getQueue());
      Log.d(LOG_TAG, "finishNotification, error");
      uploadNotification.finishNotification(element.getId(),
          context.getString(R.string.upload_video_error), element.getTitle(), false, userId);
    }
  }

  private void incrementHeadNumTries(ObjectQueue<VideoUpload> queue) {
    try {
      queue.peek().incrementNumTries();
    } catch (IOException ioException) {
      Log.d(LOG_TAG, ioException.getMessage());
      Crashlytics.log("Error increment num tries head of queue video to upload");
      Crashlytics.logException(ioException);
    }
  }

  private void removeHeadElement(ObjectQueue<VideoUpload> queue) {
    if (queue.iterator().hasNext()) {
      try {
        queue.remove();
      } catch (IOException ioException) {
        Log.d(LOG_TAG, ioException.getMessage());
        Crashlytics.log("Error removing queue video to upload");
        Crashlytics.logException(ioException);
      }
    }
  }

}
