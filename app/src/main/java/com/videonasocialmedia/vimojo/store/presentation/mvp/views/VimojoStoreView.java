package com.videonasocialmedia.vimojo.store.presentation.mvp.views;

import com.videonasocialmedia.vimojo.store.model.SkuStoreData;

import java.util.List;


public interface VimojoStoreView {
  void showStoreList(List<SkuStoreData> skuShopList);

  void hideProgressDialog();

  void showProgressDialog();

  void showError(String message);

  void updatePurchasedItem(SkuStoreData skuStoreData);

  void updateHistoryPurchasedItems(List<SkuStoreData> shopPurchasedList);
}
