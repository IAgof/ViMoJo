/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.sync.presentation;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by alvaro on 31/1/18.
 */

/*
 * Define an implementation of ContentProvider that stubs out
 * all methods
 * Dummy class, needed for authorities and plug with SyncAdapter
 * https://developer.android.com/training/sync-adapters/index.html
 */
public class StubProvider extends ContentProvider {
  /*
   * Always return true, indicating that the
   * provider loaded correctly.
   */
  @Override
  public boolean onCreate() {
    return true;
  }

  /*
   * Return no type for MIME type
   */
  @Override
  public String getType(Uri uri) {
    return null;
  }

  /*
   * query() always returns no results
   *
   */
  @Override
  public Cursor query(
      Uri uri,
      String[] projection,
      String selection,
      String[] selectionArgs,
      String sortOrder) {
    return null;
  }

  /*
   * insert() always returns null (no URI)
   */
  @Override
  public Uri insert(Uri uri, ContentValues values) {
    return null;
  }

  /*
   * delete() always returns "no rows affected" (0)
   */
  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs) {
    return 0;
  }

  /*
   * update() always returns "no rows affected" (0)
   */
  public int update(
      Uri uri,
      ContentValues values,
      String selection,
      String[] selectionArgs) {
    return 0;
  }
}