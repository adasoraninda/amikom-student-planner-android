package com.codetron.studentplanner.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.codetron.studentplanner.R
import com.codetron.studentplanner.databinding.LayoutToolBarBinding

class ToolBarMain @JvmOverloads constructor
    (context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    private var _binding: LayoutToolBarBinding? = null
    private val binding get() = _binding

    init {
        _binding = LayoutToolBarBinding.inflate(LayoutInflater.from(context), this)

        attrs?.let {
            val styledAttributes = context.obtainStyledAttributes(it, R.styleable.ToolBarMain, 0, 0)

            val titleValue = styledAttributes.getString(R.styleable.ToolBarMain_title)
            val isShowBackButton =
                styledAttributes.getBoolean(R.styleable.ToolBarMain_showBackButton, false)

            setTitle(titleValue)
            setShowBackButton(isShowBackButton)
            styledAttributes.recycle()
        }
    }

    fun setTitle(title: String?) {
        binding?.title?.text = title
    }

    fun setShowBackButton(isShow: Boolean) {
        binding?.imageBackButton?.visibility =
            if (isShow) View.VISIBLE
            else View.GONE
    }

    fun setAction(drawable: Int? = null) {
        if (drawable != null) {
            binding?.imageAction?.visibility = View.VISIBLE
            binding?.imageAction?.setImageDrawable(ContextCompat.getDrawable(context, drawable))
            return
        }
        binding?.imageAction?.visibility = View.GONE
    }

    fun setBackButtonClickListener(clickListener: (v: View?) -> Unit) {
        binding?.imageBackButton?.setOnClickListener(clickListener)
    }

    fun setActionClickListener(clickListener: (v: View?) -> Unit) {
        if (binding?.imageAction?.visibility == View.VISIBLE) {
            binding?.imageAction?.setOnClickListener(clickListener)
        }
    }

}