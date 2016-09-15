package com.videonasocialmedia.vimojo.sound.presentation.views.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Music;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.SoundRecyclerViewClickListener;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ruth on 13/09/16.
 */
public class SoundListAdapter extends  RecyclerView.Adapter<SoundListAdapter.SoundListItemViewHolder> {

        Context context;
        List<Music> musicList;
        SoundRecyclerViewClickListener clickListener;

    public void setSoundRecyclerViewClickListener(SoundRecyclerViewClickListener
        SoundRecyclerViewClickListener) {
        clickListener = SoundRecyclerViewClickListener;
        }

    @Override
    public SoundListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View rowView = LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.sound_list_item_view_holder, viewGroup, false);
        this.context = viewGroup.getContext();
        return new SoundListItemViewHolder(rowView, musicList);
        }

    @Override
    public void onBindViewHolder(SoundListItemViewHolder holder, int position) {
        Music music = musicList.get(position);

        Glide.with(context)
        .load(music.getIconResourceId())
        .error(R.drawable.gatito_rules);
        holder.soundImage.setImageResource(music.getIconResourceId());
        holder.soundTitle.setText(music.getMusicTitle());
        holder.soundAuthor.setText(music.getAuthor());
        holder.soundDuration.setText(music.getDurationMusic());
        }

    @Override
    public int getItemCount() {
        int result = 0;
        if (musicList != null)
            result = musicList.size();
        return result;
        }


    public void setMusicList(List<Music> musicList) {
        this.musicList = musicList;
        notifyDataSetChanged();
        }

    class SoundListItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.music_title)
        TextView soundTitle;
        @Bind(R.id.music_image)
        ImageView soundImage;
        @Bind(R.id.music_author)
        TextView soundAuthor;
        @Bind(R.id.music_duration)
        TextView soundDuration;

        //private MusicRecyclerViewClickListener clickListener;
        private List<Music> musicList;

        public SoundListItemViewHolder(View itemView, List<Music> musicList) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        //this.clickListener = clickListener;
            this.musicList = musicList;

         }

        @OnClick({R.id.music_title, R.id.music_image, R.id.music_author})
        public void onClick() {
            Music music = musicList.get(getAdapterPosition());
            clickListener.onClick(music);
        }
    }
}
