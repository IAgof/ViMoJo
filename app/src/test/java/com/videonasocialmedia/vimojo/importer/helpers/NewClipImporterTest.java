package com.videonasocialmedia.vimojo.importer.helpers;

import android.os.Environment;
import android.util.Log;

import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.domain.editor.ApplyAVTransitionsUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideoFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.RelaunchTranscoderTempBackgroundUseCase;
import com.videonasocialmedia.vimojo.importer.model.entities.VideoToAdapt;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptRealmRepository;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptRepository;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.record.domain.AdaptVideoToFormatUseCase;
import com.videonasocialmedia.vimojo.repository.video.VideoRealmRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.verification.VerificationMode;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by Alvaro on 03/10/2017.
 */

//@RunWith(MockitoJUnitRunner.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class, Environment.class})
public class NewClipImporterTest {

    @InjectMocks NewClipImporter injectedNewClipImporter;

    @Mock AdaptVideoToFormatUseCase mockedAdaptVideoToFormatUseCase;
    @Mock
    VideoToAdapt mockedVideoToAdapt;
    @Mock
    VideonaFormat mockedVideonaFormat;
    @Mock
    AdaptVideoToFormatUseCase.AdaptListener mockedAdaptListener;
    private File mockedStorageDir;
    @Mock NewClipImporter mockedNewClipImporter;
    @Mock GetVideoFormatFromCurrentProjectUseCase mockedGetVideoFormatFromCurrentProjectUseCase;
    @Mock VideoToAdaptRepository mockedVideoToAdaptRepository;
    private NewClipImporter newClipImporter;
    @Mock ApplyAVTransitionsUseCase mockedLaunchTranscoderAddAVTransitionUseCase;
    @Mock RelaunchTranscoderTempBackgroundUseCase mockedRelaunchTranscoderTempBackgroundUseCase;
    @Mock VideoRepository mockedVideoRepository;

    @Before
    public void injectMocks() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(Log.class);

        PowerMockito.mockStatic(Environment.class);
        mockedStorageDir = PowerMockito.mock(File.class);
        when(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)).
                thenReturn(mockedStorageDir);
        when(Environment.getExternalStorageDirectory()).thenReturn(mockedStorageDir);
    }

    @Before
    public void setUpNewClipImporter() {
        newClipImporter = new NewClipImporter(mockedGetVideoFormatFromCurrentProjectUseCase,
                mockedAdaptVideoToFormatUseCase, mockedLaunchTranscoderAddAVTransitionUseCase,
                mockedRelaunchTranscoderTempBackgroundUseCase, mockedVideoRepository,
                mockedVideoToAdaptRepository);
    }

    @Test
    public void adaptVideoToVideonaFormatCallsAdaptVideoToFormatUseCase() throws IOException {
        Project project = getAProject();
        Video video = new Video(".temporal/Vid1234.mp4", Video.DEFAULT_VOLUME);
        int position = 0;
        int cameraRotation =0;
        int retries = 0;
        VideonaFormat videonaFormat = project.getVMComposition().getVideoFormat();
        when(mockedGetVideoFormatFromCurrentProjectUseCase
                .getVideonaFormatToAdaptVideoRecordedAudioAndVideo()).thenReturn(videonaFormat);

        injectedNewClipImporter.adaptVideoToVideonaFormat(project, video, position,
                cameraRotation, retries);

        Mockito.verify(mockedAdaptVideoToFormatUseCase).adaptVideo(any(VideoToAdapt.class),
                eq(videonaFormat), any(AdaptVideoToFormatUseCase.AdaptListener.class));
    }

    @Test
    public void relaunchUnfinishedAdaptTasksCallsNewClipImporterAdaptVideoToVideonaFormat()
            throws IllegalItemOnTrack, IOException {
        Project project = getAProject();
        Video video = new Video(".temp/path", Video.DEFAULT_VOLUME);
        project.getVMComposition().getMediaTrack().insertItem(video);
        String destVideoPath = "DCIM/ViMoJo/Masters";
        int position = 0;
        int cameraRotation = 0;
        int retries = 0;
        VideoToAdapt videoToAdapt = new VideoToAdapt(video, destVideoPath, position, cameraRotation,
                retries);
        mockedVideoToAdaptRepository.add(videoToAdapt);
        List<VideoToAdapt> videosToAdapt = mockedVideoToAdaptRepository.getAllVideos();
        videosToAdapt.add(videoToAdapt);
        NewClipImporter newClipImporterSpy = Mockito.spy(newClipImporter);
        newClipImporterSpy.videoToAdaptRepository = mockedVideoToAdaptRepository;
        when(mockedVideoToAdaptRepository.getAllVideos()).thenReturn(videosToAdapt);

        newClipImporterSpy.relaunchUnfinishedAdaptTasks(project);

        Mockito.verify(newClipImporterSpy).adaptVideoToVideonaFormat(project, video, position,
                cameraRotation,
                ++retries);
        assertThat(videosToAdapt.get(0).getVideo().getIdentifier(), is(video.getIdentifier()));
    }

    @Test
    public void relaunchUnfinishedAdaptTasksGetCorrectVideoReference() throws IllegalItemOnTrack {
        Project project = getAProject();
        Video video = new Video(".temp/path", Video.DEFAULT_VOLUME);
        VideoRepository videoRepository =  Mockito.spy(new VideoRealmRepository());
        videoRepository.update(video);
        project.getVMComposition().getMediaTrack().insertItem(video);
        String destVideoPath = "DCIM/ViMoJo/Masters";
        int position = 0;
        int cameraRotation = 0;
        int retries = 0;
        VideoToAdapt videoToAdapt =  new VideoToAdapt(video, destVideoPath, position, cameraRotation,
            retries);
        VideoToAdaptRepository videoToAdaptRepository = Mockito.spy(new
                VideoToAdaptRealmRepository());
        videoToAdaptRepository.update(videoToAdapt);
        // App kills, get video and videoToAdapt from repository
        Project restartProject = getAProject();
        Video video1 = videoRepository.getAllVideos().get(0);
        restartProject.getVMComposition().getMediaTrack().insertItem(video1);
        VideoToAdapt videoToAdaptRestarted = videoToAdaptRepository.getAllVideos().get(0);


        Video videoToRelaunch = injectedNewClipImporter.getVideoToRelaunch(restartProject,
                videoToAdaptRestarted);

        assertThat(videoToRelaunch, is(video));
        assertThat(video, is(videoToAdapt.getVideo()));
    }

    private Project getAProject() {
        return Project.getInstance("title", "/path", "private/path",
                Profile.getInstance(VideoResolution.Resolution.HD720,
                        VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25));
    }
}
