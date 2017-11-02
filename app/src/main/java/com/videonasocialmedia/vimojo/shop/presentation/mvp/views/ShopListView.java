package com.videonasocialmedia.vimojo.shop.presentation.mvp.views;

import com.videonasocialmedia.vimojo.shop.model.SkuShopData;

import java.util.List;


public interface ShopListView {
  void showShopList(List<SkuShopData> skuShopList);

  void hideProgressDialog();

  void showProgressDialog();

  void showError(String message);

  void updatePurchasedItem(SkuShopData skuShopData);

  void updateHistoryPurchasedItems(List<SkuShopData> shopPurchasedList);
}
