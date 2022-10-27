package app.hotsutra.live.network.apis;

import android.net.Uri;

import app.hotsutra.live.network.model.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface FirebaseAuthApi {

    @FormUrlEncoded
    @POST("firebase_auth")
    Call<User> getPhoneAuthStatus(@Header("API-KEY") String apiKey,
                                  @Field("uid") String uid,
                                  @Field("phone") String phoneNo,
                                  @Field("version") Integer vId,
                                  @Field("udid") String uDID);

    @FormUrlEncoded
    @POST("firebase_auth")
    Call<User> getGoogleAuthStatus(@Header("API-KEY") String apiKey,
                                   @Field("uid") String uid,
                                   @Field("email") String email,
                                   @Field("name") String name,
                                   @Field("image_url") Uri image,
                                   @Field("phone") String phone,
                                   @Field("version") Integer vId,
                                   @Field("udid") String uDID);


    @FormUrlEncoded
    @POST("firebase_auth")
    Call<User> getFacebookAuthStatus(@Header("API-KEY") String apiKey,
                                     @Field("uid") String uid,
                                     @Field("name") String name,
                                     @Field("email") String email,
                                     @Field("image_url") Uri photoUrl,
                                     @Field("version") Integer vId,
                                     @Field("udid") String uDID);

}
