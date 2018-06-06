/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.sync.presentation;

/**
 * Created by alvaro on 6/2/18.
 */

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.auth.domain.usecase.GetAuthToken;
import com.videonasocialmedia.vimojo.repository.upload.UploadRepository;
import com.videonasocialmedia.vimojo.sync.helper.ProgressRequestBody;
import com.videonasocialmedia.vimojo.sync.model.VideoUpload;
import com.videonasocialmedia.vimojo.sync.presentation.ui.UploadNotification;
import com.videonasocialmedia.vimojo.sync.presentation.broadcastreceiver.UploadBroadcastReceiver;
import com.videonasocialmedia.vimojo.utils.IntentConstants;
import com.videonasocialmedia.vimojo.vimojoapiclient.VideoApiClient;
import com.videonasocialmedia.vimojo.vimojoapiclient.VimojoApiException;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.AuthToken;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.Video;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;
import static com.videonasocialmedia.vimojo.vimojoapiclient.ApiConstants.MIME_TYPE_VIDEO;

/**
 * Class to unify video uploads to platform.
 * Create/init ObjectQueue, add objects and launchQueue.
 * FIFO, atomic ObjectQueue.
 */
public class UploadToPlatform implements ProgressRequestBody.UploadCallbacks {
  private final String LOG_TAG = UploadToPlatform.class.getCanonicalName();
  private final Context context;
  private final VideoApiClient videoApiClient;
  private final GetAuthToken getAuthToken;
  private UploadNotification uploadNotification;
  private Call<Video> uploadVideoAsync;
  private UploadRepository uploadRepository;

  public UploadToPlatform(Context context, UploadNotification uploadNotification,
                          VideoApiClient videoApiClient, GetAuthToken getAuthToken,
                          UploadRepository uploadRepository) {
    this.context = context;
    this.uploadNotification = uploadNotification;
    this.videoApiClient = videoApiClient;
    this.getAuthToken = getAuthToken;
    this.uploadRepository = uploadRepository;
    Log.d(LOG_TAG, "Created sync queue...");
  }

  public void addVideoToUpload(VideoUpload videoUpload) throws IOException {
    // add videoUpload to realm
    uploadRepository.add(videoUpload);
  }

  public void processAsyncUpload(VideoUpload videoUpload) {
    Log.d(LOG_TAG, "processAsyncNextQueueItem");
    Log.d(LOG_TAG, "startNotification");
    int notificationUploadId = videoUpload.getId();
    Intent cancelUploadIntent = new Intent(context, UploadBroadcastReceiver.class);
    cancelUploadIntent.setAction(IntentConstants.ACTION_CANCEL_UPLOAD);
    cancelUploadIntent.putExtra(EXTRA_NOTIFICATION_ID, notificationUploadId);
    cancelUploadIntent.putExtra(IntentConstants.VIDEO_UPLOAD_UUID, videoUpload.getUuid());
    PendingIntent cancelUploadPendingIntent =
        PendingIntent.getBroadcast(context, 0, cancelUploadIntent, 0);
    Intent pauseUploadIntent = new Intent(context, UploadBroadcastReceiver.class);
    pauseUploadIntent.setAction(IntentConstants.ACTION_PAUSE_UPLOAD);
    pauseUploadIntent.putExtra(EXTRA_NOTIFICATION_ID, notificationUploadId);
    pauseUploadIntent.putExtra(IntentConstants.VIDEO_UPLOAD_UUID, videoUpload.getUuid());
    PendingIntent pauseUploadPendingIntent =
        PendingIntent.getBroadcast(context, 0, pauseUploadIntent, 0);
    uploadNotification.startInfiniteProgressNotification(notificationUploadId,
        R.drawable.notification_uploading_small, context.getString(R.string.uploading_video),
        cancelUploadPendingIntent, pauseUploadPendingIntent);
    AuthToken authToken = getAuthToken.getAuthToken(context);
    try {
      String token = authToken.getToken();
      // TODO(jliarte): 27/02/18 check what to do with plaform response
      Log.d(LOG_TAG, "uploading video ... videoApiClient.uploadVideo");
      File file = new File(videoUpload.getMediaPath());
      ProgressRequestBody fileBody = new ProgressRequestBody(file, MIME_TYPE_VIDEO, this);
      videoUpload.setUploading(true);
      uploadRepository.update(videoUpload);
      uploadVideoAsync = videoApiClient.uploadVideoAsync(token, videoUpload, fileBody);
      uploadVideoAsync.enqueue(new Callback<Video>() {
        @Override
        public void onResponse(Call<Video> call, Response<Video> response) {
          Log.d(LOG_TAG, "uploaded video ... videoApiClient.uploadVideo");
          removeVideoUpload(videoUpload);
          Log.d(LOG_TAG, "finishNotification success");
          uploadNotification.finishNotification(notificationUploadId, context.getString(R.string.upload_video_success),
              videoUpload.getTitle(), true, authToken.getId());
        }

        @Override
        public void onFailure(Call<Video> call, Throwable t) {
          if (call.isCanceled()) {
            int notificationUploadId = videoUpload.getId();
            uploadNotification.cancelNotification(notificationUploadId, videoUpload.getTitle());
          } else {
            retryItemUpload(videoUpload);
          }
        }
      });

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
          retryItemUpload(videoUpload);
      }
    } catch (FileNotFoundException fileNotFoundError) {
      if (BuildConfig.DEBUG) {
        fileNotFoundError.printStackTrace();
      }
      Log.d(LOG_TAG, "File " + videoUpload.getMediaPath() + " trying to upload does not exists!");
      uploadNotification.errorFileNotFound(notificationUploadId, videoUpload);
      // (jliarte): 27/02/18 Check this error management
      removeVideoUpload(videoUpload);
    }
  }

  private void retryItemUpload(VideoUpload videoUpload) {
    if (videoUpload.getNumTries() < VideoUpload.MAX_NUM_TRIES_UPLOAD) {
      videoUpload.incrementNumTries();
      uploadRepository.update(videoUpload);
      processAsyncUpload(videoUpload);
    }
  }


  public void cancelUploadByUser(VideoUpload videoUpload) {
    // TODO: 27/5/18 Cancel synchronus retrofit call videoApiClient.uploadVideo Now only works UI, video is been uploading to server.
    Log.d(LOG_TAG, "cancelUploadByUser");
    if (uploadVideoAsync != null) {
      Log.d(LOG_TAG, "cancel and remove");
      uploadVideoAsync.cancel();
      removeVideoUpload(videoUpload);
    }
  }

  public void pauseUploadByUser(VideoUpload videoUpload) {
    Log.d(LOG_TAG, "pauseUploadByUser");
    if (uploadVideoAsync != null) {
      uploadVideoAsync.cancel();
      videoUpload.setUploading(false);
      uploadRepository.update(videoUpload);
    }
  }

  private void removeVideoUpload(VideoUpload videoUpload) {
    uploadRepository.remove(videoUpload);
  }

  @Override
  public void onProgressUpdate(int percentage) {
    uploadNotification.setProgress(percentage);
  }

  @Override
  public void onError() {

  }

  @Override
  public void onFinish() {
    uploadNotification.setProgress(100);
  }
}
