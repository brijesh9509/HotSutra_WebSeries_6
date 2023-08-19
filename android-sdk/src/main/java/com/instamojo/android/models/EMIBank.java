package com.instamojo.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * EMIBank object that holds the EMI details of a bank
 */

public class EMIBank implements Parcelable {
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<EMIBank> CREATOR = new Parcelable.Creator<EMIBank>() {
        @Override
        public EMIBank createFromParcel(Parcel in) {
            return new EMIBank(in);
        }

        @Override
        public EMIBank[] newArray(int size) {
            return new EMIBank[size];
        }
    };
    private String bankName;
    private String bankCode;
    private LinkedHashMap<Integer, BigDecimal> rates = new LinkedHashMap<>();

    public EMIBank(String bankName, String bankCode, LinkedHashMap<Integer, BigDecimal> rates) {
        this.bankName = bankName;
        this.bankCode = bankCode;
        this.rates = rates;
    }

    protected EMIBank(Parcel in) {
        bankName = in.readString();
        bankCode = in.readString();
        int ratesSize = in.readInt();
        if (ratesSize == 0) {
            return;
        }
        rates = new LinkedHashMap<>();
        for (int i = 0; i < ratesSize; i++) {
            int tenure = in.readInt();
            String interest = in.readString();
            rates.put(tenure, new BigDecimal(interest));
        }
    }

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

    public HashMap<Integer, BigDecimal> getRates() {
        return rates;
    }

    public void setRates(LinkedHashMap<Integer, BigDecimal> rates) {
        this.rates = rates;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bankName);
        dest.writeString(bankCode);
        if (rates.size() < 1) {
            dest.writeInt(0);
            return;
        }

        dest.writeInt(rates.size());
        for (Map.Entry<Integer, BigDecimal> entry : rates.entrySet()) {
            dest.writeInt(entry.getKey());
            dest.writeString(entry.getValue().toPlainString());
        }
    }
}