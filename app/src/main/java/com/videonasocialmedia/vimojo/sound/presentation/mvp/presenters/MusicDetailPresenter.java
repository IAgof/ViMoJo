package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import com.crashlytics.android.Crashlytics;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.utils.ElementChangedListener;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.domain.editor.GetAudioFromProjectUseCase;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnRemoveMediaFinishedListener;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.GetPreferencesTransitionFromProjectUseCase;
import android.content.Context;
import android.support.design.widget.TabLayout;
import android.util.Log;

import com.videonasocialmedia.vimojo.sound.domain.AddAudioUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMusicListUseCase;
import com.videonasocialmedia.vimojo.sound.domain.ModifyTrackUseCase;
import com.videonasocialmedia.vimojo.sound.domain.RemoveAudioUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;

import com.videonasocialmedia.vimojo.presentation.mvp.presenters.GetMusicFromProjectCallback;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnAddMediaFinishedListener;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.presentation.mvp.views.MusicDetailView;
import com.videonasocialmedia.vimojo.sync.AssetUploadQueue;
import com.videonasocialmedia.vimojo.sync.helper.RunSyncAdapterHelper;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.view.VimojoPresenter;
import com.videonasocialmedia.vimojo.vimojoapiclient.CompositionApiClient;
import com.videonasocialmedia.vimojo.vimojoapiclient.VimojoApiException;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.AssetUpload;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.Nullable;
import javax.inject.Inject;


/**
 *
 */
public class MusicDetailPresenter extends VimojoPresenter implements OnVideosRetrieved,
    GetMusicFromProjectCallback, ElementChangedListener {

    private final String LOG_TAG = getClass().getSimpleName();
    private final ProjectInstanceCache projectInstanceCache;
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    private GetAudioFromProjectUseCase getAudioFromProjectUseCase;
    private GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase;
    private MusicDetailView musicDetailView;
    protected UserEventTracker userEventTracker;
    protected Project currentProject;
    private Music musicSelected;
    private Context context;
    private AddAudioUseCase addAudioUseCase;
    private RemoveAudioUseCase removeAudioUseCase;
    private ModifyTrackUseCase modifyTrackUseCase;
    private GetMusicListUseCase getMusicListUseCase;
    private final AssetUploadQueue assetUploadQueue;
    private final RunSyncAdapterHelper runSyncAdapterHelper;
    private final CompositionApiClient compositionApiClient;

    @Inject
    public MusicDetailPresenter(
        MusicDetailView musicDetailView, Context context, UserEventTracker userEventTracker,
        GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
        GetAudioFromProjectUseCase getAudioFromProjectUseCase,
        GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase,
        AddAudioUseCase addAudioUseCase, RemoveAudioUseCase removeAudioUseCase,
        ModifyTrackUseCase modifyTrackUseCase, GetMusicListUseCase getMusicListUseCase,
        ProjectInstanceCache projectInstanceCache, AssetUploadQueue assetUploadQueue,
        RunSyncAdapterHelper runSyncAdapterHelper, CompositionApiClient compositionApiClient) {
        this.musicDetailView = musicDetailView;
        this.userEventTracker = userEventTracker;
        this.getMediaListFromProjectUseCase = getMediaListFromProjectUseCase;
        this.getAudioFromProjectUseCase = getAudioFromProjectUseCase;
        this.getPreferencesTransitionFromProjectUseCase =
            getPreferencesTransitionFromProjectUseCase;
        this.context = context;
        this.addAudioUseCase = addAudioUseCase;
        this.removeAudioUseCase = removeAudioUseCase;
        this.modifyTrackUseCase = modifyTrackUseCase;
        this.getMusicListUseCase = getMusicListUseCase;
        this.projectInstanceCache = projectInstanceCache;
        musicSelected = new Music("", 0);
        this.assetUploadQueue = assetUploadQueue;
        this.runSyncAdapterHelper = runSyncAdapterHelper;
        this.compositionApiClient = compositionApiClient;
    }

    public void updatePresenter(String musicPath) {
        this.currentProject = projectInstanceCache.getCurrentProject();
        currentProject.addListener(this);
        musicSelected = retrieveLocalMusic(musicPath);
        // TODO:(alvaro.martinez) 12/04/17 Delete this force of volume when Vimojo support more
        // than one music, at this moment, music track same as music volume
        musicSelected.setVolume(currentProject.getAudioTracks()
            .get(Constants.INDEX_AUDIO_TRACK_MUSIC).getVolume());
        obtainMusicsAndVideos();
        if (getPreferencesTransitionFromProjectUseCase
                .isVideoFadeTransitionActivated(currentProject)) {
            musicDetailView.setVideoFadeTransitionAmongVideos();
        }
    }

    private void obtainMusicsAndVideos() {
        getAudioFromProjectUseCase.getMusicFromProject(currentProject, this);
        getMediaListFromProjectUseCase.getMediaListFromProject(currentProject, this);
    }

    public void removeMusic(final Music music) {
        removeAudioUseCase.removeMusic(currentProject, music, Constants.INDEX_AUDIO_TRACK_MUSIC,
            new OnRemoveMediaFinishedListener() {
            @Override
            public void onRemoveMediaItemFromTrackSuccess() {
                userEventTracker.trackMusicSet(currentProject);
                musicDetailView.goToSoundActivity();
            }
            @Override
            public void onRemoveMediaItemFromTrackError() {
                musicDetailView.showError(context
                    .getString(R.string.alert_dialog_title_message_removing_music));
            }
        });
    }

    private Music retrieveLocalMusic(String musicPath) {
        Music result = null;
        List<Music> musicList = getMusicListUseCase.getAppMusic();
        for (Music music : musicList) {
            if (musicPath.compareTo(music.getMediaPath()) == 0) {
                result = music;
            }
        }
        return result;
    }

    public void addMusic(Music music, float volume) {
        music.setVolume(volume);
        addAudioUseCase.addMusic(currentProject, music, Constants.INDEX_AUDIO_TRACK_MUSIC,
            new OnAddMediaFinishedListener() {
            @Override
            public void onAddMediaItemToTrackSuccess(Media media) {
                userEventTracker.trackMusicSet(currentProject);
                updateCompositionWithPlatform(currentProject);
                addAssetToUpload(music);
                musicDetailView.goToSoundActivity();
            }

            @Override
            public void onAddMediaItemToTrackError() {
                musicDetailView.showError(
                    context.getString(R.string.alert_dialog_title_message_adding_music));
            }
        });
    }

    private void updateCompositionWithPlatform(Project currentProject) {
        ListenableFuture<Project> compositionFuture = executeUseCaseCall(new Callable<Project>() {
            @Override
            public Project call() throws Exception {
                return compositionApiClient.uploadComposition(currentProject);
            }
        });
        Futures.addCallback(compositionFuture, new FutureCallback<Project>() {
            @Override
            public void onSuccess(@Nullable Project result) {
                Log.d(LOG_TAG, "Success uploading composition to server ");
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(LOG_TAG, "Error uploading composition to server " + t.getMessage());
            }
        });
    }

    private void addAssetToUpload(Media media) {
        // TODO: 21/6/18 Get projectId, currentCompositin.getProjectId()
        AssetUpload assetUpload = new AssetUpload("ElConfiHack", media);
        executeUseCaseCall((Callable<Void>) () -> {
            try {
                assetUploadQueue.addAssetToUpload(assetUpload);
                Log.d(LOG_TAG, "uploadAsset " + assetUpload.getName());
            } catch (IOException ioException) {
                ioException.printStackTrace();
                Log.d(LOG_TAG, ioException.getMessage());
                Crashlytics.log("Error adding asset to upload");
                Crashlytics.logException(ioException);
            }
            return null;
        });
        runSyncAdapterHelper.runNowSyncAdapter();
    }

    @Override
    public void onVideosRetrieved(List<Video> videoList) {
        musicDetailView.bindVideoList(videoList);
        if(currentProject.hasVoiceOver()){
            retrieveVoiceOver();
        }
    }

    @Override
    public void onNoVideosRetrieved() {
        musicDetailView.showError("No videos retrieved");
    }

    @Override
    public void onMusicRetrieved(Music musicOnProject) {

        if (musicOnProject!= null && musicOnProject.getMediaPath()
                .compareTo(musicSelected.getMediaPath()) == 0) {
            // TODO:(alvaro.martinez) 12/04/17 Delete this force of volume when Vimojo support
            // more than one music, at this moment, music track same as music volume
            musicOnProject.setVolume(currentProject.getAudioTracks()
                .get(Constants.INDEX_AUDIO_TRACK_MUSIC).getVolume());
            musicDetailView.setMusic(musicOnProject, true);
        } else {
            musicDetailView.setMusic(musicSelected, false);
        }
    }

    private void retrieveVoiceOver() {
        getAudioFromProjectUseCase.getVoiceOverFromProject(currentProject, new GetMusicFromProjectCallback() {
            @Override
            public void onMusicRetrieved(Music voiceOver) {
                musicDetailView.setVoiceOver(voiceOver);
            }
        });
    }

    public void setVolume(float volume) {
        // Now setVolume update MusicTrackVolume until Vimojo support setVolume by clip.
        modifyTrackUseCase.setTrackVolume(currentProject, currentProject.getAudioTracks()
            .get(Constants.INDEX_AUDIO_TRACK_MUSIC), volume);
    }

    @Override
    public void onObjectUpdated() {
        musicDetailView.updateProject();
    }
}
