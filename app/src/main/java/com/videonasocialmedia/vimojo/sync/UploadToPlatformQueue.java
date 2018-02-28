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
  private UploadNotification uploadNotification;

  public UploadToPlatformQueue(Context context) {
    this.context = context;
    uploadNotification = new UploadNotification(context);
    videoApiClient = new VideoApiClient();
  }

  protected ObjectQueue<VideoUpload> getQueue() {
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
    return videoUploadObjectQueue;
  }

  public void addVideoToUpload(VideoUpload videoUpload) throws IOException {
    ObjectQueue<VideoUpload> queue = getQueue();
    queue.add(videoUpload);
  }

  protected boolean isNotificationShowed(ObjectQueue<VideoUpload> queue) {
    return queue.size() > 0 && uploadNotification.isNotificationShowed();
  }

  public void startOrUpdateNotification() {
    Log.d(LOG_TAG, "launchNotification");
    if (!isNotificationShowed(getQueue()) ||
        uploadNotification.isShowedErrorNetworkNotification()) {
      Log.d(LOG_TAG, "startNotification");
      uploadNotification.startInfiniteProgressNotification(R.drawable.notification_uploading_small,
          context.getString(R.string.uploading_video));
    } else {
      Log.d(LOG_TAG, "updateNotification");
      uploadNotification.updateNotificationVideoAdded(context.getString(R.string.uploading_video), getQueue().size());
    }
  }

  public void processNextQueueItem() {
    Log.d(LOG_TAG, "processNextQueueItem");
    VideoUpload element = getQueue().iterator().next();
    try {
      // TODO(jliarte): 27/02/18 check what to do with plaform response
      String authToken = new GetAuthToken().getAuthToken(context).getToken();
      Video video = videoApiClient.uploadVideo(authToken, element);
      uploadNotification.appendResultNotification(context.getString(R.string.uploading_video),
              getQueue().size(), context.getString(R.string.upload_video_success),
              element.getTitle(), true);
      removeHeadElement(getQueue());
      if (getQueue().isEmpty()) {
        Log.d(LOG_TAG, "Empty queue, finishNotification");
        uploadNotification.finishNotification(true);
      }

    } catch (VimojoApiException vimojoApiException) {
      Log.d(LOG_TAG, "vimojoApiException " + vimojoApiException.getApiErrorCode());
      Crashlytics.log("Error process upload vimojoApiException");
      Crashlytics.logException(vimojoApiException);
      if (vimojoApiException.getApiErrorCode().equals(VimojoApiException.UNAUTHORIZED)) {
        uploadNotification.errorUnauthorizationUploadingVideos();
      }
      if (vimojoApiException.getApiErrorCode().equals(VimojoApiException.NETWORK_ERROR)) {
        uploadNotification.errorNetworkNotification();
        retryItemUpload(element);
      }
    } catch (FileNotFoundException fileNotFoundError) {
      if (BuildConfig.DEBUG) {
        Log.d(LOG_TAG, "File " + element.getMediaPath() + " trying to upload does not exists!");
      }
      uploadNotification.errorFileNotFound(element);
      // (jliarte): 27/02/18 Check this error management
      removeHeadElement(getQueue());
    }
  }

  private void retryItemUpload(VideoUpload element) {
    incrementHeadNumTries(getQueue());
    if (element.getNumTries() > VideoUpload.MAX_NUM_TRIES_UPLOAD) {
      removeHeadElement(getQueue());
      if (getQueue().isEmpty()) {
        Log.d(LOG_TAG, "finishNotification");
        uploadNotification.finishNotification(false);
      } else {
        Log.d(LOG_TAG, "appendErrorNotification");
        uploadNotification.appendResultNotification(context.getString(R.string.uploading_video),
            getQueue().size(), context.getString(R.string.upload_video_error), element.getTitle(), false);
      }
    } else {
      // (jliarte): 27/02/18 this is currently always false!!! detected by lint
      if (!uploadNotification.isShowedErrorNetworkNotification()) {
        processNextQueueItem();
      }
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
