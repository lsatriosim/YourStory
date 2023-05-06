package com.example.yourstory.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.example.yourstory.R

class CustomEmailEditText : AppCompatEditText {
    var isError = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        background = ContextCompat.getDrawable(context, R.drawable.edit_text)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = "Masukkan email Anda"
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        addTextChangedListener(onTextChanged = { text, _, _, _ ->
            if (text != null) {
                if (!text.matches(emailPattern.toRegex())) {
                    error = resources.getString(R.string.email_valid)
                    isError = true
                } else {
                    error = null
                    isError = false
                }
            }
        })
    }
}