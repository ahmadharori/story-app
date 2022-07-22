package com.dicoding.storyapp.view.custom

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.dicoding.storyapp.R

class PasswordField : AppCompatEditText {

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
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        error =
            if ((lengthBefore < lengthAfter && start < 5) ||
                (lengthBefore > lengthAfter && start <= 5) ||
                (lengthBefore == lengthAfter && lengthBefore in 1..5)) context.getString(R.string.password_min_length)
            else null
    }

}