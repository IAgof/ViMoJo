/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.view;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

/**
 * Created by alvaro on 17/9/18.
 */

public class FakeBackgroundExecute extends VimojoPresenter {

  @Override
  public <T> ListenableFuture<T> executeUseCaseCall(Callable<T> callable) {
    try {
      callable.call();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public ListenableFuture<?> executeUseCaseCall(Runnable runnable) {
    runnable.run();
    return null;
  }
}
