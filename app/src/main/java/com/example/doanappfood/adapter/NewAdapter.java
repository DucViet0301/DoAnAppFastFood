package com.example.doanappfood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doanappfood.R;
import com.example.doanappfood.model.ComboModel;
import com.example.doanappfood.model.NewModel;

import java.util.List;

public class NewAdapter extends RecyclerView.Adapter<NewAdapter.ViewHolder> {

    public interface OnNewClickListener {
        void onNewClick(NewModel newModel, int position);
    }

    private OnNewClickListener listener;

    public void setOnNewClickListener(OnNewClickListener listener) {
        this.listener = listener;
    }
    private List<NewModel> list;
    private Context context;

    // Thêm biến này để chọn giao diện, mặc định là dùng cái nhỏ
    private int layoutResource = R.layout.item_new;

    // Thêm hàm này để bên ngoài có thể đổi giao diện
    public void setLayoutResource(int layoutResource) {
        this.layoutResource = layoutResource;
    }

    public NewAdapter(List<NewModel> list, Context context) {
        this.list = list;
        this.context = context;
    }
    public  class  ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgNew;
        TextView tvTitleNew;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgNew = itemView.findViewById(R.id.imgNew);
            tvTitleNew = itemView.findViewById(R.id.tvTitleNew);
        }
    }
    public void setData(List<NewModel> newList){
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layoutResource, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewAdapter.ViewHolder holder, int position) {
        NewModel item = list.get(position);
        holder.tvTitleNew.setText(item.getTitle());
        Glide.with(context).load(item.getImage()).into(holder.imgNew);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    int viTri = holder.getAdapterPosition();

                    listener.onNewClick(list.get(viTri), viTri);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
