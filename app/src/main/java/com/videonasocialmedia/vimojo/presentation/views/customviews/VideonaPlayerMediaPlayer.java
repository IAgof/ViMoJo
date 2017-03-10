package com.videonasocialmedia.vimojo.presentation.views.customviews;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayerExo;
import com.videonasocialmedia.videonamediaframework.playback.customviews.AspectRatioVideoView;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

/**
 * Created by jliarte on 13/05/16.
 */
/**
 * Use new Implementation based in ExoPlayer instead: see {@link VideonaPlayerExo}
 * @deprecated
 */
public class VideonaPlayerMediaPlayer extends RelativeLayout implements VideonaPlayer,
        SeekBar.OnSeekBarChangeListener {

    private final Context context;
    protected Handler handler = new Handler();
    @Bind(R.id.video_editor_preview)
    AspectRatioVideoView videoPreview;
    @Bind(R.id.seekbar_editor_preview)
    SeekBar seekBar;
    @Bind(R.id.button_editor_play_pause)
    ImageButton playButton;

    private String TAG = VideonaPlayerMediaPlayer.class.getCanonicalName();
    private View videonaPlayerView;
    private VideonaPlayer.VideonaPlayerListener videonaPlayerListener;
    private AudioManager audio;
    private MediaPlayer videoPlayer;
    private MediaPlayer musicPlayer;
    private int totalVideoDuration = 0;
    private List<Video> videoList;
    private int currentTimePositionInList = 0;
    private int currentVideoListIndex = 0;
    private boolean isFullScreenBack = false;
    private List<Integer> videoStartTimesInTimeList;
    private List<Integer> videoStopTimesInTimeList;
    private final Runnable updateTimeTask = new Runnable() {
        @Override
        public void run() {
            try {
                updateSeekBarProgress();
            } catch (Exception e) {
                Log.d(TAG, "Exception in update seekbar progress thread");
                Log.d(TAG, String.valueOf(e));
            }
        }
    };
    private Music music;
    private Handler mainHandler;

    /** VideonaPlayer Constructors **/
    public VideonaPlayerMediaPlayer(Context context) {
        super(context);
        this.context = context;
        this.videonaPlayerView = ( (Activity) getContext() ).getLayoutInflater().inflate(R.layout.video_preview, this, true);
        ButterKnife.bind(this, videonaPlayerView);
        initVideonaPlayer();
    }

    public VideonaPlayerMediaPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.videonaPlayerView = ( (Activity) getContext() ).getLayoutInflater().inflate(R.layout.video_preview, this, true);
        ButterKnife.bind(this, videonaPlayerView);
        initVideonaPlayer();
    }

    public VideonaPlayerMediaPlayer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        this.videonaPlayerView = ( (Activity) getContext() ).getLayoutInflater().inflate(R.layout.video_preview, this, true);
        ButterKnife.bind(this, videonaPlayerView);
        initVideonaPlayer();
    }

    private void initVideonaPlayer() {
        audio = (AudioManager) context.getApplicationContext()
                .getSystemService(Context.AUDIO_SERVICE);
        initClipList();
        initSeekBar();
    }
    /** End of VideonaPlayer Constructors **/

    /****
     * Videona player lifecycle methods
     ****/
    @Override
    public void onShown(Context context) {
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onPause() {
        pausePreview();
        releaseVideoView();
        releaseMusicPlayer();
    }

    /**
     * Releases the media player and the video view
     */
    private void releaseVideoView() {
        videoPreview.stopPlayback();
        videoPreview.clearFocus();
        if (videoPlayer != null) {
            videoPlayer.release();
            videoPlayer = null;
        }
    }

    private void releaseMusicPlayer() {
        if (musicPlayer != null) {
            musicPlayer.stop();
            musicPlayer.release();
            musicPlayer = null;
        }
    }

    private void pauseVideo() {
        if (videoPlayer != null && videoPlayer.isPlaying())
            videoPlayer.pause();
    }

    private void pauseMusic() {
        if (musicPlayer != null && musicPlayer.isPlaying())
            musicPlayer.pause();
    }

    /****
     * end of Videona player lifecycle methods
     ****/
    private void showPlayButton() {
        playButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void setListener(VideonaPlayer.VideonaPlayerListener videonaPlayerListener) {
        this.videonaPlayerListener = videonaPlayerListener;
    }

//    @Override
//    public void initVideonaPlayer(VideonaPlayerListener videonaPlayerListener) {
//        setListener(videonaPlayerListener);
//    }

    private void initSeekBar() {
        seekBar.setProgress(0);
        seekBar.setOnSeekBarChangeListener(this);
    }

    private void initClipList() {
        this.videoList = new ArrayList<>();
    }

    @Override
    public void playPreview() {
        initPreviewLists(videoList);
        seekBar.setMax(totalVideoDuration);
        initPreview(currentTimePositionInList);
        hidePlayButton();
    }

    @Override
    public void pausePreview() {
        pauseVideo();
        pauseMusic();
        currentTimePositionInList = seekBar.getProgress();
        showPlayButton();
    }

    @Override
    public void seekTo(int timeInMsec) {
        if (videoPlayer != null && timeInMsec < totalVideoDuration) {
            currentTimePositionInList = timeInMsec;
            videoPlayer.seekTo(timeInMsec);
            seekBar.setProgress(timeInMsec);
        }
    }

    @Override
    public void seekClipTo(int seekTimeInMsec) {
        // TODO(jliarte): 7/09/16 implement this method and set the other relative to whole time line
    }

    @Override
    public void seekToClip(int position) {
        currentVideoListIndex = position;

        int progress = videoStartTimesInTimeList.get(currentVideoListIndex) -
                videoList.get(currentVideoListIndex).getStartTime();

        currentTimePositionInList = progress;
        seekBar.setProgress(progress);

        int timeInMsec = progress - videoStartTimesInTimeList.get(currentVideoListIndex) +
                videoList.get(currentVideoListIndex).getStartTime();

        if (videoPlayer != null) {
            seekToNextClip(videoList.get(position), timeInMsec);
        } else {
            initVideoPlayer(videoList.get(position), timeInMsec);
        }

        showPlayButton();
    }

    @Override
    public void setMusic(Music music) {
        this.music = music;
    }

    @Override
    public void setVoiceOver(Music voiceOver) {

    }


    @Override
    public void setVolume(float volume) {
        if (musicPlayer != null) {
            musicPlayer.setVolume(volume, volume);
        }
    }

    @Override
    public void setVideoTransitionFade() {
        // do nothing, implemented only for VideonaPlayerExo
    }

    @Override
    public void setAudioTransitionFade() {
        // do nothing, implemented only for VideonaPlayerExo
    }

    @Override
    public void bindVideoList(List<Video> videoList) {
        this.initPreviewLists(videoList);
        this.initPreview(currentTimePositionInList);
    }

    @Override
    public void initPreviewLists(List<Video> videoList) {
        this.videoList = videoList;
        this.updatePreviewTimeLists();
        this.setSeekBarTotalVideoDuration();
    }

    private void setSeekBarTotalVideoDuration() {
        seekBar.setMax(totalVideoDuration);
    }

    @Override
    public void updatePreviewTimeLists() {
        totalVideoDuration = 0;
        videoStartTimesInTimeList = new ArrayList<>();
        videoStopTimesInTimeList = new ArrayList<>();
        for (Video video : videoList) {
            videoStartTimesInTimeList.add(totalVideoDuration);
            totalVideoDuration = totalVideoDuration + video.getDuration();
            videoStopTimesInTimeList.add(totalVideoDuration);
        }
    }

    private void hidePlayButton() {
        playButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void initPreview(int instantTime) {
        if (playerHasVideos()) {
            this.currentTimePositionInList = instantTime;
            seekBar.setProgress(instantTime);

            Video clipToPlay = getClipByProgress(this.currentTimePositionInList);
            currentVideoListIndex = getClipPositioninList(clipToPlay);
            int timeInMsec = this.currentTimePositionInList
                    - videoStartTimesInTimeList.get(currentVideoListIndex)
                    + videoList.get(currentVideoListIndex).getStartTime();
            if (isFullScreenBack) {
                showPlayButton();
                initVideoPlayer(clipToPlay, timeInMsec);
                isFullScreenBack = false;
            } else {
                if (videoPlayer == null) {
                    initVideoPlayer(clipToPlay, timeInMsec);
                } else {
                    playNextClip(clipToPlay, timeInMsec);
                }
            }
        } else {
            seekBar.setProgress(0);
            showPlayButton();
            currentVideoListIndex = 0;
            this.currentTimePositionInList = 0;
        }
    }

    private void initVideoPlayer(final Video video, final int startTime) {
        videoPreview.setVideoPath(video.getMediaPath());
        videoPreview.canSeekBackward();
        videoPreview.canSeekForward();
        videoPreview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoPlayer = mp;
                videoPlayer.setVolume(0.5f, 0.5f);
                videoPlayer.setLooping(false);
                videoPlayer.seekTo(startTime);
                seekBar.setProgress(startTime);
//                videoPlayer.start();
                // TODO(jliarte): 17/06/16 This has to be called in order updateTimeTask to start the thread
                updateSeekBarProgress();
//                try {
//                    Thread.sleep(10);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                videoPlayer.onPause();
            }
        });
        videoPreview.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                videoPlayer.reset();
                initVideoPlayer(video, currentTimePositionInList);
                return false;
            }
        });
        videoPreview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                currentVideoListIndex++;
                if (hasNextClipToPlay()) {
                    playNextClip(videoList.get(currentVideoListIndex),
                            videoList.get(currentVideoListIndex).getStartTime());
                }
            }
        });
        videoPreview.requestFocus();
    }

    private Video getClipByProgress(int progress) {
        int result = -1;
        if (0 <= progress && progress < videoStopTimesInTimeList.get(0)) {
            currentVideoListIndex = 0;
        } else {
            for (int i = 0; i < videoStopTimesInTimeList.size(); i++) {
                if (i < videoStopTimesInTimeList.size() - 1) {
                    boolean inRange = videoStopTimesInTimeList.get(i) <= progress &&
                            progress < videoStopTimesInTimeList.get(i + 1);
                    if (inRange) {
                        result = i + 1;
                    }
                }
            }
            if (result == -1) {
                currentVideoListIndex = videoStopTimesInTimeList.size() - 1;
            } else {
                currentVideoListIndex = result;
            }
        }
        return videoList.get(currentVideoListIndex);
    }

    private int getClipPositioninList(Video seekVideo) {
        int position = 0;
        for (Video video : videoList) {
            if (video == seekVideo) {
                position = videoList.indexOf(video); // TODO(jliarte): this could be done with just this line?
            }
        }
        return position;
    }

    private void playNextClip(final Video video, final int instantToStart) {
        notifyNewClipPlayed();
        videoPreview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                try {
                    videoPlayer.setLooping(false);
                    videoPlayer.start();
                    if (playButton.getVisibility() == View.VISIBLE)
                        hidePlayButton();
                    videoPlayer.seekTo(instantToStart);
                    if (videoHasMusic()) {
                        muteVideo();
                        playMusicSyncWithVideo();
                    } else {
                        releaseMusicPlayer();
                        videoPlayer.setVolume(0.5f, 0.5f);
                    }
                } catch (Exception e) {
                    // TODO don't force media player. Media player must be null here
                    seekBar.setProgress(0);
                    showPlayButton();
                }
            }
        });
        videoPreview.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                videoPlayer.reset();
                initVideoPlayer(video, currentTimePositionInList);
                return false;
            }
        });
        videoPreview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                currentVideoListIndex++;
                if (hasNextClipToPlay()) {
                    playNextClip(videoList.get(currentVideoListIndex),
                            videoList.get(currentVideoListIndex).getStartTime());
                } else {
                    releaseView();
                }
            }
        });
        try {
            videoPlayer.reset();
            videoPlayer.setDataSource(video.getMediaPath());
            videoPlayer.prepare();
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
        videoPreview.requestFocus();
    }

    private void notifyNewClipPlayed() {
        if (videonaPlayerListener != null)
            videonaPlayerListener.newClipPlayed(currentVideoListIndex);
    }

    private void seekToNextClip(final Video video, final int instantToStart) {
        notifyNewClipPlayed();
        videoPreview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                try {
                    videoPlayer.seekTo(instantToStart);
                } catch (Exception e) {
                    // TODO don't force media player. Media player must be null here
                    seekBar.setProgress(0);
                    showPlayButton();
                }
            }
        });
        videoPreview.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                videoPlayer.reset();
                initVideoPlayer(video, currentTimePositionInList);
                return false;
            }
        });
        try {
            videoPlayer.reset();
            videoPlayer.setDataSource(video.getMediaPath());
            videoPlayer.prepare();
            videoPlayer.seekTo(instantToStart);
            videoPlayer.start();
            videoPlayer.pause();
            pauseMusic();
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }

    private boolean hasNextClipToPlay() {
        return currentVideoListIndex < videoList.size();
    }

    private boolean videoHasMusic() {
        return (music != null);
    }

    private void muteVideo() {
        videoPlayer.setVolume(0, 0);
    }

    private void playMusicSyncWithVideo() {
        releaseMusicPlayer();
        initMusicPlayer();
        if (!musicPlayer.isPlaying()) {
            musicPlayer.start();
        }
        musicPlayer.seekTo(seekBar.getProgress());
    }

    private void initMusicPlayer() {
        if (musicPlayer == null && videoHasMusic()) {
            musicPlayer = MediaPlayer.create(context, Uri.parse(music.getMediaPath()));
            musicPlayer.setVolume(0.5f, 0.5f);
        }
    }

    private boolean isEndOfCurrentClip() {
        return seekBar.getProgress() >= videoStopTimesInTimeList.get(currentVideoListIndex);
    }

    public void releaseView() {
        showPlayButton();
        releaseVideoView();
        setBlackBackgroundColor();
        currentVideoListIndex = 0;
        if (playerHasVideos())
            initVideoPlayer(videoList.get(currentVideoListIndex),
                    videoList.get(currentVideoListIndex).getStartTime() + 100);
        seekBar.setProgress(0);
        currentTimePositionInList = 0; // TODO(jliarte): 1/09/16 duplicated?
        notifyNewClipPlayed(); // TODO(jliarte): 30/08/16 does this call make sense here? :m

        // TODO(jliarte): 30/08/16 check this call
        releaseMusicPlayer();
//        if (musicPlayer != null && musicPlayer.isPlaying()) {
//            musicPlayer.pause();
//            releaseMusicPlayer();
//        }
    }

    private boolean playerHasVideos() {
        return this.videoList.size() > 0;
    }

    @OnClick(R.id.button_editor_play_pause)
    public void onClickPlayPauseButton() {
        if (playerHasVideos()) {
            if (videoPlayer != null) {
                if (videoPlayer.isPlaying()) {
                    videoPlayer.pause();
//                    pausePreview();
                } else {
                    videoPlayer.start();
                }
            } else {
                playPreview();
            }
        } else {
            seekBar.setProgress(0);
        }
    }

    @OnTouch(R.id.video_editor_preview)
    public boolean onTouchPreview(MotionEvent event) {
        boolean result = false;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            onClickPlayPauseButton();
            result = true;
        }
        return result;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                return true;
            default:
                return false;
        }
    }

    private void setBlackBackgroundColor() {
        videoPreview.setBackgroundColor(Color.BLACK);
    }

    private void updateSeekBarProgress() {
        if (videoPlayer != null) {
            try {
//                if (videoPlayer.isPlaying() && currentVideoListIndex < videoList.size()) {
                if (currentVideoListIndex < videoList.size()) {
                    seekBar.setProgress(videoPlayer.getCurrentPosition() +
                            videoStartTimesInTimeList.get(currentVideoListIndex) -
                            videoList.get(currentVideoListIndex).getStartTime());
                    if (isEndOfCurrentClip()) {
                        currentVideoListIndex++;
                        if (hasNextClipToPlay()) {
                            playNextClip(videoList.get(currentVideoListIndex),
                                    videoList.get(currentVideoListIndex).getStartTime());
                        } else {
                            releaseView();
                        }
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "updateSeekBarProgress: exception updating videonaplayer seekbar");
                Log.d(TAG, String.valueOf(e));
            }
            handler.postDelayed(updateTimeTask, 20);
        }
    }

//    private void updateSeekBarProgressSlim() {
//        if (videoPlayer != null) {
//            if (currentVideoListIndex < videoList.size()) {
//                seekBar.setProgress(videoPlayer.getCurrentPosition() +
//                        videoStartTimesInTimeList.get(currentVideoListIndex) -
//                        videoList.get(currentVideoListIndex).getStartTime());
//            }
//            handler.postDelayed(updateTimeTask, 20);
//        }
//    }


    @Override
    public void setSeekBarProgress(int progress){
        seekBar.setProgress(progress);
    }

    @Override
    public void setSeekBarLayoutEnabled(boolean seekBarEnabled) {
        if (seekBarEnabled) {
            seekBar.setVisibility(VISIBLE);
        } else {
            seekBar.setVisibility(GONE);
        }
    }

    @Override
    public void resetPreview() {
        setBlackBackgroundColor();
        showPlayButton();
        initPreview(0);
        setSeekBarProgress(0);
    }

    /**
     * Seekbar listeners
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            if (playerHasVideos()) {
                Video video = getClipByProgress(progress);
                int timeInMsec = progress - videoStartTimesInTimeList.get(currentVideoListIndex) +
                        videoList.get(currentVideoListIndex).getStartTime();

                if (videoPlayer != null) {
                    playNextClip(video, timeInMsec);
                } else {
                    initVideoPlayer(video, timeInMsec);
                }
                hidePlayButton();
            } else {
                seekBar.setProgress(0);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public int getCurrentPosition() {
        return currentTimePositionInList;
    }

}
