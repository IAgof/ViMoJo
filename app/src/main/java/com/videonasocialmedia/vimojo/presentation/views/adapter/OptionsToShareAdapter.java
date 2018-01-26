package com.videonasocialmedia.vimojo.presentation.views.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.model.entities.social.FtpNetwork;
import com.videonasocialmedia.vimojo.model.entities.social.SocialNetwork;
import com.videonasocialmedia.vimojo.model.entities.social.VimojoNetwork;
import com.videonasocialmedia.vimojo.presentation.mvp.views.OptionsToShareList;
import com.videonasocialmedia.vimojo.presentation.views.listener.OnOptionsToShareListClickListener;

import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ruth on 18/10/16.
 */

public class OptionsToShareAdapter extends RecyclerView.Adapter<OptionsToShareAdapter.ViewHolder> {
    List<OptionsToShareList> optionsToShareLists;
    OnOptionsToShareListClickListener clickListener;

    public OptionsToShareAdapter(List<OptionsToShareList> networkList, OnOptionsToShareListClickListener clickListener){
        this.optionsToShareLists = networkList;
        this.clickListener = clickListener;
        notifyDataSetChanged();
    }

    public OptionsToShareAdapter(OnOptionsToShareListClickListener clickListener){
        this.clickListener = clickListener;
        optionsToShareLists = new ArrayList<>();
    }

    public int getItemViewType(int position) {
        return optionsToShareLists.get(position).getListShareType();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View rowView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.share_network_viewholder, viewGroup, false);
        switch (viewType){
            case OptionsToShareList.typeFtp:
                return new ViewHolderFTP(rowView);
            case OptionsToShareList.typeSocialNetwork:
                return new ViewHolderSocialNetwork(rowView);
            case OptionsToShareList.typeVimojoNetwork:
                return new ViewHolderVimojo(rowView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        OptionsToShareList network= optionsToShareLists.get(position);
        holder.bindType(network);
    }

    @Override
    public int getItemCount() {
        return optionsToShareLists.size();
    }

    public void setOptionShareLists(List<OptionsToShareList> optionsToShareLists){
        this.optionsToShareLists = optionsToShareLists;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(final View itemView) {
            super(itemView);
        }
        public void bindType(OptionsToShareList networkList) {}
    }

    public class ViewHolderVimojo extends ViewHolder {
        @Bind(R.id.icon)
        ImageView icon;
        @Nullable
        @Bind(R.id.name)
        TextView name;
        @Nullable
        @Bind(R.id.checkbox)
        CheckBox checkBox;

        public ViewHolderVimojo(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    clickListener.onVimojoClicked((VimojoNetwork) optionsToShareLists.get(position));
                }
            });
        }

        public void bindType(OptionsToShareList item) {
            if (name != null)
                name.setText(((VimojoNetwork)item).getName());
            icon.setImageResource(((VimojoNetwork)item).getIcon());
        }
    }

    public class ViewHolderFTP extends ViewHolder {
        @Bind(R.id.icon)
        ImageView icon;
        @Nullable
        @Bind(R.id.name)
        TextView name;
        @Nullable
        @Bind(R.id.checkbox)
        CheckBox checkBox;

        public ViewHolderFTP(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    clickListener.onFtpClicked((FtpNetwork) optionsToShareLists.get(position));
                }
            });
        }

        public void bindType(OptionsToShareList item) {
            if (name != null)
                name.setText(((FtpNetwork)item).getName());
            icon.setImageResource(((FtpNetwork)item).getIcon());
        }
    }

    public class ViewHolderSocialNetwork extends ViewHolder {
        @Bind(R.id.icon)
        ImageView icon;
        @Nullable
        @Bind(R.id.name)
        TextView name;
        @Nullable
        @Bind(R.id.checkbox)
        CheckBox checkBox;

        public ViewHolderSocialNetwork(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    clickListener.onSocialNetworkClicked((SocialNetwork) optionsToShareLists.get(position));
                }
            });
        }
        public void bindType(OptionsToShareList item) {
            icon.setImageDrawable(((SocialNetwork)item).getIcon());
            if (name != null)
                name.setText(((SocialNetwork)item).getName());
        }

    }


}
