package com.videonasocialmedia.vimojo.repository;

/**
 * Created by jliarte on 11/07/18.
 */

import com.videonasocialmedia.vimojo.repository.datasource.DataSource;

import java.util.List;

/**
 * Repository pattern implementation. This class implements all the data handling logic based on
 * different data sources. Abstracts the data origin and works as a processor cache system where
 * different data sources are going to work as different cache levels.
 *
 * @param <T> The class of the contents of the items held by this repository
 */
public class VimojoRepository<T> implements DataSource<T> {
  /**
   * {@link DataSource#add(Object)}
   */
  @Override
  public void add(T item) {

  }

  /**
   * {@link DataSource#add(Iterable)}
   */
  @Override
  public void add(Iterable<T> items) {

  }

  /**
   * {@link DataSource#update(Object)}
   */
  @Override
  public void update(T item) {

  }

  /**
   * {@link DataSource#remove(Object)}
   */
  @Override
  public void remove(T item) {

  }

  /**
   * {@link DataSource#remove(Specification)}
   */
  @Override
  public void remove(Specification specification) {

  }

  /**
   * {@link DataSource#query(Specification)}
   */
  @Override
  public List<T> query(Specification specification) {
    return null;
  }
}
