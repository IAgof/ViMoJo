package com.videonasocialmedia.vimojo.shop.presentation.view.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.billingclient.api.Purchase;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.shop.model.Shop;
import com.videonasocialmedia.vimojo.shop.model.SkuShopData;
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

  private static final String LOG_TAG = ShopListActivity.class.getCanonicalName();

  @Inject ShopListPresenter presenter;

  @Bind(R.id.recycler_shop)
  RecyclerView shopList;
  @Bind (R.id.cancel_shopping)
  CardView cancelShopping;

  private ShopListAdapter adapter;
  private ProgressDialog progressDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_shopping);
    ButterKnife.bind(this);
    getActivityPresentersComponent().inject(this);
    createProgessDialog();
    initShopListRecycler();
    presenter.handleBillingManager();
  }

  private void createProgessDialog() {
    progressDialog = new ProgressDialog(ShopListActivity.this, R.style.VideonaDialog);
    progressDialog.setTitle(R.string.alert_dialog_title_shop);
    progressDialog.setMessage(getString(R.string.alert_dialog_message_shop));
    progressDialog.setProgressStyle(progressDialog.STYLE_HORIZONTAL);
    progressDialog.setIndeterminate(true);
    progressDialog.setProgressNumberFormat(null);
    progressDialog.setProgressPercentFormat(null);
    progressDialog.setCanceledOnTouchOutside(false);
    progressDialog.setCancelable(false);
  }

  private void initShopListRecycler() {
    adapter = new ShopListAdapter();
    adapter.setShopListClickListener(this);
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
  protected void onDestroy() {
    super.onDestroy();
    presenter.destroyBillingManager();
  }

  @Override
  public void onClickShopItem(Shop shop, String skuId, String billingType) {
    presenter.purchaseItem(skuId, billingType);
  }

  @Override
  public void showShopList(List<Shop> shopList, List<SkuShopData> skuShopList) {
    adapter.setShopList(shopList, skuShopList);
  }

  @Override
  public void showProgressDialog() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (!isFinishing()) {
          progressDialog.show();
        }
      }
    });
  }

  @Override
  public void hideProgressDialog() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (progressDialog != null && progressDialog.isShowing()) {
          progressDialog.dismiss();
        }
      }
    });
  }

  @Override
  public void showError(String message) {
    Snackbar.make(cancelShopping, message, Snackbar.LENGTH_LONG).show();
  }

  @Override
  public void updatePurchasedItem(SkuShopData skuShopData) {
    // TODO: 02/11/2017 User has just bought and item and expects ...
  }

  @Override
  public void updateHistoryPurchasedItems(List<Purchase> purchasesList) {
    // TODO: 02/11/2017 User bought items from list
  }

  @OnClick (R.id.cancel_shopping)
  public void cancelShopping() {
    finish();
  }
}
