/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.sync;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.squareup.tape2.ObjectQueue;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.sync.model.VideoUpload;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;


/**
 * Created by alvaro on 15/2/18.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class UploadToPlatformQueueTest {

  @InjectMocks UploadToPlatformQueue injectedUploadToPlatformQueue;

  @Mock UploadNotification mockedUploadNotification;
  @Mock Context mockedContext;

  @Before
  public void init() {
    PowerMockito.mockStatic(Log.class);
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void tearDown() throws IOException {
    ObjectQueue<VideoUpload> queue = injectedUploadToPlatformQueue.getQueue();
    queue.clear();
  }

  @Test
  public void startNotificationIfQueueIsEmpty() throws IOException {
    ObjectQueue<VideoUpload> queue = injectedUploadToPlatformQueue.getQueue();
    assertThat(queue.size(), is(0));

    injectedUploadToPlatformQueue.startOrUpdateNotification();

    verify(mockedUploadNotification)
        .startInfiniteProgressNotification(R.drawable.notification_uploading_small,
        mockedContext.getString(R.string.uploading_video));
  }

  @Test
  public void startNotificationIfQueueIsNotEmptyAndNotificationHasBeenCanceledByNetworkError() throws IOException {
    UploadToPlatformQueue uploadToPlatformQueue = Mockito.spy(getUploadToPlatformQueue());
    boolean isAcceptedUploadMobileNetwork = true;
    VideoUpload videoUpload = new VideoUpload("mediaPath",
        "title", "description", "productTypeList",
        isAcceptedUploadMobileNetwork);
    uploadToPlatformQueue.addVideoToUpload(videoUpload);
    ObjectQueue<VideoUpload> updatedQueue = injectedUploadToPlatformQueue.getQueue();
    assertThat("Queue is not empty", updatedQueue.size(), is(1));
    when(mockedUploadNotification.isNotificationShowed()).thenReturn(true);
    when(mockedUploadNotification.isShowedErrorNetworkNotification()).thenReturn(true);

    injectedUploadToPlatformQueue.startOrUpdateNotification();

    verify(mockedUploadNotification)
        .startInfiniteProgressNotification(R.drawable.notification_uploading_small,
            mockedContext.getString(R.string.uploading_video));
  }

  @NonNull
  public UploadToPlatformQueue getUploadToPlatformQueue() {
    return new UploadToPlatformQueue(mockedContext);
  }

  @Test
  public void uploadNotificationIfQueueIsNotEmpty() throws IOException {
    ObjectQueue<VideoUpload> queue = injectedUploadToPlatformQueue.getQueue();
    assertThat(queue.size(), is(0));
    boolean isAcceptedUploadMobileNetwork = true;
    VideoUpload videoUpload = new VideoUpload("mediaPath",
        "title", "description", "productTypeList",
        isAcceptedUploadMobileNetwork);
    injectedUploadToPlatformQueue.addVideoToUpload(videoUpload);
    ObjectQueue<VideoUpload> updatedQueue = injectedUploadToPlatformQueue.getQueue();
    assertThat(updatedQueue.size(), is(1));
    when(mockedUploadNotification.isNotificationShowed()).thenReturn(true);

    injectedUploadToPlatformQueue.startOrUpdateNotification();

    verify(mockedUploadNotification).updateNotificationVideoAdded(null, 1);
  }

  @Test
  public void launchQueueCallsSendNotification() throws IOException {
    // I am not able to mock videoApiClient.uploadVideo(videoUpload) and test this part. I can not
    // mock retrofit response, i do not want to check response, only processNextQueueItem

  }

}
