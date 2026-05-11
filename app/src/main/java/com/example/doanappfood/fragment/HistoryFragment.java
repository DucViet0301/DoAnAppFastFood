package com.example.doanappfood.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.doanappfood.R;
import com.example.doanappfood.Utlis.NotificationManager;
import com.example.doanappfood.Utlis.SessionManager;
import com.example.doanappfood.activity.OrderDetailActivity;
import com.example.doanappfood.adapter.OrderAdapter;
import com.example.doanappfood.model.OrderModel;
import com.example.doanappfood.viewmodel.OrderViewModel;

import java.util.List;


public class HistoryFragment extends Fragment {
    private RecyclerView rvHistory;
    private OrderViewModel orderViewModel;
    private int CURRENT_USER_ID;
    private com.example.doanappfood.Utlis.SessionManager sessionManager;
    private OrderAdapter adapter;
    private List<OrderModel> orderList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sessionManager = new com.example.doanappfood.Utlis.SessionManager(getContext());
        CURRENT_USER_ID = sessionManager.getUserId();
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvHistory = view.findViewById(R.id.rvHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        orderViewModel.init();
        loadData();
    }
    private final ActivityResultLauncher<Intent> orderDeatilLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getBooleanExtra("updated", false)) {
                        loadData();
                    }
                }
            }
    );

    public void loadData() {
//        SessionManager sessionManager =
//                new SessionManager(requireContext());
//        if(!sessionManager.isLoggedIn()){
//            rvHistory.setAdapter(null);
//            if(orderList != null){
//                orderList.clear();
//                adapter.notifyDataSetChanged();
//            }
//            return;
//        }
        orderViewModel.getAllOrder(CURRENT_USER_ID).observe(getViewLifecycleOwner(), orderModels -> {
            if (orderModels != null && !orderModels.isEmpty()) {
                orderList = orderModels;
                adapter = new OrderAdapter(getContext(), orderList);
                adapter.setOnOrderClickListener(new OrderAdapter.OnOrderClickListener() {
                    @Override
                    public void onClick(OrderModel order, int position) {
//                        Toast.makeText(getContext(),
//                                "Đơn hàng #" + order.getId(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(), OrderDetailActivity.class);
                        intent.putExtra("order_id", order.getId());
                        orderDeatilLauncher.launch(intent);
                    }
                });
                rvHistory.setAdapter(adapter);
            } else {
                Toast.makeText(getContext(), "Không có đơn hàng nào", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            CURRENT_USER_ID = sessionManager.getUserId();

            if (CURRENT_USER_ID == -1) {
                rvHistory.setAdapter(null);
                orderList = null;
                return;
            }

            loadData();
        }
    }

}