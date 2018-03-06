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

  private boolean directFalseTypeSelected = false;
  private boolean rawVideoTypeSelected = false;
  private boolean spoolTypeSelected = false;
  private boolean totalTypeSelected = false;
  private boolean graphicTypeSelected = false;
  private boolean pieceTypeSelected = false;


  public enum ProductType {
    // Live on tape
    DIRECT_FAILURE,
    // B-Roll
    RAW_VIDEOS,
    // NAT/VO
    SPOOLERS,
    // Interview
    TOTAL,
    // Graphics
    GRAPHIC,
    // Piece
    PIECE
  }

  public ProjectInfo(String title, String description, List<String> productTypeList) {
    this.title = title;
    this.description = description;
    this.productTypeList = productTypeList;
    checkSupportedProductType(productTypeList);
  }

  public ProjectInfo(ProjectInfo projectInfo) {
    this.title = projectInfo.getTitle();
    this.description = projectInfo.getDescription();
    this.productTypeList = projectInfo.getProductTypeList();
    checkSupportedProductType(productTypeList);
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
    clearSupportedProductType();
    checkSupportedProductType(productTypeList);
  }

  private void clearSupportedProductType() {
    directFalseTypeSelected = false;
    rawVideoTypeSelected = false;
    spoolTypeSelected = false;
    totalTypeSelected = false;
    graphicTypeSelected = false;
    pieceTypeSelected = false;
  }

  public void checkSupportedProductType(List<String> productTypeList) {
    for (String productType : productTypeList) {
      if (productType.equals(ProductType.DIRECT_FAILURE.name())) {
        directFalseTypeSelected = true;
      } else {
        if (productType.equals(ProductType.RAW_VIDEOS.name())) {
          rawVideoTypeSelected = true;
        } else {
          if (productType.equals(ProductType.SPOOLERS.name())) {
            spoolTypeSelected = true;
          } else {
            if (productType.equals(ProductType.TOTAL.name())) {
              totalTypeSelected = true;
            } else {
              if (productType.equals(ProductType.GRAPHIC.name())) {
                graphicTypeSelected = true;
              } else {
                if (productType.equals(ProductType.PIECE.name())) {
                  pieceTypeSelected = true;
                }
              }
            }
          }
        }
      }
    }
  }


  public boolean isDirectFalseTypeSelected() {
    return directFalseTypeSelected;
  }

  public boolean isRawVideoTypeSelected() {
    return rawVideoTypeSelected;
  }

  public boolean isSpoolTypeSelected() {
    return spoolTypeSelected;
  }

  public boolean isTotalTypeSelected() {
    return totalTypeSelected;
  }

  public boolean isGraphicTypeSelected() {
    return graphicTypeSelected;
  }

  public boolean isPieceTypeSelected() {
    return pieceTypeSelected;
  }

}
