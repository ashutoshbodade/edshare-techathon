package com.ashutosh.techathon.utils

import com.ashutosh.techathon.utils.Constants.DATE_TIME_FORMAT
import java.text.SimpleDateFormat
import java.util.*

object TimestampUtil {

    fun getDateTimeFirebaseTimestamp(timestamp:com.google.firebase.Timestamp):String{
        val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
        val sdf = SimpleDateFormat(DATE_TIME_FORMAT)
        val netDate = Date(milliseconds)
        return sdf.format(netDate).toString()
    }

}