package com.videonasocialmedia.vimojo.main;

import com.videonasocialmedia.vimojo.main.modules.ApplicationModule;
import com.videonasocialmedia.vimojo.main.modules.DataRepositoriesModule;
import com.videonasocialmedia.vimojo.main.modules.MockedDataRepositoriesModule;
import com.videonasocialmedia.vimojo.main.modules.MockedVimojoApplicationModule;
import com.videonasocialmedia.vimojo.main.modules.VimojoApplicationModule;

/**
 * Created by jliarte on 23/10/16.
 */

public class VimojoTestApplication extends VimojoApplication {
  private DataRepositoriesModule dataRepositoryModule;
  private VimojoApplicationModule vimojoApplicationModule;
  
  @Override
  public DataRepositoriesModule getDataRepositoriesModule() {
    if (dataRepositoryModule == null) {
      return new MockedDataRepositoriesModule();
    }
    return dataRepositoryModule;
  }

  public void setDataRepositoryModule(DataRepositoriesModule dataRepositoryModule) {
    this.dataRepositoryModule = dataRepositoryModule;
    initSystemComponent();
  }

  @Override
  public VimojoApplicationModule getVimojoApplicationModule() {
    if (vimojoApplicationModule == null) {
      return new MockedVimojoApplicationModule(this);
    }
    return vimojoApplicationModule;
  }

  @Override
  protected void setupDataBase() {
    // Do nothing
  }
}
