package com.example.doanappfood.model;

import com.google.gson.annotations.SerializedName;

public class MessModel {
    // ── Field cho Order (COD) ────────────────────────────────────────
    private boolean success;
    private String message;

    // ── Field cho MoMo — server trả về ──────────────────────────────
    @SerializedName("resultCode")
    private Integer resultCode;   // Integer (có thể null nếu không phải response MoMo)

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
