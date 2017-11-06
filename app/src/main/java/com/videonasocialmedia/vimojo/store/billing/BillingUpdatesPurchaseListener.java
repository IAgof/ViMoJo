package com.videonasocialmedia.vimojo.store.billing;

import com.android.billingclient.api.Purchase;

/**
 * Created by Alvaro on 01/11/2017.
 */

public interface BillingUpdatesPurchaseListener {

  void userCanceled();

  void purchasedItem(Purchase purchase);

  void showError(int responseCode);
}
