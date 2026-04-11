package com.example.doanappfood.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doanappfood.R;
import com.example.doanappfood.model.ProductModel;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<ProductModel> list;
    private Context context;
    private  OnProductClickListener listener;
    private  int selectedPosition = 0;

    public  interface OnProductClickListener{
        void OnClick(ProductModel productModel, int position);
    }
    public  void setOnProductClickListener(OnProductClickListener listener){
        this.listener = listener;
    }


    public ProductAdapter(List<ProductModel> list, Context context) {
        this.list = list;
        this.context = context;
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageProduct;
        TextView tvProductName, tvSalePrice, tvListPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvSalePrice = itemView.findViewById(R.id.tvSalePrice);
            tvListPrice = itemView.findViewById(R.id.tvListPrice);
        }
    }
    public  void setData(List<ProductModel> newlist){
        this.list = newlist;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position) {
        ProductModel item = list.get(position);
        NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        holder.tvProductName.setText(item.getName());
        Glide.with(context).load(item.getImage()).into(holder.imageProduct);
        if (item.getSale_price() > 0) {
            holder.tvSalePrice.setText(fmt.format(item.getSale_price()) + " đ");
            holder.tvSalePrice.setTextColor(Color.parseColor("#ff4b4b"));

            if (item.getList_price() > item.getSale_price()) {
                holder.tvListPrice.setVisibility(View.VISIBLE);
                holder.tvListPrice.setText(fmt.format(item.getList_price()) + " đ");
                holder.tvListPrice.setPaintFlags(
                        holder.tvListPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
                );
            } else {
                holder.tvListPrice.setVisibility(View.GONE);
                holder.tvListPrice.setPaintFlags(0);
            }
        } else {
            holder.tvSalePrice.setText(fmt.format(item.getList_price()) + " đ"); // sửa
            holder.tvSalePrice.setTextColor(Color.parseColor("#ff4b4b"));
            holder.tvListPrice.setVisibility(View.GONE);
            holder.tvListPrice.setPaintFlags(0);
        }
        holder.itemView.setOnClickListener(v ->{
            int previousSelected = selectedPosition;
            selectedPosition = holder.getLayoutPosition();
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);
            if(listener != null){
                listener.OnClick(item, selectedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
