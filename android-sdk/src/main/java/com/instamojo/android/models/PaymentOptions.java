package com.instamojo.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class PaymentOptions implements Parcelable {

    @SerializedName("card_options")
    private CardOptions cardOptions;

    @SerializedName("netbanking_options")
    private NetBankingOptions netBankingOptions;

    @SerializedName("emi_options")
    private EMIOptions emiOptions;

    @SerializedName("wallet_options")
    private WalletOptions walletOptions;

    @SerializedName("upi_options")
    private UPIOptions upiOptions;

    protected PaymentOptions(Parcel in) {
        cardOptions = in.readParcelable(CardOptions.class.getClassLoader());
        netBankingOptions = in.readParcelable(NetBankingOptions.class.getClassLoader());
        emiOptions = in.readParcelable(EMIOptions.class.getClassLoader());
        walletOptions = in.readParcelable(WalletOptions.class.getClassLoader());
        upiOptions = in.readParcelable(UPIOptions.class.getClassLoader());
    }

    public static final Creator<PaymentOptions> CREATOR = new Creator<PaymentOptions>() {
        @Override
        public PaymentOptions createFromParcel(Parcel in) {
            return new PaymentOptions(in);
        }

        @Override
        public PaymentOptions[] newArray(int size) {
            return new PaymentOptions[size];
        }
    };

    public CardOptions getCardOptions() {
        return cardOptions;
    }

    public void setCardOptions(CardOptions cardOptions) {
        this.cardOptions = cardOptions;
    }

    public NetBankingOptions getNetBankingOptions() {
        return netBankingOptions;
    }

    public void setNetBankingOptions(NetBankingOptions netBankingOptions) {
        this.netBankingOptions = netBankingOptions;
    }

    public EMIOptions getEmiOptions() {
        return emiOptions;
    }

    public void setEmiOptions(EMIOptions emiOptions) {
        this.emiOptions = emiOptions;
    }

    public WalletOptions getWalletOptions() {
        return walletOptions;
    }

    public void setWalletOptions(WalletOptions walletOptions) {
        this.walletOptions = walletOptions;
    }

    public UPIOptions getUpiOptions() {
        return upiOptions;
    }

    public void setUpiOptions(UPIOptions upiOptions) {
        this.upiOptions = upiOptions;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(cardOptions, flags);
        dest.writeParcelable(netBankingOptions, flags);
        dest.writeParcelable(emiOptions, flags);
        dest.writeParcelable(walletOptions, flags);
        dest.writeParcelable(upiOptions, flags);
    }

    @Override
    public String toString() {
        return "PaymentOptions{" +
                "cardOptions=" + cardOptions +
                ", netBankingOptions=" + netBankingOptions +
                ", emiOptions=" + emiOptions +
                ", walletOptions=" + walletOptions +
                ", upiOptions=" + upiOptions +
                '}';
    }
}
