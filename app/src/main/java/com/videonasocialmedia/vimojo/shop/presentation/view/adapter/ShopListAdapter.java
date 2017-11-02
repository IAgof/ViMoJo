package com.videonasocialmedia.vimojo.shop.presentation.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.shop.model.Shop;
import com.videonasocialmedia.vimojo.shop.model.SkuShopData;
import com.videonasocialmedia.vimojo.shop.presentation.mvp.views.ShopListClickListener;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ShopListAdapter extends
    RecyclerView.Adapter<ShopListAdapter.ShopAdapterItemViewHolder> {

  private List<SkuShopData> skuShopList;
  private List<Shop> shopList;
  private ShopListClickListener listener;
  private Context context;

  public void setShopListClickListener(ShopListClickListener listener) {
    this.listener = listener;
  }

  public void setShopList(List<Shop> shopList, List<SkuShopData> skuShopList) {
    this.shopList = shopList;
    this.skuShopList = skuShopList;
    notifyDataSetChanged();
  }

  @Override
  public ShopAdapterItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
    View rowView = LayoutInflater.from(viewGroup.getContext()).
        inflate(R.layout.shopping_list_item_view_holder, viewGroup, false);
    this.context = viewGroup.getContext();
    return new ShopAdapterItemViewHolder(rowView, shopList);
  }

  @Override
  public void onBindViewHolder(ShopAdapterItemViewHolder holder, int position) {
    Shop shop = shopList.get(position);
    String textPrice = (context.getString(R.string.buy_now) + " " + shop.getPrice() + " €");
    holder.titleShop.setText(shop.getTitle());
    holder.descriptionShop.setText(shop.getDescription());
    holder.buttonShop.setText(textPrice);

    boolean isPaid= shop.isPurchased();
    if(isPaid) {
      holder.buttonShop.setVisibility(View.GONE);
      holder.textPurchased.setVisibility(View.VISIBLE);
    } else {
      holder.buttonShop.setVisibility(View.VISIBLE);
      holder.textPurchased.setVisibility(View.GONE);
    }

  }

  @Override
  public int getItemCount() {
    int result = 0;
    if (shopList != null)
      result = shopList.size();
    return result;
  }

  class ShopAdapterItemViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.title_shop_section)
    TextView titleShop;
    @Bind(R.id.descripton_shop_section)
    TextView descriptionShop;
    @Bind(R.id.button_to_buy_shop_section)
    Button buttonShop;
   @Bind(R.id.text_view_pursached_shop)
   TextView textPurchased;

    private List<Shop> shopList;

    public ShopAdapterItemViewHolder(View itemView, List<Shop> shopList) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      this.shopList = shopList;
    }

    @OnClick({R.id.button_to_buy_shop_section})
    public void onClick() {
      SkuShopData data = getData(getAdapterPosition());
      Shop shop = shopList.get(getAdapterPosition());
      listener.onClickShopItem(shop, data.getSkuId(), data.getBillingType());
    }

    private SkuShopData getData(int adapterPosition) {
      return skuShopList == null ? null : skuShopList.get(adapterPosition);
    }
  }

}
