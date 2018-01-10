package com.videonasocialmedia.vimojo.store.billing;

import android.app.Activity;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.util.List;

/**
 * Created by jliarte on 7/11/17.
 */
public class PlayStoreBillingDelegate implements BillingConnectionListener,
        BillingHistoryPurchaseListener {
  private static final String LOG_TAG = PlayStoreBillingDelegate.class.getSimpleName();
  private final BillingManager billingManager;
  private final BillingDelegateView billingDelegateView;
  private boolean darkThemePurchased = false;
  private boolean watermarkPurchased = false;

  public PlayStoreBillingDelegate(BillingManager billingManager,
                                  BillingDelegateView billingDelegateView) {
    this.billingManager = billingManager;
    this.billingDelegateView = billingDelegateView;
  }

  public void initBilling(Activity activity) {
    billingManager.initBillingClient(activity, this);
  }

  @Override
  public void billingClientSetupFinished() {
    if (billingManager.getBillingClientResponseCode() == BillingClient.BillingResponse.OK &&
            billingManager.isServiceConnected()) {
      checkPurchasedItems();
    } else {
      Log.d(LOG_TAG, "billing client response "
              + billingManager.getBillingClientResponseCode());
      //editorActivityView.showMessage(R.string.error_message_store_not_available);
    }
  }

  private void checkPurchasedItems() {
    Log.d(LOG_TAG, "checkPurchasedItems ");
    billingManager.queryPurchaseHistoryAsync(this);
  }

  @Override
  public void historyPurchasedItems(List<Purchase> purchasesList) {
    Log.d(LOG_TAG, "historyPurchasedItems " + purchasesList.size());
    for (Purchase purchase : purchasesList) {
      if (purchase.getSku().compareTo(Constants.IN_APP_BILLING_ITEM_DARK_THEME) == 0) {
        darkThemePurchased = true;
      }
      if (purchase.getSku().compareTo(Constants.IN_APP_BILLING_ITEM_WATERMARK) == 0) {
        watermarkPurchased = true;
      }
    }
    billingDelegateView.itemDarkThemePurchased(darkThemePurchased);
    billingDelegateView.itemWatermarkPurchased(watermarkPurchased);
  }

  public interface BillingDelegateView {
    void itemDarkThemePurchased(boolean purchased);

    void itemWatermarkPurchased(boolean purchased);
  }
}
