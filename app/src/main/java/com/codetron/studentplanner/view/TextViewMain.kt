package com.codetron.studentplanner.view

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import com.codetron.studentplanner.R

class TextViewMain @JvmOverloads constructor
    (context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    AppCompatTextView(context, attrs, defStyleAttr) {

    private var isShowTextApp: Boolean? = false
    private var typeFaceLight: Typeface? = null
    private var typeFaceBold: Typeface? = null

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.TextViewMain,
            0, 0
        ).apply {
            try {
                isShowTextApp = getBoolean(R.styleable.TextViewMain_showTextApp, false)
            } finally {
                recycle()
            }
        }
        init()
    }

    private fun formatTextList(listText: List<String>): SpannableStringBuilder {
        val spannableStringBuilder = SpannableStringBuilder()

        val i: Int =
            if (isShowTextApp == true) 1
            else 0

        listText.forEachIndexed { index, s ->
            val strSpannable =
                if (index % 2 == i) formatTextLight(s)
                else formatTextBold(s)
            spannableStringBuilder.append(strSpannable)
        }

        return spannableStringBuilder
    }

    private fun formatTextBold(text: String): SpannableString {
        val spannableString = SpannableString(text)
        spannableString.setSpan(
            CustomTypefaceSpan(typeFaceBold),
            0,
            text.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }

    private fun formatTextLight(text: String): SpannableString {
        val spannableString = SpannableString(text)
        spannableString.setSpan(
            CustomTypefaceSpan(typeFaceLight),
            0,
            text.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }

    private fun init() {
        typeFaceLight = ResourcesCompat.getFont(context, R.font.poppins_light)
        typeFaceBold = ResourcesCompat.getFont(context, R.font.poppins_semibold)

        text = if (isShowTextApp == true) {
            val listString = arrayListOf<String>()
            listString.add(text.split(" ").joinToString(""))
            listString.add("App")
            formatTextList(listString)
        } else {
            formatTextList(text.split(" "))
        }
    }

}