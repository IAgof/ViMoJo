package com.videonasocialmedia.vimojo.vimojoapiclient.model;

/**
 * Created by jliarte on 27/07/18.
 */

import java.util.HashMap;
import java.util.Map;

/**
 * Class for building queries for composition API service
 */
public class CompositionQuery {
  private static final String QUERY_ORDER_BY = "orderBy";

  private final String orderBy;

  public CompositionQuery(String orderBy) {
    this.orderBy = orderBy;
  }

  public Map<String, Object> toMap() {
    Map<String, Object> returnValues = new HashMap<>();

    if (orderBy != null) {
      returnValues.put(QUERY_ORDER_BY, orderBy);
    }

    return returnValues;
  }

  public static class Builder {
    private String orderBy;
    private boolean orderByAscendant;

    private Builder() {
    }

    public static Builder create() {
      return new Builder();
    }

    public Builder withOrderBy(String orderBy, boolean ascendant) {
      this.orderBy = orderBy;
      this.orderByAscendant = ascendant;
      return this;
    }

    public CompositionQuery build() {
      String plainOrderBy = convertOrderBy(orderBy, orderByAscendant);

      return new CompositionQuery(plainOrderBy);
    }

    private String convertOrderBy(String orderBy, boolean ascendant) {
      if (orderBy == null) {
        return null;
      }

      String plainOrderBy = orderBy.toString();
      return (ascendant) ? plainOrderBy : "-" + plainOrderBy;
    }
  }
}
