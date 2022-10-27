package app.hotsutra.live.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import app.hotsutra.live.AppConfig;
import app.hotsutra.live.LoginActivity;
import app.hotsutra.live.network.RetrofitClient;

import org.json.JSONObject;

public class ApiResources {

    public static String CURRENCY; // must be valid currency code
    public static String EXCHSNGE_RATE;
    public static String PAYPAL_CLIENT_ID;
    public static String RAZORPAY_EXCHANGE_RATE;
    public static String USER_PHONE;


    String URL = AppConfig.API_SERVER_URL + RetrofitClient.API_URL_EXTENSION;

    private static Toast toast;


    String searchUrl = URL + "search";

    String getAllReply = URL + "all_replay";
    String termsURL = AppConfig.TERMS_URL;

    public String getTermsURL() {
        return termsURL;
    }

    public String getGetAllReply() {
        return getAllReply;
    }

    public String getSearchUrl() {
        return searchUrl;
    }

    public static void openLoginScreen(String mErrorBody,
                                       Context mContext) {
        try {
            JSONObject jObjError = new JSONObject(mErrorBody);
            if (toast != null ) {
                toast.cancel();
            }
            toast = Toast.makeText(mContext, jObjError.getString("message"), Toast.LENGTH_LONG);
            toast.show();

            SharedPreferences.Editor editor = mContext
                    .getSharedPreferences(Constants.USER_LOGIN_STATUS, MODE_PRIVATE).edit();
            editor.putBoolean(Constants.USER_LOGIN_STATUS, false);
            editor.apply();

            /*DatabaseHelper databaseHelper = new DatabaseHelper(mContext);
            databaseHelper.deleteUserData();*/

            PreferenceUtils.clearSubscriptionSavedData(mContext);

            Intent intent = new Intent(mContext, LoginActivity.class);
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


