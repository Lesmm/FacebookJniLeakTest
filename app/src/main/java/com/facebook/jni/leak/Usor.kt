package com.facebook.jni.leak

import java.lang.ref.PhantomReference

class Usor(private val id: String) {

    @JvmField
    var me: PhantomReference<Usor>? = null

    init {
        println("Usor $id created!")
    }

    @Throws(Throwable::class)
    protected fun finalize() {
        println("Usor $id finalized!")
        if (me != null) {
            me!!.enqueue()
        }
    }
}