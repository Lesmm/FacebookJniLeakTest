#include <jni.h>
#include <string>

#include <fbjni/fbjni.h>
#include "VideoPipeline.h"

extern "C" JNIEXPORT jstring JNICALL
Java_com_facebook_jni_leak_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
    return facebook::jni::initialize(vm, [] {
        vision::VideoPipeline::registerNatives();
    });
}