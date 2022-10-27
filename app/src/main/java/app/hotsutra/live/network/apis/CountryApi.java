package app.hotsutra.live.network.apis;

import app.hotsutra.live.models.home_content.AllCountry;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface CountryApi {

    @GET("all_country")
    Call<List<AllCountry>> getAllCountry(@Header("API-KEY") String apiKey,
                                         @Query("version") Integer id,
                                         @Query("user_id") String userId,
                                         @Query("udid") String uDID);

}
