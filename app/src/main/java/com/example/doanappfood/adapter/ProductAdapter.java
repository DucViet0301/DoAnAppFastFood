package com.example.doanappfood.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doanappfood.R;
import com.example.doanappfood.activity.LoginActivity;
import com.example.doanappfood.data.CartDAO;
import com.example.doanappfood.model.ComboItemModel;
import com.example.doanappfood.model.ProductModel;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<ProductModel> list;
    private Context context;
    private OnProductClickListener listener;
    private  int userId;
    private  int selectedPosition = 0;
    public  interface  OnCartUpdatedListener{
        void onCartUpdated();
    }
    private  OnCartUpdatedListener cartListener;
    public  void setOnCartUpdatedListener(OnCartUpdatedListener listener){
        this.cartListener = listener;
    }

    public  interface OnProductClickListener{
        void OnClick(ProductModel productModel, int position);
    }
    public  void setOnProductClickListener(OnProductClickListener listener){
        this.listener = listener;
    }


    public ProductAdapter(List<ProductModel> list, Context context, int userId) {
        this.list = list;
        this.context = context;
        this.userId = userId;
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageProduct;
        TextView tvProductName, tvSalePrice, tvListPrice, btnAddProduct;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvSalePrice = itemView.findViewById(R.id.tvSalePrice);
            tvListPrice = itemView.findViewById(R.id.tvListPrice);
            btnAddProduct = itemView.findViewById(R.id.btnAddProduct);
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
        holder.btnAddProduct.setOnClickListener(v -> {
            if(userId == -1){
                Toast.makeText(context, "Vui lòng đăng nhập để thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return;
            }
            CartDAO cartDAO = new CartDAO(context);
            double effectivePrice = item.getSale_price() > 0
                    ? item.getSale_price()
                    : item.getList_price();

            cartDAO.addItem(userId, item.getId(), item.getName(), item.getList_price(),
                    effectivePrice, 1, item.getImage(), null, null);
            if(cartListener != null){
                cartListener.onCartUpdated();
            }
            Toast.makeText(context, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
