package com.videonasocialmedia.vimojo.repository;

/**
 * Created by jliarte on 20/10/16.
 */

// TODO(jliarte): 5/07/18 migrate to karumi mapper??
public interface Mapper<From, To> {
  To map(From from);
}
