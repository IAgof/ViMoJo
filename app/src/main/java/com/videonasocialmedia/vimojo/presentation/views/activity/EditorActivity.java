package com.videonasocialmedia.vimojo.presentation.views.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayerExo;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.views.activity.DetailProjectActivity;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.views.activity.GalleryProjectListActivity;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.EditorPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.views.EditorActivityView;
import com.videonasocialmedia.vimojo.presentation.mvp.views.VideonaPlayerView;
import com.videonasocialmedia.vimojo.presentation.views.customviews.CircleImageView;
import com.videonasocialmedia.vimojo.settings.mainSettings.presentation.views.activity.SettingsActivity;
import com.videonasocialmedia.vimojo.sound.presentation.views.activity.SoundActivity;
import com.videonasocialmedia.vimojo.store.presentation.view.activity.VimojoStoreActivity;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.activity.TutorialEditorActivity;
import com.videonasocialmedia.vimojo.tutorial.presentation.mvp.views.activity.TutorialRecordActivity;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.userProfile.presentation.views.UserProfileActivity;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.videonasocialmedia.vimojo.presentation.mvp.presenters.EditorPresenter.VOLUME_MUTE;

/**
 *
 */
public abstract class EditorActivity extends VimojoActivity implements EditorActivityView,
        VideonaPlayerView, VideonaPlayer.VideonaPlayerListener {
  private static String LOG_TAG = EditorActivity.class.getCanonicalName();
  private static final String EDITOR_ACTIVITY_PROJECT_POSITION = "editor_activity_project_position";
  private static final String EDITOR_ACTIVITY_HAS_BEEN_PROJECT_EXPORTED =
          "editor_activity_has_been_project_exported";
  private static final String EDITOR_ACTIVITY_VIDEO_EXPORTED = "editor_activity_video_exported";

  @Inject
  UserEventTracker userEventTracker;
  @Inject
  EditorPresenter editorPresenter;

  // UI elements
  @Nullable
  @BindView(R.id.text_dialog)
  EditText editTextDialog;
  @BindView(R.id.edit_activity_drawer_layout)
  DrawerLayout drawerLayout;
  @BindView(R.id.navigator_view)
  NavigationView navigationView;
  @BindView(R.id.container_navigator)
  LinearLayout navigator;
  @BindView(R.id.fab_edit_room)
  FloatingActionsMenu fabMenu;
  @Nullable
  @BindView(R.id.switch_theme_dark)
  SwitchCompat switchTheme;
  @Nullable
  @BindView(R.id.switch_watermark)
  SwitchCompat switchWatermark;
  @Nullable
  @BindView(R.id.videona_player)
  VideonaPlayerExo videonaPlayer;

  private boolean darkThemePurchased = false;
  private boolean watermarkPurchased = false;
  CircleImageView imageProjectThumb;
  TextView projectName;
  TextView projectDate;
  ImageButton projectEdit;
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

  private boolean isVideoMute;
  protected String videoExportedPath;
  protected boolean projectHasBeenExported = false;
  private int currentPlayerPosition = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.editor_activity);
    ButterKnife.bind(this);
    getActivityPresentersComponent().inject(this);
  }

  @Override
  protected void onStart() {
    super.onStart();
    videonaPlayer.setListener(this);
    videonaPlayer.onShown(this);
    if (BuildConfig.FEATURE_VERTICAL_VIDEOS) {
      videonaPlayer.setAspectRatioVerticalVideos();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    setupDrawer();
    editorPresenter.updatePresenter(projectHasBeenExported, videoExportedPath, getCurrentAppliedTheme());
  }

  @Override
  protected void onPause() {
    super.onPause();
    editorPresenter.onPause();
    videonaPlayer.onPause();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    currentPlayerPosition = videonaPlayer.getCurrentPosition();
    outState.putInt(EDITOR_ACTIVITY_PROJECT_POSITION, currentPlayerPosition);
    outState.putBoolean(EDITOR_ACTIVITY_HAS_BEEN_PROJECT_EXPORTED, projectHasBeenExported);
    outState.putString(EDITOR_ACTIVITY_VIDEO_EXPORTED, videoExportedPath);
    super.onSaveInstanceState(outState);
  }

  @Override
  protected void onRestoreInstanceState(Bundle state) {
    super.onRestoreInstanceState(state);
    if (state != null) {
      currentPlayerPosition = state.getInt(EDITOR_ACTIVITY_PROJECT_POSITION,
              0);
      projectHasBeenExported = state.getBoolean(EDITOR_ACTIVITY_HAS_BEEN_PROJECT_EXPORTED,
              false);
      videoExportedPath = state.getString(EDITOR_ACTIVITY_VIDEO_EXPORTED);
    }
  }

  private void setupDrawer() {
    if (navigationView != null) {
      setUpDrawerHeader();
      setupDrawerContent(navigationView);
    }
    setupSwitchThemeAppIntoDrawer();
  }

  private void setUpDrawerHeader() {
    imageProjectThumb = navigationView.getHeaderView(0)
            .findViewById(R.id.image_drawer_thumb_project);
    projectName = navigationView.getHeaderView(0).findViewById(R.id.project_title);
    projectName.setOnClickListener(v -> showDialogUpdateCurrentProjectTitle());
    projectDate = navigationView.getHeaderView(0).findViewById(R.id.project_date);
    projectEdit = navigationView.getHeaderView(0).findViewById(R.id.project_edit_button);
    projectEdit.setOnClickListener(v -> navigateTo(DetailProjectActivity.class));
  }

  private void setupDrawerContent(NavigationView navigationView) {
    navigationView.setNavigationItemSelectedListener(
            menuItem -> {
              switch (menuItem.getItemId()) {
                case R.id.menu_navview_gallery_projects:
                  drawerLayout.closeDrawers();
                  navigateTo(GalleryProjectListActivity.class);
                  return false;
                case R.id.menu_navview_new_project:
                  showNewProjectCreationDialog(R.id.menu_navview_new_project);
                  return false;
                case R.id.menu_navview_user_profile:
                  navigateTo(UserProfileActivity.class);
                  return false;
                case R.id.menu_navview_platform:
                  int platformId = BuildConfig.DEBUG ? R.string.vimojo_platform_base_debug
                          : R.string.vimojo_platform;
                  navigateToWeb(platformId);
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
                case R.id.menu_navview_vimojo_kit_web_section:
                  navigateToWeb(R.string.vimojo_kit_web);
                  return false;
                default:
                  return false;
              }
            });
  }

  @Override
  public void setContentView(int layout) {
    super.setContentView(R.layout.editor_activity);
    setToolbar();
  }

  private void setToolbar() {
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    ActionBar ab = getSupportActionBar();
    if (ab != null) {
      ab.setDisplayShowTitleEnabled(false);
      ab.setHomeAsUpIndicator(R.drawable.ic_nav_menu);
      ab.setDisplayHomeAsUpEnabled(true);
    }
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
    LinearLayout contentLayoutEditActivity = findViewById(linearLayoutContainer);
    getLayoutInflater().inflate(layoutToAdd, contentLayoutEditActivity);
  }

  private void navigateToWeb(int url) {
    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(url)));
    startActivity(Intent.createChooser(intent, getString(R.string.choose_browser)));
  }

  protected void showNewProjectCreationDialog(final int resourceId) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.VideonaDialog);
    builder.setMessage(getResources().getString(R.string.dialog_message_clean_project));

    final DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
      switch (which) {
        case DialogInterface.BUTTON_POSITIVE:
          // TODO(jliarte): 20/04/18 review resetting exported video path and date
          resetVideoExported();
          // TODO(jliarte): 20/04/18 generic transition drawable to allow change in build phase?
          Drawable drawableFadeTransitionVideo = getDrawable(R.drawable.alpha_transition_white);
          editorPresenter.resetCurrentProject(Constants.PATH_APP, Constants.PATH_APP_ANDROID,
                  drawableFadeTransitionVideo);
          break;

        case DialogInterface.BUTTON_NEGATIVE:
          drawerLayout.closeDrawers();
          if (resourceId == R.id.button_music_navigator)
            navigateTo(SoundActivity.class);
          if (resourceId == R.id.button_edit_navigator)
            navigateTo(EditActivity.class);
          break;
      }
    };
    builder.setCancelable(true).
            setPositiveButton(R.string.dialog_accept_clean_project, dialogClickListener)
            .setNegativeButton(R.string.dialog_cancel_clean_project, dialogClickListener).show();
  }

  public void navigateTo(Class cls) {
    Intent intent = new Intent(this, cls);
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
    navigationView.getMenu().findItem(identifier).setIcon(this.getDrawable(resourceId));
  }

  @Override
  public void goToRecordOrGalleryScreen() {
    navigateTo(GoToRecordOrGalleryActivity.class);
    finish();
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
  public void restartActivity(Class clas) {
    runOnUiThread(() -> {
      Intent intent = new Intent(this, clas);
      startActivity(intent);
      finish();
    });
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
    runOnUiThread(() -> {
      setupSwitchWatermarkIntoDrawer();
    });
  }

  @Override
  public void hideWatermarkSwitch() {
    runOnUiThread(() -> {
      Menu menu = navigationView.getMenu();
      MenuItem target = menu.findItem(R.id.switch_watermark);
      target.setVisible(false);
    });
  }

  @Override
  public void setIconsFeatures() {
    runOnUiThread(() -> {
      updateNavigationIcon(R.id.switch_theme_dark, R.drawable.activity_editor_drawer_dark_theme);
      updateNavigationIcon(R.id.switch_watermark, R.drawable.activity_editor_drawer_watermark);
    });
  }

  @Override
  public void setIconsPurchaseInApp() {
    runOnUiThread(() -> {
      updateNavigationIcon(R.id.switch_theme_dark, R.drawable.ic_locked);
      updateNavigationIcon(R.id.switch_watermark, R.drawable.ic_locked);
    });
  }

  @Override
  public void hideVimojoStoreViews() {
    runOnUiThread(() -> {
      Menu menu = navigationView.getMenu();
      MenuItem target = menu.findItem(R.id.menu_navview_vimojo_store_section);
      target.setVisible(false);
      isVimojoStoreAvailable = false;
    });
  }

  @Override
  public void hideLinkToVimojoPlatform() {
    runOnUiThread(() -> {
      Menu menu = navigationView.getMenu();
      MenuItem target = menu.findItem(R.id.menu_navview_platform);
      target.setVisible(false);
    });
  }

  @Override
  public void setHeaderViewCurrentProject(String pathThumbProject, String currentProjectName,
                                          String currentProjectDate) {
    runOnUiThread(() -> {
      if (pathThumbProject != null) {
        updateCurrentProjectThumb(pathThumbProject);
      } else {
        updateCurrentProjectDefaultThumb();
      }
      projectName.setText(currentProjectName);
      projectDate.setText(currentProjectDate);
    });
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

  @Override
  public void bindVideoList(List<Video> movieList) {
    runOnUiThread(() -> {
      videonaPlayer.bindVideoList(movieList);
    });
  }

  @Override
  public void bindMusic(Music music) {
    runOnUiThread(() -> {
      videonaPlayer.setMusic(music);
    });
  }

  @Override
  public void bindVoiceOver(Music voiceOver) {
    runOnUiThread(() -> {
      videonaPlayer.setVoiceOver(voiceOver);
    });
  }

  @Override
  public void setVideoMute() {
    isVideoMute = true;
    videonaPlayer.setVideoVolume(0f);
  }

  @Override
  public void setVideoVolume(float volume) {
    videonaPlayer.setVideoVolume(volume);
  }

  @Override
  public void setVoiceOverVolume(float volume) {
    videonaPlayer.setVoiceOverVolume(volume);
  }

  @Override
  public void setMusicVolume(float volume) {
    runOnUiThread(() -> {
      videonaPlayer.setMusicVolume(volume);
    });
  }

  @Override
  public void setVideoFadeTransitionAmongVideos() {
    runOnUiThread(() -> {
      videonaPlayer.setVideoTransitionFade();
    });
  }

  @Override
  public void setAudioFadeTransitionAmongVideos() {
    runOnUiThread(() -> {
      videonaPlayer.setAudioTransitionFade();
    });
  }

  @Override
  public void seekToClip(int clipPosition) {
    runOnUiThread(() -> {
      videonaPlayer.seekToClip(clipPosition);
    });
  }

  @Override
  public void pausePreview() {
    videonaPlayer.pausePreview();
  }

  @Override
  public ListenableFuture<?> updatePlayerVideos() {
    return editorPresenter.obtainVideoFromProject();
  }

  @Override
  public void initPreviewFromVideo(List<Video> movieList) {
    runOnUiThread(() -> {
      videonaPlayer.onPause();
      videonaPlayer.onShown(this);
      videonaPlayer.initPreviewLists(movieList);
      videonaPlayer.initPreview(currentPlayerPosition);
    });
  }

  @Override
  public void newClipPlayed(int currentClipIndex) {
    if (isVideoMute) {
      videonaPlayer.setVideoVolume(VOLUME_MUTE);
    }
  }

  private void updateCurrentProjectThumb(String path) {
    File thumb = new File(path);
    if (thumb.exists()) {
      Glide.with(this)
              .load(path)
              .centerCrop()
              .error(R.drawable.fragment_gallery_no_image)
              .into(imageProjectThumb);
    }
  }

  private void updateCurrentProjectDefaultThumb() {
    imageProjectThumb.setImageResource(R.drawable.activity_gallery_project_no_preview);
  }

  private void showDialogUpdateCurrentProjectTitle() {
    View dialogView = getLayoutInflater().inflate(R.layout.dialog_insert_text, null);
    editTextDialog = dialogView.findViewById(R.id.text_dialog);
    editTextDialog.setText(projectName.getText());
    editTextDialog.setSelectAllOnFocus(true);

    final DialogInterface.OnClickListener dialogClickListener =
            (dialog, which) -> {
              hideKeyboard(editTextDialog);
              switch (which) {
                case DialogInterface.BUTTON_POSITIVE: {
                  String textPreference = editTextDialog.getText().toString();
                  if (textPreference.equals(projectName.getText()))
                    return;
                  editorPresenter.updateTitleCurrentProject(textPreference);
                  projectName.setText(textPreference);
                }
                case DialogInterface.BUTTON_NEGATIVE:
                  break;
              }
            };

    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.VideonaDialog);
    AlertDialog alertDialog = builder.setCancelable(false)
            .setTitle(getString(R.string.dialog_title_update_project_title))
            .setView(dialogView)
            .setPositiveButton(R.string.positiveButton, dialogClickListener)
            .setNegativeButton(R.string.negativeButton, dialogClickListener)
            .setCancelable(false).show();

    editTextDialog.requestFocus();
    showKeyboard();
  }

  private void showKeyboard() {
    InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
  }

  private void hideKeyboard(View v) {
    InputMethodManager keyboard =
            (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    keyboard.hideSoftInputFromWindow(v.getWindowToken(), 0);
  }

  public void initVideoPlayerFromFilePath(String videoPath) {
    this.videoExportedPath = videoPath;
    editorPresenter.setupPlayer(projectHasBeenExported, videoExportedPath);
  }

  public void resetVideoExported() {
    projectHasBeenExported = false;
    videoExportedPath = null;
  }

  private String getCurrentAppliedTheme() {
    TypedValue outValue = new TypedValue();
    this.getTheme().resolveAttribute(R.attr.themeName, outValue, true);
    return (String) outValue.string;
  }

}