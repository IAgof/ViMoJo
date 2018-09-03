/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

/**
 * Created by alvaro on 31/8/18.
 */

package com.videonasocialmedia.vimojo.repository.datasource;


/**
 * Data source interface to manage cache, quick access, saved in a object in app memory
 *
 * @param <T> The class of the values stored into this data source.
 */
public interface MemoryCacheDataSource<T> {

  T getCurrent();

  void setCurrent(T item);
}

