package com.example.doanappfood.data;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.doanappfood.model.CartItem;
import com.example.doanappfood.model.CartSauceItem;
import com.example.doanappfood.model.SaucesModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class CartDAO {
    private  final DatabaseHelper dbHelper;
    public CartDAO(Context context){
        dbHelper = DatabaseHelper.getInstance(context);
    }
    public  void addItem (int userId, int productId, String producrName, double list_price, double sale_price,
                          int quantity, String imageUrl, List<String> sauceNames, String comboDetail){

        SQLiteDatabase db = null;
        Cursor cursor = null;
        try{
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            String saucekey = sauceNames != null  ? String.join(",", sauceNames) : "";
            cursor = db.rawQuery(
                    "SELECT c." + DatabaseHelper.COLUMN_ID +
                            ", c." + DatabaseHelper.COLUMN_QUANTITY +
                            " FROM " + DatabaseHelper.TABLE_CART + " c" +
                            " WHERE c." +DatabaseHelper.COLUMN_USER_ID + " = ?" +
                            "AND c." + DatabaseHelper.COLUMN_PRODUCT_ID + " = ?" +
                            " AND IFNULL((" +
                            "SELECT GROUP_CONCAT(" + DatabaseHelper.COLUMN_SAUCE_NAME + ") " +
                            "FROM " + DatabaseHelper.TABLE_SAUCE +
                            " WHERE " + DatabaseHelper.COLUMN_SAUCE_CART_ITEM_ID + " = c." + DatabaseHelper.COLUMN_ID +
                            "), '') = ?",
                    new String[]{String.valueOf(userId), String.valueOf(productId), saucekey}
            );
            if(cursor.moveToFirst()){
                int cartId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                int currentQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_QUANTITY));
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_QUANTITY, currentQuantity + quantity);
                db.update(DatabaseHelper.TABLE_CART, values,
                        DatabaseHelper.COLUMN_ID + " = ?",
                        new String[]{String.valueOf(cartId)});

            }
            else{
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_USER_ID, userId);
                values.put(DatabaseHelper.COLUMN_PRODUCT_ID, productId);
                values.put(DatabaseHelper.COLUMN_PRODUCT_NAME, producrName);
                values.put(DatabaseHelper.COLUMN_LIST_PRICE, list_price);
                values.put(DatabaseHelper.COLUMN_SALE_PRICE, sale_price);
                values.put(DatabaseHelper.COLUMN_QUANTITY, quantity);
                values.put(DatabaseHelper.COLUMN_IMAGE, imageUrl);
                values.put(DatabaseHelper.COLUMN_COMBO_DETAIL, comboDetail);
                long newCartID = db.insert(DatabaseHelper.TABLE_CART, null, values);

                if(newCartID != -1 && sauceNames != null){
                    for (String sauceName : sauceNames) {
                        ContentValues sauceValues = new ContentValues();
                        sauceValues.put(DatabaseHelper.COLUMN_SAUCE_CART_ITEM_ID, newCartID);
                        sauceValues.put(DatabaseHelper.COLUMN_SAUCE_NAME, sauceName);
                        db.insert(DatabaseHelper.TABLE_SAUCE, null, sauceValues);
                    };
                }
            }
            db.setTransactionSuccessful();
        }
        catch (Exception e){
            Log.e(TAG, "addItem error: " + e.getMessage(), e);
        }
        finally {
            if (db != null) {
                try {
                    db.endTransaction();
                } catch (Exception ignored) {}
            }

            if(cursor != null){
                cursor.close();
            }
            if(db != null){
                db.close();
            }
        }
    }
    public void updateItem(int cartId, int quantity) {
        SQLiteDatabase db = null;
        try{
            db = dbHelper.getWritableDatabase();
            if(quantity <= 0){
                db.delete(DatabaseHelper.TABLE_CART, DatabaseHelper.COLUMN_ID + " = ? ",
                        new String[]{String.valueOf(cartId)});
            }
            else{
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_QUANTITY, quantity);
                db.update(DatabaseHelper.TABLE_CART, values,
                        DatabaseHelper.COLUMN_ID + " = ?",
                        new String[]{String.valueOf(cartId)});
            }
        } catch (Exception e) {
            Log.e(TAG, "updateItem error: " + e.getMessage(), e);
        }
        finally {
            if(db != null) db.close();
        }
    }
    public void updateFullItem ( int cartId, int quantity, double listPirce,double salePrice, List<String> sauces){
        SQLiteDatabase db = null;
        try{
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();

            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_QUANTITY, quantity);
            values.put(DatabaseHelper.COLUMN_LIST_PRICE, listPirce);
            values.put(DatabaseHelper.COLUMN_SALE_PRICE, salePrice);

            db.update(DatabaseHelper.TABLE_CART, values, DatabaseHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(cartId)});
            db.delete(DatabaseHelper.TABLE_SAUCE, DatabaseHelper.COLUMN_SAUCE_ID + " = ? ",
                    new String[]{String.valueOf(cartId)});
            for(String sauceName : sauces){
                ContentValues sValues = new ContentValues();
                sValues.put(DatabaseHelper.COLUMN_SAUCE_CART_ITEM_ID, cartId);
                sValues.put(DatabaseHelper.COLUMN_SAUCE_NAME, sauceName);
                db.insert(DatabaseHelper.TABLE_SAUCE, null, sValues);
            }
            db.setTransactionSuccessful();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            if(db != null) db.endTransaction();
            if(db != null) db.close();
        }
    }
    public  void removeItem(int cartId){
        SQLiteDatabase db = null;
        try{
            db = dbHelper.getWritableDatabase();
            db.delete(DatabaseHelper.TABLE_SAUCE,
                    DatabaseHelper.COLUMN_SAUCE_CART_ITEM_ID + " =? ",
                    new String[]{String.valueOf(cartId)});
            db.delete(DatabaseHelper.TABLE_CART,
                    DatabaseHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(cartId)});
        } catch (Exception e) {
            Log.e(TAG, "removeItem error: " + e.getMessage(), e);
        }
        finally {
            if(db != null) db.close();
        }

    }
    public List<CartItem> getAll(int userId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        LinkedHashMap<Integer, CartItem> map = new LinkedHashMap<>();
        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT c.*, s." + DatabaseHelper.COLUMN_SAUCE_ID + " AS s_id, " +
                    "s." + DatabaseHelper.COLUMN_SAUCE_NAME + " AS s_name " +
                    "FROM " + DatabaseHelper.TABLE_CART + " c " +
                    "LEFT JOIN " + DatabaseHelper.TABLE_SAUCE + " s " +
                    "ON c." + DatabaseHelper.COLUMN_ID +
                    " = s." + DatabaseHelper.COLUMN_SAUCE_CART_ITEM_ID +
                    " WHERE c." + DatabaseHelper.COLUMN_USER_ID + " = ?" +
                    " ORDER BY c." + DatabaseHelper.COLUMN_CREATED_AT + " DESC";

            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
            while (cursor.moveToNext()) {
                int cartId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                if (!map.containsKey(cartId)) {
                    CartItem item = new CartItem(
                            cartId,
                            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_NAME)),
                            cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LIST_PRICE)),
                            cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SALE_PRICE)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_QUANTITY)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE)),
                            new ArrayList<>(),
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COMBO_DETAIL)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_AT))
                    );
                    map.put(cartId, item);
                }

                if (!cursor.isNull(cursor.getColumnIndexOrThrow("s_id"))) {
                    String sauceName = cursor.getString(cursor.getColumnIndexOrThrow("s_name"));
                    map.get(cartId).getSauces().add(new CartSauceItem(0, cartId, sauceName));
                }
            }
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return new ArrayList<>(map.values());
    }

    public   int getCount(int userId){
        SQLiteDatabase db = null;
        Cursor cursor = null;
        int count = 0;
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery("SELECT SUM(" + DatabaseHelper.COLUMN_QUANTITY + ")" +
                    " FROM " + DatabaseHelper.TABLE_CART +
                    " WHERE " + DatabaseHelper.COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)});
            if (cursor.moveToFirst()) count = cursor.getInt(0);
        } catch (Exception e) {
            Log.e(TAG, "getCount error: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return count;
    }
    public void clearCart() {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            db.delete(DatabaseHelper.TABLE_CART, null, null);
        } catch (Exception e) {
            Log.e(TAG, "clearCart error: " + e.getMessage(), e);
        } finally {
            if (db != null) db.close();
        }
    }
}
