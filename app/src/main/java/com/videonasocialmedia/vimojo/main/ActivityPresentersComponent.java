package com.videonasocialmedia.vimojo.main;

import com.videonasocialmedia.vimojo.main.internals.di.PerActivity;
import com.videonasocialmedia.vimojo.main.modules.ActivityPresentersModule;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.GalleryActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.VideoDuplicateActivity;
import com.videonasocialmedia.vimojo.sound.presentation.views.activity.MusicDetailActivity;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

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
  void inject(VideoDuplicateActivity activity);
  void inject(GalleryActivity activity);
}
