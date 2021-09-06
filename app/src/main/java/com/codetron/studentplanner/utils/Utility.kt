package com.codetron.studentplanner.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.text.SimpleDateFormat
import java.util.*

object Utility {

    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd-M-yyyy", Locale.getDefault())
        return sdf.format(Date()) // current date
    }

    fun getTomorrowDate(): String {
        val sdf = SimpleDateFormat("dd-M-yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.DATE, 1)
        return sdf.format(calendar.time)
    }

    fun toDate(strDate: String): Date {
        val sdf = SimpleDateFormat("dd-M-yyyy", Locale.getDefault())
        return sdf.parse(strDate) ?: Date()
    }

    fun getGreetMessage(): LiveData<String> {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        return MutableLiveData(
            when {
                hour < 12 -> "Pagi"
                hour < 15 -> "Siang"
                hour < 18 -> "Sore"
                else -> "Malam"
            }
        )
    }

}