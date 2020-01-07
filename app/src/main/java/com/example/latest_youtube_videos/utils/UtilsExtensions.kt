package com.example.latest_youtube_videos.utils

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import java.text.SimpleDateFormat
import java.util.*

fun Fragment.showShortToastMessage(message:String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun LinearLayoutManager.shouldLoadMoreResults() =
    itemCount <= findLastVisibleItemPosition() + 1 + Utils.ITEM_SCROLL_THRESHOLD

fun Date.toString(pattern: String): String = SimpleDateFormat(pattern,
    Locale.getDefault()).format(this)

fun String.toDate() = try {
    SimpleDateFormat(
        Utils.DATE_TIME_TIMEZONE_PATTERN,
        Locale.getDefault()).parse(this)
} catch (e: Exception) {
    null
}
