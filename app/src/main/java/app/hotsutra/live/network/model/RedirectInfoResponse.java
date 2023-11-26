package app.hotsutra.live.network.model;

import com.google.gson.annotations.SerializedName;

public class RedirectInfoResponse {

	@SerializedName("url")
	private String url;


	public void setUrl(String intentUrl){
		this.url = intentUrl;
	}

	public String getUrl(){
		return url;
	}

}