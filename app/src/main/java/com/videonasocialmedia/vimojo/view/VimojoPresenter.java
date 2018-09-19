package com.videonasocialmedia.vimojo.view;

/**
 * Created by jliarte on 12/01/18.
 */

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

/**
 * Class for presenters to extend to implement presentation logic. This class provides some common
 * functionalities presenters share
 */
public class VimojoPresenter {
  // TODO(jliarte): 12/01/18 tune this parameter
  private static final int N_THREADS = 5;
  private final ListeningExecutorService executorPool;

  public VimojoPresenter() {
    executorPool = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(N_THREADS));
  }

  protected final <T> ListenableFuture<T> executeUseCaseCall(Callable<T> callable) {
    return executorPool.submit(callable);
  }

  protected final ListenableFuture<?> executeUseCaseCall(Runnable runnable) {
    return executorPool.submit(runnable);
  }


}
