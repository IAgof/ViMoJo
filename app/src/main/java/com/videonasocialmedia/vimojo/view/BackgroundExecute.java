/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.view;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Callable;

/**
 * Created by alvaro on 17/9/18.
 */

public interface BackgroundExecute {
  <T> ListenableFuture<T> executeUseCaseCall(Callable<T> callable);

  ListenableFuture<?> executeUseCaseCall(Runnable runnable);
}
