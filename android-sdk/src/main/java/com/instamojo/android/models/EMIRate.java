package com.instamojo.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class EMIRate implements Parcelable {

    @SerializedName("tenure")
    private int tenure;

    @SerializedName("interest")
    private String interest;

    protected EMIRate(Parcel in) {
        tenure = in.readInt();
        interest = in.readString();
    }

    public static final Creator<EMIRate> CREATOR = new Creator<EMIRate>() {
        @Override
        public EMIRate createFromParcel(Parcel in) {
            return new EMIRate(in);
        }

        @Override
        public EMIRate[] newArray(int size) {
            return new EMIRate[size];
        }
    };

    public int getTenure() {
        return tenure;
    }

    public void setTenure(int tenure) {
        this.tenure = tenure;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(tenure);
        parcel.writeString(interest);
    }
}
