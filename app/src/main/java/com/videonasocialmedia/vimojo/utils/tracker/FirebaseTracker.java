package com.videonasocialmedia.vimojo.utils.tracker;

/**
 * Created by jliarte on 27/08/18.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

/**
 * TrackerIntegration class for Firebase google analytics.
 */
public class FirebaseTracker extends UserEventTracker.TrackerIntegration<FirebaseAnalytics> {
  public static final UserEventTracker.TrackerIntegration.Factory FACTORY = userEventTracker -> {
    Context context = userEventTracker.getApplication();
    return new FirebaseTracker(context);
  };
  private final FirebaseAnalytics firebaseAnalytics;
  private SharedPreferences trackingSuperProperties;
  private SharedPreferences trackingUserProperties;

  private FirebaseTracker(Context context) {
    this.firebaseAnalytics = FirebaseAnalytics.getInstance(context);
    initializePreferences(context);
  }

  private void initializePreferences(Context context) {
    trackingSuperProperties = context.getSharedPreferences(
            ConfigPreferences.SETTINGS_SHARED_PREFERENCES_TRACKING_SUPER_PROPERTIES,
            Context.MODE_PRIVATE);
    trackingUserProperties = context.getSharedPreferences(
            ConfigPreferences.SETTINGS_SHARED_PREFERENCES_TRACKING_USER_PROPERTIES,
            Context.MODE_PRIVATE);
  }


  @Override
  public void identify(String id) {
    firebaseAnalytics.setUserId(id);
  }

  @Override
  public void track(UserEventTracker.Event event) {
    Bundle eventBundle = fromJson(event.getProperties());
    appendSuperProperties(eventBundle);
    // TODO(jliarte): 28/08/18 map event and params to firebase standards
    String eventName = event.getName().replace(" ", "_"); // (jliarte): 28/08/18 Name must consist of letters, digits or _ (underscores).
    firebaseAnalytics.logEvent(eventName, eventBundle);
  }

  private void appendSuperProperties(Bundle eventBundle) {
    Map<String, ?> superProperties = trackingSuperProperties.getAll();
    for (Map.Entry<String, ?> entry : superProperties.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      appendSuperProperty(eventBundle, key, value);
    }
  }

  private void appendSuperProperty(Bundle eventBundle, String key, Object value) {
    if (value instanceof String) {
      eventBundle.putString(key, (String) value);
    } else if (value instanceof Integer) {
      eventBundle.putInt(key, (Integer) value);
    } else if (value instanceof Boolean) {
      eventBundle.putBoolean(key, (Boolean) value);
    } else {
      eventBundle.putString(key, String.valueOf(value));
    }
  }

  @Override
  public void setUserProperties(JSONObject userProperties) {
    for (Iterator<String> it = userProperties.keys(); it.hasNext(); ) {
      String key = it.next();
      try {
        Object value = userProperties.get(key);
        setLocalUserProperty(key, value);
        firebaseAnalytics.setUserProperty(key, String.valueOf(value));
      } catch (JSONException e) {
        // TODO(jliarte): 28/08/18 check this error
        e.printStackTrace();
      }
    }
  }

  private void setLocalUserProperty(String key, Object value) {
    if (value instanceof String) {
      trackingUserProperties.edit().putString(key, (String) value).apply();
    } else {
      trackingUserProperties.edit().putString(key, String.valueOf(value)).apply();
    }
  }

  private void setLocalUserPropertyOnce(String key, Object value) {
    if (!isLocalUserPropertySet(key)) {
      setLocalUserProperty(key, value);
    }
  }

  private boolean isLocalUserPropertySet(String propertyName) {
    Map<String, ?> props = trackingUserProperties.getAll();
    return props.containsKey(propertyName);
  }

  @Override
  public void setUserProperties(String propertyName, String propertyValue) {
    setLocalUserProperty(propertyName, propertyValue);
    firebaseAnalytics.setUserProperty(propertyName, propertyValue);
  }

  @Override
  public void setUserProperties(String propertyName, boolean propertyValue) {
    setLocalUserProperty(propertyName, propertyValue);
    firebaseAnalytics.setUserProperty(propertyName, String.valueOf(propertyValue));
  }

  @Override
  public void setUserPropertiesOnce(JSONObject userProperties) {
    for (Iterator<String> it = userProperties.keys(); it.hasNext(); ) {
      String key = it.next();
      try {
        Object value = userProperties.get(key);
        setLocalUserPropertyOnce(key, value);
        setUserPropertiesOnce(key, String.valueOf(value));
      } catch (JSONException e) {
        // TODO(jliarte): 28/08/18 check this error
        e.printStackTrace();
      }
    }
  }

  @Override
  public void setUserPropertiesOnce(String propertyName, String propertyValue) {
    boolean propertyIsSet = isLocalUserPropertySet(propertyName);
    if (!propertyIsSet) {
      firebaseAnalytics.setUserProperty(propertyName, propertyValue);
    }
  }

  @Override
  public void registerSuperProperties(JSONObject superProperties) {
    for (Iterator<String> it = superProperties.keys(); it.hasNext(); ) {
      String key = it.next();
      try {
        Object value = superProperties.get(key);
        registerSuperProperty(key, value);
      } catch (JSONException e) {
        // TODO(jliarte): 28/08/18 check this error
        e.printStackTrace();
      }
    }
  }

  private void registerSuperProperty(String key, Object value) {
    if (value instanceof String) {
      trackingSuperProperties.edit().putString(key, (String) value).apply();
    } else if (value instanceof Integer) {
      trackingSuperProperties.edit().putInt(key, (Integer) value).apply();
    } else if (value instanceof Boolean) {
      trackingSuperProperties.edit().putBoolean(key, (Boolean) value).apply();
    } else {
      trackingSuperProperties.edit().putString(key, String.valueOf(value)).apply();
    }
  }

  @Override
  public void registerSuperPropertiesOnce(JSONObject superProperties) {
    for (Iterator<String> it = superProperties.keys(); it.hasNext(); ) {
      String key = it.next();
      try {
        Object value = superProperties.get(key);
        registerSuperPropertyOnce(key, value);
      } catch (JSONException e) {
        // TODO(jliarte): 28/08/18 check this error
        e.printStackTrace();
      }
    }
  }

  private void registerSuperPropertyOnce(String key, Object value) {
    if (!isSuperPropertySet(key)) {
      registerSuperProperty(key, value);
    }
  }

  private boolean isSuperPropertySet(String propertyName) {
    Map<String, ?> props = trackingSuperProperties.getAll();
    return props.containsKey(propertyName);
  }

  @Override
  public int getSuperProperty(String propertyName, int defValue) {
    return trackingSuperProperties.getInt(propertyName, defValue);
  }

  @Override
  public String getSuperProperty(String propertyName, String defValue) {
    return trackingSuperProperties.getString(propertyName, defValue);
  }

  @Override
  public void incrementUserProperty(String propertyName, int increment) {
    int value = getSuperProperty(propertyName, 0);
    registerSuperProperty(propertyName, value + increment);
  }

  @Override
  public void flush() {
    // (jliarte): 28/08/18 firebase doesn't need/support flush
  }

  @Override
  public void startView(Class<? extends VimojoActivity> activity) {
    // (jliarte): 28/08/18 null implementation by now as firebase analytics automatically track activity views
  }

  @Override
  public void endView(Class<? extends VimojoActivity> activity) {
    // (jliarte): 28/08/18 null implementation by now as firebase analytics automatically track activity views
  }


  /** Convert a JSON object to a Bundle that can be passed as the extras of
   * an Intent. It passes each number as a double, and everything else as a
   * String, arrays of those two are also supported. */
  public static Bundle fromJson(JSONObject s) {
    Bundle bundle = new Bundle();

    for (Iterator<String> it = s.keys(); it.hasNext(); ) {
      String key = it.next();
      JSONArray arr = s.optJSONArray(key);
      Double num = s.optDouble(key);
      String str = s.optString(key);

      if (arr != null && arr.length() <= 0)
        bundle.putStringArray(key, new String[]{});

      else if (arr != null && !Double.isNaN(arr.optDouble(0))) {
        double[] newarr = new double[arr.length()];
        for (int i=0; i<arr.length(); i++)
          newarr[i] = arr.optDouble(i);
        bundle.putDoubleArray(key, newarr);
      }

      else if (arr != null && arr.optString(0) != null) {
        String[] newarr = new String[arr.length()];
        for (int i=0; i<arr.length(); i++)
          newarr[i] = arr.optString(i);
        bundle.putStringArray(key, newarr);
      }

      else if (!num.isNaN())
        bundle.putDouble(key, num);

      else if (str != null)
        bundle.putString(key, str);

      else
        System.err.println("unable to transform json to bundle " + key);
    }

    return bundle;
  }

}
