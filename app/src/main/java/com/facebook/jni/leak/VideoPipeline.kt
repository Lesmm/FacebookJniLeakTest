package com.facebook.jni.leak

import android.graphics.SurfaceTexture
import android.util.Log
import android.view.Surface
import androidx.annotation.Keep
import com.facebook.jni.HybridData


@Keep
@Suppress("KotlinJniMissingFunction")
class VideoPipeline(private val width: Int, private val height: Int) : SurfaceTexture.OnFrameAvailableListener {

    companion object {
        private const val TAG = "VideoPipeline"
    }

    @Keep
    private val mHybridData: HybridData

    private val surface: Surface
    private val surfaceTexture: SurfaceTexture
    private var transformMatrix = FloatArray(16)

    @JvmField
    public var isManualDeleteHybridData: Boolean = false  // if false, the corresponding native object will be leak

    init {
        Log.i(TAG, "Initializing $width x $height Video Pipeline")
        mHybridData = initHybrid(width, height)
        surfaceTexture = SurfaceTexture(false)
        surfaceTexture.setDefaultBufferSize(width, height)
        surfaceTexture.setOnFrameAvailableListener(this)
        surface = Surface(surfaceTexture)
    }

    fun close() {
        synchronized(this) {
            removeRecordingSessionOutputSurface()
            surfaceTexture.setOnFrameAvailableListener(null, null)
            surfaceTexture.release()
            surface.release()

            if (isManualDeleteHybridData) {
                mHybridData.resetNative()
            }
        }
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture) {
        synchronized(this) {
            surfaceTexture.updateTexImage()

            surfaceTexture.getTransformMatrix(transformMatrix)

            setRecordingSessionOutputSurface(surface)
        }
    }

    @Throws(Throwable::class)
    protected fun finalize() {
        println("🚸 VideoPipeline [$width X $height] object is finalized")
    }

    /**
     * Native functions
     */
    private external fun initHybrid(width: Int, height: Int): HybridData
    private external fun setRecordingSessionOutputSurface(surface: Any)
    private external fun removeRecordingSessionOutputSurface()
}
