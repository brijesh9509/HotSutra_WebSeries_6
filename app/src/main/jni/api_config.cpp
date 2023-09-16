#include <jni.h>
#include <string>

//DEBUG
std::string SERVER_URL = "RISb/lflD3+fO3ZT2wWAPMRIfKAOkzf390GQ43P69IQ=]KbwL6xj/MtC2s7yHlHRpHA==]zpBGvMMWx6Y1zC5oqfP5x5hNX1jiO1qF3+TTsoMRvGfre78bs95g7ivpWI8J/Qgc";

//RELEASE
//std::string SERVER_URL = "YZWquVilQTEhM9VpWEks8f1ncgaX7tgu90mGS/wdzfA=]BFNC4l0Q0Ryb0FFfZU3IIA==]lz1hpF4jnv43HWBl3RJtpFrKCfZWjSl/Nd4plx+eZxslp5zFmS52HxH46yZkqjBj";

//std::string SERVER_URL = "https://hotsutra.live/phpadmin/rest-api"; //LIVE

std::string PURCHASE_CODE = "xxxxxxxxxxxxxxx";
std::string ONESIGNAL_APP_ID = "ca0b7d1c-da39-46fe-917b-922bc4159fc5";


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