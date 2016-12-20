package com.videonasocialmedia.vimojo.retrieveProjects.presentation.views.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.FileDescriptorBitmapDecoder;
import com.bumptech.glide.load.resource.bitmap.VideoBitmapDecoder;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.retrieveProjects.presentation.mvp.views.RetrieveProjectClickListener;
import com.videonasocialmedia.vimojo.utils.DateUtils;
import com.videonasocialmedia.vimojo.utils.TimeUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *
 */
public class RetrieveProjectListAdapter extends
    RecyclerView.Adapter<RetrieveProjectListAdapter.RetrieveProjectListItemViewHolder> {

  private Context context;
  private List<Project> projectList;
  private RetrieveProjectClickListener clickListener;

  public void setRetrieveProjectClickListener(RetrieveProjectClickListener
                                                  RetrieveProjectClickListener) {
    clickListener = RetrieveProjectClickListener;
  }

  @Override
  public RetrieveProjectListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
    View rowView = LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.retrieve_project_view_holder, viewGroup, false);
    this.context = viewGroup.getContext();
    return new RetrieveProjectListItemViewHolder(rowView, projectList);
  }

  @Override
  public void onBindViewHolder(RetrieveProjectListItemViewHolder holder, int position) {
    Project project = projectList.get(position);
    if (project.getMediaTrack().getItems().size() > 0) {
      drawVideoThumbnail(holder.imagenProject, project);
    } else {
      holder.imagenProject.setImageResource(R.drawable.activity_gallery_project_no_preview);
    }
    holder.dateProject.setText(DateUtils.toFormatDateDayMonthYear(project.getLastModification()));
    holder.durationProject.setText(
        TimeUtils.toFormattedTimeWithMinutesAndSeconds(project.getDuration()));
    holder.titleProject.setText(project.getTitle());
  }

  public void drawVideoThumbnail(ImageView thumbnailView, Project project) {

    Video firstVideo = (Video) project.getMediaTrack().getItems().get(0);
    int microSecond = firstVideo.getStartTime() * 1000;
    BitmapPool bitmapPool = Glide.get(context).getBitmapPool();
    FileDescriptorBitmapDecoder decoder = new FileDescriptorBitmapDecoder(
        new VideoBitmapDecoder(microSecond),
        bitmapPool,
        DecodeFormat.PREFER_ARGB_8888);
    String path = firstVideo.getIconPath() != null
        ? firstVideo.getIconPath() : firstVideo.getMediaPath();

    Glide.with(context)
        .load(path)
        .centerCrop()
        .error(R.drawable.fragment_gallery_no_image)
        .into(thumbnailView);
  }

  @Override
  public int getItemCount() {
    int result = 0;
    if (projectList != null)
      result = projectList.size();
    return result;
  }


  public void setProjectList(List<Project> projectList) {
    this.projectList = projectList;
    notifyDataSetChanged();
  }

  class RetrieveProjectListItemViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.retrieve_project_date)
    TextView dateProject;
    @Bind(R.id.retrieve_project_duration)
    TextView durationProject;
    @Bind(R.id.retrieve_project_image)
    ImageView imagenProject;
    @Bind(R.id.retrieve_project_title)
    TextView titleProject;


    private List<Project> projectList;

    public RetrieveProjectListItemViewHolder(View itemView, List<Project> projectList) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      this.projectList = projectList;
    }

    @OnClick({R.id.retrieve_project_menu})
    public void onClick() {
      Project project = projectList.get(getAdapterPosition());
      clickListener.onClick(project);
    }

    @OnClick(R.id.retrieve_project_button_duplicate)
    public void onClickDuplicateProject() {
      int position = getAdapterPosition();
      notifyItemChanged(position);
      clickListener.onDuplicateProject(projectList.get(position));
    }

    @OnClick(R.id.retrieve_project_button_delete)
    public void onClickDeleteProject() {
      int position = getAdapterPosition();
      notifyItemChanged(position);
      clickListener.onDeleteProject(projectList.get(position));
    }

    @OnClick(R.id.retrieve_project_button_edit)
    public void onClickGoToProjectDetail() {
      clickListener.goToDetailActivity();
    }

    @OnClick(R.id.retrieve_project_image)
    public void onClickGoToEdit() {
      clickListener.goToEditActivity(projectList.get(getAdapterPosition()));
    }

    @OnClick(R.id.retrieve_project_button_share)
    public void onClickGoToShare() {
      clickListener.goToShareActivity(projectList.get(getAdapterPosition()));
    }
  }
}