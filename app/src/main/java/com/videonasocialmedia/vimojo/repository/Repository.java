package com.videonasocialmedia.vimojo.repository;

import java.util.List;

/**
 * Created by jliarte on 20/10/16.
 */

public interface Repository<T> {
  void add(T item);

  void add(Iterable<T> items);

  void update(T item);

  void remove(T item);

  void remove(Specification specification);

  List<T> query(Specification specification);
}
