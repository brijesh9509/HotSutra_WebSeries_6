package com.instamojo.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Order Class to hold the details of a Order.
 */
public class Order implements Parcelable {

    @SerializedName("id")
    private String id;

    @SerializedName("transaction_id")
    private String transactionID;

    @SerializedName("name")
    private String buyerName;

    @SerializedName("email")
    private String buyerEmail;

    @SerializedName("phone")
    private String buyerPhone;

    @SerializedName("amount")
    private String amount;

    @SerializedName("description")
    private String description;

    @SerializedName("currency")
    private String currency;

    @SerializedName("redirect_url")
    private String redirectUrl;

    @SerializedName("webhook_url")
    private String webhookUrl;

    @SerializedName("status")
    private String status;

    protected Order(Parcel in) {
        id = in.readString();
        transactionID = in.readString();
        buyerName = in.readString();
        buyerEmail = in.readString();
        buyerPhone = in.readString();
        amount = in.readString();
        description = in.readString();
        currency = in.readString();
        redirectUrl = in.readString();
        webhookUrl = in.readString();
        status = in.readString();
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerEmail() {
        return buyerEmail;
    }

    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public void setBuyerPhone(String buyerPhone) {
        this.buyerPhone = buyerPhone;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(transactionID);
        parcel.writeString(buyerName);
        parcel.writeString(buyerEmail);
        parcel.writeString(buyerPhone);
        parcel.writeString(amount);
        parcel.writeString(description);
        parcel.writeString(currency);
        parcel.writeString(redirectUrl);
        parcel.writeString(webhookUrl);
        parcel.writeString(status);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", transactionID='" + transactionID + '\'' +
                ", buyerName='" + buyerName + '\'' +
                ", buyerEmail='" + buyerEmail + '\'' +
                ", buyerPhone='" + buyerPhone + '\'' +
                ", amount='" + amount + '\'' +
                ", description='" + description + '\'' +
                ", currency='" + currency + '\'' +
                ", redirectUrl='" + redirectUrl + '\'' +
                ", webhookUrl='" + webhookUrl + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}