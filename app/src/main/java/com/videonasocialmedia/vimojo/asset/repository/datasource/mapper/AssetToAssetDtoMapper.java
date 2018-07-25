package com.videonasocialmedia.vimojo.asset.repository.datasource.mapper;

import com.videonasocialmedia.vimojo.asset.domain.model.Asset;
import com.videonasocialmedia.vimojo.repository.KarumiMapper;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.AssetDto;

/**
 * Created by jliarte on 20/07/18.
 */

public class AssetToAssetDtoMapper extends KarumiMapper<Asset, AssetDto> {
  @Override
  public AssetDto map(Asset asset) {
    AssetDto assetDto = new AssetDto();
    assetDto.mediaId = asset.getMediaId();
    assetDto.projectId = asset.getProjectId();
    assetDto.uri = asset.getUri();
    assetDto.mimetype = asset.getMimetype();
    assetDto.filename = asset.getFilename();
    assetDto.type = asset.getType();
    assetDto.date = asset.getDate();
    return assetDto;
  }

  @Override
  public Asset reverseMap(AssetDto assetDto) {
    Asset asset = new Asset();
    asset.id = assetDto.getId();
    asset.mediaId = assetDto.getMediaId();
    asset.name = assetDto.getName();
    asset.type = assetDto.getType();
    asset.hash = assetDto.getHash();
    asset.filename = assetDto.getFilename();
    asset.mimetype = assetDto.getMimetype();
    asset.uri = assetDto.getUri();
    asset.projectId = assetDto.getProjectId();
    asset.date = assetDto.getDate();
    asset.creationDate = assetDto.getCreationDate();
    asset.modificationDate = assetDto.getModificationDate();
    asset.createdBy = assetDto.getCreatedBy();
    return asset;
  }
}
