package app.hotsutra.live.network.apis;

import app.hotsutra.live.network.model.Channel;
import app.hotsutra.live.network.model.LiveTvCategory;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface LiveTvApi {

    @GET("all_tv_channel_by_category")
    Call<List<LiveTvCategory>> getLiveTvCategories(@Header("API-KEY") String apiKey,
                                                   @Query("version") Integer vId,
                                                   @Query("user_id") String userId,
                                                   @Query("udid") String uDID);

    @GET("featured_tv_channel")
    Call<List<Channel>> getFeaturedTV(@Header("API-KEY") String apiKey,
                                      @Query("page") int page,
                                      @Query("version") Integer vId,
                                      @Query("user_id") String userId,
                                      @Query("udid") String uDID);

}