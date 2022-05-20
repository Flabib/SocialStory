package id.my.fahdilabib.socialstory.utils

import android.content.Context
import android.text.Editable
import android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
import android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class MyEditText : AppCompatEditText {
    var listener: OnErrorInterface? = null

    interface OnErrorInterface {
        fun onError(message: String?)
    }

    fun initOnErrorInterface(listener: OnErrorInterface){
        this.listener = listener
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isEmpty()) {
                    listener?.onError("Field ini tidak boleh kosong")
                }
                else if (inputType - 1 == TYPE_TEXT_VARIATION_PASSWORD && s.length < 6) {
                    listener?.onError("Field ini minimal 6 karakter")
                }
                else if (inputType - 1 == TYPE_TEXT_VARIATION_EMAIL_ADDRESS && !s.isValidEmail()) {
                    listener?.onError("Field ini harus berupa email")
                }
                else {
                    listener?.onError(null)
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }
}