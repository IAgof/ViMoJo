package com.videonasocialmedia.vimojo.asset.domain.usecase;

/**
 * Created by jliarte on 16/08/18.
 */

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.asset.domain.helper.HashCountGenerator;
import com.videonasocialmedia.vimojo.asset.domain.model.Asset;
import com.videonasocialmedia.vimojo.asset.repository.AssetRepository;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.repository.DataPersistanceType;
import com.videonasocialmedia.vimojo.utils.FileUtils;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

/**
 * Use Case for retrieving asset files from backend.
 */
public class GetCompositionAssets {
  private static final String LOG_TAG = GetCompositionAssets.class.getSimpleName();
  private AssetRepository assetRepository;
  private HashCountGenerator hashCountGenerator;
  private DownloadManager downloadManager;

  private HashMap<Long, Asset> downloadRefs;
  private BroadcastReceiver onCompleteReceiver;
  private UpdateAssetFilesListener listener;
  private final ArrayList<String> assetIds;

  @Inject
  public GetCompositionAssets(AssetRepository assetRepository,
                              HashCountGenerator hashCountGenerator,
                              DownloadManager downloadManager) {
    this.assetIds = new ArrayList<>();
    this.assetRepository = assetRepository;
    this.hashCountGenerator = hashCountGenerator;
    this.downloadManager = downloadManager;
    this.downloadRefs = new HashMap<>();
    createOnCompleteReceiver();
  }

  public BroadcastReceiver updateAssetFiles(Project project, UpdateAssetFilesListener listener) {
    this.listener = listener;
    if (project.getDataPersistanceType() != DataPersistanceType.LOCAL) {
      ArrayList<Asset> assets = new ArrayList<Asset>(project.getAssets().values());
      for (Asset asset : assets) {
        if (!assetIds.contains(asset.getId())) {
          assetIds.add(asset.getId());
          updateAssetFile(asset);
        }
      }
    }
    if (downloadRefs.size() == 0) {
      notifyDownloadCompletion();
    }
    return getCompletionReceiver();
  }

  private void updateAssetFile(Asset asset) {
    // TODO(jliarte): 16/08/18 if asset is music, asset should be downloaded to music path or composition path
    Uri assetUri = Uri.parse(asset.getUri());
    String fileName = assetUri.getLastPathSegment();
    asset.setFileName(fileName);

    String assetFilePath = asset.getPath();
    if (FileUtils.fileExists(assetFilePath)) {
      if (!hashCountGenerator.getHash(assetFilePath).equals(asset.getHash())) {
        FileUtils.move(assetFilePath, getBakFilePath(assetFilePath));
        asset.setTmpFile(downloadAssetFile(asset));
      }
    } else {
      asset.setTmpFile(downloadAssetFile(asset));
    }
  }

  private String getBakFilePath(String filePath) {
    return filePath.substring(0, filePath.lastIndexOf(".")) + ".bak"
            + filePath.substring(filePath.lastIndexOf("."));
  }

  private String downloadAssetFile(Asset asset) {
    Uri assetUri = Uri.parse(asset.getUri());
    String fileName = assetUri.getLastPathSegment();
    asset.setFileName(fileName);
    DownloadManager.Request fileRequest = new DownloadManager.Request(assetUri);
//    fileRequest.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
    fileRequest.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
            .setAllowedOverRoaming(false)
            .setTitle("Downloading composition asset " + fileName)
            .setDescription("Downloading description")
            .setVisibleInDownloadsUi(true)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                    "/" + BuildConfig.FLAVOR + "/" + fileName);
    downloadRefs.put(downloadManager.enqueue(fileRequest), asset);
    return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            + "/" + BuildConfig.FLAVOR + "/" + fileName;
  }

  private BroadcastReceiver getCompletionReceiver() {
    return onCompleteReceiver;
  }

  @NonNull
  private BroadcastReceiver createOnCompleteReceiver() {
    this.onCompleteReceiver = new BroadcastReceiver() {
      public void onReceive(Context ctxt, Intent intent) {
        long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        Asset asset = downloadRefs.remove(referenceId);
//        FileUtils.move(asset.getTmpFile(), compositionPath + "/" + asset.getFileName())
        if (asset != null) {
          FileUtils.move(asset.getTmpFile(), asset.getPath());
          Log.d(LOG_TAG, "onComplete ref " + referenceId + " asset is null. DownloadRefs.size" +
                  " is " + downloadRefs.size() + " this " + this);
        }

        if (downloadRefs.isEmpty()) {
          notifyDownloadCompletion();
        } else {
          notifyDownloadProgress(downloadRefs.size());
        }

      }
    };
    return onCompleteReceiver;
  }

  private void notifyDownloadCompletion() {
    if (this.listener != null) {
      listener.onCompletion();
    }
  }

  private void notifyDownloadProgress(int remaining) {
    if (this.listener != null) {
      listener.onProgress(remaining);
    }
  }

  public interface UpdateAssetFilesListener {
    void onCompletion();
    void onProgress(int remaining);
  }
}
