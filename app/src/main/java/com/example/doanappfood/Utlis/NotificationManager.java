package com.example.doanappfood.Utlis;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.doanappfood.model.NotificationModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NotificationManager {
    private static final String PREF_NAME = "notifications";
    private static final String KEY_LIST  = "notif_list";
    private  static  String getKey(int userId){
        return KEY_LIST + "_" + userId;
    }

    public static void addNotification(Context ctx,int userId,  NotificationModel notif) {
        List<NotificationModel> list = getAll(ctx, userId);
        list.add(0, notif);
        save(ctx,userId, list);
    }

    public static List<NotificationModel> getAll(Context ctx, int userId) {

        SharedPreferences prefs =  ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(getKey(userId), null);

        if (json == null) {
            return new ArrayList<>();
        }

        Type type = new TypeToken<List<NotificationModel>>() {}.getType();

        return new Gson().fromJson(json, type);
    }


    private static void save(Context ctx, int userId,  List<NotificationModel> list) {
        SharedPreferences prefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(getKey(userId), new Gson().toJson(list)).apply();
    }

}