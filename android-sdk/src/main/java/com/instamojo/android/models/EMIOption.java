package com.instamojo.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EMIOption implements Parcelable {

    @SerializedName("bank_name")
    private String bankName;

    @SerializedName("bank_code")
    private String bankCode;

    @SerializedName("rates")
    private List<EMIRate> emiRates;

    protected EMIOption(Parcel in) {
        bankName = in.readString();
        bankCode = in.readString();
    }

    public static final Creator<EMIOption> CREATOR = new Creator<EMIOption>() {
        @Override
        public EMIOption createFromParcel(Parcel in) {
            return new EMIOption(in);
        }

        @Override
        public EMIOption[] newArray(int size) {
            return new EMIOption[size];
        }
    };

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public List<EMIRate> getEmiRates() {
        return emiRates;
    }

    public void setEmiRates(List<EMIRate> emiRates) {
        this.emiRates = emiRates;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(bankName);
        parcel.writeString(bankCode);
    }
}
