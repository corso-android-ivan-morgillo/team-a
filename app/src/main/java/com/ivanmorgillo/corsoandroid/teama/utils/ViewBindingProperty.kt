package com.ivanmorgillo.corsoandroid.teama.utils

import android.os.Handler
import android.os.Looper
import androidx.annotation.MainThread
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

abstract class ViewBindingProperty<in R : Any, T : ViewBinding>(
    private val viewBinder: (R) -> T,
) : ReadOnlyProperty<R, T> {

    private var viewBinding: T? = null
    private val lifecycleObserver = ClearOnDestroyLifecycleObserver()
    private var thisRef: R? = null

    protected abstract fun getLifecycleOwner(thisRef: R): LifecycleOwner

    @MainThread
    override fun getValue(thisRef: R, property: KProperty<*>): T {
        viewBinding?.let { return it }

        this.thisRef = thisRef
        val lifecycle = getLifecycleOwner(thisRef).lifecycle
        if (lifecycle.currentState == Lifecycle.State.DESTROYED) {
            mainHandler.post { viewBinding = null }
        } else {
            lifecycle.addObserver(lifecycleObserver)
        }
        return viewBinder(thisRef).also { viewBinding = it }
    }

    @MainThread
    private fun clear() {
        val thisRef = thisRef ?: return
        this.thisRef = null
        getLifecycleOwner(thisRef).lifecycle.removeObserver(lifecycleObserver)
        // This operation is posted at the end of the handler queue so that the view binding is
        // still there in the body of Fragment.onDestroyView().
        mainHandler.post { viewBinding = null }
    }

    // A lifecycle observer that calls the clear() method when ON_DESTROY event occurs.
    private inner class ClearOnDestroyLifecycleObserver : DefaultLifecycleObserver {
        @MainThread
        override fun onDestroy(owner: LifecycleOwner): Unit = clear()
    }

    private companion object {
        private val mainHandler = Handler(Looper.getMainLooper())
    }
}
