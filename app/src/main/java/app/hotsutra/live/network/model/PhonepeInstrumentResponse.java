package app.hotsutra.live.network.model;

import com.google.gson.annotations.SerializedName;

public class PhonepeInstrumentResponse {

	@SerializedName("intentUrl")
	private String intentUrl;

	@SerializedName("type")
	private String type;

	public void setIntentUrl(String intentUrl){
		this.intentUrl = intentUrl;
	}

	public String getIntentUrl(){
		return intentUrl;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	@SerializedName("redirectInfo")
	private RedirectInfoResponse redirectInfoResponse;

	public void setRedirectInfoResponse(RedirectInfoResponse redirectInfoResponse){
		this.redirectInfoResponse = redirectInfoResponse;
	}

	public RedirectInfoResponse getRedirectInfoResponse(){
		return redirectInfoResponse;
	}
}