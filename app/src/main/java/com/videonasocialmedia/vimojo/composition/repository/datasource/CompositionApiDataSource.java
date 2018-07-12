package com.videonasocialmedia.vimojo.composition.repository.datasource;

/**
 * Created by jliarte on 12/07/18.
 */

import android.util.Log;

import com.videonasocialmedia.vimojo.auth0.UserAuth0Helper;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.repository.datasource.mapper.CompositionToCompositionDtoMapper;
import com.videonasocialmedia.vimojo.repository.Specification;
import com.videonasocialmedia.vimojo.repository.datasource.ApiDataSource;
import com.videonasocialmedia.vimojo.vimojoapiclient.CompositionApiClient;
import com.videonasocialmedia.vimojo.vimojoapiclient.VimojoApiException;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

/**
 * API DataSource for projects. Provide remote persistance of Projects using vimojo API
 * via {@link com.videonasocialmedia.vimojo.vimojoapiclient.model.ProjectDto} class.
 */
public class CompositionApiDataSource extends ApiDataSource<Project> {
  private static final String LOG_TAG = CompositionApiDataSource.class.getSimpleName();
  private CompositionApiClient compositionApiClient;

  private final CompositionToCompositionDtoMapper mapper = new CompositionToCompositionDtoMapper();

  @Inject
  public CompositionApiDataSource(CompositionApiClient compositionApiClient,
                                  UserAuth0Helper userAuth0Helper) {
    super(userAuth0Helper);
    this.compositionApiClient = compositionApiClient;
  }

  @Override
  public void add(Project item) {
    try {
      // TODO(jliarte): 12/07/18 move this to background?
      String accessToken = getApiAccessToken().get().getAccessToken();
      this.compositionApiClient.addComposition(mapper.map(item), accessToken);
      Log.d(LOG_TAG, "Composition added to platform!");
    } catch (VimojoApiException apiError) {
      processApiError(apiError);
    } catch (InterruptedException | ExecutionException e) {
      // TODO(jliarte): 12/07/18 manage this error
      e.printStackTrace();
    }
  }

  @Override
  public void add(Iterable<Project> items) {
    for (Project project : items) {
      add(project);
    }
  }

  @Override
  public void update(Project item) {

  }

  @Override
  public void remove(Project item) {

  }

  @Override
  public void remove(Specification specification) {

  }

  @Override
  public List<Project> query(Specification specification) {
    return null;
  }

}
