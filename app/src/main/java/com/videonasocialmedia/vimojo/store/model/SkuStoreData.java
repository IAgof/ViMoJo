package com.videonasocialmedia.vimojo.store.model;

/**
 * A model for SkusAdapter's row which holds all the data to render UI
 */
public class SkuStoreData {
  private final String skuId, title, price, description, billingType;
  private boolean isPurchased = false;

  public SkuStoreData(String skuId, String title, String price, String description, String type) {
    this.skuId = skuId;
    this.title = title;
    this.price = price;
    this.description = description;
    this.billingType = type;
  }

  public String getSkuId() {
    return skuId;
  }

  public String getTitle() {
    return title;
  }

  public String getPrice() {
    return price;
  }

  public String getDescription() {
    return description;
  }

  public String getBillingType() {
    return billingType;
  }

  public boolean isPurchased() {
    return isPurchased;
  }

  public void setPurchased(boolean purchased) {
    isPurchased = purchased;
  }
}
