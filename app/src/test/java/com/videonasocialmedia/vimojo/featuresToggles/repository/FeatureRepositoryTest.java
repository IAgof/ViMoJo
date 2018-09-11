package com.videonasocialmedia.vimojo.featuresToggles.repository;

/**
 * Created by jliarte on 3/09/18.
 */

import com.videonasocialmedia.vimojo.featuresToggles.domain.model.FeatureToggle;
import com.videonasocialmedia.vimojo.featuresToggles.repository.datasource.FeatureApiDataSource;
import com.videonasocialmedia.vimojo.featuresToggles.repository.datasource.FeatureInMemoryDataSource;
import com.videonasocialmedia.vimojo.featuresToggles.repository.datasource.FeatureSharedPreferencesDataSource;
import com.videonasocialmedia.vimojo.repository.datasource.BackgroundScheduler;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class FeatureRepositoryTest {
  @Mock private FeatureApiDataSource mockedRemoteDS;
  @Mock private FeatureSharedPreferencesDataSource mockedLocalDs;
  @Mock private FeatureInMemoryDataSource mockedCacheDS;
  @Mock private BackgroundScheduler mockedBackgroundScheduler;
  private FeatureRepository featureRepository;
  private List<FeatureToggle> remoteResponse;
  private List<FeatureToggle> localResponse;
  private List<FeatureToggle> cacheResponse;

  @Before
  public void injectMocks() {
    MockitoAnnotations.initMocks(this);
    setupUserFeaturesRepository();
  }

  private void setupUserFeaturesRepository() {
    featureRepository = new FeatureRepository(mockedRemoteDS,
            mockedLocalDs, mockedCacheDS, mockedBackgroundScheduler);
    Mockito.doReturn(remoteResponse).when(mockedRemoteDS).getById(ArgumentMatchers.anyString());
    Mockito.doReturn(localResponse).when(mockedLocalDs).getById(ArgumentMatchers.anyString());
    Mockito.doReturn(cacheResponse).when(mockedCacheDS).getById(ArgumentMatchers.anyString());
  }


}