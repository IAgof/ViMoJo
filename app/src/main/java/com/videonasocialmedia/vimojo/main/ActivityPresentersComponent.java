package com.videonasocialmedia.vimojo.main;

import com.videonasocialmedia.vimojo.auth.presentation.view.activity.UserAuthActivity;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.views.activity.DetailProjectActivity;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.views.activity.GalleryProjectListActivity;
import com.videonasocialmedia.vimojo.main.internals.di.PerActivity;
import com.videonasocialmedia.vimojo.main.modules.ActivityPresentersModule;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditorActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.GalleryActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.InitAppActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.RecordActivity;
import com.videonasocialmedia.vimojo.share.presentation.views.activity.ShareActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.VideoDuplicateActivity;
import com.videonasocialmedia.vimojo.record.presentation.views.activity.RecordCamera2Activity;
import com.videonasocialmedia.vimojo.cameraSettings.presentation.view.activity.CameraSettingsActivity;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.presentation.view.activity.LicenseDetailActivity;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.presentation.view.activity.LicensesActivity;
import com.videonasocialmedia.vimojo.store.presentation.view.activity.VimojoStoreActivity;
import com.videonasocialmedia.vimojo.sound.presentation.views.activity.MusicDetailActivity;
import com.videonasocialmedia.vimojo.sound.presentation.views.activity.MusicListActivity;
import com.videonasocialmedia.vimojo.sound.presentation.views.activity.SoundActivity;
import com.videonasocialmedia.vimojo.sound.presentation.views.activity.VoiceOverRecordActivity;
import com.videonasocialmedia.vimojo.sound.presentation.views.activity.VoiceOverVolumeActivity;
import com.videonasocialmedia.vimojo.split.presentation.views.activity.VideoSplitActivity;
import com.videonasocialmedia.vimojo.text.presentation.views.activity.VideoEditTextActivity;
import com.videonasocialmedia.vimojo.trim.presentation.views.activity.VideoTrimActivity;
import com.videonasocialmedia.vimojo.userProfile.presentation.views.UserProfileActivity;

import dagger.Component;

/**
 * Created by jliarte on 1/12/16.
 */

@PerActivity
@Component(dependencies = {SystemComponent.class}, modules = {ActivityPresentersModule.class})
public interface ActivityPresentersComponent {
  void inject(VimojoActivity activity);
  void inject(MusicDetailActivity activity);
  void inject(EditActivity activity);
  void inject(SoundActivity activity);
  void inject(MusicListActivity activity);
  void inject(LicensesActivity activity);
  void inject(CameraSettingsActivity activity);
  void inject(LicenseDetailActivity activity);
  void inject(VoiceOverRecordActivity activity);
  void inject(VideoDuplicateActivity activity);
  void inject(GalleryActivity activity);
  void inject(RecordActivity activity);
  void inject(RecordCamera2Activity activity);
  void inject(VideoSplitActivity activity);
  void inject(VideoTrimActivity activity);
  void inject(ShareActivity activity);
  void inject(InitAppActivity activity);
  void inject(VoiceOverVolumeActivity activity);
  void inject(EditorActivity activity);
  void inject(GalleryProjectListActivity activity);
  void inject(DetailProjectActivity activity);
  void inject(VideoEditTextActivity activity);
  void inject(VimojoStoreActivity activity);
  void inject(UserAuthActivity activity);
  void inject(UserProfileActivity activity);
}
