package com.videonasocialmedia.vimojo.presentation.views.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.views.activity.GalleryProjectListActivity;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.EditorPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.views.EditorActivityView;
import com.videonasocialmedia.vimojo.presentation.views.customviews.CircleImageView;
import com.videonasocialmedia.vimojo.settings.mainSettings.presentation.views.activity.SettingsActivity;
import com.videonasocialmedia.vimojo.store.presentation.view.activity.VimojoStoreActivity;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.activity.TutorialEditorActivity;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.activity.TutorialRecordActivity;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.userProfile.presentation.mvp.views.UserProfileActivity;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import java.io.File;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 */
public abstract class EditorActivity extends VimojoActivity implements EditorActivityView {

  @Inject
  UserEventTracker userEventTracker;
  @Inject
  EditorPresenter editorPresenter;

  private AlertDialog alertDialog;

  @Bind(R.id.edit_activity_drawer_layout)
  DrawerLayout drawerLayout;
  @Bind(R.id.navigator_view)
  NavigationView navigationView;
  @Bind(R.id.container_navigator)
  LinearLayout navigator;
  @Bind(R.id.fab_edit_room)
  FloatingActionsMenu fabMenu;
  @Nullable
  @Bind(R.id.switch_theme_dark)
  SwitchCompat switchTheme;
  @Nullable
  @Bind(R.id.switch_watermark)
  SwitchCompat switchWatermark;
  private boolean darkThemePurchased = false;
  private boolean watermarkPurchased = false;
  CircleImageView imageUserThumb;
  TextView projectName;
  TextView projectDate;
  String userThumbPath = Constants.PATH_APP_TEMP + File.separator + Constants.USER_THUMB;
  private int REQUEST_ICON_USER = 100;
  private boolean isVimojoStoreAvailable = true;
  private CompoundButton.OnCheckedChangeListener watermarkOnCheckedChangeListener =
          new CompoundButton.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
      if (isWatermarkAvailable()) {
        editorPresenter.switchPreference(isChecked, ConfigPreferences.WATERMARK);
      } else {
        switchWatermark.setChecked(true);
        navigateTo(VimojoStoreActivity.class);
      }
    }
  };
  private CompoundButton.OnCheckedChangeListener themeOnCheckedChangeListener
          = new CompoundButton.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isDarkThemeChecked) {
      if (isDarkThemeAvailable()) {
        editorPresenter.switchPreference(isDarkThemeChecked, ConfigPreferences.THEME_APP_DARK);
      } else {
        switchTheme.setChecked(false);
        navigateTo(VimojoStoreActivity.class);
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.editor_activity);
    ButterKnife.bind(this);
    getActivityPresentersComponent().inject(this);
    setUpAndCheckHeaderViewCurrentProject();
  }

  private void setUpAndCheckHeaderViewCurrentProject() {
    imageUserThumb = (CircleImageView) navigationView.getHeaderView(0)
            .findViewById(R.id.image_drawer_thumb_project);
    projectName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.project_name);
    projectDate = (TextView) navigationView.getHeaderView(0).findViewById(R.id.project_date);
    editorPresenter.updateHeaderViewCurrentProject();
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
      setUpAndCheckHeaderViewCurrentProject();
    }
    editorPresenter.init();
    setupSwitchThemeAppIntoDrawer();
    editorPresenter.updateTheme();
  }

  @Override
  protected void onPause() {
    super.onPause();
    editorPresenter.onPause();
  }

  private boolean checkIfThemeDarkIsSelected() {
    return editorPresenter.getPreferenceThemeApp();
  }

  private boolean checkIfWatermarkIsSelected() {
    return editorPresenter.getPreferenceWaterMark();
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
      case R.id.action_settings_suggestions:
        navigateToMail("mailto:info@videona.com");
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
              public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                  case R.id.menu_navview_gallery_projects:
                    drawerLayout.closeDrawers();
                    navigateTo(GalleryProjectListActivity.class);
                    return false;
                  case R.id.menu_navview_delete_clip:
                    createDialog(R.id.menu_navview_delete_clip);
                    return false;
                  case R.id.menu_navview_user_profile:
                    navigateTo(UserProfileActivity.class);
                    return false;
                  case R.id.menu_navview_settings:
                    navigateTo(SettingsActivity.class);
                    return false;
                  case R.id.menu_navview_tutorial_edition:
                    navigateTo(TutorialEditorActivity.class);
                    return false;
                  case R.id.menu_navview_tutorial_record:
                    navigateTo(TutorialRecordActivity.class);
                    return false;
                  case R.id.menu_navview_vimojo_store_section:
                    navigateTo(VimojoStoreActivity.class);
                    return false;
                  default:
                    return false;
                }
              }
            });
  }

  private void createDialog(final int resourceItemMenuId) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.VideonaDialog);
    if (resourceItemMenuId == R.id.menu_navview_delete_clip)
      builder.setMessage(getResources().getString(R.string.dialog_message_clean_project));

    final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        switch (which) {
          case DialogInterface.BUTTON_POSITIVE:
            if (resourceItemMenuId == R.id.menu_navview_delete_clip)
              editorPresenter.createNewProject(Constants.PATH_APP, Constants.PATH_APP_ANDROID);
            break;

          case DialogInterface.BUTTON_NEGATIVE:
            drawerLayout.closeDrawers();
            break;
        }
      }
    };

    alertDialog = builder.setCancelable(true).
            setPositiveButton(R.string.dialog_accept_clean_project, dialogClickListener)
            .setNegativeButton(R.string.dialog_cancel_clean_project, dialogClickListener).show();
  }

  public void navigateTo(Class cls) {
    Intent intent = new Intent(VimojoApplication.getAppContext(), cls);
    startActivity(intent);
  }

  private void navigateToMail(String url) {
    Intent i = new Intent(Intent.ACTION_VIEW);
    i.setData(Uri.parse(url));
    startActivity(i);
  }

  private void setupSwitchThemeAppIntoDrawer() {
    switchTheme = (SwitchCompat) navigationView.getMenu().findItem(R.id.switch_theme_dark)
        .getActionView();
    if (switchTheme != null) {
      boolean themeDarkIsSelected = checkIfThemeDarkIsSelected();
      switchTheme.setOnCheckedChangeListener(null);
      switchTheme.setChecked(themeDarkIsSelected);
      switchTheme.setOnCheckedChangeListener(themeOnCheckedChangeListener);
    }
  }

  private boolean isDarkThemeAvailable() {
    return darkThemePurchased || !isVimojoStoreAvailable;
  }

  private boolean isWatermarkAvailable() {
    return watermarkPurchased || !isVimojoStoreAvailable;
  }

  private void setupSwitchWatermarkIntoDrawer() {
    switchWatermark = (SwitchCompat) navigationView.getMenu().findItem(R.id.switch_watermark)
            .getActionView();
    if (switchWatermark != null) {
      boolean watermarkIsSelected = checkIfWatermarkIsSelected();
      switchWatermark.setOnCheckedChangeListener(null);
      switchWatermark.setChecked(watermarkIsSelected);
      switchWatermark.setOnCheckedChangeListener(watermarkOnCheckedChangeListener);
    }
  }

  private void updateNavigationIcon(int identifier, int resourceId) {
    navigationView.getMenu().findItem(identifier)
             .setIcon(this.getDrawable(resourceId));
  }

  @Override
  public void updateViewResetProject() {
    navigateTo(GoToRecordOrGalleryActivity.class);
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

  @Override
  public void restartShareActivity(String videoPath) {
    Intent intent = getIntent();
    intent.putExtra("videoPath", videoPath);
    startActivity(intent);
    finish();
  }

  @Override
  public void restartActivity() {
    Intent intent = getIntent();
    startActivity(intent);
    finish();
  }

  @Override
  public void itemDarkThemePurchased() {
    darkThemePurchased = true;
    updateNavigationIcon(R.id.switch_theme_dark, R.drawable.ic_unlocked);
  }

  @Override
  public void itemWatermarkPurchased() {
    watermarkPurchased = true;
    updateNavigationIcon(R.id.switch_watermark, R.drawable.ic_unlocked);
  }

  @Override
  public void watermarkFeatureAvailable() {
    setupSwitchWatermarkIntoDrawer();
  }

  @Override
  public void hideWatermarkSwitch() {
    Menu menu = navigationView.getMenu();
    MenuItem target = menu.findItem(R.id.switch_watermark);
    target.setVisible(false);
  }

  @Override
  public void setIconsFeatures() {
    updateNavigationIcon(R.id.switch_theme_dark, R.drawable.activity_editor_drawer_dark_theme);
    updateNavigationIcon(R.id.switch_watermark, R.drawable.activity_editor_drawer_watermark);
  }

  @Override
  public void setIconsPurchaseInApp() {
    updateNavigationIcon(R.id.switch_theme_dark, R.drawable.ic_locked);
    updateNavigationIcon(R.id.switch_watermark, R.drawable.ic_locked);
  }

  @Override
  public void hideVimojoStoreViews() {
    Menu menu = navigationView.getMenu();
    MenuItem target = menu.findItem(R.id.menu_navview_vimojo_store_section);
    target.setVisible(false);
    isVimojoStoreAvailable = false;
  }

  @Override
  public void setHeaderViewCurrentProject(String pathThumbProject, String currentProjectName,
                                          String currentProjectDate) {
    updateCurrentProjectThumb(pathThumbProject);
    projectName.setText(currentProjectName);
    projectDate.setText(currentProjectDate);
  }

  @Override
  public void deactivateDarkTheme() {
    switchTheme.setOnCheckedChangeListener(null);
    switchTheme.setChecked(false);
    switchTheme.setOnCheckedChangeListener(themeOnCheckedChangeListener);
  }

  @Override
  public void activateWatermark() {
    switchWatermark.setOnCheckedChangeListener(null);
    switchWatermark.setChecked(true);
    switchWatermark.setOnCheckedChangeListener(watermarkOnCheckedChangeListener);
  }

  private void updateCurrentProjectThumb(String path) {
    File thumb = new File(path);
    if (thumb.exists()) {
      Glide.with(this)
          .load(path)
          .centerCrop()
          .error(R.drawable.fragment_gallery_no_image)
          .into(imageUserThumb);
    }
  }

}