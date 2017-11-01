package com.videonasocialmedia.vimojo.shop.presentation.view.activity;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.shop.model.Shop;
import com.videonasocialmedia.vimojo.shop.presentation.mvp.presenters.ShopListPresenter;
import com.videonasocialmedia.vimojo.shop.presentation.mvp.views.ShopListClickListener;
import com.videonasocialmedia.vimojo.shop.presentation.mvp.views.ShopListView;
import com.videonasocialmedia.vimojo.shop.presentation.view.adapter.ShopListAdapter;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ShopListActivity extends VimojoActivity implements ShopListView, ShopListClickListener {

  @Inject ShopListPresenter presenter;

  @Bind(R.id.recycler_shop)
  RecyclerView shopList;
  @Bind (R.id.cancel_shopping)
  CardView cancelShopping;

  private ShopListAdapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_shopping);
    ButterKnife.bind(this);
    getActivityPresentersComponent().inject(this);
    initShopListRecycler();
  }

  private void initShopListRecycler() {
    adapter = new ShopListAdapter();
    adapter.setShopListClickListener(this);
    presenter.getShopList();
    LinearLayoutManager layoutManager =
        new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    shopList.setLayoutManager(layoutManager);
    shopList.setAdapter(adapter);
  }

  @Override
  protected void onResume() {
    super.onResume();
    presenter.getShopList();
  }

  @Override
  public void onClick(Shop shop) {

  }

  @Override
  public void showShopList(List<Shop> shopList) {
    adapter.setShopList(shopList);
  }

  @OnClick (R.id.cancel_shopping)
  public void cancelShopping() {
    finish();
  }
}
