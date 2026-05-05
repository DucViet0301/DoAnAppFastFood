package com.example.doanappfood.Utlis;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LocationHelper {

    public interface LocationCallback {
        void onAddressRetrieved(String address);
        void onError(String errorMessage);
    }

    public static void getAddressFromApi(double lat, double lng, LocationCallback callback) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=" + lat + "&lon=" + lng + "&accept-language=vi";
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "DoAnAppFood/1.0")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                sendError(callback, "Lỗi kết nối OSM: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String jsonData = response.body().string();
                        JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();

                        if (jsonObject.has("display_name")) {
                            String address = jsonObject.get("display_name").getAsString();
                            sendSuccess(callback, address);
                        } else {
                            sendError(callback, "Không tìm thấy địa chỉ trên OSM");
                        }
                    } catch (Exception e) {
                        sendError(callback, "Lỗi xử lý dữ liệu OSM");
                    }
                }
            }
        });
    }

    private static void sendSuccess(LocationCallback callback, String address) {
        new Handler(Looper.getMainLooper()).post(() -> callback.onAddressRetrieved(address));
    }

    private static void sendError(LocationCallback callback, String error) {
        new Handler(Looper.getMainLooper()).post(() -> callback.onError(error));
    }
}