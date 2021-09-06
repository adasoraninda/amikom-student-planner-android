package com.codetron.studentplanner.utils

import java.util.*
import kotlin.math.abs

object Formatter {

    @JvmStatic
    fun formatEducation(education: String?, grade: Int?): String {
        return "$education ${formatGrade(grade)}".trim()
    }

    @JvmStatic
    fun formatDateRemaining(date: String?): String {
        val listDate = date?.split("-")?.map { it.toInt() } ?: listOf()
        val listCurrentDate = Utility.getCurrentDate().split("-").map { it.toInt() }
        val day = toDay(listDate) - toDay(listCurrentDate)
        return if (day < 1) "Selesai" else "${abs(day)} hari lagi"
    }

    @JvmStatic
    fun formatDate(date: String?): String {
        return date ?: Utility.getTomorrowDate()
    }

    @JvmStatic
    private fun toDay(date: List<Int>): Int {
        return date
            .mapIndexed { index, i ->
                when (index) {
                    1 -> i * Calendar.DAY_OF_MONTH
                    2 -> Calendar.DAY_OF_YEAR
                    else -> i
                }
            }.sum()
    }

    @JvmStatic
    private fun formatGrade(grade: Int?): String {
        if (grade == 0)
            return ""
        return "$grade"
    }
}