package com.videonasocialmedia.vimojo.billing;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;

import java.util.List;

/**
 * Created by Alvaro on 01/11/2017.
 */

public interface BillingUpdatesListener {
    void onBillingClientSetupFinished();
    void onConsumeFinished(String token, @BillingClient.BillingResponse int result);
    void onPurchasesUpdated(List<Purchase> purchases);
}
