package com.videonasocialmedia.vimojo.settings.licensesVimojo.presentation.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.presentation.mvp.views.LicensesVimojoClickListener;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.model.LicenseVimojo;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *
 */

public class LicensesVimojoAdapter extends RecyclerView.Adapter <LicensesVimojoAdapter.LicensesListItemViewHolder> {

  private List<LicenseVimojo> licenses;
  private Context context;
  private LicensesVimojoClickListener clickListener;

  public void setLicensesClickListener(LicensesVimojoClickListener listener) {
    this.clickListener=listener;
  }

  public void setLicenseList(List<LicenseVimojo> licenseList) {
    this.licenses = licenseList;
    notifyDataSetChanged();
  }

  @Override
  public LicensesListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
    View rowView = LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.licenses_view_holder, viewGroup, false);
    this.context = viewGroup.getContext();
    return new LicensesListItemViewHolder(rowView, licenses);
  }

  @Override
  public void onBindViewHolder(LicensesListItemViewHolder holder, int position) {
    LicenseVimojo license = licenses.get(position);
    holder.licenseId.setText(license.getIdLicenseVimojo());
 
  }


  @Override
  public int getItemCount() {
    return licenses.size();
  }

  class LicensesListItemViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.license_title)
    TextView licenseId;

    private List<LicenseVimojo> licenseList;

    public LicensesListItemViewHolder(View itemView, List<LicenseVimojo> licenseList) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      this.licenseList = licenseList;
    }

    @OnClick({R.id.license_title})
    public void onClick() {
      LicenseVimojo licenseVimojo = licenseList.get(getAdapterPosition());
      clickListener.onClick(licenseVimojo);
    }
  }
}
