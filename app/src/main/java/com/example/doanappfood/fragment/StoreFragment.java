package com.example.doanappfood.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.example.doanappfood.R;
import com.example.doanappfood.activity.MainActivity;
import com.example.doanappfood.activity.ProductDetailActivity;
import com.example.doanappfood.adapter.CategoryAdapter;
import com.example.doanappfood.adapter.ProductAdapter;
import com.example.doanappfood.viewmodel.CategoryViewModel;
import com.example.doanappfood.viewmodel.ComboViewModel;
import com.example.doanappfood.viewmodel.ProductViewModel;
import com.example.doanappfood.viewmodel.SSEViewModel;

import java.util.ArrayList;


public class StoreFragment extends Fragment {
    RecyclerView recyclerViewCategory, recyclerViewProduct;
    CategoryAdapter categoryAdapter;
    ProductAdapter productAdapter;
    CategoryViewModel categoryViewModel;
    ProductViewModel productViewModel;
    private ComboViewModel comboViewModel;
    boolean isFirstLoad = true;
    com.example.doanappfood.Utlis.SessionManager sessionManager;
    private  int currentCategoryId = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store, container, false);

        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        comboViewModel = new ViewModelProvider(requireActivity()).get(ComboViewModel.class); // FIX: thêm dòng này

        sessionManager = new com.example.doanappfood.Utlis.SessionManager(requireContext());
        int idCate = -1;
        if(getArguments() != null){
            idCate = getArguments().getInt("IdCate", -1);
        }

        initViewProduct(view);
        initViewCategory(view);
        initViewModelCategory(idCate);

        SSEViewModel sseViewModel = new ViewModelProvider(requireActivity()).get(SSEViewModel.class);
        sseViewModel.getProductChanged().observe(getViewLifecycleOwner(), action -> {
            if (action != null && currentCategoryId != -1) {
                loadProduct(currentCategoryId);
            }
        });
        sseViewModel.getComboChanged().observe(getViewLifecycleOwner(), action -> {
            if (action != null && currentCategoryId != -1) {
                loadProduct(currentCategoryId);
            }
        });

        return view;
    }

    public void initViewCategory(View view) {
        recyclerViewCategory = view.findViewById(R.id.RecyclerViewCategory);
        recyclerViewCategory.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        categoryAdapter = new CategoryAdapter(requireContext(), new ArrayList<>());
        recyclerViewCategory.setAdapter(categoryAdapter);

        categoryAdapter.setOnCategoryClickListener((categoryModel, position) ->
                loadProduct(categoryModel.getId())
        );
    }

    private void initViewModelCategory(int idCate) {
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        categoryViewModel.getCategoryList().observe(getViewLifecycleOwner(), categoryModels -> {
            if (categoryModels != null && !categoryModels.isEmpty()) {
                categoryAdapter.setData(categoryModels);

                if (isFirstLoad) {
                    if(idCate != -1){
                        categoryAdapter.setSelectedCategory(idCate);
                        loadProduct(idCate);
                    } else {
                        categoryAdapter.setSelectedCategory(categoryModels.get(0).getId());
                        loadProduct(categoryModels.get(0).getId());
                    }
                    isFirstLoad = false;
                }
            }
        });
    }

    private void initViewProduct(View view) {
        recyclerViewProduct = view.findViewById(R.id.RecyclerViewProduct);
        recyclerViewProduct.setLayoutManager(new GridLayoutManager(getContext(), 2));
        productAdapter = new ProductAdapter(new ArrayList<>(), requireContext(), sessionManager.getUserId());
        recyclerViewProduct.setAdapter(productAdapter);
        productAdapter.setOnProductClickListener((productModel, position) -> {
            Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
            intent.putExtra("product_id", productModel.getId());
            startActivity(intent);
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        productAdapter.setOnCartUpdatedListener(() -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).updateBadge();
            }
        });
    }

    private void loadProduct(int idCate) {
        currentCategoryId = idCate;
        productViewModel.getProducts(idCate).observe(getViewLifecycleOwner(), productModels -> {
            if (productModels != null) {
                productAdapter.setData(productModels);
                final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down);
                recyclerViewProduct.setLayoutAnimation(controller);
                productAdapter.notifyDataSetChanged();
                recyclerViewProduct.scheduleLayoutAnimation();
            }
        });
    }
    public void loadCategoryFromHome(int categoryId) {
        if (categoryAdapter != null) {
            categoryAdapter.setSelectedCategory(categoryId);
        }
        loadProduct(categoryId);
    }
}