package app.hotsutra.live.network.apis;


import app.hotsutra.live.network.model.PaytmResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PaymentApi {

    @FormUrlEncoded
    @POST("store_payment_info")
    Call<ResponseBody> savePayment(@Header("API-KEY") String apiKey,
                                   @Field("plan_id") String planId,
                                   @Field("user_id") String userId,
                                   @Field("paid_amount") String paidAmount,
                                   @Field("payment_info") String paymentInfo,
                                   @Field("payment_method") String paymentMethod,
                                   @Field("version") Integer vId,
                                   @Field("udid") String uDID);


    @GET("paytm_initiate_transaction")
    Call<PaytmResponse> getPaytmToken(@Header("API-KEY") String apiKey,
                                      @Query("plan_id") String planId,
                                      @Query("user_id") String userId,
                                      @Query("version") Integer vId,
                                      @Query("udid") String uDID);
}
