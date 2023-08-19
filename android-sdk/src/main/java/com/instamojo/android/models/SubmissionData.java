package com.instamojo.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class SubmissionData implements Parcelable {

    @SerializedName("merchant_id")
    private String merchantID;

    @SerializedName("order_id")
    private String orderID;

    protected SubmissionData(Parcel in) {
        merchantID = in.readString();
        orderID = in.readString();
    }

    public static final Creator<SubmissionData> CREATOR = new Creator<SubmissionData>() {
        @Override
        public SubmissionData createFromParcel(Parcel in) {
            return new SubmissionData(in);
        }

        @Override
        public SubmissionData[] newArray(int size) {
            return new SubmissionData[size];
        }
    };

    public String getMerchantID() {
        return merchantID;
    }

    public void setMerchantID(String merchantID) {
        this.merchantID = merchantID;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(merchantID);
        parcel.writeString(orderID);
    }
}
