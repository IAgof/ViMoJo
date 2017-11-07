package com.videonasocialmedia.vimojo.store.presentation.mvp.presenters;

import android.content.Context;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.store.billing.BillingConnectionListener;
import com.videonasocialmedia.vimojo.store.billing.BillingHistoryPurchaseListener;
import com.videonasocialmedia.vimojo.store.billing.BillingManager;
import com.videonasocialmedia.vimojo.store.billing.BillingUpdatesPurchaseListener;
import com.videonasocialmedia.vimojo.store.model.SkuStoreData;
import com.videonasocialmedia.vimojo.store.presentation.mvp.views.VimojoStoreView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.android.billingclient.api.BillingClient.SkuType.INAPP;

public class VimojoStorePresenter implements BillingUpdatesPurchaseListener,
    BillingHistoryPurchaseListener, BillingConnectionListener {

  private static final String LOG_TAG = VimojoStorePresenter.class.getCanonicalName();
  private Context context;
  private List<SkuStoreData> skuStoreItemList = new ArrayList<>();
  private VimojoStoreView vimojoStoreView;
  private BillingManager billingManager;

  @Inject
  public VimojoStorePresenter(VimojoStoreView vimojoStoreView, Context context, BillingManager
      billingManager) {
    this.context = context;
    this.vimojoStoreView = vimojoStoreView;
    this.billingManager = billingManager;
  }

  public void initBilling() {
    if(!isSkuStoreItemPopulated()) {
      billingManager.initBillingClient(this);
    }
  }

  private boolean isSkuStoreItemPopulated() {
    return skuStoreItemList.size() != 0;
  }

  public void handleBillingManager() {
    // Start querying for in-app SKUs
    List<String> inAppSkus = billingManager.getSkus(INAPP);
    if(billingManager.isServiceConnected()) {
      vimojoStoreView.showProgressDialog();
      billingManager.querySkuDetailsAsync(INAPP, inAppSkus,
          new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
              if (responseCode == BillingClient.BillingResponse.OK && skuDetailsList != null) {
                // Repacking the result for an adapter
                for (SkuDetails details : skuDetailsList) {
                  skuStoreItemList.add(new SkuStoreData(details.getSku(),
                      details.getTitle(), details.getPrice(), details.getDescription(),
                      details.getType()));
                }
                if (skuStoreItemList.size() == 0) {
                  vimojoStoreView.showError(context
                          .getString(R.string.error_message_store_no_items));
                } else {
                  vimojoStoreView.showStoreList(skuStoreItemList);
                }
              }
              vimojoStoreView.hideProgressDialog();
            }
          });
    }
  }

  public void queryPurchaseHistory(){
    billingManager.queryPurchaseHistoryAsync(this);
  }

  public void purchaseItem(String skuId, String billingType) {
    billingManager.startPurchaseFlow(skuId, billingType, this);
  }

  public void destroyBillingManager() {
    billingManager.destroy();
  }

  @Override
  public void userCanceled() {
    // TODO: 01/11/2017 User cancel flow buying item

  }

  @Override
  public void purchasedItem(Purchase purchase) {
    for (SkuStoreData skuStoreData : skuStoreItemList) {
      if (skuStoreData.getSkuId().compareTo(purchase.getSku()) == 0) {
        skuStoreData.setPurchased(true);
        vimojoStoreView.updatePurchasedItem(skuStoreData);
      }
    }
  }

  @Override
  public void showError(int responseCode) {
    String message = context.getString(R.string.error_message_store_purchasing_items);
    vimojoStoreView.showError(message + " " + responseCode);
  }

  @Override
  public void billingClientSetupFinished() {
    if (billingManager.getBillingClientResponseCode() == BillingClient.BillingResponse.OK &&
        billingManager.isServiceConnected()) {
      handleBillingManager();
    } else {
      vimojoStoreView.hideProgressDialog();
      vimojoStoreView.showError(context.getString(R.string.error_message_store_not_available));
      Log.d(LOG_TAG, "billing client response " + billingManager.getBillingClientResponseCode());
    }
  }

  @Override
  public void historyPurchasedItems(List<Purchase> purchasesList) {
    List<SkuStoreData> storePurchasesList = new ArrayList<>();
    for(SkuStoreData skuStoreData : skuStoreItemList) {
      for(Purchase purchase: purchasesList) {
        if(skuStoreData.getSkuId().compareTo(purchase.getSku()) == 0){
          skuStoreData.setPurchased(true);
          //Log.d(LOG_TAG, "item purchased " + purchase.getSku());
          storePurchasesList.add(skuStoreData);
        }
      }
    }
    vimojoStoreView.updateHistoryPurchasedItems(storePurchasesList);
  }
}
