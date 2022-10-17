package com.example.testapp.extention

import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

fun Fragment.toast(message: String, duration: Int = Toast.LENGTH_SHORT) = try {
    Toast.makeText(activity, message, duration).show()
} catch (e: java.lang.Exception) {
}

fun Fragment.toast(@StringRes messageId: Int, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(activity, messageId, duration).show()

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.isVisible() = this.visibility == View.VISIBLE

fun View.setVisibleOrGone(isVisible: Boolean) = if (isVisible) this.visible() else this.gone()

fun View.setVisibleOrInvisible(isVisible: Boolean) =
    if (isVisible) this.visible() else this.invisible()