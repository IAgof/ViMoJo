package com.videonasocialmedia.vimojo.store.billing;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.android.billingclient.api.BillingClient.*;

/**
 * Created by Alvaro on 01/11/2017.
 */

public class BillingManager implements PurchasesUpdatedListener {

  private final String LOG_TAG = BillingManager.class.getCanonicalName();
  private BillingClient billingClient;
  private Activity activity;
  private boolean isServiceConnected = false;
  private int billingClientResponseCode = -1;
  private static final HashMap<String, List<String>> SKUS;

  static {
    SKUS = new HashMap<>();
    SKUS.put(BillingClient.SkuType.INAPP, Arrays.asList(Constants.IN_APP_BILLING_ITEM_WATERMARK,
        Constants.IN_APP_BILLING_ITEM_DARK_THEME));
    // Future use, app model subscription
    // SKUS.put(BillingClient.SkuType.SUBS, Arrays.asList("monthly", "yearly"));
  }

  private BillingUpdatesPurchaseListener billingUpdatesPurchaseListener;

  private List<Purchase> purchases = new ArrayList<>();

  public void initBillingClient(Activity activity,
                                final BillingConnectionListener billingConnectionListener) {
    this.activity = activity;
    activity.runOnUiThread(() -> {
      billingClient = newBuilder(activity).setListener(this).build();
      billingClient.startConnection(new BillingClientStateListener() {
        @Override
        public void onBillingSetupFinished(@BillingResponse int billingResponse) {
          if (billingResponse == BillingResponse.OK) {
            isServiceConnected = true;
            Log.i(LOG_TAG, "onBillingSetupFinished() response: " + billingResponse);
          } else {
            Log.w(LOG_TAG, "onBillingSetupFinished() error code: " + billingResponse);
          }
          billingClientResponseCode = billingResponse;
          billingConnectionListener.billingClientSetupFinished();
        }

        @Override
        public void onBillingServiceDisconnected() {
          Log.w(LOG_TAG, "onBillingServiceDisconnected()");
          isServiceConnected = false;
          billingConnectionListener.billingClientSetupFinished();
        }
      });
    });
  }

  @Override
  public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
    if (responseCode == BillingResponse.OK
        && purchases != null) {
      for (Purchase purchase : purchases) {
        handlePurchase(purchase);
        if(billingUpdatesPurchaseListener != null)
          billingUpdatesPurchaseListener.purchasedItem(purchase);
      }

    } else if (responseCode == BillingResponse.USER_CANCELED) {
      // Handle an error caused by a user cancelling the purchase flow.
      if(billingUpdatesPurchaseListener != null)
        billingUpdatesPurchaseListener.userCanceled();
    } else {
      // Handle any other error codes.
      if(billingUpdatesPurchaseListener != null)
        billingUpdatesPurchaseListener.showError(responseCode);
    }
  }

  private void handlePurchase(Purchase purchase) {
    // TODO:(alvaro.martinez) 2/11/17 Manage new purchase. Added to sharedPreferences?, tracking?
    // Perform purchase validation on your own secure server.
    if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
      // Skip a purchase if the signature isn't valid.
      return;
    }
    purchases.add(purchase);
  }

  private boolean verifyValidSignature(String originalJson, String signature) {
    return false;
  }

  // TODO(jliarte): 10/07/18 inject activity as param and remove field
  public void startPurchaseFlow(final String skuId, final String billingType,
                                BillingUpdatesPurchaseListener
                                    billingUpdatesPurchaseListener) {

    this.billingUpdatesPurchaseListener = billingUpdatesPurchaseListener;
    Log.e(LOG_TAG, "startPurchaseflow id " + skuId + " type " + billingType);
    // Specify a runnable to start when connection to Billing client is established
    Runnable executeOnConnectedService = new Runnable() {
      @Override
      public void run() {
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
            .setType(billingType)
            .setSku(skuId)
            .build();
        Log.e(LOG_TAG, "executeOnConnectedService id " + skuId + " type " + billingType);
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
          //billingConnectionListener.billingClientSetupFinished();
        }

        @Override
        public void onBillingServiceDisconnected() {
          Log.w(LOG_TAG, "onBillingServiceDisconnected()");
          isServiceConnected = false;
          //billingConnectionListener.billingClientSetupFinished();
        }
      });
    }
  }

  public void destroy() {
    billingClient.endConnection();
    activity = null;
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

  public void queryPurchaseHistoryAsync(final BillingHistoryPurchaseListener
                                            billingHistoryPurchaseListener) {

    // Specify a runnable to start when connection to Billing client is established
    Runnable executeOnConnectedService = new Runnable() {
      @Override
      public void run() {
        billingClient.queryPurchaseHistoryAsync(SkuType.INAPP,
            new PurchaseHistoryResponseListener() {
              @Override
              public void onPurchaseHistoryResponse(@BillingResponse int responseCode,
                                                    List<Purchase> purchasesList) {
                if (responseCode == BillingResponse.OK
                    && purchasesList != null) {
                  billingHistoryPurchaseListener.historyPurchasedItems(purchasesList);
                }
              }
            });
      }
    };

    // If Billing client was disconnected, we retry 1 time
    // and if success, execute the query
    startServiceConnectionIfNeeded(executeOnConnectedService);
  }

  public List<String> getSkus(@BillingClient.SkuType String type) {
    return SKUS.get(type);
  }

  public boolean isServiceConnected() {
    return isServiceConnected;
  }

  public int getBillingClientResponseCode() {
    return billingClientResponseCode;
  }

  public List<Purchase> getPurchases() {
    return purchases;
  }

}
