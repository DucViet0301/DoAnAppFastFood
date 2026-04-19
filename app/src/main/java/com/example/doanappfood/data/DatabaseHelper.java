package com.example.doanappfood.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "doanappfood.db";
    private static final int DB_VERSION = 6;
    private static DatabaseHelper instance;
    //Cart_item
    public static final String TABLE_CART = "cart_item";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_PRODUCT_ID = "product_id";
    public static final String COLUMN_PRODUCT_NAME = "product_name";
    public static final String COLUMN_LIST_PRICE = "list_price";
    public static final String COLUMN_SALE_PRICE = "sale_price";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_IMAGE = "image_url";
    public static final String COLUMN_COMBO_DETAIL = "combo_detail";
    public static final String COLUMN_CREATED_AT = "created_at";
    // cart_item_sauce
    public static final String TABLE_SAUCE = "cart_item_sauce";
    public static final String COLUMN_SAUCE_ID = "id";
    public static final String COLUMN_SAUCE_CART_ITEM_ID = "cart_item_id";
    public static final String COLUMN_SAUCE_NAME = "sauce_name";

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
                COLUMN_USER_ID + " INTEGER NOT NULL, " +
                COLUMN_PRODUCT_ID + " INTEGER NOT NULL, " +
                COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " +
                COLUMN_LIST_PRICE + " REAL NOT NULL, " +
                COLUMN_SALE_PRICE + " REAL, " +
                COLUMN_QUANTITY + " INTEGER DEFAULT 1, " +
                COLUMN_IMAGE + " TEXT, " +
                COLUMN_COMBO_DETAIL + " TEXT, " +
                COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(CREATE_CART_TABLE);

        String CREATE_SAUCE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_SAUCE + " (" +
                COLUMN_SAUCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SAUCE_CART_ITEM_ID + " INTEGER NOT NULL, " +
                COLUMN_SAUCE_NAME + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_SAUCE_CART_ITEM_ID + ") REFERENCES " + TABLE_CART + "(" + COLUMN_ID + "))";
        db.execSQL(CREATE_SAUCE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAUCE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
            onCreate(db);
        }
    }
}