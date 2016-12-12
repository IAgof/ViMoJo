package com.videonasocialmedia.vimojo.main;

import com.videonasocialmedia.vimojo.main.modules.DataRepositoriesModule;
import com.videonasocialmedia.vimojo.main.modules.ActivityPresentersModule;
import com.videonasocialmedia.vimojo.main.modules.TrackerModule;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.sound.presentation.views.activity.MusicDetailActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by jliarte on 1/12/16.
 */

@Singleton
@Component(modules = {DataRepositoriesModule.class, ActivityPresentersModule.class,
        TrackerModule.class})
public interface ActivityPresentersComponent {
  void inject(VimojoActivity activity);
  void inject(MusicDetailActivity activity);
  void inject(EditActivity activity);
}
