/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.sync;

import android.util.Log;

import com.squareup.tape2.ObjectQueue;
import com.videonasocialmedia.vimojo.sync.model.VideoUpload;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
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
@PowerMockIgnore("javax.net.ssl.*")
@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class UploadToPlatformQueueTest {

  @InjectMocks UploadToPlatformQueue injectedUploadToPlatformQueue;

  @Mock SendNotification mockedSendNotification;

  @Before
  public void init() {
    PowerMockito.mockStatic(Log.class);
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void tearDown() throws IOException {
    //ObjectQueue<VideoUpload> queue = injectedUploadToPlatformQueue.getQueue();
    //queue.clear();
  }

  @Test
  public void addVideoUploadUpdateNotificationIfNotificationIsShowed() throws IOException {
    ObjectQueue<VideoUpload> queue = injectedUploadToPlatformQueue.getQueue();
    assertThat(queue.size(), is(0));
    VideoUpload videoUpload = new VideoUpload("authToken", "mediaPath",
        "title", "description", "productTypeList");
    when(mockedSendNotification.isNotificationShowed()).thenReturn(true);
    injectedUploadToPlatformQueue.addVideoToUpload(videoUpload);
    ObjectQueue<VideoUpload> updatedQueue = injectedUploadToPlatformQueue.getQueue();
    assertThat(updatedQueue.size(), is(1));
    assertThat(injectedUploadToPlatformQueue.isNotificationShowed(updatedQueue), is(true));

    injectedUploadToPlatformQueue.addVideoToUpload(videoUpload);

    verify(mockedSendNotification).updateNotificationVideoAdded(null, 1);
  }

  @Test
  public void launchQueueFinishNotificationIfQueueIsEmpty() throws IOException {

    injectedUploadToPlatformQueue.launchQueueVideoUploads();

    verify(mockedSendNotification).sendInfiniteProgressNotification(1,
        "uploadingVideo");

  }

  @Test
  public void launchQueueCallsSendNotification() throws IOException {


  }

}
