package com.videonasocialmedia.vimojo.shop.presentation.mvp.presenters;

import android.content.Context;

import com.videonasocialmedia.vimojo.shop.domain.GetShopListUseCase;
import com.videonasocialmedia.vimojo.shop.model.Shop;
import com.videonasocialmedia.vimojo.shop.presentation.mvp.views.ShopListView;

import java.util.List;

import javax.inject.Inject;

public class ShopListPresenter {

  private Context context;
  private List<Shop> shopList;
  private ShopListView shopListView;
  GetShopListUseCase getShopListUseCase;

 @Inject
 public ShopListPresenter(ShopListView shopListView, Context context, GetShopListUseCase getShopListUseCase) {
    this.context = context;
    shopList = getShopListUseCase.getShopList();
    this.shopListView = shopListView;

  }

  public void getShopList() {
    shopListView.showShopList(shopList);
  }

}
