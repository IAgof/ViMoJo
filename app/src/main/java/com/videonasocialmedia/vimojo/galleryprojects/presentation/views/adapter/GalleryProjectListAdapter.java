package com.videonasocialmedia.vimojo.galleryprojects.presentation.views.adapter;

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
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.views.GalleryProjectClickListener;
import com.videonasocialmedia.vimojo.repository.DataPersistanceType;
import com.videonasocialmedia.vimojo.utils.DateUtils;
import com.videonasocialmedia.vimojo.utils.TimeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Data adapter for {@link Project} recycler view in projects gallery activity
 */
public class GalleryProjectListAdapter extends
    RecyclerView.Adapter<GalleryProjectListAdapter.RetrieveProjectListItemViewHolder> {

  private Context context;
  private List<Project> projectList;
  private GalleryProjectClickListener clickListener;

  public void setRetrieveProjectClickListener(
          GalleryProjectClickListener GalleryProjectClickListener) {
    clickListener = GalleryProjectClickListener;
  }

  @Override
  public RetrieveProjectListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
    View rowView = LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.gallery_project_view_holder, viewGroup, false);
    this.context = viewGroup.getContext();
    return new RetrieveProjectListItemViewHolder(rowView, projectList);
  }

  @Override
  public void onBindViewHolder(RetrieveProjectListItemViewHolder holder, int position) {
    Project project = projectList.get(position);
    if (project.getMediaTrack().getItems().size() > 0) {
      drawVideoThumbnail(holder.projectPoster, project);
    } else {
      // TODO(jliarte): 9/08/18 if project has poster
      holder.projectPoster.setImageResource(R.drawable.activity_gallery_project_no_preview);
    }
    if (project.getDataPersistanceType() == DataPersistanceType.API) {
      holder.cloudProjectIcon.setVisibility(View.VISIBLE);
    }
    // TODO(jliarte): 8/08/18 remove after fixing dates from api?
    try {
      holder.dateProject.setText(DateUtils.toFormatDateDayMonthYear(project.getLastModification()));
    } catch (Exception ignored) {}
    // TODO(jliarte): 9/08/18 FIXME it seems that onBindViewHolder is called multiple times for a
    //                item, resulting in appending data from several projects in the same viewholder
    holder.durationProject.setText(TimeUtils.toFormattedTimeWithMinutesAndSeconds(project.getDuration()));
    holder.numClipsProject.setText(String.valueOf(project.numberOfClips()));
    holder.titleProject.setText(project.getProjectInfo().getTitle());

    double projectSizeMb = project.getProjectSizeMbVideoToExport();
    double formatProjectSizeMb = Math.round(projectSizeMb * 100.0) / 100.0;
    holder.sizeMbProject.setText(formatProjectSizeMb + " Mb");
  }

  private void drawVideoThumbnail(ImageView thumbnailView, Project project) {
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
    @BindView(R.id.project_date)
    TextView dateProject;
    @BindView(R.id.project_duration)
    TextView durationProject;
    @BindView(R.id.project_num_clips)
    TextView numClipsProject;
    @BindView(R.id.project_image)
    ImageView projectPoster;
    @BindView(R.id.cloud_project)
    ImageView cloudProjectIcon;
    @BindView(R.id.project_title)
    TextView titleProject;
    @BindView(R.id.project_size_mb)
    TextView sizeMbProject;


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

    @OnClick(R.id.project_button_duplicate)
    public void onClickDuplicateProject() {
      int position = getAdapterPosition();
      notifyItemChanged(position);
      clickListener.onDuplicateProject(projectList.get(position));
    }

    @OnClick(R.id.project_button_delete)
    public void onClickDeleteProject() {
      int position = getAdapterPosition();
      notifyItemChanged(position);
      clickListener.onDeleteProject(projectList.get(position));
    }

    @OnClick(R.id.project_button_edit)
    public void onClickGoToProjectDetail() {
      clickListener.goToDetailActivity(projectList.get(getAdapterPosition()));
    }

    @OnClick(R.id.project_image)
    public void onClickGoToEdit() {
      clickListener.goToEditActivity(projectList.get(getAdapterPosition()));
    }

    @OnClick(R.id.project_button_share)
    public void onClickGoToShare() {
      clickListener.goToShareActivity(projectList.get(getAdapterPosition()));
    }

  }
}
