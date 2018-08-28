package com.videonasocialmedia.vimojo.utils.tracker;

/**
 * Created by jliarte on 27/08/18.
 */

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * TrackerIntegration class for Firebase google analytics.
 */
public class FirebaseTracker extends UserEventTracker.TrackerIntegration<FirebaseAnalytics> {
  public static final UserEventTracker.TrackerIntegration.Factory FACTORY = userEventTracker -> {
    Context context = userEventTracker.getApplication();
    return new FirebaseTracker(context);
  };
  private final FirebaseAnalytics firebaseAnalytics;

  private FirebaseTracker(Context context) {
    this.firebaseAnalytics = FirebaseAnalytics.getInstance(context);
  }

  @Override
  public void identify(String id) {
    firebaseAnalytics.setUserId(id);
  }

  @Override
  public void track(UserEventTracker.Event event) {
    Bundle eventBundle = fromJson(event.getProperties());
    // TODO(jliarte): 28/08/18 add "super" properties?
    // TODO(jliarte): 28/08/18 map event and params to firebase standards
    String eventName = event.getName().replace(" ", "_"); // (jliarte): 28/08/18 Name must consist of letters, digits or _ (underscores).
    firebaseAnalytics.logEvent(eventName, eventBundle);
  }

  @Override
  public void setUserProperties(JSONObject userProperties) {
    for (Iterator<String> it = userProperties.keys(); it.hasNext(); ) {
      String key = it.next();
      try {
        Object value = userProperties.get(key);
        firebaseAnalytics.setUserProperty(key, String.valueOf(value));
      } catch (JSONException e) {
        // TODO(jliarte): 28/08/18 check this error
        e.printStackTrace();
      }
    }
  }

  @Override
  public void setUserProperties(String propertyName, String propertyValue) {
    firebaseAnalytics.setUserProperty(propertyName, propertyValue);
  }

  @Override
  public void setUserPropertiesOnce(JSONObject userProperties) {
    for (Iterator<String> it = userProperties.keys(); it.hasNext(); ) {
      String key = it.next();
      try {
        Object value = userProperties.get(key);
        setUserPropertiesOnce(key, String.valueOf(value));
      } catch (JSONException e) {
        // TODO(jliarte): 28/08/18 check this error
        e.printStackTrace();
      }
    }
  }

  @Override
  public void setUserPropertiesOnce(String propertyName, String propertyValue) {
    // TODO(jliarte): 28/08/18 check the property is not already set
    boolean propertyIsSet = false;
    if (!propertyIsSet) {
      firebaseAnalytics.setUserProperty(propertyName, propertyValue);
    }
  }

  @Override
  public void registerSuperProperties(JSONObject superProperties) {
    // TODO(jliarte): 28/08/18 this should save super properties in a shared pref archive to retrieve them before sending an event
  }

  @Override
  public void registerSuperPropertiesOnce(JSONObject superProperties) {
    // TODO(jliarte): 28/08/18 this should save super properties in a shared pref archive - if, not already set - to retrieve them before sending an event
  }

  @Override
  public int getSuperProperty(String propertyName, int defValue) {
    // TODO(jliarte): 28/08/18 null implementation as firebase dont support super properties
    return 0;
  }

  @Override
  public void incrementUserProperty(String propertyName, int increment) {
    // TODO(jliarte): 28/08/18 should retrieve current value from shared prefs, increment and set
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
