package app.hotsutra.live.network.apis;

import app.hotsutra.live.models.home_content.Video;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface TvSeriesApi {

    @GET("tvseries")
    Call<List<Video>> getTvSeries(@Header("API-KEY") String apiKey,
                                  @Query("page") int page,
                                  @Query("version") Integer vId,
                                  @Query("user_id") String userId,
                                  @Query("udid") String uDID);


}
