package com.example.doanappfood.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.doanappfood.data.CartDAO;
import com.example.doanappfood.model.CartItem;
import com.example.doanappfood.model.CartSauceItem;
import com.example.doanappfood.model.CheckoutModel;
import com.google.gson.Gson;

import java.sql.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CartViewModel  extends AndroidViewModel {
    private  final MutableLiveData<List<CartItem>> cartItems = new MutableLiveData<>(new ArrayList<>());
    private  final  MutableLiveData<Double > saleTotal = new MutableLiveData<>(0.0);
    private  final  MutableLiveData<Double > listTotal = new MutableLiveData<>(0.0);

    private  final  MutableLiveData<CheckoutModel> checkoutData = new MutableLiveData<>();
    private  final CartDAO  cartDAO;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public CartViewModel(@NonNull Application application) {
        super(application);
        cartDAO = new CartDAO(application);
    }
    public LiveData<List<CartItem>> getCartItems(){ return cartItems; };
    public LiveData<Double> getSaleTotal()         { return saleTotal; }
    public  LiveData<Double> getListTotal()         { return listTotal; }
    public LiveData<CheckoutModel> getCheckoutData() { return checkoutData; }

    public  void loadCartItems(int userId){
        executor.execute(() -> {
            List<CartItem> fresh = cartDAO.getAll(userId);
            cartItems.postValue(fresh);
            recalculateTotals(fresh);
        });
    }
    public  void recalculateTotals(List<CartItem> items){
        double sale = 0, list = 0;
        for(CartItem item : items){
            int qty = item.getQuantity();
            double salePrice = item.getSale_price();
            double listPrice = item.getList_price();
            sale += (salePrice > 0 && salePrice < listPrice ? salePrice : listPrice) * qty;
            list += listPrice * qty;
        }
        saleTotal.postValue(sale);
        listTotal.postValue(list);
    }
   public  void removeItems (int cartId, int position ){
        executor.execute(() -> {
            cartDAO.removeItem(cartId);
            List<CartItem> current = new ArrayList<>(
                    cartItems.getValue() != null ? cartItems.getValue() : new ArrayList<>()
            );
            if(position >= 0 && position < current.size()){
                current.remove(position);
            }
            cartItems.postValue(current);
            recalculateTotals(current);
         });
   }
   public  void updateQuantity(int cartId, int quantity){
        executor.execute(() -> {
            cartDAO.updateItem(cartId, quantity);
            List<CartItem> current = cartItems.getValue();
            if(current != null) recalculateTotals(current);
        });
   }
   public  void prepareCheckout(){
        List<CartItem> items = cartItems.getValue();
        if(items  == null || items.isEmpty()) return;

        double sale = saleTotal.getValue() != null ? saleTotal.getValue() : 0;
        double list = listTotal.getValue() != null ? listTotal.getValue() : 0;
        int userId = items.get(0).getUserId();

        List<Map<String, Object>> cartMapList = buildMapList(items);
        String cartJson = new Gson().toJson(cartMapList);
        checkoutData.postValue(new CheckoutModel(sale, list, userId, cartJson));

   }
   public  List<Map<String, Object>> buildMapList(List<CartItem> items){
        List<Map<String, Object>> result = new ArrayList<>();
        for(CartItem item : items){
            List<Map<String, Object>> sauces = new ArrayList<>();
            if(item.getSauces() != null){
                for(CartSauceItem s : item.getSauces()){
                    Map<String, Object> sauce = new LinkedHashMap<>();
                    sauce.put("name", s.getName());
                    sauce.put("quantity", s.getQuantity());
                    sauces.add(sauce);
            }
        }
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("product_id", item.getProductId());
            map.put("quantity", item.getQuantity());
            map.put("list_price", item.getList_price());
            map.put("sale_price", item.getSale_price());
            map.put("sauces", sauces);
            result.add(map);
        }
        return result;
   }
}
