package com.videonasocialmedia.vimojo.repository.datasource;

/**
 * Created by jliarte on 12/07/18.
 */

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.auth0.android.authentication.storage.CredentialsManagerException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.result.Credentials;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.crashlytics.android.Crashlytics;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.auth0.GetUserId;
import com.videonasocialmedia.vimojo.auth0.UserAuth0Helper;
import com.videonasocialmedia.vimojo.vimojoapiclient.VimojoApiException;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

/**
 * Abstract Data source class to get and persist data via API.
 *
 * <p>This class handles common tasks for api data sources, such as authenting requests or
 * processing API response errors.</p>
 *
 * @param <T> The class of the values stored into this data source.
 */
public abstract class ApiDataSource<T> implements DataSource<T> {
  private static final String LOG_TAG = ApiDataSource.class.getSimpleName();
  private static final int N_THREADS = 2;
  private final UserAuth0Helper userAuth0Helper;
  private final GetUserId getUserId;
  private static final int PRIORITY_NORMAL = 3;
  private JobManager jobManager;
  private ListeningExecutorService listeningExecutorService;

  protected ApiDataSource(UserAuth0Helper userAuth0Helper, GetUserId getUserId,
                          JobManager jobManager) {
    listeningExecutorService = MoreExecutors.listeningDecorator(
            Executors.newFixedThreadPool(N_THREADS));
    this.userAuth0Helper = userAuth0Helper;
    this.getUserId = getUserId;
    this.jobManager = jobManager;
  }

  protected SettableFuture<Credentials> getApiAccessToken() {
    SettableFuture<Credentials> credentialsListenableFuture = SettableFuture.create();
    userAuth0Helper.getAccessToken(new BaseCallback<Credentials, CredentialsManagerException>() {
      @Override
      public void onFailure(CredentialsManagerException error) {
        // No credentials were previously saved or they couldn't be refreshed
        Log.e(LOG_TAG, "processAsyncUpload, getApiAccessToken onFailure No credentials were " +
                "previously saved or they couldn't be refreshed");
        Crashlytics.log("Error processAsyncUpload getApiAccessToken");
        Crashlytics.logException(error);
        credentialsListenableFuture.setException(error);
      }

      @Override
      public void onSuccess(Credentials credentials) {
        credentialsListenableFuture.set(credentials);
      }
    });
    return credentialsListenableFuture;
  }

  protected String getUserId() {
    return getUserId.getUserId().getId();
  }

  protected void processApiError(VimojoApiException apiError) {
    // TODO(jliarte): 11/07/18 what to do on API error??? retry! user notification?

    // TODO(jliarte): 11/07/18 extract log helper
    String msg = "Error adding project to API";
    Crashlytics.log(msg);
    Crashlytics.logException(apiError);
    Log.e(LOG_TAG, msg);
    if (BuildConfig.DEBUG) {
      apiError.printStackTrace();
    }
  }


  protected void schedule(Callable callable) {
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
        listeningExecutorService.submit(callable).get(); // (jliarte): 7/09/18 call get to propagate exceptions
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
}
