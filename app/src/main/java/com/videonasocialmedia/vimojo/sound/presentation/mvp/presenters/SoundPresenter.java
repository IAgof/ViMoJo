package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.track.AudioTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.domain.editor.GetMusicFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.project.GetTracksInProjectCallback;
import com.videonasocialmedia.vimojo.domain.project.GetTracksInProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.GetMusicFromProjectCallback;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.VideoListErrorCheckerDelegate;
import com.videonasocialmedia.vimojo.presentation.mvp.views.VideoTranscodingErrorNotifier;
import com.videonasocialmedia.vimojo.settings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.UpdateAudioTrackProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.UpdateVideoTrackProjectUseCase;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.SoundView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by ruth on 13/09/16.
 */
public class SoundPresenter implements OnVideosRetrieved, GetMusicFromProjectCallback,
    GetTracksInProjectCallback{

    private SoundView soundView;
    private final VideoTranscodingErrorNotifier videoTranscodingErrorNotifier;
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    private GetMusicFromProjectUseCase getMusicFromProjectUseCase;
    private GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase;
    private UpdateVideoTrackProjectUseCase updateVideoTrackProjectUseCase;
    private UpdateAudioTrackProjectUseCase updateAudioTrackProjectUseCase;
    private final Project currentProject;
  // TODO(jliarte): 2/05/17 inject delegate?
  final VideoListErrorCheckerDelegate videoListErrorCheckerDelegate
          = new VideoListErrorCheckerDelegate();

  private GetTracksInProjectUseCase getTracksInProjectUseCase;

   @Inject
    public SoundPresenter(SoundView soundView,
        VideoTranscodingErrorNotifier videoTranscodingErrorNotifier,
        GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
        GetMusicFromProjectUseCase getMusicFromProjectUseCase,
        GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase,
        UpdateVideoTrackProjectUseCase updateVideoTrackProjectUseCase,
        UpdateAudioTrackProjectUseCase updateAudioTrackProjectUseCase,
        GetTracksInProjectUseCase getTracksInProjectUseCase) {

        this.soundView = soundView;
        this.videoTranscodingErrorNotifier = videoTranscodingErrorNotifier;
        this.getMediaListFromProjectUseCase = getMediaListFromProjectUseCase;
        this.getMusicFromProjectUseCase = getMusicFromProjectUseCase;
        this.currentProject = loadCurrentProject();
        this.getPreferencesTransitionFromProjectUseCase = getPreferencesTransitionFromProjectUseCase;
        this.updateVideoTrackProjectUseCase = updateVideoTrackProjectUseCase;
        this.updateAudioTrackProjectUseCase = updateAudioTrackProjectUseCase;
        this.getTracksInProjectUseCase = getTracksInProjectUseCase;
    }

    public Project loadCurrentProject() {
        // TODO(jliarte): this should make use of a repository or use case to load the Project
        return Project.getInstance(null, null, null);
    }

    public void init() {
      checkVoiceOverFeatureToggle(BuildConfig.FEATURE_VOICE_OVER);
      // TODO:(alvaro.martinez) 22/03/17 Player should be in charge of these checks from VMComposition 
      checkAVTransitionsActivated();
      retrieveTracksInfo();
      obtainVideos();
      retrieveCompositionMusic();
    }

  private void retrieveTracksInfo() {
    getTracksInProjectUseCase.getTracksInProject(this);
  }

  private void checkAVTransitionsActivated() {
    if(getPreferencesTransitionFromProjectUseCase.isVideoFadeTransitionActivated()){
      soundView.setVideoFadeTransitionAmongVideos();
    }
    if(getPreferencesTransitionFromProjectUseCase.isAudioFadeTransitionActivated() &&
        !currentProject.getVMComposition().hasMusic()){
      soundView.setAudioFadeTransitionAmongVideos();
    }
  }

  private void retrieveCompositionMusic() {
    if (currentProject.hasMusic()) {
        getMusicFromProjectUseCase.getMusicFromProject(this);
      }
      if(currentProject.hasVoiceOver()){
        getMusicFromProjectUseCase.getVoiceOverFromProject(this);
      }
    }

    private void obtainVideos() {
        getMediaListFromProjectUseCase.getMediaListFromProject(this);
    }

    @Override
    public void onVideosRetrieved(List<Video> videoList) {
        soundView.bindVideoList(videoList);
        videoListErrorCheckerDelegate.checkWarningMessageVideosRetrieved(videoList, videoTranscodingErrorNotifier);
    }

  @Override
    public void onNoVideosRetrieved() {
        //TODO Show error
        soundView.resetPreview();
    }

    @Override
    public void onMusicRetrieved(Music music) {
        // TODO:(alvaro.martinez) 7/03/17 Get from project use case list<Music> instead music
        List<Music> musicList = new ArrayList<>();
        musicList.add(music);
        if (isMusicAVoiceOver(music)) {
          soundView.bindVoiceOverList(musicList);
        } else {
          soundView.bindMusicList(musicList);
        }
    }

  protected void checkVoiceOverFeatureToggle(boolean featureVoiceOver) {
    if(featureVoiceOver){
      soundView.addVoiceOverOptionToFab();
    } else {
      soundView.hideVoiceOverCardView();
    }
  }

  private boolean isMusicAVoiceOver(Music music) {
    return music.getMusicTitle()
        .compareTo(com.videonasocialmedia.vimojo.utils.Constants.MUSIC_AUDIO_VOICEOVER_TITLE) == 0;
  }

  public void setVideoVolume(int videoVolume) {
    MediaTrack mediaTrack = currentProject.getMediaTrack();
    updateVideoTrackProjectUseCase.setVideoTrackVolume(mediaTrack, (float) (videoVolume*0.01));
  }


  public void setMusicVolume(int musicVolume) {
    AudioTrack track = currentProject.getAudioTracks().get(Constants.INDEX_AUDIO_TRACKS_MUSIC);
    updateAudioTrackProjectUseCase.setAudioTrackVolume(track, (float) (musicVolume*0.01));
  }

  public void setVoiceOverVolume(int voiceOverVolume) {
    AudioTrack track = currentProject.getAudioTracks().get(Constants.INDEX_AUDIO_TRACKS_VOICE_OVER);
    updateAudioTrackProjectUseCase.setAudioTrackVolume(track, (float) (voiceOverVolume*0.01));
  }


  public void soloVideo(boolean isChecked) {
    MediaTrack mediaTrack = currentProject.getMediaTrack();
    updateVideoTrackProjectUseCase.setVideoTrackSolo(mediaTrack, isChecked);
  }

  public void soloMusic(boolean isChecked) {
    AudioTrack track = currentProject.getAudioTracks().get(Constants.INDEX_AUDIO_TRACKS_MUSIC);
    updateAudioTrackProjectUseCase.setAudioTrackSolo(track, isChecked);
  }

  public void soloVoiceOver(boolean isChecked) {
    AudioTrack track = currentProject.getAudioTracks().get(Constants.INDEX_AUDIO_TRACKS_VOICE_OVER);
    updateAudioTrackProjectUseCase.setAudioTrackSolo(track, isChecked);
  }

  public void muteVideo(boolean isChecked) {
    MediaTrack mediaTrack = currentProject.getMediaTrack();
    updateVideoTrackProjectUseCase.setVideoTrackMute(mediaTrack, isChecked);
  }

  public void muteMusic(boolean isChecked) {
    AudioTrack track = currentProject.getAudioTracks().get(Constants.INDEX_AUDIO_TRACKS_MUSIC);
    updateAudioTrackProjectUseCase.setAudioTrackMute(track, isChecked);
  }

  public void muteVoiceOver(boolean isChecked) {
    AudioTrack track = currentProject.getAudioTracks().get(Constants.INDEX_AUDIO_TRACKS_VOICE_OVER);
    updateAudioTrackProjectUseCase.setAudioTrackMute(track, isChecked);
  }

  @Override
  public void onTracksRetrieved(List<Track> trackList) {
    Track musicTrack = null;
    Track voiceOverTrack = null;
    for(Track track: trackList){
      switch (track.getId()){
        case Constants.INDEX_MEDIA_TRACKS:
          if(currentProject.getVMComposition().hasVideos())
            soundView.bindVideoTrack(track.getVolume(), track.isMute(), track.isSolo());
          break;
        case Constants.INDEX_AUDIO_TRACKS_MUSIC:
          if(currentProject.hasMusic())
            musicTrack = track;

          break;
        case Constants.INDEX_AUDIO_TRACKS_VOICE_OVER:
          if(currentProject.hasVoiceOver())
            voiceOverTrack = track;
          break;
        default:
      }
    }

    if(musicTrack != null && voiceOverTrack == null){
      soundView.bindMusicTrack(musicTrack.getVolume(), musicTrack.isMute(), musicTrack.isSolo(),
          musicTrack.getPosition());
      return;
    }

    if(musicTrack == null && voiceOverTrack != null){
      soundView.bindVoiceOverTrack(voiceOverTrack.getVolume(), voiceOverTrack.isMute(),
          voiceOverTrack.isSolo(), voiceOverTrack.getPosition());
      return;
    }

    if(musicTrack != null && voiceOverTrack != null){
      if(musicTrack.getPosition() > voiceOverTrack.getPosition()){
        soundView.bindVoiceOverTrack(voiceOverTrack.getVolume(), voiceOverTrack.isMute(),
            voiceOverTrack.isSolo(), voiceOverTrack.getPosition());
        soundView.bindMusicTrack(musicTrack.getVolume(), musicTrack.isMute(), musicTrack.isSolo(),
            musicTrack.getPosition());
      } else {
        soundView.bindMusicTrack(musicTrack.getVolume(), musicTrack.isMute(), musicTrack.isSolo(),
            musicTrack.getPosition());
        soundView.bindVoiceOverTrack(voiceOverTrack.getVolume(), voiceOverTrack.isMute(),
            voiceOverTrack.isSolo(), voiceOverTrack.getPosition());
      }
    }
  }
}
