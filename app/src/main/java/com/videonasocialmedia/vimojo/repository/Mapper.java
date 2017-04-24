package com.videonasocialmedia.vimojo.repository;

/**
 * Created by jliarte on 20/10/16.
 */

public interface Mapper<From, To> {
  To map(From from);
}
