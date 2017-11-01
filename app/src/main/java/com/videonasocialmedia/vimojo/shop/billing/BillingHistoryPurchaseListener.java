package com.videonasocialmedia.vimojo.shop.billing;

import com.android.billingclient.api.Purchase;

import java.util.List;

/**
 * Created by Alvaro on 02/11/2017.
 */

public interface BillingHistoryPurchaseListener {
    void historyPurchasedItems(List<Purchase> purchasesList);
}
