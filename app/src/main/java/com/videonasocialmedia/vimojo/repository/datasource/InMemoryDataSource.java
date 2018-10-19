package com.videonasocialmedia.vimojo.repository.datasource;

/**
 * Created by jliarte on 4/09/18.
 */

import com.videonasocialmedia.vimojo.repository.model.IdentificableModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Generic implementation for an in memory data source using {@link HashMap}
 */
public abstract class InMemoryDataSource<K, T extends IdentificableModel> implements DataSource<T> {
  private final HashMap<K, T> items;

  public InMemoryDataSource() {
    items = new HashMap<>();
  }

  @Override
  public void add(T item) {
    if (item != null) {
      items.put((K) item.getId(), item);
    }
  }

  @Override
  public void add(Iterable<T> items) {
    for (T item : items) {
      this.add(item);
    }
  }

  @Override
  public void update(T item) {
    if (item != null) {
      items.put((K) item.getId(), item);
    }
  }

  @Override
  public void remove(T item) {
    items.remove(item.getId());
  }

  @Override
  public T getById(String id) {
    return items.get(id);
  }

  public List<T> getAll() {
    return new ArrayList<T>(items.values());
  }
}
