package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.VimojoApplication;
import com.videonasocialmedia.vimojo.domain.social.ObtainNetworksToShareUseCase;
import com.videonasocialmedia.vimojo.model.entities.social.SocialNetwork;
import com.videonasocialmedia.vimojo.presentation.mvp.views.ShareVideoView;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.Utils;

import java.util.List;

/**
 * Created by jca on 11/12/15.
 */
public class ShareVideoPresenter {

    private ObtainNetworksToShareUseCase obtainNetworksToShareUseCase;
    private ShareVideoView shareVideoView;
    private SharedPreferences sharedPreferences;

    public ShareVideoPresenter(ShareVideoView shareVideoView) {
        this.shareVideoView = shareVideoView;
        sharedPreferences = VimojoApplication.getAppContext().getSharedPreferences(
                ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE);
    }

    public void onCreate() {
        obtainNetworksToShareUseCase = new ObtainNetworksToShareUseCase();
    }

    public void onResume() {
        obtainNetworksToShare();
    }

    public void obtainNetworksToShare() {
        List networks = obtainNetworksToShareUseCase.obtainMainNetworks();

        shareVideoView.showShareNetworksAvailable(networks);
    }

    public void shareVideo(String videoPath, SocialNetwork appToShareWith, Context ctx) {
        final ComponentName name = new ComponentName(appToShareWith.getAndroidPackageName(),
                appToShareWith.getAndroidActivityName());

        Uri uri = Utils.obtainUriToShare(ctx, videoPath);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("video/*");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                VimojoApplication.getAppContext().getResources().getString(R.string.sharedWithVideona));
        intent.putExtra(Intent.EXTRA_TEXT,
                VimojoApplication.getAppContext().getResources().getString(R.string.videonaTags));
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        intent.setComponent(name);

        ctx.startActivity(intent);
    }

    public void obtainExtraAppsToShare() {
        List networks = obtainNetworksToShareUseCase.obtainSecondaryNetworks();
        shareVideoView.hideShareNetworks();
        shareVideoView.showMoreNetworks(networks);
    }

    public double getVideoLength() {
        return sharedPreferences.getLong(ConfigPreferences.VIDEO_DURATION, 0);
    }

    public double getNumberOfClips() {
        return sharedPreferences.getInt(ConfigPreferences.NUMBER_OF_CLIPS, 1);
    }

    public String getResolution() {
        return sharedPreferences.getString(ConfigPreferences.RESOLUTION, "1280x720");
    }

}
