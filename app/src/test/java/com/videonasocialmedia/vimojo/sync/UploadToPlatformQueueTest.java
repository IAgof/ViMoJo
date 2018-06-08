/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.sync;

/**
 * Created by alvaro on 15/2/18.
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.squareup.tape2.ObjectQueue;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.auth.domain.usecase.GetAuthToken;
import com.videonasocialmedia.vimojo.sync.model.VideoUpload;
import com.videonasocialmedia.vimojo.sync.presentation.ui.UploadNotification;
import com.videonasocialmedia.vimojo.vimojoapiclient.VideoApiClient;
import com.videonasocialmedia.vimojo.vimojoapiclient.VimojoApiException;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.AuthToken;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.Video;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.FileNotFoundException;
import java.io.IOException;

import static com.videonasocialmedia.vimojo.sync.model.VideoUpload.MAX_NUM_TRIES_UPLOAD;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class UploadToPlatformQueueTest {
  UploadToPlatformQueue injectedUploadToPlatformQueue;

  @Mock
  UploadNotification mockedUploadNotification;
  @Mock Context mockedContext;
  @Mock VideoApiClient mockedVideoApiClient;
  @Mock GetAuthToken mockedGetAuthToken;
  @Rule
  public ExpectedException thrown = ExpectedException.none();
  private com.videonasocialmedia.vimojo.vimojoapiclient.model.Video video;

  @Before
  public void init() {
    PowerMockito.mockStatic(Log.class);
    MockitoAnnotations.initMocks(this);
    injectedUploadToPlatformQueue = createQueue();
  }

  private UploadToPlatformQueue createQueue() {
    return new UploadToPlatformQueue(mockedContext, mockedUploadNotification,
            mockedVideoApiClient, mockedGetAuthToken);
  }

  @After
  public void tearDown() throws IOException {
    ObjectQueue<VideoUpload> queue = injectedUploadToPlatformQueue.getQueue();
    queue.clear();
  }

  @Test
  public void processNextQueueShowFinishNotificationIfUploadSuccess() throws IOException,
      VimojoApiException {
    UploadToPlatformQueue uploadToPlatformQueue = Mockito.spy(getUploadToPlatformQueue());
    VideoUpload videoUpload = getAVideoUpload();
    video = getAVideo();
    uploadToPlatformQueue.addVideoToUpload(videoUpload);
    String token = "token";
    String userId = "user_id";
    doReturn(new AuthToken(token, userId))
            .when(mockedGetAuthToken).getAuthToken(any(Context.class));
    //Upload success
    when(mockedVideoApiClient.uploadVideo(token, videoUpload)).thenReturn(video);

    uploadToPlatformQueue.processNextQueueItem();

    verify(mockedUploadNotification).finishNotification(videoUpload.getId(), mockedContext.getString(R.string.upload_video_success),
        videoUpload.getTitle(),
        true, userId);
  }

  @Test
  public void retryItemUploadShowFinishNotificationErrorIfMaxNumTriesOfUpload() throws IOException {
    UploadToPlatformQueue uploadToPlatformQueue = Mockito.spy(getUploadToPlatformQueue());
    VideoUpload videoUpload = getAVideoUpload();
    video = getAVideo();
    String userId = "user_id";
    // Add two element to queue
    uploadToPlatformQueue.addVideoToUpload(videoUpload);
    // MAX_NUM_TRIES_UPLOAD = 3
    videoUpload.incrementNumTries();
    videoUpload.incrementNumTries();
    videoUpload.incrementNumTries();
    videoUpload.incrementNumTries();
    assertThat(videoUpload.getNumTries() > MAX_NUM_TRIES_UPLOAD, is(true));

    uploadToPlatformQueue.retryItemUpload(videoUpload, userId);

    verify(mockedUploadNotification).finishNotification(videoUpload.getId(), mockedContext.getString(R.string.upload_video_error),
        videoUpload.getTitle(),
        false, userId);
  }

  @Test
  public void processNextQueueRemoveItemIfUploadSuccess() throws IOException,
      VimojoApiException {
    UploadToPlatformQueue uploadToPlatformQueue = Mockito.spy(getUploadToPlatformQueue());
    VideoUpload videoUpload = getAVideoUpload();
    video = getAVideo();
    // Add two element to queue
    uploadToPlatformQueue.addVideoToUpload(videoUpload);
    uploadToPlatformQueue.addVideoToUpload(videoUpload);
    String token = "token";
    doReturn(new AuthToken(token, ""))
            .when(mockedGetAuthToken).getAuthToken(any(Context.class));
    //Upload success
    when(mockedVideoApiClient.uploadVideo(token, videoUpload)).thenReturn(video);
    assertThat("Queue size is 2", uploadToPlatformQueue.getQueue().size(), is(2));

    uploadToPlatformQueue.processNextQueueItem();

    assertThat("Queue size is 1", uploadToPlatformQueue.getQueue().size(), is(1));
  }


  @Test
  public void processNextQueueItemShowErrorFileNotFoundIfThrowsException()
      throws IOException, VimojoApiException {
    thrown.expect(FileNotFoundException.class);
    UploadToPlatformQueue uploadToPlatformQueue = Mockito.spy(getUploadToPlatformQueue());
    VideoUpload videoUpload = getAVideoUpload();
    video = getAVideo();
    uploadToPlatformQueue.addVideoToUpload(videoUpload);
    assertThat("Queue size is 1", uploadToPlatformQueue.getQueue().size(), is(1));
    String token = "token";
    doReturn(new AuthToken(token, ""))
            .when(mockedGetAuthToken).getAuthToken(any(Context.class));
    doThrow(new FileNotFoundException()).when(mockedVideoApiClient).uploadVideo(token, videoUpload);
    mockedVideoApiClient.uploadVideo(token, videoUpload);

    uploadToPlatformQueue.processNextQueueItem();

    assertThat("Queue size is empty", uploadToPlatformQueue.getQueue().size(), is(0));
  }

  @Test
  public void errorFileNotFoundRemoveElementFromQueue()
      throws IOException, VimojoApiException {
    thrown.expect(FileNotFoundException.class);
    UploadToPlatformQueue uploadToPlatformQueue = Mockito.spy(getUploadToPlatformQueue());
    VideoUpload videoUpload = getAVideoUpload();
    video = getAVideo();
    uploadToPlatformQueue.addVideoToUpload(videoUpload);
    String token = "token";
    doReturn(new AuthToken(token, ""))
            .when(mockedGetAuthToken).getAuthToken(any(Context.class));
    doThrow(new FileNotFoundException()).when(mockedVideoApiClient).uploadVideo(token, videoUpload);
    mockedVideoApiClient.uploadVideo(token, videoUpload);

    uploadToPlatformQueue.processNextQueueItem();

    verify(mockedUploadNotification).errorFileNotFound(videoUpload.getId(), videoUpload);
  }

  @Test
  public void processNextQueueItemShowErrorNetworkIfThrowsException()
      throws IOException, VimojoApiException {
    thrown.expect(VimojoApiException.class);
    UploadToPlatformQueue uploadToPlatformQueue = Mockito.spy(getUploadToPlatformQueue());
    VideoUpload videoUpload = getAVideoUpload();
    video = getAVideo();
    uploadToPlatformQueue.addVideoToUpload(videoUpload);
    String token = "token";
    doReturn(new AuthToken(token, ""))
            .when(mockedGetAuthToken).getAuthToken(any(Context.class));
    doThrow(new VimojoApiException(-1, VimojoApiException.NETWORK_ERROR))
            .when(mockedVideoApiClient).uploadVideo(token, videoUpload);
    mockedVideoApiClient.uploadVideo(token, videoUpload);

    uploadToPlatformQueue.processNextQueueItem();

    verify(mockedUploadNotification).errorNetworkNotification(videoUpload.getId());
  }

  @Test
  public void processNextQueueItemShowUnauthorizatedErrorIfThrowsException()
      throws IOException, VimojoApiException {
    thrown.expect(VimojoApiException.class);
    UploadToPlatformQueue uploadToPlatformQueue = Mockito.spy(getUploadToPlatformQueue());
    VideoUpload videoUpload = getAVideoUpload();
    video = getAVideo();
    uploadToPlatformQueue.addVideoToUpload(videoUpload);
    String token = "token";
    doReturn(new AuthToken(token, "")).when(mockedGetAuthToken).getAuthToken(any(Context.class));
    doThrow(new VimojoApiException(401, VimojoApiException.UNAUTHORIZED))
            .when(mockedVideoApiClient).uploadVideo(token, videoUpload);
    mockedVideoApiClient.uploadVideo(token, videoUpload);

    uploadToPlatformQueue.processNextQueueItem();

    verify(mockedUploadNotification).errorUnauthorizedUploadingVideos(videoUpload.getId());
  }

  @Test
  public void vimojoApiExceptionUnknownErrorRetryItemUpload() throws IOException,
      VimojoApiException {
    thrown.expect(VimojoApiException.class);
    UploadToPlatformQueue uploadToPlatformQueue = Mockito.spy(getUploadToPlatformQueue());
    VideoUpload videoUpload = getAVideoUpload();
    video = getAVideo();
    uploadToPlatformQueue.addVideoToUpload(videoUpload);
    String token = "token";
    String userId = "user_id";
    doReturn(new AuthToken(token, userId)).when(mockedGetAuthToken).getAuthToken(any(Context.class));
    doThrow(new VimojoApiException(-1, VimojoApiException.UNKNOWN_ERROR))
            .when(mockedVideoApiClient).uploadVideo(token, videoUpload);
    mockedVideoApiClient.uploadVideo(token, videoUpload);

    uploadToPlatformQueue.processNextQueueItem();

    verify(uploadToPlatformQueue).retryItemUpload(videoUpload, userId);
  }

  @NonNull
  public UploadToPlatformQueue getUploadToPlatformQueue() {
    return new UploadToPlatformQueue(mockedContext, mockedUploadNotification, mockedVideoApiClient,
        mockedGetAuthToken);
  }

  @NonNull
  public VideoUpload getAVideoUpload() {
    boolean isAcceptedUploadMobileNetwork = true;
    return new VideoUpload(1, "mediaPath",
        "title", "description", "productTypeList",
        isAcceptedUploadMobileNetwork, false);
  }

  @NonNull
  public Video getAVideo() {
    return new Video("owner", "video", "poster", "description",
        "_id");
  }
}
