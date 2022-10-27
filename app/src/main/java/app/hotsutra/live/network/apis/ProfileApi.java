package app.hotsutra.live.network.apis;

import app.hotsutra.live.network.model.ResponseStatus;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ProfileApi {
    //@FormUrlEncoded
    @Multipart
    @POST("update_profile")
    Call<ResponseStatus> updateProfile(@Header("API-KEY") String apiKey,
                                       @Part("id") RequestBody id,
                                       @Part("name") RequestBody name,
                                       @Part("email") RequestBody email,
                                       @Part("phone") RequestBody phone,
                                       @Part("password") RequestBody password,
                                       @Part("current_password") RequestBody currentPassword,
                                       @Part MultipartBody.Part photo,
                                       @Part("gender") RequestBody gender,
                                       @Part("version") RequestBody vId,
                                       @Query("user_id") RequestBody userId,
                                       @Query("udid") String uDID);

}
