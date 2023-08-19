package com.instamojo.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * UPIOptions Class to hold the details of a UPISubmission options.
 */

public class UPIOptions implements Parcelable {

    @SerializedName("submission_url")
    private String submissionURL;

    protected UPIOptions(Parcel in) {
        submissionURL = in.readString();
    }

    public static final Creator<UPIOptions> CREATOR = new Creator<UPIOptions>() {
        @Override
        public UPIOptions createFromParcel(Parcel in) {
            return new UPIOptions(in);
        }

        @Override
        public UPIOptions[] newArray(int size) {
            return new UPIOptions[size];
        }
    };

    public String getSubmissionURL() {
        return submissionURL;
    }

    public void setSubmissionURL(String submissionURL) {
        this.submissionURL = submissionURL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(submissionURL);
    }

    @Override
    public String toString() {
        return "UPIOptions{" +
                "submissionURL='" + submissionURL + '\'' +
                '}';
    }
}