package com.videonasocialmedia.vimojo.sound.presentation.views.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.presentation.views.adapter.helper.ItemTouchHelperViewHolder;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.AudioTimeLineRecyclerViewClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by alvaro on 7/03/17.
 */

public class AudioTimeLineAdapter extends RecyclerView.Adapter<AudioTimeLineAdapter.AudioViewHolder> {

  private AudioTimeLineRecyclerViewClickListener audioTimeLineListener;
  private Context context;
  private List<Video> audioList;

  public AudioTimeLineAdapter(AudioTimeLineRecyclerViewClickListener audioTimeLineListener){
    this.audioTimeLineListener = audioTimeLineListener;
    this.audioList = new ArrayList<>();
  }

  @Override
  public AudioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    this.context = parent.getContext();
    View view = LayoutInflater.from(context)
        .inflate(R.layout.edit_audiotimeline_audio_item, parent, false);
    return new AudioViewHolder(view);
  }

  @Override
  public void onBindViewHolder(AudioViewHolder holder, int position) {
    Video current = audioList.get(position);
    holder.thumbOrder.setText(String.valueOf(position + 1));
  }

  @Override
  public int getItemCount() {
    return audioList.size();
  }

  public void setAudioList(List<Video> audioList) {
    this.audioList = audioList;
  }

  class AudioViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

    @Bind(R.id.text_clip_order)
    TextView thumbOrder;

    public AudioViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    @OnClick(R.id.timeline_audio_thumb)
    public void onClipClick() {
      audioTimeLineListener.onAudioClipClicked(getAdapterPosition());
    }

    @Override
    public void onItemSelected() {

    }

    @Override
    public void onItemClear() {

    }
  }
}
