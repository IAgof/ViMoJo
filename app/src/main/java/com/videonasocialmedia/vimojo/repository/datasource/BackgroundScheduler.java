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

public interface BackgroundScheduler {
  void schedule(Callable callable);

  ListenableFuture scheduleWithFuture(Callable callable);
}
