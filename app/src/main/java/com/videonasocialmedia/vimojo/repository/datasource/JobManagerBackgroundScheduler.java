package com.videonasocialmedia.vimojo.repository.datasource;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.auth0.android.authentication.storage.CredentialsManagerException;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.videonasocialmedia.vimojo.vimojoapiclient.VimojoApiException;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

/**
 * Scheduler delegate class to schedule {@link Callable} tasks in background when network
 * connection is available.
 */
public class JobManagerBackgroundScheduler implements BackgroundScheduler {
  private String LOG_TAG = JobManagerBackgroundScheduler.class.getSimpleName();
  private static final int PRIORITY_NORMAL = 3;
  private static final int N_THREADS = 2;
  private JobManager jobManager;
  private ListeningExecutorService listeningExecutorService;

  public JobManagerBackgroundScheduler(JobManager jobManager) {
    this.listeningExecutorService = MoreExecutors.listeningDecorator(
            Executors.newFixedThreadPool(N_THREADS));
    this.jobManager = jobManager;
  }

  @Override
  public void schedule(Callable callable) {
    schedule(callable, null);
  }

  @Override
  public ListenableFuture scheduleWithFuture(Callable callable) {
    SettableFuture futureResult = SettableFuture.create();
    schedule(callable, new ResultListener() {
      @Override
      public void notifyResult(Object result) {
        futureResult.set(result);
      }

      @Override
      public void notifyError(Throwable throwable) {
        futureResult.setException(throwable);
      }
    });
    return futureResult;
  }

  private void schedule(Callable callable, ResultListener listener) {
    // TODO(jliarte): 7/09/18 cannot persist job as JobSerializer tries to serialize
    //     CompositionApiDataSource, we should make a service that receive a CompositionDto
    //     and call CompositionApiDataSource.launchUpdateCompositionDto
    Job job = new Job(new Params(PRIORITY_NORMAL).requireNetwork()) {
      @Override
      public void onAdded() {
        Log.d(LOG_TAG, "Added API job " + callable.toString());
      }

      @Override
      public void onRun() throws Throwable {
        Log.d(LOG_TAG, "Executing API job " + callable.toString());
        Object result = listeningExecutorService.submit(callable).get(); // (jliarte): 7/09/18 call get to propagate exceptions
        if (listener != null) {
          listener.notifyResult(result);
        }
      }

      @Override
      protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

      }

      /**
       * If {@code onRun} method throws an exception, this method is called.
       * <p>
       * If you simply want to return retry or cancel, you can use {@link RetryConstraint#RETRY} or
       * {@link RetryConstraint#CANCEL}.
       * <p>
       * You can also use a custom {@link RetryConstraint} where you can change the Job's priority or
       * add a delay until the next run (e.g. exponential back off).
       * <p>
       * Note that changing the Job's priority or adding a delay may alter the original run order of
       * the job. So if the job was added to the queue with other jobs and their execution order is
       * important (e.g. they use the same groupId), you should not change job's priority or add a
       * delay unless you really want to change their execution order.
       *
       * @param throwable   The exception that was thrown from {@link #onRun()}
       * @param runCount    The number of times this job run. Starts from 1.
       * @param maxRunCount The max number of times this job can run. Decided by {@link #getRetryLimit()}
       * @return A {@link RetryConstraint} to decide whether this Job should be tried again or not and
       * if yes, whether we should add a delay or alter its priority. Returning null from this method
       * is equal to returning {@link RetryConstraint#RETRY}.
       */
      @Override
      protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        if (throwable.getCause() instanceof ExecutionException) {
          Throwable cause = throwable.getCause().getCause();
          if (cause instanceof CredentialsManagerException) {
            return logCauseAndExponentialBackoffRetry(throwable.getCause(), runCount);
          }
        } else if (throwable.getCause() instanceof VimojoApiException) {
          return logCauseAndExponentialBackoffRetry(throwable, runCount);
        } // else InterruptedException ???
        Log.e(LOG_TAG, "Not retrying. Error cause: " + throwable.getMessage());
        if (listener != null) {
          listener.notifyError(throwable);
        }
        return RetryConstraint.CANCEL;
      }

      @NonNull
      private RetryConstraint logCauseAndExponentialBackoffRetry(@NonNull Throwable throwable, int runCount) {
        Log.e(LOG_TAG,
                "Error making API call. Cause: " + throwable.getCause().getMessage() + " Retrying");
        return RetryConstraint.createExponentialBackoff(runCount, 10000 /*base delay*/);
      }

    };
    jobManager.addJobInBackground(job);
  }

  private interface ResultListener {
    void notifyResult(Object result) ;

    void notifyError(Throwable throwable);
  }
}