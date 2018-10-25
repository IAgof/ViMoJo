package com.videonasocialmedia.vimojo.featuresToggles.domain.model;

/**
 * Created by jliarte on 4/09/18.
 */

import com.videonasocialmedia.vimojo.repository.model.IdentificableModel;

/**
 * Model representing feature flags for configuring app features or user enabled features, eg.:
 * based on user subscribed plan
 */
public class FeatureToggle extends IdentificableModel<String> {
  public String name;
  public String description;
  public boolean enabled;

  public FeatureToggle(String name, boolean enabled) {
    this.name = name;
    this.enabled = enabled;
  }

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

  @Override
  public String getId() {
    return getName();
  }
}
