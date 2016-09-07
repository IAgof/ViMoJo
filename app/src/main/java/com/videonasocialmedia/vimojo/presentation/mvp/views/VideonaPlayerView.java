package com.videonasocialmedia.vimojo.presentation.mvp.views;

import android.content.Context;

import com.videonasocialmedia.vimojo.model.entities.editor.media.Music;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.presentation.views.listener.VideonaPlayerListener;

import java.util.List;

/**
 * Created by jliarte on 13/05/16.
 */
public interface VideonaPlayerView {

    void onShown(Context context);

    void onDestroy();

    void onPause();

    void setListener(VideonaPlayerListener videonaPlayerListener);

    void initPreview(int instantTime);

    void initPreviewLists(List<Video> videoList);

    void bindVideoList(List<Video>videoList);

    void updatePreviewTimeLists();

    void playPreview();

    void pausePreview();

    void seekTo(int timeInMsec);

    void seekClipTo(int seekTimeInMsec);

    void seekToClip(int position);

    void setMusic(Music music);

    int getCurrentPosition();

    void setSeekBarProgress(int progress);

    void setSeekBarEnabled(boolean seekBarEnabled);

    void resetPreview();
}
