package com.videonasocialmedia.vimojo.shop.model;



public class Shop {
  private String title;
  private String description;
  private String price;

  public Shop(String title, String description, String price) {
    this.title = title;
    this.description = description;
    this.price = price;
  }

  public String getDescription() {
    return description;
  }

  public String getTitle() {
    return title;
  }

  public String getPrice() {
    return price;
  }

}
