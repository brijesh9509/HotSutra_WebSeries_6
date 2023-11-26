package app.hotsutra.live;

public class AppConfig {

    static {
        System.loadLibrary("api_config");
    }

    public static native String getApiServerUrl();

   // public static native String getApiKey();

    public static native String getPurchaseCode();

    public static native String getOneSignalAppID();

    public static final String API_SERVER_URL = getApiServerUrl();
    //public static final String API_KEY = getApiKey();
    //copy your terms url from php admin dashboard & paste below
    public static final String TERMS_URL = "https://sutrax.app/privacy-policy/";
    public static final String TOLL_WEB_URL = "http://tiny.cc/helpcall";
    public static final String ONE_SIGNAL_APP_ID = getOneSignalAppID();

    public static final String WP_URL = "http://tiny.cc/llp-whatsapp";
    public static final String TELEGRAM_URL = "http://tiny.cc/llp-telegram";
    /*public static final String WP_URL = "https://wa.me/13322424532";
    public static final String TELEGRAM_URL = "https://tttttt.me/hs_help";*/

    //paypal payment status
    public static final boolean PAYPAL_ACCOUNT_LIVE = true;

    // download option for non subscribed user
    public static final boolean ENABLE_DOWNLOAD_TO_ALL = true;

    //enable RTL
    public static boolean ENABLE_RTL = true;

    //enable external player
    public static final boolean ENABLE_EXTERNAL_PLAYER = false;

    //default theme
    public static boolean DEFAULT_DARK_THEME_ENABLE = true;

    // First, you have to configure firebase to enable facebook, phone and google login
    // facebook authentication
    public static final boolean ENABLE_FACEBOOK_LOGIN = false;

    //Phone authentication
    public static final boolean ENABLE_PHONE_LOGIN = false;

    //Google authentication
    public static final boolean ENABLE_GOOGLE_LOGIN = true;
}
