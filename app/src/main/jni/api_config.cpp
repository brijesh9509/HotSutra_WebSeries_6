#include <jni.h>
#include <string>

/*std::string SERVER_URL = "https://moodtv.in/ALL_SERVER/HSLIVE/1/be/rest-api/";
std::string API_KEY = "0e73a6a2b6ec22ab250db32e";*/

std::string SERVER_URL = "https://hotsutra.live/phpadmin/rest-api"; //LIVE
std::string API_KEY = "da64cb369c5bdf300750fa95";  //LIVE

/*std::string SERVER_URL = "http://phpstack-781815-2699502.cloudwaysapps.com/phpadmin/rest-api"; //Test
std::string API_KEY = "da64cb369c5bdf300750fa95";  //Test*/

std::string PURCHASE_CODE = "xxxxxxxxxxxxxxx";
std::string ONESIGNAL_APP_ID = "ca0b7d1c-da39-46fe-917b-922bc4159fc5";


//WARNING: ==>> Don't change anything below.
extern "C" JNIEXPORT jstring JNICALL
Java_app_hotsutra_live_AppConfig_getApiServerUrl(
        JNIEnv *env,
        jclass clazz) {
    return env->NewStringUTF(SERVER_URL.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_app_hotsutra_live_AppConfig_getApiKey(
        JNIEnv *env,
        jclass clazz) {
    return env->NewStringUTF(API_KEY.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_app_hotsutra_live_AppConfig_getPurchaseCode(
        JNIEnv *env,
        jclass clazz) {
    return env->NewStringUTF(PURCHASE_CODE.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_app_hotsutra_live_AppConfig_getOneSignalAppID(
        JNIEnv *env,
        jclass clazz) {
    return env->NewStringUTF(ONESIGNAL_APP_ID.c_str());
}