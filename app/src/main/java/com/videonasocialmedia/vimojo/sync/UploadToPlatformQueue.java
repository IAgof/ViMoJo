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
import com.crashlytics.android.Crashlytics;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
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
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

/**
 * Class to unify video uploads to platform.
 * Create/init ObjectQueue, add objects and launchQueue.
 * FIFO, atomic ObjectQueue.
 */
public class UploadToPlatformQueue {
  private final String LOG_TAG = UploadToPlatformQueue.class.getCanonicalName();
  private final Context context;
  // TODO(jliarte): 12/01/18 tune this parameter
  private static final int N_THREADS = 5;
  private final ListeningExecutorService executorPool;
  private final VideoApiClient videoApiClient;
  private MoshiConverter converter;
  private UploadNotification uploadNotification;
  private boolean isUploadCanceledByNetworkError = false;

  public UploadToPlatformQueue(Context context) {
    this.context = context;
    executorPool = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(N_THREADS));
    uploadNotification = new UploadNotification(context);
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
    startOrUploadNotification();
  }

  protected boolean isNotificationShowed(ObjectQueue<VideoUpload> queue) {
    return queue.size() > 0 && uploadNotification.isNotificationShowed();
  }

  public void startOrUploadNotification() {
    Log.d(LOG_TAG, "launchNotification");
    if(!isNotificationShowed(getQueue())) {
      Log.d(LOG_TAG, "startNotification");
      uploadNotification.startInfiniteProgressNotification(R.drawable.notification_uploading_small,
          context.getString(R.string.uploading_video));
    } else {
      Log.d(LOG_TAG, "updateNotification");
      uploadNotification.updateNotificationVideoAdded(context.getString(R.string.uploading_video),
          getQueue().size());
    }
  }

  public void launchQueueVideoUploads() {
    Log.d(LOG_TAG, "startUploading");
    ObjectQueue<VideoUpload> queue = getQueue();
    Iterator<VideoUpload> iterator = queue.iterator();
    VideoUpload element = iterator.next();
    String title = element.getTitle();
    Video video = process(element);
    if (video != null) {
      removeHeadElement(getQueue());
      if (getQueue().isEmpty()) {
        Log.d(LOG_TAG, "finishNotification");
        uploadNotification.finishNotification(context.getString(R.string.upload_video_success),
            title, true);
      } else {
        Log.d(LOG_TAG, "appendSuccessNotification");
        uploadNotification.appendResultNotification(context.getString(R.string.uploading_video),
            queue.size(), context.getString(R.string.upload_video_success), title, true);
      }
    } else {
      incrementHeadNumTries(getQueue());
      if (element.getNumTries() > VideoUpload.MAX_NUM_TRIES_UPLOAD) {
        removeHeadElement(getQueue());
        if (getQueue().isEmpty()) {
          Log.d(LOG_TAG, "finishNotification");
          uploadNotification.finishNotification(context.getString(R.string.upload_video_error),
              title, false);
        } else {
          Log.d(LOG_TAG, "appendErrorNotification");
          uploadNotification.appendResultNotification(context.getString(R.string.uploading_video),
              queue.size(), context.getString(R.string.upload_video_error), title, false);
        }
      } else {
        if(!isUploadCanceledByNetworkError) {
          launchQueueVideoUploads();
        }
      }
    }
  }

  private void incrementHeadNumTries(ObjectQueue<VideoUpload> queue) {
    try {
      queue.peek().incrementNumTries();
    } catch (IOException ioException) {
      //ioException.printStackTrace();
      Log.d(LOG_TAG, ioException.getMessage());
      Crashlytics.log("Error increment num tries head of queue video to upload");
      Crashlytics.logException(ioException);
    }
  }

  protected void removeHeadElement(ObjectQueue<VideoUpload> queue) {
    try {
      queue.remove();
    } catch (IOException ioException) {
      //ioException.printStackTrace();
      Log.d(LOG_TAG, ioException.getMessage());
      Crashlytics.log("Error removing queue video to upload");
      Crashlytics.logException(ioException);
    }
  }

  private Video process(VideoUpload videoUpload) {
    try {
      VideoApiClient videoApiClient = new VideoApiClient();
      return videoApiClient.uploadVideo(obtainAuthToken(), videoUpload);
    } catch (VimojoApiException e) {
      e.printStackTrace();
    }
    return null;
  }

  private String obtainAuthToken() {
    GetAuthToken getAuthToken = new GetAuthToken();
    final String[] authToken = {""};
    ListenableFuture<String> authTokenFuture = executeUseCaseCall(new Callable<String>() {
      @Override
      public String call() throws Exception {
        return getAuthToken.getAuthToken(context).getToken();
      }
    });
    Futures.addCallback(authTokenFuture, new FutureCallback<String>() {
      @Override
      public void onSuccess(String authorizationToken) {
        authToken[0] = authorizationToken;
      }

      @Override
      public void onFailure(Throwable errorGettingToken) {
      }
    });
    try {
      authTokenFuture.get();
    } catch (InterruptedException interruptedException) {
      interruptedException.printStackTrace();
      Crashlytics.log("Error getting info from user interruptedException");
      Crashlytics.logException(interruptedException);
    } catch (ExecutionException executionException) {
      executionException.printStackTrace();
      Crashlytics.log("Error getting info from user executionException");
      Crashlytics.logException(executionException);
    }
    return authToken[0];
  }

  protected final <T> ListenableFuture<T> executeUseCaseCall(Callable<T> callable) {
    return executorPool.submit(callable);
  }
}
