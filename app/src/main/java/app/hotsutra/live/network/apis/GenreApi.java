package app.hotsutra.live.network.apis;

import app.hotsutra.live.models.home_content.AllGenre;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface GenreApi {

    @GET("all_genre")
    Call<List<AllGenre>> getGenre(@Header("API-KEY") String apiKey,
                                  @Query("version") Integer vId,
                                  @Query("user_id") String userId,
                                  @Query("udid") String uDID);


}
