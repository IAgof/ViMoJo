package com.videonasocialmedia.vimojo.main;

import com.videonasocialmedia.vimojo.main.modules.DataRepositoriesModule;
import com.videonasocialmedia.vimojo.main.modules.MockedDataRepositoriesModule;

/**
 * Created by jliarte on 23/10/16.
 */

public class VimojoTestApplication extends VimojoApplication {
  private DataRepositoriesModule dataRepositoryModule;

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
  protected void setupDataBase() {
    // Do nothing
  }
}
