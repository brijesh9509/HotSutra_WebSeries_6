package app.hotsutra.live.network.model;

import com.google.gson.annotations.SerializedName;

public class PhonepeData {

	@SerializedName("instrumentResponse")
	private PhonepeInstrumentResponse instrumentResponse;

	@SerializedName("merchantId")
	private String merchantId;

	@SerializedName("merchantTransactionId")
	private String merchantTransactionId;

	public void setInstrumentResponse(PhonepeInstrumentResponse instrumentResponse){
		this.instrumentResponse = instrumentResponse;
	}

	public PhonepeInstrumentResponse getInstrumentResponse(){
		return instrumentResponse;
	}

	public void setMerchantId(String merchantId){
		this.merchantId = merchantId;
	}

	public String getMerchantId(){
		return merchantId;
	}

	public void setMerchantTransactionId(String merchantTransactionId){
		this.merchantTransactionId = merchantTransactionId;
	}

	public String getMerchantTransactionId(){
		return merchantTransactionId;
	}
}