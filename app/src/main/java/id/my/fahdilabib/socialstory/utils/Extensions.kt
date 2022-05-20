package id.my.fahdilabib.socialstory.utils

import android.util.Patterns
import android.view.View
import androidx.core.view.isVisible

fun CharSequence?.isValidEmail():Boolean{
    return !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun showGroupViews(vararg view: View) {
    view.forEach {
        it.isVisible = true
    }
}

val Any.TAG: String
    get() {
        val tag = javaClass.simpleName
        return if (tag.length <= 23) tag else tag.substring(0, 23)
    }