package app.hotsutra.live.network.apis;

import app.hotsutra.live.network.model.ConfigResponse;
import app.hotsutra.live.network.model.config.Configuration;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ConfigurationApi {
    /*@GET("config")
    Call<Configuration> getConfigurationData(@Header("API-KEY") String apiKey,
                                             @Query("version") Integer id,
                                             @Query("user_id") String userId
                                             *//*@Query("udid") String uDID*//*);

    @GET("app_config")
    Call<ConfigResponse> getConfigData(@Query("user_id") String userId);*/

    @GET("config")
    Call<Configuration> getConfigurationData(@Header("API-KEY") String apiKey,
                                             @Query("version") Integer id,
                                             @Query("user_id") String userId,
                                             @Query("app_id") String versionName,
                                             @Query("secret_key") String sKey
            /*@Query("udid") String uDID*/);

    @GET("app_access")
    Call<ConfigResponse> getConfigData(@Query("user_id") String userId,
                                       @Query("app_id") String versionName,
                                       @Query("secret_key") String sKey);
}
