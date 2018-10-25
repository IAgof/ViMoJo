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
  private static final String QUERY_CASCADE = "cascade";

  private final String orderBy;
  private final boolean cascade;

  public CompositionQuery(String orderBy, boolean cascade) {
    this.orderBy = orderBy;
    this.cascade = cascade;
  }

  public Map<String, Object> toMap() {
    Map<String, Object> returnValues = new HashMap<>();

    if (orderBy != null) {
      returnValues.put(QUERY_ORDER_BY, orderBy);
    }

    if (cascade) {
      returnValues.put(QUERY_CASCADE, cascade);
    }

    return returnValues;
  }

  public static class Builder {
    private String orderBy;
    private boolean orderByAscendant;
    private boolean cascade;

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

    public Builder withCascade(boolean cascade) {
      this.cascade = cascade;
      return this;
    }

    public CompositionQuery build() {
      String plainOrderBy = convertOrderBy(orderBy, orderByAscendant);

      return new CompositionQuery(plainOrderBy, cascade);
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
