package com.videonasocialmedia.vimojo.main;

import com.videonasocialmedia.vimojo.export.ExportTempBackgroundService;
import com.videonasocialmedia.vimojo.main.modules.DataRepositoriesModule;
import com.videonasocialmedia.vimojo.main.modules.ExporterServiceModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by jliarte on 13/12/16.
 */

@Singleton
@Component(modules = {DataRepositoriesModule.class, ExporterServiceModule.class})
public interface ExporterServiceComponent {
  void inject(ExportTempBackgroundService exporterService);
}
