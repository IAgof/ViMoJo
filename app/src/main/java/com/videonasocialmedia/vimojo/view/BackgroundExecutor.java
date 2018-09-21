/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.view;

/**
 * Created by alvaro on 19/9/18.
 */

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import javax.inject.Inject;

/**
 * Delegate class to execute background tasks out of UI thread.
 */
public class BackgroundExecutor {

  private static final int N_THREADS = 5;
  private final ListeningExecutorService executorPool;

  @Inject
  public BackgroundExecutor() {
    executorPool = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(N_THREADS));
  }

  public <T> ListenableFuture<T> submit(Callable<T> callable) {
    return executorPool.submit(callable);
  }

  public ListenableFuture<?> submit(Runnable runnable) {
    return executorPool.submit(runnable);
  }

  public void addCallback(ListenableFuture<?> listenableFuture, FutureCallback<Object>
      futureCallback) {
    Futures.addCallback(listenableFuture, futureCallback);
  }
}
