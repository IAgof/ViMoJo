package com.videonasocialmedia.vimojo.vimojoapiclient.model;

/**
 * Created by jliarte on 14/08/18.
 */

import java.util.HashMap;
import java.util.Map;

/**
 * Class for building queries for asset API service
 */
public class AssetQuery {
  private static final String QUERY_ORDER_BY = "orderBy";
  private static final String QUERY_HASH = "hash";
  private static final String QUERY_CREATED_BY = "created_by";
  private static final String QUERY_COMPOSITION_ID = "compositionId";

  private final String orderBy;
  private final String hash;
  private final String createdBy;
  private final String compositionId;

  public AssetQuery(String orderBy, String hash, String createdBy, String compositionId) {
    this.orderBy = orderBy;
    this.hash = hash;
    this.createdBy = createdBy;
    this.compositionId = compositionId;
  }

  public Map<String, Object> toMap() {
    Map<String, Object> returnValues = new HashMap<>();

    if (orderBy != null) {
      returnValues.put(QUERY_ORDER_BY, orderBy);
    }

    if (hash != null) {
      returnValues.put(QUERY_HASH, hash);
    }

    if (createdBy != null) {
      returnValues.put(QUERY_CREATED_BY, createdBy);
    }

    return returnValues;
  }

  public static class Builder {
    private String orderBy;
    private boolean orderByAscendant;
    private String hash;
    private String createdBy;
    private String compositionId;

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

    public Builder withHash(String hash) {
      this.hash = hash;
      return this;
    }

    public Builder withCreatedBy(String createdBy) {
      this.createdBy = createdBy;
      return this;
    }

    public Builder withCompositionId(String compositionId) {
      this.compositionId = compositionId;
      return this;
    }

    public AssetQuery build() {
      String plainOrderBy = convertOrderBy(orderBy, orderByAscendant);

      return new AssetQuery(plainOrderBy, hash, createdBy, compositionId);
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
