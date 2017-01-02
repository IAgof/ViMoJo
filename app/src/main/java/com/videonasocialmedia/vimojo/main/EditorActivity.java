package com.videonasocialmedia.vimojo.main;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.views.activity.GalleryProjectListActivity;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.EditorPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.views.EditorActivityView;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.GalleryActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.SettingsActivity;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 */

public abstract class EditorActivity extends VimojoActivity implements EditorActivityView {

  protected SharedPreferences sharedPreferences;
  protected UserEventTracker userEventTracker;
  private EditorPresenter editorPresenter;
  private AlertDialog alertDialog;

  @Bind(R.id.edit_activity_drawer_layout)
  DrawerLayout drawerLayout;
  @Bind(R.id.navigator_view)
  NavigationView navigationView;
  @Bind(R.id.container_navigator)
  LinearLayout navigator;
  @Bind(R.id.fab_edit_room)
  FloatingActionsMenu fabMenu;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.editor_activity);
    ButterKnife.bind(this);
    this.userEventTracker = UserEventTracker.getInstance(MixpanelAPI.getInstance(this, BuildConfig.MIXPANEL_TOKEN));
    sharedPreferences = getSharedPreferences(ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
        Context.MODE_PRIVATE);
    editorPresenter = new EditorPresenter(this, sharedPreferences, VimojoApplication.getAppContext());
  }


  @Override
  public void setContentView(int layout) {
    super.setContentView(R.layout.editor_activity);
    setToolbar();
  }

  private void setToolbar() {
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    ActionBar ab = getSupportActionBar();
    if (ab != null) {
      ab.setDisplayShowTitleEnabled(false);
      ab.setHomeAsUpIndicator(R.drawable.ic_nav_menu);
      ab.setDisplayHomeAsUpEnabled(true);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (navigationView != null) {
      setupDrawerContent(navigationView);
      editorPresenter.getPreferenceUserName();
      editorPresenter.getPreferenceEmail();
    }

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
      getMenuInflater().inflate(R.menu.menu_editor_activity, menu);
      return true;
    }
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.

    switch (item.getItemId()) {
      case R.id.action_settings_edit_options:
        navigateTo(SettingsActivity.class);
        return true;
      case R.id.action_settings_edit_gallery:
        navigateTo(GalleryActivity.class);
        return true;
      case android.R.id.home:
        drawerLayout.openDrawer(GravityCompat.START);
        return true;
      default:

    }
    return super.onOptionsItemSelected(item);
  }

  protected void inflateLinearLayout(int linearLayoutContainer, int layoutToAdd) {
    LinearLayout contentLayoutEditActivity = (LinearLayout) findViewById(linearLayoutContainer);
    getLayoutInflater().inflate(layoutToAdd, contentLayoutEditActivity);
  }

  private void setupDrawerContent(NavigationView navigationView) {

    navigationView.setNavigationItemSelectedListener(
        new NavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(MenuItem menuItem) {

            switch (menuItem.getItemId()) {
              case R.id.menu_navview_gallery_projects:
                drawerLayout.closeDrawers();
                navigateTo(GalleryProjectListActivity.class);
                return false;
              case R.id.menu_navview_delete_clip:
                createDialog(R.id.menu_navview_delete_clip);
                return false;
              case R.id.menu_navview_mail:
                createDialog(R.id.menu_navview_mail);
                return false;
              case R.id.menu_navview_username:
                createDialog(R.id.menu_navview_username);
              default:
                return false;
            }
          }
        });
  }

  private void createDialog(final int resourceItemMenuId) {

    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.VideonaAlertDialog);

    if (resourceItemMenuId == R.id.menu_navview_delete_clip)
      builder.setMessage(getResources().getString(R.string.dialog_message_clean_project));
    if (resourceItemMenuId == R.id.menu_navview_mail)
      builder.setMessage(getResources().getString(R.string.dialog_change_email));
    if (resourceItemMenuId == R.id.menu_navview_username)
      builder.setMessage(getResources().getString(R.string.dialog_change_user_name));


    final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        switch (which) {
          case DialogInterface.BUTTON_POSITIVE:
            drawerLayout.closeDrawers();
            if(resourceItemMenuId == R.id.menu_navview_delete_clip)
              editorPresenter.resetProject();
            if(resourceItemMenuId == R.id.menu_navview_mail)
              navigateTo(SettingsActivity.class);
            if(resourceItemMenuId == R.id.menu_navview_username)
              navigateTo(SettingsActivity.class);
            break;

          case DialogInterface.BUTTON_NEGATIVE:
            drawerLayout.closeDrawers();
            break;
        }
      }
    };

    alertDialog=builder.setCancelable(true).
        setPositiveButton(R.string.dialog_accept_clean_project, dialogClickListener)
        .setNegativeButton(R.string.dialog_cancel_clean_project, dialogClickListener).show();
  }

  public void navigateTo(Class cls){
    Intent intent=new Intent(VimojoApplication.getAppContext(),cls);
    startActivity(intent);
  }

  @Override
  public void showPreferenceUserName(String data) {
    Menu menu = navigationView.getMenu();
    menu.findItem(R.id.menu_navview_username).setTitle(data);

  }

  @Override
  public void showPreferenceEmail(String emailPreference) {
    Menu menu = navigationView.getMenu();
    menu.findItem(R.id.menu_navview_mail).setTitle(emailPreference);
  }

  @Override
  public void updateViewResetProject() {
    navigateTo(EditActivity.class);
  }

  @Override
  public void expandFabMenu() {
    fabMenu.expand();
  }

  @Override
  public void showError(final int stringToast) {
    Snackbar snackbar = Snackbar.make(fabMenu, stringToast, Snackbar.LENGTH_LONG);
    snackbar.show();
  }

  @Override
  public void showMessage(final int stringToast) {
    Snackbar snackbar = Snackbar.make(fabMenu, stringToast, Snackbar.LENGTH_LONG);
    snackbar.show();
  }

}




