package com.instamojo.android.models;

import com.google.gson.annotations.SerializedName;

public class CardPaymentResponse {

    @SerializedName("payment")
    private Payment payment;

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public String getUrl() {
        return this.payment.getAuthentication().getUrl();
    }
}

class Payment {

    @SerializedName("authentication")
    private Authentication authentication;

    public Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }
}

class Authentication {

    @SerializedName("url")
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
