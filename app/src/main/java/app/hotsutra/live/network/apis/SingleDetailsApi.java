package app.hotsutra.live.network.apis;


import app.hotsutra.live.models.single_details.SingleDetails;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface SingleDetailsApi {

    @GET("single_details")
    Call<SingleDetails> getSingleDetails(@Header("API-KEY") String key,
                                         @Query("type") String type,
                                         @Query("id") String id,
                                         @Query("version") Integer vId,
                                         @Query("user_id") String userId,
                                         @Query("udid") String uDID);
}
