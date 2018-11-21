package com.videonasocialmedia.vimojo.sound.presentation.views.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.SoundRecyclerViewClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by ruth on 13/09/16.
 */
public class SoundListAdapter extends  RecyclerView.Adapter<SoundListAdapter.SoundListItemViewHolder> {

    private static final String LOG_TAG = SoundListAdapter.class.getCanonicalName();
    private Context context;
    private List<Music> musicList;
    private SoundRecyclerViewClickListener clickListener;
    private int selectedVideoPosition = -1;

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
        drawThumbnail(holder.soundImage, music);
        holder.soundTitle.setText(music.getMusicTitle());
        holder.soundAuthor.setText(music.getAuthor());
        holder.soundDuration.setText(music.getMusicDuration());
        holder.bindData(position, selectedVideoPosition);
        }

    private void drawThumbnail(ImageView soundImage, Media currentMedia) {
        Music currentMusic = (Music) currentMedia;
        Glide.with(context)
            .load(currentMusic.getIconResourceId())
            .asBitmap()
            .transform(new RoundedCornersTransformation(context, 10, 10))
            .override(soundImage.getMaxWidth(), soundImage.getMaxHeight())
            .signature(new StringSignature(currentMusic.getUuid() + currentMusic.getStartTime()))
            .error(R.drawable.fragment_gallery_no_image)
            .into(soundImage);
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

    public void updateSelection(int position) {
        if (selectedVideoPosition == position) {
            Log.d(LOG_TAG, "musicList: updateSelection same position");
            return;
        }
        notifyItemChanged(selectedVideoPosition);
        this.selectedVideoPosition = position;
        notifyItemChanged(position);
        Log.d(LOG_TAG, "musicList: updateSelection position " + position);
    }

    class SoundListItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.music_title)
        TextView soundTitle;
        @BindView(R.id.music_image)
        ImageView soundImage;
        @BindView(R.id.music_author)
        TextView soundAuthor;
        @BindView(R.id.music_duration)
        TextView soundDuration;

        //private MusicRecyclerViewClickListener clickListener;
        private List<Music> musicList;

        public SoundListItemViewHolder(View itemView, List<Music> musicList) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        //this.clickListener = clickListener;
            this.musicList = musicList;

         }

        @OnClick({R.id.music_item_layout})
        public void onClick() {
            Music music = musicList.get(getAdapterPosition());
            clickListener.onClick(music);
        }

        public void bindData(int position, int selectedVideoPosition) {
            this.soundImage.setSelected(position == selectedVideoPosition);
        }
    }
}
