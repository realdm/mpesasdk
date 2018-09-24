package mz.co.moovi.mpesalibui.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

val TEST_OBSERVER: LifecycleOwner = object : LifecycleOwner {

    private val registry = LifecycleRegistry(this).apply {
        // Creates a LifecycleRegistry in RESUMED state.
        handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        handleLifecycleEvent(Lifecycle.Event.ON_START)
        handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun getLifecycle() = registry
}