package com.videonasocialmedia.vimojo.shop.domain;


import com.videonasocialmedia.vimojo.shop.model.Shop;
import com.videonasocialmedia.vimojo.shop.source.ShopListProvider;

import java.util.List;

import javax.inject.Inject;


public class GetShopListUseCase {
  private ShopListProvider shopListProvider;

  @Inject
  public GetShopListUseCase(ShopListProvider shopListProvider) {
    this.shopListProvider = shopListProvider;

  }

  public List<Shop> getShopList() {
    return shopListProvider.getAll();
  }
}
