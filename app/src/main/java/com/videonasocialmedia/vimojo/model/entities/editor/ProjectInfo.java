/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.model.entities.editor;

import java.util.List;

/**
 * Project info.
 * Class to unify info for Project, title, description, product types, ...
 * <p>
 * Created by alvaro on 7/2/18.
 */

public class ProjectInfo {

  private String title;
  private String description;
  private List<String> productTypeList;

  public ProjectInfo(String title, String description, List<String> productTypeList) {
    this.title = title;
    this.description = description;
    this.productTypeList = productTypeList;
  }

  public ProjectInfo(ProjectInfo projectInfo) {
    this.title = projectInfo.getTitle();
    this.description = projectInfo.getDescription();
    this.productTypeList = projectInfo.getProductTypeList();
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<String> getProductTypeList() {
    return productTypeList;
  }

  public void setProductTypeList(List<String> productTypeList) {
    this.productTypeList = productTypeList;
  }

}
