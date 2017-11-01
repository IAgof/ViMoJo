package com.videonasocialmedia.vimojo.billing;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.List;

import static com.android.billingclient.api.BillingClient.*;

/**
 * Created by Alvaro on 01/11/2017.
 */

public class BillingManager implements PurchasesUpdatedListener {

    private final String LOG_TAG = BillingManager.class.getCanonicalName();
    private final BillingUpdatesListener billingUpdatesListener;
    private final BillingClient billingClient;
    private Activity activity;
    private boolean isServiceConnected;
    private int billingClientResponseCode;

    public BillingManager(Activity activity, final BillingUpdatesListener updatesListener) {
        this.activity = activity;
        billingUpdatesListener = updatesListener;
        billingClient = newBuilder(activity).setListener(this).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingResponse int billingResponse) {
                if (billingResponse == BillingResponse.OK) {
                    Log.i(LOG_TAG, "onBillingSetupFinished() response: " + billingResponse);
                } else {
                    Log.w(LOG_TAG, "onBillingSetupFinished() error code: " + billingResponse);
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.w(LOG_TAG, "onBillingServiceDisconnected()");
            }
        });
    }

    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {

    }

    public void startPurchaseFlow(final String skuId, final String billingType) {
        // Specify a runnable to start when connection to Billing client is established
        Runnable executeOnConnectedService = new Runnable() {
            @Override
            public void run() {
                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                        .setType(billingType)
                        .setSku(skuId)
                        .build();
                billingClient.launchBillingFlow(activity, billingFlowParams);
            }
        };

        // If Billing client was disconnected, we retry 1 time
        // and if success, execute the query
        startServiceConnectionIfNeeded(executeOnConnectedService);
    }

    // executeServiceRequest
    private void startServiceConnectionIfNeeded(final Runnable executeOnSuccess) {
        if (billingClient.isReady()) {
            if (executeOnSuccess != null) {
                executeOnSuccess.run();
            }
        } else {
            // If the billing service disconnects, try to reconnect once.
            billingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(@BillingResponse int billingResponse) {
                    if (billingResponse == BillingResponse.OK) {
                        isServiceConnected = true;
                        Log.i(LOG_TAG, "onBillingSetupFinished() response: " + billingResponse);
                        if (executeOnSuccess != null) {
                            executeOnSuccess.run();
                        }
                    } else {
                        Log.w(LOG_TAG, "onBillingSetupFinished() error code: " + billingResponse);
                    }
                    billingClientResponseCode = billingResponse;
                }

                @Override
                public void onBillingServiceDisconnected() {
                    Log.w(LOG_TAG, "onBillingServiceDisconnected()");
                    isServiceConnected = false;
                }
            });
        }
    }

    public void destroy() {
        billingClient.endConnection();
    }

    public void querySkuDetailsAsync(@BillingClient.SkuType final String itemType,
                                     final List<String> skuList, final SkuDetailsResponseListener
                                             listener) {
        // Specify a runnable to start when connection to Billing client is established
        Runnable executeOnConnectedService = new Runnable() {
            @Override
            public void run() {
                SkuDetailsParams skuDetailsParams = SkuDetailsParams.newBuilder()
                        .setSkusList(skuList).setType(itemType).build();
                billingClient.querySkuDetailsAsync(skuDetailsParams,
                        new SkuDetailsResponseListener() {
                            @Override
                            public void onSkuDetailsResponse(int responseCode,
                                                             List<SkuDetails> skuDetailsList) {
                                listener.onSkuDetailsResponse(responseCode, skuDetailsList);
                            }
                        });
            }
        };

        // If Billing client was disconnected, we retry 1 time
        // and if success, execute the query
        startServiceConnectionIfNeeded(executeOnConnectedService);
    }
}
