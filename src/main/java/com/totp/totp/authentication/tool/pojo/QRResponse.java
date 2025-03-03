package com.totp.totp.authentication.tool.pojo;

public class QRResponse {
    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    String qrCode;

    public QRResponse(String qrCode) {
        this.qrCode = qrCode;
    }
}
