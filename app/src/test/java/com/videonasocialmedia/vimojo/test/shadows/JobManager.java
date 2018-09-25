package com.videonasocialmedia.vimojo.test.shadows;

/**
 * Created by jliarte on 25/09/18.
 */

import org.robolectric.annotation.Implements;

/**
 * Shadow class for {@link com.birbit.android.jobqueue.JobManager} trying to fix tests problems:
 *
 */
@Implements(JobManager.class)
public class JobManager {
  /**
   * found error is:
   java.lang.RuntimeException: com.almworks.sqlite4java.SQLiteException: [-91] cannot load library: java.lang.UnsatisfiedLinkError: Native Library /tmp/robolectric-libs/li
       bsqlite4java.so already loaded in another classloader
       at org.robolectric.shadows.util.SQLiteLibraryLoader.loadFromDirectory(SQLiteLibraryLoader.java:147)
       at org.robolectric.shadows.util.SQLiteLibraryLoader.doLoad(SQLiteLibraryLoader.java:64)
       at org.robolectric.shadows.util.SQLiteLibraryLoader.load(SQLiteLibraryLoader.java:54)
       at org.robolectric.shadows.ShadowSQLiteConnection.nativeOpen(ShadowSQLiteConnection.java:72)
       at android.database.sqlite.SQLiteConnection.nativeOpen(SQLiteConnection.java)
       at android.database.sqlite.SQLiteConnection.open(SQLiteConnection.java:209)
       at android.database.sqlite.SQLiteConnection.open(SQLiteConnection.java:193)
       at android.database.sqlite.SQLiteConnectionPool.openConnectionLocked(SQLiteConnectionPool.java:463)
       at android.database.sqlite.SQLiteConnectionPool.open(SQLiteConnectionPool.java:185)
       at android.database.sqlite.SQLiteConnectionPool.open(SQLiteConnectionPool.java:177)
       at android.database.sqlite.SQLiteDatabase.openInner(SQLiteDatabase.java:806)
       at android.database.sqlite.SQLiteDatabase.open(SQLiteDatabase.java:791)
       at android.database.sqlite.SQLiteDatabase.openDatabase(SQLiteDatabase.java:694)
       at android.app.ContextImpl.openOrCreateDatabase(ContextImpl.java:1142)
       at android.content.ContextWrapper.openOrCreateDatabase(ContextWrapper.java:267)
       at android.database.sqlite.SQLiteOpenHelper.getDatabaseLocked(SQLiteOpenHelper.java:223)
       at android.database.sqlite.SQLiteOpenHelper.getWritableDatabase(SQLiteOpenHelper.java:163)
       at com.birbit.android.jobqueue.persistentQueue.sqlite.SqliteJobQueue.<init>(SqliteJobQueue.java:49)
       at com.birbit.android.jobqueue.DefaultQueueFactory.createPersistentQueue(DefaultQueueFactory.java:27)
       at com.birbit.android.jobqueue.JobManagerThread.<init>(JobManagerThread.java:87)
       at com.birbit.android.jobqueue.JobManager.<init>(JobManager.java:62)
   */

}
