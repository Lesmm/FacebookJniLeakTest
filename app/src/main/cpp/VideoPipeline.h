#pragma once

#include <jni.h>
#include <android/log.h>
#include <fbjni/fbjni.h>

namespace vision {

    using namespace facebook;

    class VideoPipeline : public jni::HybridClass<VideoPipeline> {
    public:
        static auto constexpr kJavaDescriptor = "Lcom/facebook/jni/leak/VideoPipeline;";

        static void registerNatives();

        static jni::local_ref<jhybriddata> initHybrid(jni::alias_ref<jhybridobject> jThis, int width, int height);

    public:
        ~VideoPipeline();

        void setRecordingSessionOutputSurface(jobject surface);

        void removeRecordingSessionOutputSurface();

    private:
        // Private constructor. Use `create(..)` to create new instances.
        explicit VideoPipeline(jni::alias_ref<jhybridobject> jThis, int width, int height);

    private:
        // Input Surface Texture
        int _width = 0;
        int _height = 0;

    private:
        friend HybridBase;
        jni::global_ref<javaobject> _javaPart;
        static constexpr auto TAG = "VideoPipeline";
    };

} // namespace vision
