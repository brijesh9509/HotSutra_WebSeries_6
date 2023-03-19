package app.hotsutra.live.network.model;

import com.google.gson.annotations.SerializedName;

public class PaytmResponse {

	@SerializedName("amount")
	private String amount;

	@SerializedName("callBackUrl")
	private String callBackUrl;

	@SerializedName("orderId")
	private String orderId;

	@SerializedName("mid")
	private String mid;

	@SerializedName("token")
	private String token;

	@SerializedName("status")
	private int status;

	public String getAmount(){
		return amount;
	}

	public String getCallBackUrl(){
		return callBackUrl;
	}

	public String getOrderId(){
		return orderId;
	}

	public String getMid(){
		return mid;
	}

	public String getToken(){
		return token;
	}

	public int getStatus(){
		return status;
	}
}