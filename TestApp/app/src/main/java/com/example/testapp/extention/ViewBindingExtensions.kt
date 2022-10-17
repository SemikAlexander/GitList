package com.example.testapp.extention

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import java.lang.reflect.Method
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

inline fun <reified T : ViewBinding> Fragment.viewBinding(): ViewBindingDelegate<T> {
    return ViewBindingDelegate(this, T::class)
}

class ViewBindingDelegate<T : ViewBinding> @PublishedApi internal constructor(
    private val fragment: Fragment,
    private val viewBindingClass: KClass<T>
) : ReadOnlyProperty<Any?, T> {

    private var binding: T? = null

    init {
        fragment.viewLifecycleOwnerLiveData.observe(fragment) { lifecycleOwner ->
            lifecycleOwner.lifecycle.doOnDestroy { binding = null }
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = binding ?: obtainBinding()

    private fun obtainBinding(): T {
        val view = checkNotNull(fragment.view) {
            "ViewBinding is only valid between onCreateView and onDestroyView."
        }
        return viewBindingClass.bind(view)
            .also { binding = it }
    }
}

inline fun <reified T : ViewBinding> ViewGroup.inflateViewBinding(
    context: Context = this.context,
    attachToRoot: Boolean = true
): T {
    return T::class.inflate(LayoutInflater.from(context), this, attachToRoot)
}

inline fun <reified T : ViewBinding> Context.inflateViewBinding(
    parent: ViewGroup? = null,
    attachToRoot: Boolean = parent != null
): T {
    return T::class.inflate(LayoutInflater.from(this), parent, attachToRoot)
}

inline fun <reified T : ViewBinding> LayoutInflater.inflateViewBinding(
    parent: ViewGroup? = null,
    attachToRoot: Boolean = parent != null
): T {
    return T::class.inflate(this, parent, attachToRoot)
}

fun <T : ViewBinding> KClass<T>.inflate(
    inflater: LayoutInflater,
    parent: ViewGroup?,
    attachToRoot: Boolean
): T {
    val inflateMethod = java.getInflateMethod()
    @Suppress("UNCHECKED_CAST")
    return if (inflateMethod.parameterTypes.size > 2) {
        inflateMethod(null, inflater, parent, attachToRoot)
    } else {
        if (!attachToRoot) Log.d("bindingError", "ViewBinding: attachToRoot is always true for ${java.simpleName}.inflate")
        inflateMethod(null, inflater, parent)
    } as T
}

private val inflateMethodsCache = mutableMapOf<Class<out ViewBinding>, Method>()

private fun Class<out ViewBinding>.getInflateMethod(): Method {
    return inflateMethodsCache.getOrPut(this) {
        declaredMethods.find { method ->
            val parameterTypes = method.parameterTypes
            method.name == "inflate" &&
                    parameterTypes[0] == LayoutInflater::class.java &&
                    parameterTypes.getOrNull(1) == ViewGroup::class.java &&
                    (parameterTypes.size == 2 || parameterTypes[2] == Boolean::class.javaPrimitiveType)
        } ?: error("Method ${this.simpleName}.inflate(LayoutInflater, ViewGroup[, boolean]) not found.")
    }
}

inline fun <reified T : ViewBinding> View.getBinding(): T {
    return T::class.bind(this)
}

fun <T : ViewBinding> KClass<T>.bind(rootView: View): T {
    val bindMethod = java.getBindMethod()
    @Suppress("UNCHECKED_CAST")
    return bindMethod(null, rootView) as T
}

private val bindMethodsCache = mutableMapOf<Class<out ViewBinding>, Method>()

private fun Class<out ViewBinding>.getBindMethod(): Method {
    return bindMethodsCache.getOrPut(this) { getDeclaredMethod("bind", View::class.java) }
}

fun Lifecycle.doOnDestroy(action: (LifecycleOwner) -> Unit) {
    addObserver(
        LifecycleEventObserver { lifecycleOwner, event ->
            if (event == Lifecycle.Event.ON_DESTROY) action(lifecycleOwner)
        }
    )
}