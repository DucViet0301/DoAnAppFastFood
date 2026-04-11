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
import com.example.doanappfood.activity.ProductDetailActivity;
import com.example.doanappfood.adapter.CategoryAdapter;
import com.example.doanappfood.adapter.ProductAdapter;
import com.example.doanappfood.model.CategoryModel;
import com.example.doanappfood.repository.CategoryRepository;
import com.example.doanappfood.repository.ProductRepository;
import com.example.doanappfood.viewmodel.CategoryViewModel;
import com.example.doanappfood.viewmodel.ProductViewModel;

import java.util.ArrayList;


public class StoreFragment extends Fragment {
    RecyclerView recyclerViewCategory, recyclerViewProduct;
    CategoryAdapter categoryAdapter;
    ProductAdapter productAdapter;
    CategoryViewModel categoryViewModel;
    ProductViewModel productViewModel;
    boolean isFirstLoad = true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store, container, false);

        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        int idCate = -1;
        if(getArguments() != null){
            idCate = getArguments().getInt("IdCate", -1);
        }


        initViewProduct(view);
        initViewCategory(view);
        initViewModelCategory(idCate);

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
        productAdapter = new ProductAdapter(new ArrayList<>(), requireContext());
        recyclerViewProduct.setAdapter(productAdapter);
        productAdapter.setOnProductClickListener((productModel, position) -> {
            Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
            intent.putExtra("product_id", productModel.getId());
            startActivity(intent);
        });
    }

    private void loadProduct(int idCate) {
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
}