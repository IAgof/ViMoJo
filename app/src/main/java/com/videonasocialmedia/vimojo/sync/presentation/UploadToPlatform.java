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

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.authentication.storage.CredentialsManagerException;
import com.auth0.android.authentication.storage.SecureCredentialsManager;
import com.auth0.android.authentication.storage.SharedPreferencesStorage;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.result.Credentials;
import com.auth0.android.result.UserProfile;
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
import com.videonasocialmedia.vimojo.vimojoapiclient.UserApiClient;
import com.videonasocialmedia.vimojo.vimojoapiclient.VideoApiClient;
import com.videonasocialmedia.vimojo.vimojoapiclient.VimojoApiException;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.AuthToken;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.Video;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;
import static com.videonasocialmedia.vimojo.vimojoapiclient.ApiConstants.MIME_TYPE_VIDEO;

/**
 * Class to unify video uploads to platform.
 * List of videos to uploads managed with UploadRepository
 * Add video to upload, process async upload.
 * User can pause, cancel, remove upload
 */
public class UploadToPlatform implements ProgressRequestBody.UploadCallbacks {
  private final String LOG_TAG = UploadToPlatform.class.getCanonicalName();
  private final Context context;
  private final VideoApiClient videoApiClient;
  private final UserApiClient userApiClient;
  private UploadNotification uploadNotification;
  private Call<Video> uploadVideoAsync;
  private UploadRepository uploadRepository;
  private int notificationUploadId;
  private PendingIntent cancelUploadPendingIntent;
  private PendingIntent pauseUploadPendingIntent;
  private PendingIntent removePendingIntent;
  private int percentageShowed = 0;

  public UploadToPlatform(Context context, UploadNotification uploadNotification,
                          VideoApiClient videoApiClient, UserApiClient userApiClient,
                          UploadRepository uploadRepository) {
    this.context = context;
    this.uploadNotification = uploadNotification;
    this.videoApiClient = videoApiClient;
    this.userApiClient = userApiClient;
    this.uploadRepository = uploadRepository;
  }

  public void addVideoToUpload(VideoUpload videoUpload) throws IOException {
    uploadRepository.add(videoUpload);
  }

  public boolean isBeingSendingToPlatform(VideoUpload videoUpload) {
    for (VideoUpload videoAddedToUpload: uploadRepository.getAllVideosToUpload()) {
      if (videoAddedToUpload.getMediaPath().equals(videoUpload.getMediaPath())) {
        return true;
      }
    }
    return false;
  }

  public void processAsyncUpload(VideoUpload videoUpload) {
    Log.d(LOG_TAG, "processAsyncUpload");
    Log.d(LOG_TAG, "startNotification " + videoUpload.getUuid());
    notificationUploadId = videoUpload.getId();
    createPendingIntents(videoUpload.getUuid());
    resetProgressBar();
    uploadNotification.startInfiniteProgressNotification(notificationUploadId,
        R.drawable.notification_uploading_small, context.getString(R.string.uploading_video),
        cancelUploadPendingIntent, pauseUploadPendingIntent, removePendingIntent);

    userApiClient.getManager().getCredentials(new BaseCallback<Credentials,
        CredentialsManagerException>() {
      @Override
      public void onSuccess(Credentials credentials) {
        //Get credentials, tokenUser
        String accesToken = credentials.getAccessToken();
        try {
          // UserId
          String userId = userApiClient.getUserId(accesToken);
          // Uploading video
          uploadVideo(accesToken, userId, videoUpload);
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
              videoUpload.setUploading(false);
              uploadRepository.update(videoUpload);
              break;
            default:
              if (videoUpload.getNumTries() < VideoUpload.MAX_NUM_TRIES_UPLOAD) {
                retryItemUpload(videoUpload);
              }
          }
        } catch (FileNotFoundException fileNotFoundError) {
          if (BuildConfig.DEBUG) {
            fileNotFoundError.printStackTrace();
          }
          Log.d(LOG_TAG, "File " + videoUpload.getMediaPath()
              + " trying to upload does not exists!");
          uploadNotification.errorFileNotFound(notificationUploadId, videoUpload);
          // (jliarte): 27/02/18 Check this error management
          removeVideoUpload(videoUpload);
        }
      }

      @Override
      public void onFailure(CredentialsManagerException error) {
        //No credentials were previously saved or they couldn't be refreshed
      }
    });
  }

  private void uploadVideo(String token, String userId, VideoUpload videoUpload)
      throws VimojoApiException, FileNotFoundException {
    // TODO(jliarte): 27/02/18 check what to do with plaform response
    Log.d(LOG_TAG, "uploading video ... videoApiClient.uploadVideo");
    Log.d(LOG_TAG, "token " + token + " id " + userId);
    File file = new File(videoUpload.getMediaPath());
    ProgressRequestBody fileBody = new ProgressRequestBody(file, MIME_TYPE_VIDEO, this);
    videoUpload.setUploading(true);
    uploadRepository.update(videoUpload);
    uploadVideoAsync = videoApiClient.uploadVideoAsync(token, videoUpload, fileBody);
    uploadVideoAsync.enqueue(new Callback<Video>() {
      @Override
      public void onResponse(Call<Video> call, Response<Video> response) {
        Log.d(LOG_TAG, "onResponse uploaded video ... videoApiClient.uploadVideo");
        removeVideoUpload(videoUpload);
        Log.d(LOG_TAG, "finishNotification success");
        resetProgressBar();
        uploadNotification.finishNotification(notificationUploadId,
            context.getString(R.string.upload_video_success), videoUpload.getTitle(),
            true, userId);
      }
      @Override
      public void onFailure(Call<Video> call, Throwable t) {
        Log.d(LOG_TAG, "onFailure uploading video ... " + t.getMessage()
            + " cause " + t.getCause());
        if (!call.isCanceled()) {
          if (t instanceof IOException) {
            videoUpload.setUploading(false);
            uploadRepository.update(videoUpload);
            uploadNotification.errorNetworkNotification(notificationUploadId);
            return;
          }
          if (videoUpload.getNumTries() < VideoUpload.MAX_NUM_TRIES_UPLOAD) {
            retryItemUpload(videoUpload);
          }
        }
      }
    });
  }

  public void resetProgressBar() {
    percentageShowed = 0;
  }

  private void createPendingIntents(String videoUploadUuid) {
    Intent cancelUploadIntent = new Intent(context, UploadBroadcastReceiver.class);
    cancelUploadIntent.setAction(IntentConstants.ACTION_CANCEL_UPLOAD);
    cancelUploadIntent.putExtra(EXTRA_NOTIFICATION_ID, notificationUploadId);
    cancelUploadIntent.putExtra(IntentConstants.VIDEO_UPLOAD_UUID, videoUploadUuid);
    cancelUploadPendingIntent = PendingIntent.getBroadcast(context, 0,
        cancelUploadIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    Intent pauseUploadIntent = new Intent(context, UploadBroadcastReceiver.class);
    pauseUploadIntent.setAction(IntentConstants.ACTION_PAUSE_UPLOAD);
    pauseUploadIntent.putExtra(EXTRA_NOTIFICATION_ID, notificationUploadId);
    pauseUploadIntent.putExtra(IntentConstants.VIDEO_UPLOAD_UUID, videoUploadUuid);
    pauseUploadPendingIntent = PendingIntent.getBroadcast(context, 0,
        pauseUploadIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    Intent removeUploadIntent = new Intent(context, UploadBroadcastReceiver.class);
    removeUploadIntent.setAction(IntentConstants.ACTION_REMOVE_UPLOAD);
    removePendingIntent = PendingIntent.getBroadcast(
        context, 0, removeUploadIntent, PendingIntent.FLAG_UPDATE_CURRENT);
      }

  private void retryItemUpload(VideoUpload videoUpload) {
    videoUpload.incrementNumTries();
    uploadRepository.update(videoUpload);
    processAsyncUpload(videoUpload);
  }

  public void cancelUploadByUser(VideoUpload videoUpload) {
    Log.d(LOG_TAG, "cancelUploadByUser, cancel and remove video upload");
    uploadVideoAsync.cancel();
    removeVideoUpload(videoUpload);
    uploadNotification.cancelNotification(notificationUploadId, videoUpload.getTitle());
  }

  public void pauseUploadByUser(VideoUpload videoUpload) {
    Log.d(LOG_TAG, "pauseUploadByUser, cancel upload, not remove video");
    uploadVideoAsync.cancel();
    // Update pauseUploadIntent with action activate upload, not pause.
    Intent pauseUploadIntent = new Intent(context, UploadBroadcastReceiver.class);
    pauseUploadIntent.setAction(IntentConstants.ACTION_ACTIVATE_UPLOAD);
    pauseUploadIntent.putExtra(EXTRA_NOTIFICATION_ID, notificationUploadId);
    pauseUploadIntent.putExtra(IntentConstants.VIDEO_UPLOAD_UUID, videoUpload.getUuid());
    pauseUploadPendingIntent = PendingIntent.getBroadcast(context, 0,
        pauseUploadIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    uploadNotification.pauseNotification(notificationUploadId, videoUpload.getTitle(),
        cancelUploadPendingIntent, pauseUploadPendingIntent, removePendingIntent);
  }

  public void removeUploadByUser() {
    uploadNotification.removeNotification(notificationUploadId);
  }

  private void removeVideoUpload(VideoUpload videoUpload) {
    uploadRepository.remove(videoUpload);
  }

  @Override
  public void onProgressUpdate(int percentage) {
    if(percentageShowed < percentage && (percentage % 5 == 0)) {
      Log.d(LOG_TAG, "progress " + percentage);
      percentageShowed = percentage;
      uploadNotification.setProgress(notificationUploadId, R.drawable.notification_uploading_small,
          context.getString(R.string.uploading_video), cancelUploadPendingIntent,
          pauseUploadPendingIntent, removePendingIntent, percentage);
    }
  }
}
