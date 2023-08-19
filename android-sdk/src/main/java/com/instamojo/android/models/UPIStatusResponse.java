package com.instamojo.android.models;

import com.google.gson.annotations.SerializedName;

public class UPIStatusResponse {

    @SerializedName("status_code")
    private int statusCode;

    @SerializedName("status_message")
    private String statusMessage;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
