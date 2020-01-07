package com.example.latest_youtube_videos.utils

object UtilsFunctions {

        fun extractHoursMinutesSeconds(duration: String): Triple<Int, Int, Int> {

            val oneOrMoreDigits = Utils.ONE_OR_MORE_DIGITS_REGEX_PATTERN
            val hours = extractNum(oneOrMoreDigits +
                    Utils.DURATION_HOUR_SYMBOL, duration)
            val minutes = extractNum(oneOrMoreDigits + Utils.DURATION_MINUTE_SYMBOL,
                duration)
            val seconds = extractNum(oneOrMoreDigits + Utils.DURATION_SECOND_SYMBOL,
                duration)
            return Triple(hours, minutes, seconds)
        }

        /*
        This method will extract 14 from "PT14M05S", if we are looking for the minutes
        value or will return zero if the duration doesn't contain minutes
         */
        private fun extractNum(pattern: String, fromString: String): Int {
            return try {
                Regex(pattern).findAll(fromString).map {
                    it.value.filter {
                            newIt -> newIt.isDigit()
                    }
                }.toList().first().toInt()
            } catch (e: Exception) {
                0
            }
        }

}
