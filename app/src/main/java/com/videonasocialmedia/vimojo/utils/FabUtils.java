package com.videonasocialmedia.vimojo.utils;

import android.graphics.drawable.Drawable;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.videonasocialmedia.vimojo.main.VimojoApplication;

/**
 * Created by ruth on 29/11/16.
 */

public class FabUtils{

    public static FloatingActionButton createNewFab(int id, int icon, int color) {
      FloatingActionButton floatingActionButton = new FloatingActionButton(VimojoApplication.getAppContext());
      floatingActionButton.setIcon(icon);
      floatingActionButton.setId(id);
      floatingActionButton.setColorNormalResId(color);
      floatingActionButton.setColorPressedResId(color);

      return floatingActionButton;
    }

  public static FloatingActionButton createNewFabMini(int id, int icon, int color) {
    FloatingActionButton floatingActionButton = createNewFab(id, icon,color);
    floatingActionButton.setSize(FloatingActionButton.SIZE_MINI);
    return floatingActionButton;
  }
}
