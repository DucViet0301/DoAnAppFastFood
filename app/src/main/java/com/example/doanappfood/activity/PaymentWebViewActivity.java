package com.example.doanappfood.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doanappfood.R;

public class PaymentWebViewActivity extends AppCompatActivity {

    private WebView     webView;
    private ProgressBar progressBar;
    private View        layoutLoading;
    private View        layoutError;
    private TextView    tvErrorMsg;
    private Button      btnRetry;

    private String payUrl;
    private String orderId;
    private String redirectHost;
    private String redirectPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_web_view);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(android.graphics.Color.parseColor("#AE2070"));
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        payUrl       = getIntent().getStringExtra("pay_url");
        orderId      = getIntent().getStringExtra("order_id");
        redirectHost = getIntent().getStringExtra("redirect_host");
        redirectPath = getIntent().getStringExtra("redirect_path");

        if (payUrl == null || payUrl.isEmpty()) {
            finishCanceled("Không có URL thanh toán");
            return;
        }


        bindViews();
        setupWebView();
        webView.loadUrl(payUrl);
    }

    private void bindViews() {
        webView       = findViewById(R.id.webview);
        progressBar   = findViewById(R.id.progress_bar);
        layoutLoading = findViewById(R.id.layout_loading);
        layoutError   = findViewById(R.id.layout_error);
        tvErrorMsg    = findViewById(R.id.tv_error_message);
        btnRetry      = findViewById(R.id.btn_retry);

        findViewById(R.id.btn_close).setOnClickListener(v -> confirmCancel());
        btnRetry.setOnClickListener(v -> {
            layoutError.setVisibility(View.GONE);
            webView.reload();
        });

        setupBackHandler(); // xử lý nút back
    }

    private void setupWebView() {
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        s.setBuiltInZoomControls(false);
        s.setDisplayZoomControls(false);
        s.setSupportZoom(false);
        s.setCacheMode(WebSettings.LOAD_NO_CACHE);

        // Cho phép load ảnh (QR code, banner...)
        s.setLoadsImagesAutomatically(true);
        s.setBlockNetworkImage(false);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return interceptUrl(request.getUrl().toString());
            }

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return interceptUrl(url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                layoutLoading.setVisibility(View.VISIBLE);
                layoutError.setVisibility(View.GONE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                layoutLoading.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (request.isForMainFrame()) {
                    layoutLoading.setVisibility(View.GONE);
                    tvErrorMsg.setText("Không tải được trang thanh toán.\nKiểm tra kết nối mạng.");
                    layoutError.setVisibility(View.VISIBLE);
                }
            }

            private boolean interceptUrl(String url) {
                if (!url.contains(redirectHost + redirectPath)) return false;
                Uri uri = Uri.parse(url);
                String resultCode = uri.getQueryParameter("resultCode");
                String message    = uri.getQueryParameter("message");
                String retOrderId = uri.getQueryParameter("orderId");
                if ("0".equals(resultCode)) {
                    finishSuccess(retOrderId != null ? retOrderId : orderId);
                } else {
                    finishCanceled(message != null ? message : "Thanh toán thất bại");
                }
                return true;
            }
        });

        webView.setWebChromeClient(new android.webkit.WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                progressBar.setVisibility(newProgress < 100 ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void finishSuccess(String returnedOrderId) {
        Intent result = new Intent();
        result.putExtra("order_id", returnedOrderId);
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    private void finishCanceled(String message) {
        Intent result = new Intent();
        result.putExtra("error_message", message);
        setResult(Activity.RESULT_CANCELED, result);
        finish();
    }

    // Thay onBackPressed() deprecated bằng OnBackPressedCallback
    private void setupBackHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    confirmCancel();
                }
            }
        });
    }

    private void confirmCancel() {
        new AlertDialog.Builder(this)
                .setTitle("Hủy thanh toán?")
                .setMessage("Bạn có chắc muốn hủy thanh toán không?")
                .setPositiveButton("Hủy thanh toán", (d, w) -> finishCanceled("Người dùng hủy thanh toán"))
                .setNegativeButton("Tiếp tục", null)
                .show();
    }
}