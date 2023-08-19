package com.instamojo.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * EMIOptions object that holds an array {@link EMIBank} and order details
 */
public class EMIOptions implements Parcelable {

    @SerializedName("emi_list")
    private List<EMIOption> emiOptions;

    @SerializedName("submission_url")
    private String submissionURL;

    @SerializedName("submission_data")
    private SubmissionData submissionData;

    protected EMIOptions(Parcel in) {
        submissionURL = in.readString();
        submissionData = in.readParcelable(SubmissionData.class.getClassLoader());
    }

    public static final Creator<EMIOptions> CREATOR = new Creator<EMIOptions>() {
        @Override
        public EMIOptions createFromParcel(Parcel in) {
            return new EMIOptions(in);
        }

        @Override
        public EMIOptions[] newArray(int size) {
            return new EMIOptions[size];
        }
    };

    public List<EMIOption> getEmiOptions() {
        return emiOptions;
    }

    public void setEmiOptions(List<EMIOption> emiOptions) {
        this.emiOptions = emiOptions;
    }

    public String getSubmissionURL() {
        return submissionURL;
    }

    public void setSubmissionURL(String submissionURL) {
        this.submissionURL = submissionURL;
    }

    public SubmissionData getSubmissionData() {
        return submissionData;
    }

    public void setSubmissionData(SubmissionData submissionData) {
        this.submissionData = submissionData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(submissionURL);
        parcel.writeParcelable(submissionData, i);
    }

    @Override
    public String toString() {
        return "EMIOptions{" +
                "emiOptions=" + emiOptions +
                ", submissionURL='" + submissionURL + '\'' +
                ", submissionData=" + submissionData +
                '}';
    }
}