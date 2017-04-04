package com.videonasocialmedia.vimojo.main;

import com.videonasocialmedia.vimojo.galleryprojects.presentation.views.activity.DetailProjectActivity;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.views.activity.GalleryProjectListActivity;
import com.videonasocialmedia.vimojo.main.internals.di.PerActivity;
import com.videonasocialmedia.vimojo.main.modules.ActivityPresentersModule;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditorActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.GalleryActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.InitAppActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.RecordActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.ShareActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.VideoDuplicateActivity;
import com.videonasocialmedia.vimojo.record.presentation.views.activity.RecordCamera2Activity;
import com.videonasocialmedia.vimojo.sound.presentation.views.activity.MusicDetailActivity;
import com.videonasocialmedia.vimojo.sound.presentation.views.activity.MusicListActivity;
import com.videonasocialmedia.vimojo.sound.presentation.views.activity.SoundActivity;
import com.videonasocialmedia.vimojo.sound.presentation.views.activity.SoundVolumeActivity;
import com.videonasocialmedia.vimojo.sound.presentation.views.activity.VoiceOverActivity;
import com.videonasocialmedia.vimojo.split.presentation.views.activity.VideoSplitActivity;
import com.videonasocialmedia.vimojo.text.presentation.views.activity.VideoEditTextActivity;
import com.videonasocialmedia.vimojo.trim.presentation.views.activity.VideoTrimActivity;

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
  void inject(VoiceOverActivity activity);
  void inject(VideoDuplicateActivity activity);
  void inject(GalleryActivity activity);
  void inject(RecordActivity activity);
  void inject(RecordCamera2Activity activity);
  void inject(VideoSplitActivity activity);
  void inject(VideoTrimActivity activity);
  void inject(ShareActivity activity);
  void inject(InitAppActivity activity);
  void inject(SoundVolumeActivity activity);
  void inject(EditorActivity activity);
  void inject(GalleryProjectListActivity activity);
  void inject(DetailProjectActivity activity);
  void inject(VideoEditTextActivity activity);
}
