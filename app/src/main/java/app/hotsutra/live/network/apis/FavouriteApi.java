package app.hotsutra.live.network.apis;

import app.hotsutra.live.models.Movie;
import app.hotsutra.live.network.model.FavoriteModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface FavouriteApi {

    @GET("favorite")
    Call<List<Movie>> getFavoriteList(@Header("API-KEY") String apiKey,
                                      @Query("user_id") String userId,
                                      @Query("page") int page,
                                      @Query("version") Integer vId,
                                      @Query("udid") String uDID);

    @GET("add_favorite")
    Call<FavoriteModel> addToFavorite(@Header("API-KEY") String apiKey,
                                      @Query("user_id") String userId,
                                      @Query("videos_id") String videoId,
                                      @Query("version") Integer vId,
                                      @Query("udid") String uDID);

    @GET("remove_favorite")
    Call<FavoriteModel> removeFromFavorite(@Header("API-KEY") String apiKey,
                                           @Query("user_id") String userId,
                                           @Query("videos_id") String videoId,
                                           @Query("version") Integer vId,
                                           @Query("udid") String uDID);

    @GET("verify_favorite_list")
    Call<FavoriteModel> verifyFavoriteList(@Header("API-KEY") String apiKey,
                                           @Query("user_id") String userId,
                                           @Query("videos_id") String videoId,
                                           @Query("version") Integer vId,
                                           @Query("udid") String uDID);
}
