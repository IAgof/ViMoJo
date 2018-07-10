/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.vimojoapiclient.model;

/**
 * Created by alvaro on 21/6/18.
 */

public class AssetDto {
  private String name;
  private String type;
  private String hash;
  private String date;

  public AssetDto(String name, String type, String hash, String date) {
    this.name = name;
    this.type = type;
    this.hash = hash;
    this.date = date;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public String getHash() {
    return hash;
  }

  public String getDate() {
    return date;
  }



}
