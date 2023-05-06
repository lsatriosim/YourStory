package com.example.yourstory.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.example.yourstory.R

class CustomEditText : AppCompatEditText {
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
        hint = "Masukkan password Anda"
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        addTextChangedListener(onTextChanged = { text, _, _, _ ->
            if (!TextUtils.isEmpty(text) && text.toString().length < 8) {
                error = resources.getString(R.string.min_pass)
                isError = true
            } else {
                error = null
                isError = false
            }
        })
    }
}