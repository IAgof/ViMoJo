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
import com.videonasocialmedia.vimojo.auth.domain.usecase.GetAuthToken;
import com.videonasocialmedia.vimojo.vimojoapiclient.VideoApiClient;
import com.videonasocialmedia.vimojo.vimojoapiclient.VimojoApiException;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.Video;
import com.videonasocialmedia.vimojo.sync.model.VideoUpload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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
    uploadNotification.startInfiniteProgressNotification(notificationUploadId,
            R.drawable.notification_uploading_small, context.getString(R.string.uploading_video));
    try {
      String authToken = getAuthToken.getAuthToken(context).getToken();
      // TODO(jliarte): 27/02/18 check what to do with plaform response
      Video video = videoApiClient.uploadVideo(authToken, element);
      removeHeadElement(getQueue());
      Log.d(LOG_TAG, "finishNotification success");
      uploadNotification.finishNotification(notificationUploadId,
          context.getString(R.string.upload_video_success), element.getTitle(), true);
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
          retryItemUpload(element);
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

  protected void retryItemUpload(VideoUpload element) {
    incrementHeadNumTries(getQueue());
    if (element.getNumTries() > VideoUpload.MAX_NUM_TRIES_UPLOAD) {
      removeHeadElement(getQueue());
      Log.d(LOG_TAG, "finishNotification, error");
      uploadNotification.finishNotification(element.getId(),
          context.getString(R.string.upload_video_error), element.getTitle(), false);

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
    try {
      queue.remove();
    } catch (IOException ioException) {
      Log.d(LOG_TAG, ioException.getMessage());
      Crashlytics.log("Error removing queue video to upload");
      Crashlytics.logException(ioException);
    }
  }

}
