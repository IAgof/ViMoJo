package com.videonasocialmedia.vimojo.repository.datasource;

/**
 * Created by jliarte on 12/07/18.
 */

import android.util.Log;

import com.auth0.android.authentication.storage.CredentialsManagerException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.result.Credentials;
import com.crashlytics.android.Crashlytics;
import com.google.common.util.concurrent.SettableFuture;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.auth0.GetUserId;
import com.videonasocialmedia.vimojo.auth0.UserAuth0Helper;
import com.videonasocialmedia.vimojo.vimojoapiclient.VimojoApiException;

import java.util.concurrent.Callable;

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
  private final UserAuth0Helper userAuth0Helper;
  private final GetUserId getUserId;
  private final BackgroundScheduler backgroundScheduler;

  protected ApiDataSource(UserAuth0Helper userAuth0Helper, GetUserId getUserId,
                          BackgroundScheduler backgroundScheduler) {
    this.backgroundScheduler = backgroundScheduler;
    this.userAuth0Helper = userAuth0Helper;
    this.getUserId = getUserId;
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
    backgroundScheduler.schedule(callable);
  }
}
