package com.videonasocialmedia.vimojo.asset.repository;

/**
 * Created by jliarte on 16/08/18.
 */

import com.videonasocialmedia.vimojo.asset.domain.helper.HashCountGenerator;
import com.videonasocialmedia.vimojo.asset.domain.model.Asset;
import com.videonasocialmedia.vimojo.asset.repository.datasource.AssetApiDataSource;
import com.videonasocialmedia.vimojo.repository.DeletePolicy;
import com.videonasocialmedia.vimojo.repository.ReadPolicy;
import com.videonasocialmedia.vimojo.repository.Specification;
import com.videonasocialmedia.vimojo.repository.VimojoRepository;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.util.List;

import javax.inject.Inject;

/**
 * Repository for providing {@link Asset} via repository pattern
 *
 * <p>This class handles saving and retrieving {@link Asset}s from different data sources and merge
 * Asset provided by them for returning results. This also handles asset retrieval from backend and
 * local storage</p>
 */
public class AssetRepository extends VimojoRepository<Asset> {
  private AssetApiDataSource assetApiDataSource;
  private HashCountGenerator hashCountGenerator;

  @Inject
  public AssetRepository(AssetApiDataSource assetApiDataSource,
                         HashCountGenerator hashCountGenerator) {
    this.assetApiDataSource = assetApiDataSource;
    this.hashCountGenerator = hashCountGenerator;
    // TODO(jliarte): 16/08/18 inject this?
  }

  @Override
  public void add(Asset item) {
    // TODO(jliarte): 16/08/18 method implementation
  }

  @Override
  public void add(Iterable<Asset> items) {
    // TODO(jliarte): 16/08/18 method implementation
  }

  @Override
  public void update(Asset item) {
    // TODO(jliarte): 16/08/18 method implementation
  }

  @Override
  public void remove(Asset item) {
    // TODO(jliarte): 16/08/18 method implementation
  }

  @Override
  public void remove(Specification specification) {
    // TODO(jliarte): 16/08/18 method implementation
  }

  @Override
  public List<Asset> query(Specification specification) {
    // TODO(jliarte): 16/08/18 method implementation
    return null;
  }

  @Override
  public Asset getById(String id) {
    // TODO(jliarte): 16/08/18 method implementation
    return null;
  }

  @Override
  public void remove(Asset item, DeletePolicy policy) {
    // TODO(jliarte): 16/08/18 method implementation
  }

  @Override
  public Asset getById(String id, ReadPolicy readPolicy) {
    return getById(id, ReadPolicy.READ_ALL);
  }

}
