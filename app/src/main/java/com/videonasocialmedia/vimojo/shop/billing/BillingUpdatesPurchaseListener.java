package com.videonasocialmedia.vimojo.shop.billing;

import com.android.billingclient.api.Purchase;

import java.util.List;

/**
 * Created by Alvaro on 01/11/2017.
 */

public interface BillingUpdatesPurchaseListener {

    void userCanceled();

    void purchasedItem(Purchase purchase);

    void showError(int responseCode);
}
