package com.videonasocialmedia.vimojo.view;

/**
 * Created by jliarte on 12/01/18.
 */

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Callable;

/**
 * Class for presenters to extend to implement presentation logic. This class provides some common
 * functionalities presenters share
 */
public class VimojoPresenter {
  private BackgroundExecutor backgroundExecutor;

  public VimojoPresenter(BackgroundExecutor backgroundExecutor) {
    this.backgroundExecutor = backgroundExecutor;
  }

  public final <T> ListenableFuture<T> executeUseCaseCall(Callable<T> callable) {
    return backgroundExecutor.submit(callable);
  }

  public final ListenableFuture<?> executeUseCaseCall(Runnable runnable) {
    return backgroundExecutor.submit(runnable);
  }

  public final void addCallback(ListenableFuture listenableFuture, FutureCallback futureCallback) {
    backgroundExecutor.addCallback(listenableFuture, futureCallback);
  }

}
