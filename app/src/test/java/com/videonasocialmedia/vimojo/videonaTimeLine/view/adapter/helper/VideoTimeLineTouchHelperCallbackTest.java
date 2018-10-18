package com.videonasocialmedia.vimojo.videonaTimeLine.view.adapter.helper;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.main.VimojoTestApplication;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.test.shadows.JobManager;
import com.videonasocialmedia.vimojo.videonaTimeLine.view.customview.TimeLineVideoViewHolder;
import com.videonasocialmedia.vimojo.videonaTimeLine.view.adapter.VideoTimeLineAdapter;
import com.videonasocialmedia.vimojo.presentation.views.listener.VideoTimeLineRecyclerViewClickListener;
import com.videonasocialmedia.vimojo.test.shadows.ShadowMultiDex;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by jliarte on 25/04/17.
 */
@RunWith(RobolectricTestRunner.class)
@Config(application = VimojoTestApplication.class, constants = BuildConfig.class, sdk = 21,
        shadows = {ShadowMultiDex.class, JobManager.class}, packageName = "com.videonasocialmedia.vimojo.debug")
public class VideoTimeLineTouchHelperCallbackTest {
  @Mock private VideoTimeLineTouchHelperCallbackAdapterListener mockedCallbackAdapter;
  @Mock private RecyclerView mockedRecyclerView;
  @Mock private RecyclerView.ViewHolder mockedViewHolder;
  @Mock private RecyclerView.ViewHolder mockedViewHolderTarget;
  @Mock private VideoTimeLineRecyclerViewClickListener mockedListener;
  @Mock ProjectInstanceCache mockedProjectInstanceCache;
  private Project currentProject;

  @Before
  public void setUpTestDoubles() {
    MockitoAnnotations.initMocks(this);
 //   getAProject();
 //   when(mockedProjectInstanceCache.getCurrentProject()).thenReturn(currentProject);
  }

  @Test
  public void testGetMovementFlagsAllowsDragInAnyDirection() {
    VideoTimeLineTouchHelperCallback callback =
            new VideoTimeLineTouchHelperCallback(mockedCallbackAdapter);

    int movementFlags = callback.getMovementFlags(mockedRecyclerView, mockedViewHolder);

    int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
            ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT;
    assertThat(movementFlags, is(ItemTouchHelper.Callback.makeMovementFlags(
            dragFlags, 0)));
  }

  @Test
  public void testIsLongPressDragEnabled() {
    VideoTimeLineTouchHelperCallback callback =
            new VideoTimeLineTouchHelperCallback(mockedCallbackAdapter);

    assertThat(callback.isLongPressDragEnabled(), is(true));
  }

  @Test
  public void testIsItemViewSwipeEnabled() {
    VideoTimeLineTouchHelperCallback callback =
            new VideoTimeLineTouchHelperCallback(mockedCallbackAdapter);

    assertThat(callback.isItemViewSwipeEnabled(), is(false));
  }

  @Test
  public void onMoveCallsAdapter() {
    VideoTimeLineTouchHelperCallback callback =
            new VideoTimeLineTouchHelperCallback(mockedCallbackAdapter);
    doReturn(1).when(mockedViewHolderTarget).getAdapterPosition();

    callback.onMove(mockedRecyclerView, mockedViewHolder, mockedViewHolderTarget);

    verify(mockedCallbackAdapter).onItemMove(0, 1);
  }

  @Test
  public void clearViewCallsAdapterFinishMovement() {
    EditActivity editActivity = Robolectric.buildActivity(EditActivity.class).create().get();
    VideoTimeLineAdapter videoTimeLineAdapter = spy(new VideoTimeLineAdapter(mockedListener));
    View viewRoot = editActivity.findViewById(android.R.id.content);
    View videoItem = LayoutInflater.from(editActivity)
            .inflate(R.layout.edit_videotimeline_video_item, (ViewGroup) viewRoot);;
    TimeLineVideoViewHolder viewHolder =
            new TimeLineVideoViewHolder(videoTimeLineAdapter, videoItem, mockedListener);
    VideoTimeLineTouchHelperCallback callback =
            new VideoTimeLineTouchHelperCallback(videoTimeLineAdapter);

    callback.clearView(mockedRecyclerView, viewHolder);

    verify(videoTimeLineAdapter).finishMovement();
  }

  public void getAProject() {
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
        VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, "/path", "private/path", profile);
    if(currentProject.getVMComposition().getProfile() == null){
      currentProject.setProfile(profile);
    }
  }
}