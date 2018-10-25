package com.videonasocialmedia.vimojo.utils.tracker;

/**
 * Created by jliarte on 27/08/18.
 */

import android.content.Context;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.utils.AnalyticsConstants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * TrackerIntegration class for Mixpanel event tracking.
 */
public class MixpanelTracker extends UserEventTracker.TrackerIntegration<MixpanelAPI> {
  public static final UserEventTracker.TrackerIntegration.Factory FACTORY = userEventTracker -> {
    Context context = userEventTracker.getApplication();
    return new MixpanelTracker(context);
  };
  private static final String ANDROID_PUSH_SENDER_ID = "783686583047"; // TODO(jliarte): 28/08/18 move to gradle?
  private final MixpanelAPI mixpanel;

  private MixpanelTracker(Context context) {
    this.mixpanel = MixpanelAPI.getInstance(context, BuildConfig.MIXPANEL_TOKEN);
    if (mixpanel != null) {
      mixpanel.getPeople().identify(mixpanel.getPeople().getDistinctId());
      mixpanel.getPeople().initPushHandling(ANDROID_PUSH_SENDER_ID);
    }
  }

  @Override
  public void identify(String id) {
    mixpanel.identify(id);
    mixpanel.getPeople().identify(id);
  }

  @Override
  public void aliasUser(String idAlias) {
    mixpanel.alias(idAlias, mixpanel.getDistinctId());
  }

  @Override
  public void track(UserEventTracker.Event event) {
    mixpanel.track(event.getName(), event.getProperties());
  }

  @Override
  public void setUserProperties(JSONObject userProperties) {
    mixpanel.getPeople().set(userProperties);
  }

  @Override
  public void setUserProperties(String propertyName, String propertyValue) {
    mixpanel.getPeople().set(propertyName, propertyValue);
  }

  @Override
  public void setUserProperties(String propertyName, boolean propertyValue) {
    mixpanel.getPeople().set(propertyName, propertyValue);
  }

  @Override
  public void setUserPropertiesOnce(JSONObject userProperties) {
    mixpanel.getPeople().setOnce(userProperties);
  }

  @Override
  public void setUserPropertiesOnce(String propertyName, String propertyValue) {
    mixpanel.getPeople().setOnce(propertyName, propertyValue);
  }

  @Override
  public void registerSuperProperties(JSONObject superProperties) {
    mixpanel.registerSuperProperties(superProperties);
  }

  @Override
  public void registerSuperPropertiesOnce(JSONObject superProperties) {
    mixpanel.registerSuperPropertiesOnce(superProperties);
  }

  @Override
  public int getSuperProperty(String propertyName, int defValue) {
    try {
      return mixpanel.getSuperProperties().getInt(propertyName);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return defValue;
  }

  @Override
  public String getSuperProperty(String propertyName, String defValue) {
    try {
      return mixpanel.getSuperProperties().getString(propertyName);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return defValue;
  }

  @Override
  public void incrementUserProperty(String propertyName, int increment) {
    mixpanel.getPeople().increment(propertyName, increment);
  }

  @Override
  public void flush() {
    mixpanel.flush();
  }

  @Override
  public void startView(Class<? extends VimojoActivity> activity) {
    mixpanel.timeEvent(AnalyticsConstants.TIME_IN_ACTIVITY);
  }

  @Override
  public void endView(Class<? extends VimojoActivity> activity) {
    JSONObject activityProperties = new JSONObject();
    try {
      activityProperties.put(AnalyticsConstants.ACTIVITY, activity.getSimpleName());
      mixpanel.track(AnalyticsConstants.TIME_IN_ACTIVITY, activityProperties);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
}
