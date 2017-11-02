package com.videonasocialmedia.vimojo.shop.presentation.mvp.views;

import com.videonasocialmedia.vimojo.shop.model.Shop;


public interface ShopListClickListener {
  void onClickShopItem(Shop shop, String skuId, String billingType);
}
