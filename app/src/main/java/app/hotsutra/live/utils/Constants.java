package app.hotsutra.live.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.provider.Settings;

import app.hotsutra.live.models.single_details.Country;
import app.hotsutra.live.models.single_details.Genre;
import app.hotsutra.live.network.model.TvCategory;
import app.hotsutra.live.R;

import java.io.File;
import java.util.List;

public class Constants {

    public static final String ADMOB = "admob";
    public static final String START_APP = "startApp";
    public static final String NETWORK_AUDIENCE = "fan";

    public static final String PAYPAL = "paypal";
    public static final String STRIP = "strip";
    public static final String RAZOR_PAY = "razorpay";
    public static final String OFFLINE_PAY = "offline_pay";
    public static final String GOOGLE_PAY = "google_pay";

    public static String workId;

    //public static String DOWNLOAD_DIR = Environment.getExternalStorageDirectory().toString()+File.separator + Environment.DIRECTORY_DOWNLOADS + File.separator;

    public static String getDownloadDir(Context context) {
        //downloads/oxoo
       // return  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator;
       //return context.getExternalFilesDirs(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator;

        File dir = new File(Environment.getExternalStorageDirectory() + "/Download/" + context.getString(R.string.app_name));
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dir.getPath();
    }

    public static final String USER_LOGIN_STATUS = "login_status_2";
    public static final String USER_PASSWORD_AVAILABLE = "pass_available";

    public static List<Genre> genreList = null;
    public static List<Country> countryList = null;
    public static List<TvCategory> tvCategoryList = null;

    //room related constants
    public static final String ROOM_DB_NAME = "continue_watching_db";

    public static final String CONTENT_ID = "content_id";
    public static final String CONTENT_TITLE = "title";
    public static final String IMAGE_URL = "image_url";
    public static final String PROGRESS = "progress";
    public static final String POSITION ="position";
    public static final String STREAM_URL = "stream_url";
    public static final String CATEGORY_TYPE = "category_type";
    public static final String SERVER_TYPE = "server_type";
    public static final String IS_FROM_CONTINUE_WATCHING = "continue_watching_bool";
    public static final String YOUTUBE = "youtube";
    public static final String YOUTUBE_LIVE = "youtube_live";

    @SuppressLint("HardwareIds")
    public static String getDeviceId(Context mContext) {
        return Settings.Secure.getString(mContext.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }
}
