package com.codetron.studentplanner.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.codetron.studentplanner.R

class ButtonPrimary @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {

    private var buttonBackground: Drawable? = null
    private var txtColorEnabled: Int = 0
    private var txtColorDisabled: Int = 0

    init {
        initButton()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        background = buttonBackground
        textSize = 14f
        isAllCaps = false
        gravity = Gravity.CENTER
        setTextColor(
            when {
                isEnabled -> txtColorEnabled
                else -> txtColorDisabled
            }
        )
    }

    private fun initButton() {
        txtColorEnabled = ContextCompat.getColor(context, R.color.white)
        txtColorDisabled = ContextCompat.getColor(context, R.color.gray_primary)
        buttonBackground =
            ResourcesCompat.getDrawable(resources, R.drawable.bg_button_primary, null)
    }

}