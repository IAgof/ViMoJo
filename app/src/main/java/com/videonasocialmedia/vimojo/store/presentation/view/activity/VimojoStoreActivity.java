package com.videonasocialmedia.vimojo.store.presentation.view.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.store.model.SkuStoreData;
import com.videonasocialmedia.vimojo.store.presentation.mvp.presenters.VimojoStorePresenter;
import com.videonasocialmedia.vimojo.store.presentation.mvp.views.StoreListClickListener;
import com.videonasocialmedia.vimojo.store.presentation.mvp.views.VimojoStoreView;
import com.videonasocialmedia.vimojo.store.presentation.view.adapter.StoreListAdapter;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class VimojoStoreActivity extends VimojoActivity implements VimojoStoreView, StoreListClickListener {

  private static final String LOG_TAG = VimojoStoreActivity.class.getCanonicalName();

  @Inject
  VimojoStorePresenter presenter;

  @BindView(R.id.recycler_store)
  RecyclerView storeList;
  @BindView(R.id.cancel_shopping)
  CardView cancelShopping;

  private StoreListAdapter adapter;
  private ProgressDialog progressDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_store);
    ButterKnife.bind(this);
    getActivityPresentersComponent().inject(this);
    createProgessDialog();
    initVimojoStoreRecycler();
  }

  private void createProgessDialog() {
    progressDialog = new ProgressDialog(VimojoStoreActivity.this, R.style.VideonaDialog);
    progressDialog.setTitle(R.string.alert_dialog_title_store);
    progressDialog.setMessage(getString(R.string.alert_dialog_message_store));
    progressDialog.setProgressStyle(progressDialog.STYLE_HORIZONTAL);
    progressDialog.setIndeterminate(true);
    progressDialog.setProgressNumberFormat(null);
    progressDialog.setProgressPercentFormat(null);
    progressDialog.setCanceledOnTouchOutside(false);
    progressDialog.setCancelable(false);
  }

  private void initVimojoStoreRecycler() {
    adapter = new StoreListAdapter();
    adapter.setStoreListClickListener(this);
    LinearLayoutManager layoutManager =
        new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    storeList.setLayoutManager(layoutManager);
    storeList.setAdapter(adapter);
  }

  @Override
  protected void onResume() {
    super.onResume();
    presenter.initBilling();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    presenter.destroyBillingManager();
  }

  @Override
  public void onClickStoreItem(String skuId, String billingType) {
    presenter.purchaseItem(skuId, billingType);
  }

  @Override
  public void showStoreList(List<SkuStoreData> skuStoreList) {
    adapter.setStoreItemsList(skuStoreList);
    presenter.queryPurchaseHistory();
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
  public void updatePurchasedItem(SkuStoreData skuStoreData) {
    adapter.notifyDataSetChanged();
  }

  @Override
  public void updateHistoryPurchasedItems(List<SkuStoreData> storePurchasesList) {
    adapter.notifyDataSetChanged();
  }

  @OnClick(R.id.cancel_shopping)
  public void cancelShopping() {
    finish();
  }
}
