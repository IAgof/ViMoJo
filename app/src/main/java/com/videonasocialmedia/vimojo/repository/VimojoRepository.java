package com.videonasocialmedia.vimojo.repository;

/**
 * Created by jliarte on 11/07/18.
 */

import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.repository.datasource.DataSource;

/**
 * Repository pattern implementation. This class implements all the data handling logic based on
 * different data sources. Abstracts the data origin and works as a processor cache system where
 * different data sources are going to work as different cache levels.
 *
 * @param <T> The class of the contents of the items held by this repository
 */
public abstract class VimojoRepository<T> implements DataSource<T> {

  public abstract void remove(T item, DeletePolicy policy);
}
