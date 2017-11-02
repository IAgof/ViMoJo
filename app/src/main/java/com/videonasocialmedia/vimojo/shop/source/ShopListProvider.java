package com.videonasocialmedia.vimojo.shop.source;

import android.content.Context;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.shop.model.Shop;

import java.util.ArrayList;
import java.util.List;

public class ShopListProvider {
  private final Context context;
  private List<Shop> shopList = new ArrayList();

  public ShopListProvider(Context context) {
    this.context = context;
    populateShopList();
  }

  public List<Shop> getAll() {
    return shopList;
  }

  private void populateShopList() {
    shopList.add(new Shop(context.getString(R.string.title_theme_app_shop),
        context.getString(R.string.description_theme_app_shop), "0,79"));
    shopList.add(new Shop(context.getString(R.string.title_watermark_shop),
        context.getString(R.string.description_theme_app_shop), "0,79"));
  }
}
