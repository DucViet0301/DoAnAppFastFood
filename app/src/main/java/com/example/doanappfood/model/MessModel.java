package com.example.doanappfood.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MessModel {
    private boolean success;
    private String message;
    @SerializedName("order_id")
    private int order_id;

    public int getOrder_id() {
        return order_id;
    }

    @SerializedName("resultCode")
    private Integer resultCode;  

    @SerializedName("payUrl")
    private String payUrl;

    @SerializedName("orderId")
    private String orderId;

    // ── isSuccess() xử lý cả 2 trường hợp ──────────────────────────
    public boolean isSuccess() {
        // Nếu là response MoMo (có resultCode)
        if (resultCode != null) {
            return resultCode == 0 && payUrl != null && !payUrl.isEmpty();
        }
        // Nếu là response Order (COD)
        return success;
    }

    // Getter / Setter
    public boolean getRawSuccess()        { return success; }
    public void setSuccess(boolean s)     { this.success = s; }
    public String getMessage()            { return message; }
    public void setMessage(String m)      { this.message = m; }
    public Integer getResultCode()        { return resultCode; }
    public String getPayUrl()             { return payUrl; }
    public String getOrderId()            { return orderId; }
}
