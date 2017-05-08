package com.videonasocialmedia.vimojo.presentation.views.adapter.timeline;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoTestApplication;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.presentation.views.adapter.helper.VideoTimeLineTouchHelperCallbackAdapter;
import com.videonasocialmedia.vimojo.presentation.views.listener.VideoTimeLineRecyclerViewClickListener;
import com.videonasocialmedia.vimojo.test.shadows.ShadowMultiDex;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by jliarte on 25/04/17.
 */
@RunWith(RobolectricTestRunner.class)
@Config(application = VimojoTestApplication.class, constants = BuildConfig.class, sdk = 21,
        shadows = {ShadowMultiDex.class}, packageName = "com.videonasocialmedia.vimojo.debug")
public class VideoTimeLineAdapterTest {
  private VideoTimeLineAdapter videoTimeLineAdapter;
  private EditActivity editActivity;

  @Mock private VideoTimeLineRecyclerViewClickListener mockedListener;

  @Before
  public void setUpTestDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @Before
  public void setUpAdapter() {
    videoTimeLineAdapter = new VideoTimeLineAdapter(mockedListener);
    editActivity = Robolectric.buildActivity(EditActivity.class).create().get();
  }

  @Test
  public void adapterImplementsVideoTimeLineTouchHelperCallbackAdapter() {
    assertThat(videoTimeLineAdapter, is(instanceOf(VideoTimeLineTouchHelperCallbackAdapter.class)));
  }

  @Test
  public void testOnBindViewHolderCallsViewHolderBindData() {
    TimeLineVideoViewHolder videoItemViewholder
            = getVideoViewHolder(videoTimeLineAdapter);
    TimeLineVideoViewHolder holderSpy = spy(videoItemViewholder);
    Video video = new Video("media/path");
    videoTimeLineAdapter.updateVideoList(Collections.singletonList(video));

    videoTimeLineAdapter.onBindViewHolder(holderSpy, 0);

    verify(holderSpy).bindData(video, 0, 0);
  }

  @Test
  public void setVideoListNotifiesDataSetChange() {
    Video video = new Video("media/path");
    VideoTimeLineAdapter adapterSpy = Mockito.spy(videoTimeLineAdapter);
    adapterSpy.updateVideoList(Collections.singletonList(video));

    verify(adapterSpy).notifyDataSetChanged();
  }

  @Test
  public void getItemCountReturnsVideoListSize() {
    assertThat(videoTimeLineAdapter.getItemCount(), is(0));

    Video video = new Video("media/path");
    videoTimeLineAdapter.updateVideoList(Collections.singletonList(video));

    assertThat(videoTimeLineAdapter.getItemCount(), is(1));
  }

  @Test
  public void onItemMoveForwards() {
    ArrayList<Video> videoList = new ArrayList<>();
    Video video1 = new Video("video/1");
    Video video2 = new Video("video/2");
    Video video3 = new Video("video/3");
    videoList.add(video1);
    videoList.add(video2);
    videoList.add(video3);
    videoTimeLineAdapter.updateVideoList(videoList);

    videoTimeLineAdapter.onItemMove(0, 1);

    assertThat(videoList.get(0), is(video2));
    assertThat(videoList.get(1), is(video1));
    assertThat(videoList.get(2), is(video3));
  }

  @Test
  public void onItemMoveBackwards() {
    ArrayList<Video> videoList = new ArrayList<>();
    Video video1 = new Video("video/1");
    Video video2 = new Video("video/2");
    Video video3 = new Video("video/3");
    videoList.add(video1);
    videoList.add(video2);
    videoList.add(video3);
    videoTimeLineAdapter.updateVideoList(videoList);

    videoTimeLineAdapter.onItemMove(2, 1);

    assertThat(videoList.get(0), is(video1));
    assertThat(videoList.get(1), is(video3));
    assertThat(videoList.get(2), is(video2));
  }

  @Test
  public void onItemMoveMoreThanOnePosition() {
    ArrayList<Video> videoList = new ArrayList<>();
    Video video1 = new Video("video/1");
    Video video2 = new Video("video/2");
    Video video3 = new Video("video/3");
    Video video4 = new Video("video/4");
    videoList.add(video1);
    videoList.add(video2);
    videoList.add(video3);
    videoList.add(video4);
    videoTimeLineAdapter.updateVideoList(videoList);

    videoTimeLineAdapter.onItemMove(1, 3);

    assertThat(videoList.get(0), is(video1));
    assertThat(videoList.get(1), is(video3));
    assertThat(videoList.get(2), is(video4));
    assertThat(videoList.get(3), is(video2));
  }

  @Test
  public void onItemMoveNotifiesChangeToAdapter() {
    ArrayList<Video> videoList = getVideoListWithTwoItems();
    VideoTimeLineAdapter adapterSpy = Mockito.spy(videoTimeLineAdapter);
    adapterSpy.updateVideoList(videoList);

    adapterSpy.onItemMove(1, 0);

    verify(adapterSpy).notifyItemMoved(1,0);
  }

  @Test
  public void onItemMoveNotifiesChangeToListener() {
    ArrayList<Video> videoList = getVideoListWithTwoItems();
    videoTimeLineAdapter.updateVideoList(videoList);

    videoTimeLineAdapter.onItemMove(0, 1);

    verify(mockedListener).onClipMoved(0, 1);
  }

  @NonNull
  private ArrayList<Video> getVideoListWithTwoItems() {
    ArrayList<Video> videoList = new ArrayList<>();
    Video video1 = new Video("video/1");
    Video video2 = new Video("video/2");
    videoList.add(video1);
    videoList.add(video2);
    return videoList;
  }

  @Test
  public void updateSelection() {
    VideoTimeLineAdapter adapterSpy = Mockito.spy(videoTimeLineAdapter);
    List<Video> videoList = new ArrayList<>();
    videoList.add(new Video("video/1"));
    videoList.add(new Video("video/2"));
    adapterSpy.updateVideoList(videoList);
    assertThat(adapterSpy.getSelectedVideoPosition(), is(0));

    adapterSpy.updateSelection(1);

    verify(adapterSpy).notifyItemChanged(0);
    verify(adapterSpy).notifyItemChanged(1);
    assertThat(adapterSpy.getSelectedVideoPosition(), is(1));
  }

  @Test
  public void remove() {
    videoTimeLineAdapter.remove(5);

    verify(mockedListener).onClipRemoveClicked(5);
  }

  @Test
  public void finishMovementNotifiesListener() {
    videoTimeLineAdapter.finishMovement(4);

    verify(mockedListener).onClipReordered(4);
  }

  @NonNull
  private TimeLineVideoViewHolder getVideoViewHolder(VideoTimeLineAdapter adapter) {
    View viewRoot = editActivity.findViewById(android.R.id.content);
    View videoItem = LayoutInflater.from(editActivity)
            .inflate(R.layout.edit_videotimeline_video_item, (ViewGroup) viewRoot);
    return new TimeLineVideoViewHolder(adapter, videoItem, mockedListener);
  }
}