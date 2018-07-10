package com.videonasocialmedia.vimojo.repository.asset.datasource;

import com.videonasocialmedia.vimojo.vimojoapiclient.AssetApiClient;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.AssetDto;

import java.util.Collection;

/**
 * Created by jliarte on 5/07/18.
 */

public class AssetApiDataSource {
  private final AssetApiClient assetApiClient;

  public AssetApiDataSource(AssetApiClient assetApiClient) {
    this.assetApiClient = assetApiClient;
  }

  /**
   * Gets all Assets of current user
   *
   * @return A collection of Assets including all assets of user
   */
  public Collection<AssetDto> getAll() {
    // TODO(jliarte): 6/07/18 implement this method!
    return null;
  }

  public Collection<AssetDto> getProjectAssets() {
    // TODO(jliarte): 6/07/18 implement this method!
    return null;
  }
}
