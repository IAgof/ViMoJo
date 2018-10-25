/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.repository.datasource;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Callable;

/**
 * Created by alvaro on 13/9/18.
 */

public class FakeBackgroundScheduler implements BackgroundScheduler {
  @Override
  public void schedule(Callable callable) {
    // Do nothing https://github.com/yigit/android-priority-jobqueue/issues/64
    /*try {
      callable.call();
    } catch (Exception e) {
      e.printStackTrace();
    }*/
  }

  @Override
  public ListenableFuture scheduleWithFuture(Callable callable) {
    // TODO: 13/9/18 provide implementation when needed
    return null;
  }
}
