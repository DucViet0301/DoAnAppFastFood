package com.example.doanappfood.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.doanappfood.R;
import com.example.doanappfood.activity.AllNewsActivity;
import com.example.doanappfood.activity.NewDetailActivity;
import com.example.doanappfood.activity.ProductDetailActivity;
import com.example.doanappfood.Utlis.SlideEffect;
import com.example.doanappfood.adapter.BannerAdapter;
import com.example.doanappfood.adapter.ComboAdapter;
import com.example.doanappfood.adapter.NewAdapter;
import com.example.doanappfood.adapter.ProductAdapter;
import com.example.doanappfood.repository.BannerRepository;
import com.example.doanappfood.viewmodel.BannerViewModel;
import com.example.doanappfood.viewmodel.ComboViewModel;
import com.example.doanappfood.viewmodel.NewViewModel;
import com.example.doanappfood.viewmodel.ProductViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    //Banner
    ViewPager2 viewPager2;
    RecyclerView recyclerViewCombo, recyclerViewNew;

    //Adapter
    private BannerAdapter bannerAdapter;
    private ComboAdapter comboAdapter;
    private NewAdapter newAdapter;
    private ProductAdapter productAdapter;

    //ViewModel
    private BannerViewModel bannerViewModel;
    private ComboViewModel comboViewModel;
    private NewViewModel newViewModel;
    ProductViewModel productViewModel;

    //Auto-scroll
    private Handler autoScrollHandler;
    private Runnable autoScrollRunnable;
    private static final long Auto_Scroll_Delay = 3000L;

    // ĐÃ SỬA Ở ĐÂY: Khai báo thêm tvSeeAllNews ngay cạnh SeeAllProduct
    TextView SeeAllProduct, tvSeeAllNews;
    CardView CardViewGiftBox, CardViewBestSeller, CardViewChicken, CardViewLocationStore;

    BannerRepository bannerRepository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //Banner
        initViewBanner(view);
        initViewModelBanner();
        setAutoScrollBanner();

        //Combo
        initViewCombo(view);
        initViewModelCombo();

        //New
        initViewNew(view);
        initViewModelNew();

        SeeAllProduct = view.findViewById(R.id.tvSeeALLUD);
        CardViewGiftBox = view.findViewById(R.id.CardViewGiftBox);
        CardViewBestSeller = view.findViewById(R.id.CardViewBestSeller);
        CardViewChicken = view.findViewById(R.id.CardViewChicken);
        CardViewLocationStore = view.findViewById(R.id.CardViewLoactionStore);


        tvSeeAllNews = view.findViewById(R.id.tvSeeALLNEW);
        if (tvSeeAllNews != null) {
            tvSeeAllNews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), AllNewsActivity.class);
                    startActivity(intent);
                    requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
        }

        setCardView(SeeAllProduct, 2);
        setCardView(CardViewBestSeller, 1);
        setCardView(CardViewChicken, 9);
        CardViewLocationStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SlideEffect.changeFragment(requireActivity(), new MapFragment());
            }
        });

        return view;
    }

    private void setCardView(View view, int categoryId) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
                bottomNavigationView.setSelectedItemId(R.id.store);

                Bundle bundle = new Bundle();
                bundle.putInt("IdCate", categoryId);

                Fragment storeFragment = new StoreFragment();
                storeFragment.setArguments(bundle);

                //Animamtion
                SlideEffect.changeFragment(requireActivity(), storeFragment);
            }
        });
    }

    private void setAutoScrollBanner() {
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

    private void initViewModelBanner() {
        bannerViewModel = new ViewModelProvider(this).get(BannerViewModel.class);
        bannerViewModel.getBannerList().observe(getViewLifecycleOwner(), bannerModels -> {
            if (bannerModels != null) {
                bannerAdapter.updateList(bannerModels);
            }
        });
    }

    private void initViewModelCombo() {
        comboViewModel = new ViewModelProvider(this).get(ComboViewModel.class);
        comboViewModel.getComboList().observe(getViewLifecycleOwner(), comboModels -> {
            if (comboModels != null) {
                comboAdapter.setData(comboModels);
            }
        });
    }

    private void initViewModelNew() {
        newViewModel = new ViewModelProvider(this).get(NewViewModel.class);
        newViewModel.getNewList().observe(getViewLifecycleOwner(), newModels -> {
            if (newModels != null) {
                newAdapter.setData(newModels);
            }
        });
    }

    private void initViewBanner(View view) {
        viewPager2 = view.findViewById(R.id.viewPager2);
        bannerAdapter = new BannerAdapter(new ArrayList<>(), requireContext());
        viewPager2.setAdapter(bannerAdapter);
    }

    private void initViewCombo(View view) {
        recyclerViewCombo = view.findViewById(R.id.RecyclerViewCombo);
        recyclerViewCombo.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        comboAdapter = new ComboAdapter(new ArrayList<>(), requireContext());
        recyclerViewCombo.setAdapter(comboAdapter);
    }

    private void initViewNew(View view) {
        recyclerViewNew = view.findViewById(R.id.RecyclerViewNew);
        recyclerViewNew.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        newAdapter = new NewAdapter(new ArrayList<>(), requireContext());
        recyclerViewNew.setAdapter(newAdapter);

        newAdapter.setOnNewClickListener((newModel, position) -> {
            Intent intent = new Intent(getActivity(), NewDetailActivity.class);
            intent.putExtra("new_id", newModel.getId());
            intent.putExtra("title", newModel.getTitle());
            intent.putExtra("description", newModel.getDescription());
            intent.putExtra("image", newModel.getImage());
            startActivity(intent);
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (autoScrollHandler != null && autoScrollRunnable != null) {
            autoScrollHandler.removeCallbacks(autoScrollRunnable);
        }
    }
}