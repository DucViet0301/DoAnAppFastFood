package com.example.doanappfood.Utlis;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "auth";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_PHONE = "user_phone";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_ADDRESS = "user_address";
    private static final String KEY_USER_BIRTHDATE = "user_birth_date";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    // Lưu sau khi đăng nhập thành công
    public void saveSession(String token, String refreshToken, int id, String name,
                            String phone, String email,
                            String address, String birthDate) {
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.putInt(KEY_USER_ID, id);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_PHONE, phone);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_ADDRESS, address);
        editor.putString(KEY_USER_BIRTHDATE, birthDate);
        editor.apply();
    }

    // Kiểm tra đã đăng nhập chưa
    public boolean isLoggedIn() {
        return pref.getString(KEY_TOKEN, null) != null;
    }

    // Lấy token để gửi kèm API request
    public String getToken() {
        return pref.getString(KEY_TOKEN, null);
    }

    public String getRefreshToken() {
        return pref.getString(KEY_REFRESH_TOKEN, null);
    }

    // Lấy thông tin user
    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    public String getUserName() {
        return pref.getString(KEY_USER_NAME, "");
    }

    public String getUserPhone() {
        return pref.getString(KEY_USER_PHONE, "");
    }

    public String getUserEmail() {
        return pref.getString(KEY_USER_EMAIL, "");
    }

    public String getUserAddress() {
        return pref.getString(KEY_USER_ADDRESS, "");
    }

    public String getUserBirthDate() {
        return pref.getString(KEY_USER_BIRTHDATE, "");
    }

    // Cập nhật Access Token mới sau khi refresh thành công
    public void saveNewAccessToken(String newToken) {
        editor.putString(KEY_TOKEN, newToken);
        editor.apply();
    }

    // Xóa khi đăng xuất
    public void logout() {
        editor.clear();
        editor.apply();
    }
}