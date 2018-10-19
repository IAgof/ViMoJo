package com.videonasocialmedia.vimojo.view;

/**
 * Created by jliarte on 12/01/18.
 */

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import java.util.concurrent.Callable;

import javax.inject.Inject;

/**
 * Class for presenters to extend to implement presentation logic. This class provides some common
 * functionalities presenters share
 */
public class VimojoPresenter {
  private BackgroundExecutor backgroundExecutor;
  private UserEventTracker userEventTracker;

  @Inject
  public VimojoPresenter(BackgroundExecutor backgroundExecutor,
                         UserEventTracker userEventTracker) {
    this.backgroundExecutor = backgroundExecutor;
    this.userEventTracker = userEventTracker;
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

  public void onActivityDestroy() {
    userEventTracker.flush();
  }

  public void onActivityStart(Class<? extends VimojoActivity> activity) {
    userEventTracker.startView(activity);
  }

  public void onActivityPause(Class<? extends VimojoActivity> activity) {
    userEventTracker.endView(activity);
  }
}
