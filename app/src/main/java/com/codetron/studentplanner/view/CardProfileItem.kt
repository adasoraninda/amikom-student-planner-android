package com.codetron.studentplanner.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import com.codetron.studentplanner.R
import com.codetron.studentplanner.databinding.LayoutProfileItemBinding

class CardProfileItem
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    private var _binding: LayoutProfileItemBinding? = null
    private val binding get() = _binding

    init {
        _binding = LayoutProfileItemBinding.inflate(LayoutInflater.from(context), this)

        attrs.let {
            val styledAttributes =
                context.obtainStyledAttributes(it, R.styleable.CardProfileItem, 0, 0)

            val titleValue = styledAttributes.getString(R.styleable.CardProfileItem_titleProfile)
            val drawableValue =
                styledAttributes.getDrawable(R.styleable.CardProfileItem_drawableProfile)

            setTitle(titleValue)
            setDrawable(drawableValue)
            styledAttributes.recycle()
        }
    }

    fun setDrawable(drawable: Drawable?) {
        drawable?.let { binding?.imageItem?.setImageDrawable(it) }
    }

    fun setTitle(title: String?) {
        binding?.title?.text = title
    }

}