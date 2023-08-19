package com.instamojo.android.models;

import com.google.gson.annotations.SerializedName;

/**
 * Response Model for UPISubmission submission Call.
 */

public class UPISubmissionResponse {

    @SerializedName("payment_id")
    private String paymentID;

    @SerializedName("status_code")
    private int statusCode;

    @SerializedName("payer_virtual_address")
    private String payerVirtualAddress;

    @SerializedName("payee_virtual_address")
    private String payeeVirtualAddress;

    @SerializedName("status_check_url")
    private String statusCheckURL;

    @SerializedName("upi_bank")
    private String upiBank;

    @SerializedName("status_message")
    private String statusMessage;

    public String getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(String paymentID) {
        this.paymentID = paymentID;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getPayerVirtualAddress() {
        return payerVirtualAddress;
    }

    public void setPayerVirtualAddress(String payerVirtualAddress) {
        this.payerVirtualAddress = payerVirtualAddress;
    }

    public String getPayeeVirtualAddress() {
        return payeeVirtualAddress;
    }

    public void setPayeeVirtualAddress(String payeeVirtualAddress) {
        this.payeeVirtualAddress = payeeVirtualAddress;
    }

    public String getStatusCheckURL() {
        return statusCheckURL;
    }

    public void setStatusCheckURL(String statusCheckURL) {
        this.statusCheckURL = statusCheckURL;
    }

    public String getUpiBank() {
        return upiBank;
    }

    public void setUpiBank(String upiBank) {
        this.upiBank = upiBank;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
