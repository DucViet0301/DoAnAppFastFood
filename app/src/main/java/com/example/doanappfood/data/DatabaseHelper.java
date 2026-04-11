package com.example.doanappfood.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "doanappfood.db";
    private static final int DB_VERSION = 3;
    private static DatabaseHelper instance;
    public static final String TABLE_CART = "cart_item";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PRODUCT_ID = "product_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_LIST_PRICE = "list_price";
    public static final String COLUMN_SALE_PRICE = "sale_price";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_IMAGE = "image_url";

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CART_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_CART + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PRODUCT_ID + " INTEGER NOT NULL UNIQUE, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_LIST_PRICE + " REAL NOT NULL, " +
                COLUMN_SALE_PRICE + " REAL, " +
                COLUMN_QUANTITY + " INTEGER DEFAULT 1, " +
                COLUMN_IMAGE + " TEXT, " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(CREATE_CART_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
            onCreate(db);
        }
    }
}