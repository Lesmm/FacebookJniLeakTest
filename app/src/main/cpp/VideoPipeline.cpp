#include "VideoPipeline.h"

namespace vision {

    jni::local_ref<VideoPipeline::jhybriddata>
    VideoPipeline::initHybrid(jni::alias_ref<jhybridobject> jThis, int width, int height) {
        return makeCxxInstance(jThis, width, height);
    }

    VideoPipeline::VideoPipeline(jni::alias_ref<jhybridobject> jThis, int width, int height)
            : _javaPart(jni::make_global(jThis)) {
        _width = width;
        _height = height;
    }

    VideoPipeline::~VideoPipeline() {
        __android_log_print(ANDROID_LOG_INFO, TAG, "VideoPipeline::~VideoPipeline() [%d X %d]", _width, _height);
        removeRecordingSessionOutputSurface();
    }

    void VideoPipeline::setRecordingSessionOutputSurface(jobject surface) {
        removeRecordingSessionOutputSurface();
    }

    void VideoPipeline::removeRecordingSessionOutputSurface() {
    }

    void VideoPipeline::registerNatives() {
        registerHybrid({
                               makeNativeMethod(
                                       "initHybrid",
                                       VideoPipeline::initHybrid
                               ),
                               makeNativeMethod(
                                       "setRecordingSessionOutputSurface",
                                       VideoPipeline::setRecordingSessionOutputSurface
                               ),
                               makeNativeMethod(
                                       "removeRecordingSessionOutputSurface",
                                       VideoPipeline::removeRecordingSessionOutputSurface
                               ),
                       });
    }

}
