package com.videonasocialmedia.vimojo.presentation.views.adapter.timeline.helper;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoTestApplication;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.presentation.views.adapter.helper.VideoTimeLineTouchHelperCallbackAdapter;
import com.videonasocialmedia.vimojo.presentation.views.adapter.timeline.TimeLineVideoViewHolder;
import com.videonasocialmedia.vimojo.presentation.views.adapter.timeline.VideoTimeLineAdapter;
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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by jliarte on 25/04/17.
 */
@RunWith(RobolectricTestRunner.class)
@Config(application = VimojoTestApplication.class, constants = BuildConfig.class, sdk = 21,
        shadows = {ShadowMultiDex.class}, packageName = "com.videonasocialmedia.vimojo.debug")
public class VideoTimeLineTouchHelperCallbackTest {
  @Mock private VideoTimeLineTouchHelperCallbackAdapter mockedCallbackAdapter;
  @Mock private RecyclerView mockedRecyclerView;
  @Mock private RecyclerView.ViewHolder mockedViewHolder;
  @Mock private RecyclerView.ViewHolder mockedViewHolderTarget;
  @Mock private VideoTimeLineRecyclerViewClickListener mockedListener;

  @Before
  public void setUpTestDoubles() {
    MockitoAnnotations.initMocks(this);
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

    verify(videoTimeLineAdapter).finishMovement(viewHolder.getAdapterPosition());
  }
}