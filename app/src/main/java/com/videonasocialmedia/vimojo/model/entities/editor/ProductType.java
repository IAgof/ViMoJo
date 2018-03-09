/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.model.entities.editor;

import android.support.annotation.NonNull;

/**
 * Created by alvaro on 6/3/18.
 * Product type.
 * Determine type of video product in project.
 * Id por position, name enum value
 *
 */

public class ProductType implements Comparable<ProductType>{

  private int position;
  private String name;

  public ProductType(int position, String name) {
    this.position = position;
    this.name = name;
  }

  public int getPosition() {
    return position;
  }

  public String getName() {
    return name;
  }


  @Override
  public int compareTo(@NonNull ProductType o) {
    return getPosition() - o.getPosition();
  }
}
