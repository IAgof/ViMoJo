package com.videonasocialmedia.vimojo.view;

/**
 * Created by jliarte on 12/01/18.
 */

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import javax.inject.Inject;

/**
 * Class for presenters to extend to implement presentation logic. This class provides some common
 * functionalities presenters share
 */
public class VimojoPresenter {
  // TODO(jliarte): 12/01/18 tune this parameter
  private static final int N_THREADS = 5;
  private final ListeningExecutorService executorPool;
  private UserEventTracker userEventTracker;

  @Inject
  public VimojoPresenter(UserEventTracker userEventTracker) {
    this.userEventTracker = userEventTracker;
    executorPool = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(N_THREADS));
  }

  public VimojoPresenter() {
    executorPool = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(N_THREADS));
  }

  protected final <T> ListenableFuture<T> executeUseCaseCall(Callable<T> callable) {
    return executorPool.submit(callable);
  }

  protected final ListenableFuture<?> executeUseCaseCall(Runnable runnable) {
    return executorPool.submit(runnable);
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
