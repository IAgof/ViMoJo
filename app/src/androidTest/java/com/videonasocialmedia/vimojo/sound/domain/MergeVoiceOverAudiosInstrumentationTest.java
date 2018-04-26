package com.videonasocialmedia.vimojo.sound.domain;

import android.media.MediaMetadataRetriever;

import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.integration.AssetManagerAndroidTest;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.utils.Constants;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by alvaro on 9/10/17.
 */

public class MergeVoiceOverAudiosInstrumentationTest extends AssetManagerAndroidTest{

  private String testPath;
  @Mock OnMergeVoiceOverAudiosListener mockedOnMergeVoiceOverAudiosListener;
  private Project currentProject;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    testPath = getInstrumentation().getTargetContext().getExternalCacheDir()
        .getAbsolutePath();
    getAProject();
  }

  @Test
  public void testMergeVoiceOverAudios() throws IOException {

    MergeVoiceOverAudiosUseCase mergeAudio = new MergeVoiceOverAudiosUseCase();
    String voiceOver1Path = getAssetPath("VoiceOver1.mp4");
    String voiceOver2Path = getAssetPath("VoiceOver2.mp4");
    String voiceOver3Path = getAssetPath("VoiceOver3.mp4");
    String pathFinalAudioMerged = new File(voiceOver1Path).getAbsolutePath() + File.separator +
        Constants.AUDIO_TEMP_RECORD_VOICE_OVER_FILENAME;

    mergeAudio.mergeAudio(currentProject, pathFinalAudioMerged, mockedOnMergeVoiceOverAudiosListener);

    File fileAudioMerged = new File(pathFinalAudioMerged);
    assertThat(fileAudioMerged.exists(), is(true));
    int audioMergedDuration = Integer.parseInt(getVideoDuration(pathFinalAudioMerged));
    int voiceOver1Duration = Integer.parseInt(getVideoDuration(new File(voiceOver1Path)
        .getAbsolutePath()));
    int voiceOver2Duration = Integer.parseInt(getVideoDuration(new File(voiceOver2Path)
        .getAbsolutePath()));
    int voiceOver3Duration = Integer.parseInt(getVideoDuration(new File(voiceOver3Path)
        .getAbsolutePath()));
    int totalVoiceOverDuration = voiceOver1Duration + voiceOver2Duration + voiceOver3Duration;
    assertThat(audioMergedDuration, is(totalVoiceOverDuration));
  }

  private String getVideoDuration(String videoPath) {
    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
    mediaMetadataRetriever.setDataSource(videoPath);
    return mediaMetadataRetriever
        .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
  }

  private void getAProject() {
    Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
        VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
    ProjectInfo projectInfo = new ProjectInfo("Project title",
        "Project description", new ArrayList<>());
    currentProject = new Project(projectInfo, "/path","private/path", compositionProfile);
  }
}
