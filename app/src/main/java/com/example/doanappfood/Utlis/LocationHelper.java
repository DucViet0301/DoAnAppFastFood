package com.example.doanappfood.Utlis;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.util.List;
import java.util.Locale;

public class LocationHelper {
    public  interface  LocationCallback{
        void onAddressRetrieved(String address);
        void onError(String errorMessage);
    }
    public static  void getAddressFromLatLng(Context context, double lat , double lng, LocationCallback callback){
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try{
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if(addresses != null && !addresses.isEmpty()){
                Address address = addresses.get(0);
                String fullAddress = address.getAddressLine(0);
                callback.onAddressRetrieved(fullAddress);
            }else{
                callback.onError("Không tìm thấy địa chỉ");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}