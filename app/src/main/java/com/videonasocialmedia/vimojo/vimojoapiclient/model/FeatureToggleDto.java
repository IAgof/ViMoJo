/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.vimojoapiclient.model;

/**
 * Created by alvaro on 30/8/18.
 */

import com.google.gson.annotations.SerializedName;

/**
 * Model class for feature toggles vimojo API calls.
 */
public class FeatureToggleDto {
  @SerializedName("name") public String name;
  @SerializedName("description") public String description;
  @SerializedName("enabled") public boolean enabled;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public FeatureToggleDto() {

  }

  @Override
  public String toString() {
    return "UserFeaturesDto{"
        + "name='"
        + name
        + '\''
        + ", description='"
        + description
        + '\''
        + ", enabled='"
        + enabled
        +
        '}';
  }

}
