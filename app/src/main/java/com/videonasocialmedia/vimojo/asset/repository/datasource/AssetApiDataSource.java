package com.videonasocialmedia.vimojo.asset.repository.datasource;

/**
 * Created by jliarte on 5/07/18.
 */

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.videonasocialmedia.vimojo.asset.domain.model.Asset;
import com.videonasocialmedia.vimojo.asset.repository.datasource.mapper.AssetToAssetDtoMapper;
import com.videonasocialmedia.vimojo.auth0.GetUserId;
import com.videonasocialmedia.vimojo.auth0.UserAuth0Helper;
import com.videonasocialmedia.vimojo.repository.Specification;
import com.videonasocialmedia.vimojo.repository.datasource.ApiDataSource;
import com.videonasocialmedia.vimojo.sync.AssetUploadQueue;
import com.videonasocialmedia.vimojo.sync.helper.RunSyncAdapterHelper;
import com.videonasocialmedia.vimojo.vimojoapiclient.AssetApiClient;
import com.videonasocialmedia.vimojo.vimojoapiclient.VimojoApiException;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.AssetDto;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.AssetQuery;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

/**
 * API DataSource for assets. Provide remote persistence of media files using vimojo API
 * via {@link com.videonasocialmedia.vimojo.vimojoapiclient.model.AssetDto} class.
 */
public class AssetApiDataSource extends ApiDataSource<Asset> {
  private static final String LOG_TAG = AssetApiDataSource.class.getSimpleName();
  private AssetUploadQueue assetUploadQueue;
  private RunSyncAdapterHelper runSyncAdapterHelper;
  private AssetApiClient assetApiClient;
  private AssetToAssetDtoMapper mapper = new AssetToAssetDtoMapper();

  @Inject
  public AssetApiDataSource(UserAuth0Helper userAuth0Helper, AssetUploadQueue assetUploadQueue,
                            RunSyncAdapterHelper runSyncAdapterHelper,
                            AssetApiClient assetApiClient, GetUserId getUserId) {
    super(userAuth0Helper, getUserId);
    this.assetUploadQueue = assetUploadQueue;
    this.runSyncAdapterHelper = runSyncAdapterHelper;
    this.assetApiClient = assetApiClient;
  }

  // TODO(jliarte): 18/07/18 enqueue method vs add???
  @Override
  public void add(Asset asset) {
    enqueueAssetUpload(asset);
  }

  @Override
  public void add(Iterable<Asset> items) {
    for (Asset asset : items) {
      add(asset);
    }
  }

  @Override
  public void update(Asset item) {
    // TODO(jliarte): 18/07/18 enqueue method vs update???
    enqueueAssetUpload(item);
  }

  @Override
  public void remove(Asset item) {

  }

  @Override
  public void remove(Specification specification) {

  }

  @Override
  public List<Asset> query(Specification specification) {
    return null;
  }

  @Override
  public Asset getById(String id) {
    try {
      String accessToken = getApiAccessToken().get().getAccessToken();
      AssetDto assetDto = this.assetApiClient.get(id, accessToken);
      return mapper.reverseMap(assetDto);
    } catch (VimojoApiException apiError) {
      processApiError(apiError);
    } catch (InterruptedException | ExecutionException e) {
      // TODO(jliarte): 12/07/18 manage this error
      e.printStackTrace();
    }
    return null; // TODO(jliarte): 20/07/18 check this path
  }

  private void enqueueAssetUpload(Asset asset) {
    try {
      // TODO(jliarte): 18/07/18 get project
      assetUploadQueue.addAssetToUpload(asset);
      runSyncAdapterHelper.runNowSyncAdapter();
      Log.d(LOG_TAG, "addAsset " + asset.getPath());
    } catch (IOException ioException) {
      ioException.printStackTrace();
      Log.d(LOG_TAG, ioException.getMessage());
      Crashlytics.log("Error adding video to upload");
      Crashlytics.logException(ioException);
    }
  }

//  /**
//   * Gets all Assets of current user
//   *
//   * @return A collection of Assets including all assets of user
//   */
//  public Collection<AssetDto> getAll() {
//    // TODO(jliarte): 6/07/18 implement this method!
//    return null;
//  }
//
//  public Collection<AssetDto> getProjectAssets() {
//    // TODO(jliarte): 6/07/18 implement this method!
//    return null;
//  }

}
