package com.codetron.studentplanner.utils

import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.codetron.studentplanner.R
import com.warkiz.tickseekbar.TickSeekBar

@BindingAdapter("app:imgUrl")
fun ImageView.setImageUrl(url: String?) {
    Glide.with(context)
        .load(url)
        .apply(
            RequestOptions()
                .error(R.drawable.bg_img_error)
                .placeholder(R.color.pink_primary)
        )
        .into(this)
}

@BindingAdapter("app:taskImage")
fun ImageView.setTaskImage(url: String?) {
    if (url.isNullOrBlank().or(url.equals("null"))) setImageResource(R.drawable.ic_image_default)
    else setImageUrl(url)
}

@BindingAdapter("app:profilePhoto")
fun ImageView.setPhotoProfile(url: String?) {
    if (url.isNullOrBlank().or(url.equals("null"))) setImageResource(R.drawable.bg_img_error)
    else setImageUrl(url)
}

@BindingAdapter("app:stringBackgroundColor")
fun View.setBackgroundColorFromString(color: String?) {
    color?.toColorInt()?.let { setBackgroundColor(it) }
}

@BindingAdapter("app:progressPriority")
fun TickSeekBar.setProgressPriority(priority: Int?) {
    setProgress((priority ?: 1).toFloat())
}

@BindingAdapter("app:cardPriorityColor")
fun CardView.setBackgroundColorPriority(priority: Int?) {
    setCardBackgroundColor(
        ColorStateList.valueOf(ContextCompat.getColor(context, getPriorityColor(priority)))
    )
}

@BindingAdapter("app:backgroundPriorityColor")
fun View.setPriorityColor(priority: Int?) {
    setBackgroundColor(ContextCompat.getColor(context, getPriorityColor(priority)))
}

@BindingAdapter("app:statusImage")
fun ImageView.setStatusImage(date: String?) {
    val dateRemaining = Formatter.formatDateRemaining(date)
    val drawable =
        if (dateRemaining == "Selesai") R.drawable.ic_task_done
        else R.drawable.ic_task_wait
    setImageResource(drawable)
}

private fun getPriorityColor(priority: Int?): Int =
    when (priority) {
        1 -> R.color.blue_primary
        2 -> R.color.green_primary
        3 -> R.color.yellow_primary
        4 -> R.color.orange_primary
        else -> R.color.pink_primary
    }