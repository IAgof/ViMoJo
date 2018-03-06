/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.model.sources;

import com.videonasocialmedia.vimojo.model.entities.editor.ProductType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by alvaro on 6/3/18.
 */

public class ProductTypeProvider {

  public enum Types {
    // Live on tape
    LIVE_ON_TAPE,
    // B-Roll
    B_ROLL,
    // NAT/VO
    NAT_VO,
    // Interview
    INTERVIEW,
    // Graphics
    GRAPHICS,
    // Piece
    PIECE
  }

  public static List<ProductType> getProductTypeList() {
    List<ProductType> productTypeList = new ArrayList<>();
    productTypeList.add(new ProductType(0, Types.LIVE_ON_TAPE.name()));
    productTypeList.add(new ProductType(1, Types.B_ROLL.name()));
    productTypeList.add(new ProductType(2, Types.NAT_VO.name()));
    productTypeList.add(new ProductType(3, Types.INTERVIEW.name()));
    productTypeList.add(new ProductType(4, Types.GRAPHICS.name()));
    productTypeList.add(new ProductType(5, Types.PIECE.name()));
    return productTypeList;
  }

}
