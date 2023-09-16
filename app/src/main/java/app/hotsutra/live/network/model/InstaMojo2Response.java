package app.hotsutra.live.network.model;

import com.google.gson.annotations.SerializedName;


public class InstaMojo2Response {

    @SerializedName("amount")
    private String amount;

    @SerializedName("phone")
    private String phone;

    @SerializedName("longurl")
    private String longUrl;

    @SerializedName("name")
    private String name;

    @SerializedName("client_secret")
    private String clientSecret;

    @SerializedName("order_id")
    private String orderId;

    @SerializedName("email")
    private String email;

    @SerializedName("client_id")
    private String clientId;

    public String getAmount() {
        return amount;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getEmail() {
        return email;
    }

    public String getClientId() {
        return clientId;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}