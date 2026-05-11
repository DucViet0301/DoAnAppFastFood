package com.example.doanappfood.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.doanappfood.R;
import com.example.doanappfood.Utlis.NotificationManager;
import com.example.doanappfood.Utlis.SessionManager;
import com.example.doanappfood.adapter.NotificationAdapter;
import com.example.doanappfood.model.NotificationModel;
import com.example.doanappfood.model.OrderModel;

import java.util.List;

public class NotifactionFragment extends Fragment {

    private RecyclerView rvNotification;
    private NotificationAdapter adapter;

    private int userId;
    private com.example.doanappfood.Utlis.SessionManager sessionManager;
    private List<NotificationModel> notiList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifaction, container, false);
        rvNotification = view.findViewById(R.id.rvNotifaction);
        rvNotification.setLayoutManager(new LinearLayoutManager(getContext()));
        sessionManager = new com.example.doanappfood.Utlis.SessionManager(getContext());
        userId = sessionManager.getUserId();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadNotifications();
    }

    private void loadNotifications() {
        List<NotificationModel> list = NotificationManager.getAll(requireContext(), userId);
        adapter = new NotificationAdapter(requireContext(), list);
        rvNotification.setAdapter(adapter);
    }
    @Override
    public  void onHiddenChanged(boolean hidden){
        super.onHiddenChanged(hidden);
        if(!hidden){
            userId = sessionManager.getUserId();
            if(userId == -1){
                rvNotification.setAdapter(null);
                notiList = null;
                return;
            }
            loadNotifications();
        }
    }
}