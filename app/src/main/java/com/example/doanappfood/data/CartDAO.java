package com.example.doanappfood.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.doanappfood.model.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartDAO {
    private  final DatabaseHelper dbHelper;
    public CartDAO(Context context){
        dbHelper = DatabaseHelper.getInstance(context);
    }
    public  void addItem (int productId, String name, double list_price, double sale_price, int quantity, String imageUrl){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT quantity FROM cart_item WHERE product_id = ? ",
                new String[]{String.valueOf(productId)}
        );
        if(cursor.moveToFirst()){
            int currentQuantity = cursor.getInt(0);
            db.execSQL(
                    "UPDATE cart_item SET quantity = ? WHERE product_id = ?",
                    new Object[]{currentQuantity + 1, productId}

            );
        }else{
            ContentValues values = new ContentValues();
            values.put("product_id", productId);
            values.put("name", name);
            values.put("list_price", list_price);
            values.put("sale_price", sale_price);
            values.put("image_url", imageUrl);
            values.put("quantity", 1);
            db.insert("cart_item", null, values);
        }
        cursor.close();
        db.close();
    }
    public  void updateItem(int productId, int quantity){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(quantity <= 0){
            db.execSQL("DELETE FROM cart_item WHERE product_id = ?",
                    new Object[]{productId});
        }
        else{
            db.execSQL("UPDATE cart_item SET quantity = ? WHERE product_id = ?",
                    new Object[]{quantity, productId});
        }
        db.close();
    }
    public  void removeItem(int productId){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM cart_item WHERE product_id = ?",
                new Object[]{productId});
        db.close();
    }
    public List<CartItem> getAll(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<CartItem> list = new ArrayList<>();

        Cursor cursor = db.rawQuery("" +
                "SELECT * FROM cart_item", null);
        while (cursor.moveToNext()){
            list.add(new CartItem(
                    cursor.getInt(1),
                    cursor.getString(2),
                    cursor.getDouble(3),
                    cursor.getDouble(4),
                    cursor.getInt(5),
                    cursor.getString(6)
            ));
        }
        cursor.close();
        db.close();
        return  list;
    }
    public   int getCount(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(quantity) FROM cart_item", null);
        int count = cursor.moveToFirst() ? cursor.getInt(0) : 0;
        cursor.close();
        db.close();
        return count;
    }
}
