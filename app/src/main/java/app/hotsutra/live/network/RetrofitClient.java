package app.hotsutra.live.network;

import app.hotsutra.live.AppConfig;

import app.hotsutra.live.utils.AESHelper;
import app.hotsutra.live.utils.MyAppClass;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    public static final String API_URL_EXTENSION = "/v130/";
    //copy your api username and password from php admin dashboard & paste below
    public static final String API_USER_NAME = "admin";
    public static final String API_PASSWORD = "1234";

    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(new BasicAuthInterceptor(API_USER_NAME, API_PASSWORD)).build();

        if (retrofit == null) {
             /*retrofit = new Retrofit.Builder()
                    .baseUrl(AESHelper.decrypt(MyAppClass.HASH_KEY,AppConfig.API_SERVER_URL) + API_URL_EXTENSION)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();*/

            retrofit = new Retrofit.Builder()
                    .baseUrl(AppConfig.API_SERVER_URL + API_URL_EXTENSION)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}
