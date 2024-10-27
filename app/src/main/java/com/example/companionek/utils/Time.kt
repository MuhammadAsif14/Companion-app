package com.example.companionek.utils

import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

object Time {

    fun timeStamp(): String {

        val timeStamp = Timestamp(System.currentTimeMillis())
        val sdf = SimpleDateFormat("HH:mm")
        val time = sdf.format(Date(timeStamp.time))

        return time.toString()
    }


        fun convertTimestampToTime(timestamp: Long): String {
            // Format the timestamp to match the format of `Time.timeStamp()`
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
            return sdf.format(java.util.Date(timestamp))
        }

    // Converts formatted time back to milliseconds
    fun parseTimeToMillis(time: String): Long {
        // Implement parsing logic here based on your time format
        // Example: If your time is in "yyyy-MM-dd HH:mm:ss" format
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return format.parse(time)?.time ?: System.currentTimeMillis()
    }



}