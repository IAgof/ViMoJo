package com.videonasocialmedia.vimojo.repository.datasource;

/**
 * Created by jliarte on 20/10/16.
 */

import com.videonasocialmedia.vimojo.repository.Specification;

import java.util.List;

/**
 * Data source interface meant to be used only to persist data.
 *
 * @param <T> The class of the values stored into this data source.
 */
public interface DataSource<T> {
  void add(T item);

  void add(Iterable<T> items);

  void update(T item);

  void remove(T item);

  void remove(Specification specification);

  List<T> query(Specification specification);
}
