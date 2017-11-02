package com.videonasocialmedia.vimojo.shop.presentation.mvp.presenters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.shop.billing.BillingHistoryPurchaseListener;
import com.videonasocialmedia.vimojo.shop.billing.BillingManager;
import com.videonasocialmedia.vimojo.shop.billing.BillingUpdatesPurchaseListener;
import com.videonasocialmedia.vimojo.shop.model.SkuShopData;
import com.videonasocialmedia.vimojo.shop.presentation.mvp.views.ShopListView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.android.billingclient.api.BillingClient.SkuType.INAPP;

public class ShopListPresenter implements BillingUpdatesPurchaseListener,
    BillingHistoryPurchaseListener {

  private static final String LOG_TAG = ShopListPresenter.class.getCanonicalName();
  private Context context;
  private List<SkuShopData> skuShopItemList = new ArrayList<>();
  private ShopListView shopListView;
  private BillingManager billingManager;

  @Inject
  public ShopListPresenter(ShopListView shopListView, Context context) {
    this.context = context;
    this.shopListView = shopListView;
  }

  public void createBillingManager(){
    this.billingManager = new BillingManager((Activity) context, this, this);
  }

  public void handleBillingManager() {
    // Start querying for in-app SKUs
    List<String> inAppSkus = billingManager.getSkus(INAPP);
    if(billingManager.isServiceConnected()) {
      shopListView.showProgressDialog();
      billingManager.querySkuDetailsAsync(INAPP, inAppSkus,
          new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
              if (responseCode == BillingClient.BillingResponse.OK && skuDetailsList != null) {
                // Repacking the result for an adapter
                for (SkuDetails details : skuDetailsList) {
                  Log.i(LOG_TAG, "Found sku: " + details);
                  skuShopItemList.add(new SkuShopData(details.getSku(),
                      details.getTitle(), details.getPrice(), details.getDescription(),
                      details.getType()));
                }
                if (skuShopItemList.size() == 0) {
                  shopListView.showError(context.getString(R.string.error_message_shop_no_items));
                } else {
                  shopListView.showShopList(skuShopItemList);
                }
              }
              shopListView.hideProgressDialog();
            }
          });
    }

    billingManager.queryPurchaseHistoryAsync();
  }

  public void getShopList() {
    if (skuShopItemList.size() != 0) {
      shopListView.showShopList(skuShopItemList);
    }
  }

  public void purchaseItem(String skuId, String billingType) {
    billingManager.startPurchaseFlow(skuId, billingType);
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
    for (SkuShopData skuShopData : skuShopItemList) {
      if (purchase.getSku() == skuShopData.getSkuId()) {
        skuShopData.setPurchased(true);
        shopListView.updatePurchasedItem(skuShopData);
      }
    }
  }

  @Override
  public void showError(int responseCode) {
    String message = context.getString(R.string.error_message_shop_purchasing_items);
    shopListView.showError(message + " " + responseCode);
  }

  @Override
  public void billingClientSetupFinished() {
    if (billingManager.getBillingClientResponseCode() == BillingClient.BillingResponse.OK &&
        billingManager.isServiceConnected()) {
      return;
    } else {
      shopListView.hideProgressDialog();
      shopListView.showError(context.getString(R.string.error_message_shop_not_available));
      Log.d(LOG_TAG, "billing client response " + billingManager.getBillingClientResponseCode());
    }
  }

  @Override
  public void historyPurchasedItems(List<Purchase> purchasesList) {
    List<SkuShopData> shopPurchasesList = new ArrayList<>();
    for(SkuShopData skuShopData: skuShopItemList) {
      for(Purchase purchase: purchasesList) {
        if(skuShopData.getSkuId().compareTo(purchase.getSku()) == 0){
          skuShopData.setPurchased(true);
          Log.d(LOG_TAG, "item purchased " + purchase.getSku());
          shopPurchasesList.add(skuShopData);
        }
      }
    }
    shopListView.updateHistoryPurchasedItems(shopPurchasesList);
  }
}
