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
import com.example.doanappfood.model.ComboItemModel;
import com.example.doanappfood.model.ComboModel;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ComboAdapter  extends RecyclerView.Adapter<ComboAdapter.ViewHolder> {
    private List<ComboModel> list;
    private Context context;
    private  OnComboClickListner listner;
    private  int selectedPosition = 0;
    public  interface OnComboClickListner{
        void OnClick(ComboModel comboModel,int position);
    }

    public  void setOnComboClickListener(OnComboClickListner listener){
        this.listner = listener;
    }

    public ComboAdapter(List<ComboModel> list, Context context) {
        this.list = list;
        this.context = context;
    }
    public  class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgCombo;
        TextView tvComboName, tvComboItems, tvSalePrice, tvListPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCombo = itemView.findViewById(R.id.imgCombo);
            tvComboName = itemView.findViewById(R.id.tvComboName);
            tvComboItems = itemView.findViewById(R.id.tvComboItems);
            tvSalePrice = itemView.findViewById(R.id.tvSalePrice);
            tvListPrice = itemView.findViewById(R.id.tvListPrice);
        }
    }
    public void setData(List<ComboModel> newList){
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ComboAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_combo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComboAdapter.ViewHolder holder, int position) {
        ComboModel item = list.get(position);
        NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        holder.tvComboName.setText(item.getName());
        Glide.with(context).load(item.getImage()).into(holder.imgCombo);
        // Combo Item
        StringBuilder sb = new StringBuilder();
        for (ComboItemModel ci : item.getItems()){
            sb.append(ci.getQuantity()).append(" ").append(ci.getName()).append("\n");
        }
        holder.tvComboItems.setText(sb.toString().trim());
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
            if(listner != null){
                listner.OnClick(item, selectedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
