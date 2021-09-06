package com.codetron.studentplanner.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import com.codetron.studentplanner.R
import com.codetron.studentplanner.databinding.LayoutFeatureBinding

class CardFeatureItem
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : CardView(context, attrs, defStyleAttr) {

    private var _binding: LayoutFeatureBinding? = null
    private val binding get() = _binding

    init {
        _binding = LayoutFeatureBinding.inflate(LayoutInflater.from(context), this)

        attrs.let {
            val styledAttributes =
                context.obtainStyledAttributes(attrs, R.styleable.CardFeatureItem, 0, 0)

            val textValue = styledAttributes.getString(R.styleable.CardFeatureItem_textFeature)
            val drawableValue =
                styledAttributes.getDrawable(R.styleable.CardFeatureItem_drawableFeature)

            setText(textValue)
            setDrawable(drawableValue)

            styledAttributes.recycle()
        }

    }

    fun setDrawable(drawable: Drawable?) {
        binding?.imageFeature?.setImageDrawable(drawable)
    }

    fun setText(text: String?) {
        binding?.textFeature?.text = text
    }

}