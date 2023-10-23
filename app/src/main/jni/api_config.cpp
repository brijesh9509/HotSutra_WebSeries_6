#include <jni.h>
#include <string>

//DEBUG
//std::string SERVER_URL = "IgwzyBph8chwaxEzTEtDDGMVlNaXJngfhipW0xUGMB8=]ArLXXwoM5mFMrQt3U284SA==]vGgvrsr78mVR0mspmHAYEsbKj1/sHBblSscRL3Biu/cwlp9iadHEDx7LCHoggdMV";

//RELEASE LIVE
std::string SERVER_URL = "aoZuE7yabDuC6DneRUtNuBoRKKroD8vGQAIciaPFJBQ=]dmr3CWv3fosFS4er4CPH6A==]b5n+EKbDrGBKdhLyLX3xOkl6AJio9o5VR2Cv9vZUGNNERyzpsO6pEjB3ev54f5sE";

//std::string SERVER_URL = "https://hotsutra.live/phpadmin/rest-api"; //LIVE

std::string PURCHASE_CODE = "xxxxxxxxxxxxxxx";
std::string ONESIGNAL_APP_ID = "673fa8de-8fd1-431b-9aac-bfc4d8fa1744";


//WARNING: ==>> Don't change anything below.
extern "C" JNIEXPORT jstring JNICALL
Java_app_hotsutra_live_AppConfig_getApiServerUrl(
        JNIEnv *env,
        jclass clazz) {
    return env->NewStringUTF(SERVER_URL.c_str());
}

/*extern "C" JNIEXPORT jstring JNICALL
Java_app_hotsutra_live_AppConfig_getApiKey(
        JNIEnv *env,
        jclass clazz) {
    return env->NewStringUTF(API_KEY.c_str());
}*/

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