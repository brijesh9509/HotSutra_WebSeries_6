package app.hotsutra.live.network.apis;

import app.hotsutra.live.network.model.AllPackage;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface PackageApi {

    @GET("all_package")
    Call<AllPackage> getAllPackage(@Header("API-KEY") String apiKey,
                                   @Query("version") Integer vId,
                                   @Query("user_id") String userId,
                                   @Query("udid") String uDID);

}
