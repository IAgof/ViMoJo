package com.videonasocialmedia.vimojo.shop.model;

/**
 * A model for SkusAdapter's row which holds all the data to render UI
 */
public class SkuShopData {
  private final String skuId, title, price, description, billingType;

  public SkuShopData(String skuId, String title, String price, String description, String type) {
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
}
