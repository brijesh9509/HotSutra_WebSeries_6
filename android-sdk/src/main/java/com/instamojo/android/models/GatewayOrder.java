package com.instamojo.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class GatewayOrder implements Parcelable {

    @SerializedName("order")
    private Order order;

    @SerializedName("payment_options")
    private PaymentOptions paymentOptions;

    protected GatewayOrder(Parcel in) {
        order = in.readParcelable(Order.class.getClassLoader());
        paymentOptions = in.readParcelable(PaymentOptions.class.getClassLoader());
    }

    public static final Creator<GatewayOrder> CREATOR = new Creator<GatewayOrder>() {
        @Override
        public GatewayOrder createFromParcel(Parcel in) {
            return new GatewayOrder(in);
        }

        @Override
        public GatewayOrder[] newArray(int size) {
            return new GatewayOrder[size];
        }
    };

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public PaymentOptions getPaymentOptions() {
        return paymentOptions;
    }

    public void setPaymentOptions(PaymentOptions paymentOptions) {
        this.paymentOptions = paymentOptions;
    }

    @Override
    public String toString() {
        return "GatewayOrder{" +
                "order=" + order +
                ", paymentOptions=" + paymentOptions +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(order, flags);
        dest.writeParcelable(paymentOptions, flags);
    }
}
