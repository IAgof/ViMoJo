package com.videonasocialmedia.vimojo.presentation.views.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.views.activity.GalleryProjectListActivity;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.EditorPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.views.EditorActivityView;
import com.videonasocialmedia.vimojo.presentation.views.customviews.CircleImageView;
import com.videonasocialmedia.vimojo.settings.presentation.views.activity.SettingsActivity;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.utils.Utils;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 */

public abstract class EditorActivity extends VimojoActivity implements EditorActivityView{
  @Inject UserEventTracker userEventTracker;
  @Inject EditorPresenter editorPresenter;

  private AlertDialog alertDialog;

  @Bind(R.id.edit_activity_drawer_layout)
  DrawerLayout drawerLayout;
  @Bind(R.id.navigator_view)
  NavigationView navigationView;
  @Bind(R.id.container_navigator)
  LinearLayout navigator;
  @Bind(R.id.fab_edit_room)
  FloatingActionsMenu fabMenu;
  @Nullable @Bind(R.id.switch_theme_dark)
  SwitchCompat switchTheme;

  CircleImageView imageUserThumb;
  String userThumbPath = Constants.PATH_APP_TEMP + File.separator + Constants.USER_THUMB;
  private int REQUEST_ICON_USER = 100;
  private final String THEME_DARK = "dark";
  private final String THEME_LIGHT = "light";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.editor_activity);
    ButterKnife.bind(this);
    getActivityPresentersComponent().inject(this);
    setUpAndCheckUserThumb();
  }

  private void setUpAndCheckUserThumb() {
    imageUserThumb = (CircleImageView) navigationView.getHeaderView(0)
        .findViewById(R.id.image_drawer_user);
    imageUserThumb.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        showDialogUserAddThumb();
      }
    });
    updateUserThumb(userThumbPath);
  }

  private void updateUserThumb(String path) {
    File thumb = new File(path);
    if (thumb.getName().compareTo(Constants.USER_THUMB) != 0) {
      try {
        Utils.copyFile(path,userThumbPath);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if(thumb.exists()) {
      Glide.with(this)
              .load(userThumbPath)
              .diskCacheStrategy(DiskCacheStrategy.RESULT)
              .signature(new StringSignature(String.valueOf(thumb.lastModified())))
              .into(imageUserThumb);
    }
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
      setUpAndCheckUserThumb();
      editorPresenter.getPreferenceUserName();
      editorPresenter.getPreferenceEmail();
    }
    editorPresenter.init();
    setupSwitchThemeAppIntoDrawer();
    updateTheme();
  }

  private void updateTheme() {
    boolean isDark = checkIfThemeDarkIsSelected();
    String currentTheme = getCurrentAppliedTheme();
    if (isDark && currentTheme.equals(THEME_LIGHT) || !isDark && currentTheme.equals(THEME_DARK)) {
      restartActivity();
    }

  }

  private String getCurrentAppliedTheme() {
    String currentTheme;
    TypedValue outValue = new TypedValue();
    getTheme().resolveAttribute(R.attr.themeName, outValue, true);
    if (THEME_DARK.equals(outValue.string)) {
      currentTheme = THEME_DARK;
    } else {
      currentTheme = THEME_LIGHT;
    }
    return currentTheme;
  }

  private boolean checkIfThemeDarkIsSelected() {
    return editorPresenter.getPreferenceThemeApp();
  }

  private void restartActivity() {
    Intent intent = getIntent();
    startActivity(intent);
    finish();
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
                return false;
              case R.id.menu_navview_settings:
                navigateTo(SettingsActivity.class);
                return false;
              case R.id.menu_navview_suggestions:
                navigateToMail("mailto:info@videona.com");
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
    if (resourceItemMenuId == R.id.menu_navview_mail)
      builder.setMessage(getResources().getString(R.string.dialog_change_email));
    if (resourceItemMenuId == R.id.menu_navview_username)
      builder.setMessage(getResources().getString(R.string.dialog_change_user_name));

    final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        switch (which) {
          case DialogInterface.BUTTON_POSITIVE:
            if(resourceItemMenuId == R.id.menu_navview_delete_clip)
                editorPresenter.createNewProject(Constants.PATH_APP, Constants.PATH_APP_ANDROID,
                    BuildConfig.FEATURE_WATERMARK);
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

    alertDialog = builder.setCancelable(true).
        setPositiveButton(R.string.dialog_accept_clean_project, dialogClickListener)
        .setNegativeButton(R.string.dialog_cancel_clean_project, dialogClickListener).show();
  }

  public void navigateTo(Class cls) {
    Intent intent = new Intent(VimojoApplication.getAppContext(),cls);
    startActivity(intent);
  }

  private void navigateToMail(String url) {
    Intent i = new Intent(Intent.ACTION_VIEW);
    i.setData(Uri.parse(url));
    startActivity(i);
  }

  public void setupSwitchThemeAppIntoDrawer() {
    switchTheme = (SwitchCompat) navigationView.getMenu().findItem(R.id.switch_theme_dark)
        .getActionView();
    boolean themeDarkIsSelected = checkIfThemeDarkIsSelected();
    if (switchTheme != null) {
      switchTheme.setChecked(themeDarkIsSelected);
      switchTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isDarkThemeChecked) {
          editorPresenter.switchTheme(isDarkThemeChecked);
          drawerLayout.closeDrawers();
          restartActivity();
        }
      });
    }
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

  public void showDialogUserAddThumb() {
      // dialog pick or take photo
    final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        File file = new File(userThumbPath);
        Uri uri = Uri.fromFile(file);

        Intent userThumbSetterIntent = null;
        switch (which) {
          /*case DialogInterface.BUTTON_POSITIVE:
            // Take photo button clicked
            userThumbSetterIntent = new Intent("android.media.action.IMAGE_CAPTURE");
            setIntentExtras(uri, userThumbSetterIntent);
            startActivityForResult(userThumbSetterIntent, REQUEST_ICON_USER);
            break;*/
          case DialogInterface.BUTTON_NEUTRAL:
            // Pick from gallery button clicked
            userThumbSetterIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            userThumbSetterIntent.setType("image/*");
            setIntentExtras(uri, userThumbSetterIntent);
            startActivityForResult(userThumbSetterIntent, REQUEST_ICON_USER);
            break;
        }
      }

      private void setIntentExtras(Uri uri, Intent takePicIntent) {
        takePicIntent.putExtra("crop", "true");
        takePicIntent.putExtra("outputX", 600);
        takePicIntent.putExtra("outputY", 600);
        takePicIntent.putExtra("aspectX", 1);
        takePicIntent.putExtra("aspectY", 1);
        takePicIntent.putExtra("scale", true);
        takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        takePicIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
      }
    };

    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this,
        R.style.VideonaDialog);
    builder.setMessage(R.string.dialog_editor_user_thumb_message)
        .setNeutralButton(R.string.dialog_editor_user_thumb_pick_photo, dialogClickListener).show();
    //.setPositiveButton(R.string.dialog_editor_user_thumb_take_photo, dialogClickListener)
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == RESULT_OK && requestCode == REQUEST_ICON_USER && data != null) {
      if(data.getData() != null) {
        final String inPath = Utils.getPath(this, data.getData());
        if (inPath != null)
          updateUserThumb(inPath);
      }
    }
  }

}




