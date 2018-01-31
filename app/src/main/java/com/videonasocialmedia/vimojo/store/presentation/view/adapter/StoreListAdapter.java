package com.videonasocialmedia.vimojo.store.presentation.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.store.model.SkuStoreData;
import com.videonasocialmedia.vimojo.store.presentation.mvp.views.StoreListClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class StoreListAdapter extends
    RecyclerView.Adapter<StoreListAdapter.StoreAdapterItemViewHolder> {

  private List<SkuStoreData> skuStoreList;
  private StoreListClickListener listener;
  private Context context;

  public void setStoreListClickListener(StoreListClickListener listener) {
    this.listener = listener;
  }

  public void setStoreItemsList(List<SkuStoreData> skuStoreList) {
    this.skuStoreList = skuStoreList;
    notifyDataSetChanged();
  }

  @Override
  public StoreAdapterItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
    View rowView = LayoutInflater.from(viewGroup.getContext()).
        inflate(R.layout.shopping_list_item_view_holder, viewGroup, false);
    this.context = viewGroup.getContext();
    return new StoreAdapterItemViewHolder(rowView, skuStoreList);
  }

  @Override
  public void onBindViewHolder(StoreAdapterItemViewHolder holder, int position) {
    SkuStoreData skuStoreData = skuStoreList.get(position);
    String textPrice = (context.getString(R.string.buy_now) + " " + skuStoreData.getPrice());
    holder.storeItemTitle.setText(skuStoreData.getTitle());
    holder.storeItemDescription.setText(skuStoreData.getDescription());
    holder.purchaseButton.setText(textPrice);

    boolean isPurchased = skuStoreData.isPurchased();
    if (isPurchased) {
      holder.purchaseButton.setVisibility(View.GONE);
      holder.textPurchased.setVisibility(View.VISIBLE);
    } else {
      holder.purchaseButton.setVisibility(View.VISIBLE);
      holder.textPurchased.setVisibility(View.GONE);
    }
  }

  @Override
  public int getItemCount() {
    int result = 0;
    if (skuStoreList != null)
      result = skuStoreList.size();
    return result;
  }

  class StoreAdapterItemViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.store_item_title)
    TextView storeItemTitle;
    @BindView(R.id.store_item_descripton)
    TextView storeItemDescription;
    @BindView(R.id.purchase_button)
    Button purchaseButton;
    @BindView(R.id.text_view_item_purchased)
    TextView textPurchased;

    private List<SkuStoreData> skuStoreList;

    public StoreAdapterItemViewHolder(View itemView, List<SkuStoreData> skuStoreList) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      this.skuStoreList = skuStoreList;
    }

    @OnClick({R.id.purchase_button})
    public void onClick() {
      SkuStoreData data = getData(getAdapterPosition());
      listener.onClickStoreItem(data.getSkuId(), data.getBillingType());
    }

    private SkuStoreData getData(int adapterPosition) {
      return skuStoreList == null ? null : skuStoreList.get(adapterPosition);
    }
  }

}
