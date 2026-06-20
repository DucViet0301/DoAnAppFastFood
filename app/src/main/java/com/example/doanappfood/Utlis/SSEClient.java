package com.example.doanappfood.Utlis;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class SSEClient {
    private static final String TAG = "SSEClient";
    private static final int RECONNECT_DELAY_MS = 5000;

    public interface SSEListener {
        void onEvent(String event, String data);
        void onConnected();
        void onDisconnected();
    }

    private final String url;
    private final SSEListener listener;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final AtomicBoolean running    = new AtomicBoolean(false);
    private HttpURLConnection connection;

    public SSEClient(String url, SSEListener listener) {
        this.url      = url;
        this.listener = listener;
    }

    public void connect() {
        if (running.get()) return;
        running.set(true);
        executor.execute(this::listenLoop);
    }

    public void disconnect() {
        running.set(false);
        if (connection != null) connection.disconnect();
        executor.shutdownNow();
    }

    private void listenLoop() {
        while (running.get()) {
            try {
                Log.d(TAG, "SSE connecting → " + url);
                URL sseUrl = new URL(url);
                connection = (HttpURLConnection) sseUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "text/event-stream");
                connection.setRequestProperty("Cache-Control", "no-cache");
                connection.setConnectTimeout(15_000);
                connection.setReadTimeout(0);
                connection.connect();

                if (connection.getResponseCode() == 200) {
                    listener.onConnected();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream())
                    );
                    String event = "message";
                    String line;
                    while (running.get() && (line = reader.readLine()) != null) {
                        if (line.startsWith("event:")) {
                            event = line.substring(6).trim();
                        } else if (line.startsWith("data:")) {
                            String data = line.substring(5).trim();
                            listener.onEvent(event, data);
                            event = "message";
                        }
                    }
                    reader.close();
                }
            } catch (Exception e) {
                if (running.get()) Log.e(TAG, "SSE error: " + e.getMessage());
            } finally {
                if (connection != null) connection.disconnect();
                listener.onDisconnected();
            }

            if (running.get()) {
                try {
                    Log.d(TAG, "SSE reconnect in " + RECONNECT_DELAY_MS + "ms");
                    Thread.sleep(RECONNECT_DELAY_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
