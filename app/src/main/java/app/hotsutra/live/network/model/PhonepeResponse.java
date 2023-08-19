package app.hotsutra.live.network.model;

import com.google.gson.annotations.SerializedName;

public class PhonepeResponse {

	@SerializedName("code")
	private String code;

	@SerializedName("data")
	private PhonepeData data;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private int status;

	public void setCode(String code){
		this.code = code;
	}

	public String getCode(){
		return code;
	}

	public void setData(PhonepeData data){
		this.data = data;
	}

	public PhonepeData getData(){
		return data;
	}

	public void setSuccess(boolean success){
		this.success = success;
	}

	public boolean isSuccess(){
		return success;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setStatus(int status){
		this.status = status;
	}

	public int getStatus(){
		return status;
	}
}

