package com.example.doanappfood.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.doanappfood.R;
import com.example.doanappfood.adapter.BannerAdapter;
import com.example.doanappfood.repository.BannerRepository;
import com.example.doanappfood.viewmodel.BannerViewModel;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    //Banner
    ViewPager2 viewPager2;
    private BannerAdapter bannerAdapter;
    private BannerViewModel bannerViewModel;
    //Auto-scroll
    private Handler autoScrollHandler;
    private  Runnable autoScrollRunnable;
    private  static  final  long Auto_Scroll_Delay = 3000L;

    BannerRepository bannerRepository;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        //Banner
        initView(view);
        initViewModel();
        setAutoScroll();

        return  view;
    }

    private void setAutoScroll() {
        autoScrollHandler = new Handler(Looper.getMainLooper());
        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                if (bannerAdapter.getItemCount() > 0) {
                    int nextItem = (viewPager2.getCurrentItem() + 1) % bannerAdapter.getItemCount();
                    viewPager2.setCurrentItem(nextItem, true);
                }
                autoScrollHandler.postDelayed(this, Auto_Scroll_Delay);
            }
        };
        autoScrollHandler.postDelayed(autoScrollRunnable, Auto_Scroll_Delay);
    }

    private void initViewModel() {
        bannerViewModel = new ViewModelProvider(this).get(BannerViewModel.class);
        bannerViewModel.getBannerList().observe(getViewLifecycleOwner(),bannerModels ->{
            if (bannerModels != null){
                bannerAdapter.updateList(bannerModels);
            }
        });

    }

    private void  initView(View view){
        viewPager2 = view.findViewById(R.id.viewPager2);
        bannerAdapter = new BannerAdapter(new ArrayList<>(), requireContext());
        viewPager2.setAdapter(bannerAdapter);
    }

}