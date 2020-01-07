package com.example.latest_youtube_videos.utils

import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.*
import org.junit.Test

/**
 * The result of extractHoursMinutesSeconds from the UtilsFunctions companion object
 * is returned in form of a Triple<Int, Int, Int>; the first value find the number of
 * hours, the second of minutes, and the third of seconds.
 *
 * The argument that is being passed to the function is in the form of "PT10H30S20S"
 * which translates to a duration of 10 hours, 30 minutes and 20 seconds. If a time unit
 * is missing, then it won't be included in the duration string and the corresponding
 * value from the Triple will be zero.
 */

class UtilsFunctionsTest {

    private val callExtractHoursMinutesSeconds = { argument: String ->
        UtilsFunctions.extractHoursMinutesSeconds(argument)
    }

    @Test
    fun getHoursMinutesSeconds_valuesContainZero_noZerosReturned() {

        val duration = "PT02H11M01S"

        val result = callExtractHoursMinutesSeconds(duration)

        val expected = Triple(2, 11, 1)

        assertThat(result, `is`(expected))

    }

    @Test
    fun getHoursMinutesSeconds_noHours_returnZeroForHours() {

        val duration = "PT11M25S"

        val result = callExtractHoursMinutesSeconds(duration)

        val expected = Triple(0, 11, 25)

        assertThat(result, `is`(expected))
    }

    @Test
    fun getHoursMinutesSeconds_noMinutes_returnZeroForMinutes() {

        val duration = "PT01H11S"

        val result = callExtractHoursMinutesSeconds(duration)

        val expected = Triple(1, 0, 11)

        assertThat(result, `is`(expected))
    }

    @Test
    fun getHoursMinutesSeconds_noSeconds_returnZeroForSeconds() {

        val duration = "PT01H11M"

        val result = callExtractHoursMinutesSeconds(duration)

        val expected = Triple(1, 11, 0)

        assertThat(result, `is`(expected))
    }

    @Test
    fun getHoursMinutesSeconds_justHours_returnZeroForMinutesAndSeconds() {

        val duration = "PT10H"

        val result = callExtractHoursMinutesSeconds(duration)

        val expected = Triple(10, 0, 0)

        assertThat(result, `is`(expected))
    }

    @Test
    fun getHoursMinutesSeconds_justMinutes_returnZeroForHoursAndSeconds() {

        val duration = "PT11M"

        val result = callExtractHoursMinutesSeconds(duration)

        val expected = Triple(0, 11, 0)

        assertThat(result, `is`(expected))

    }

    @Test
    fun getHoursMinutesSeconds_justSeconds_returnZeroForHoursAndMinutes() {

        val duration = "PT05S"

        val result = callExtractHoursMinutesSeconds(duration)

        val expected = Triple(0, 0, 5)

        assertThat(result, `is`(expected))
    }

    @Test
    fun getHoursMinutesSeconds_noTimeValues_returnZeroForEveryTimeUnit() {

        val duration = "PT"

        val result = callExtractHoursMinutesSeconds(duration)

        val expected = Triple(0, 0, 0)

        assertThat(result, `is`(expected))
    }

    @Test
    fun getHoursMinutesSeconds_emptyString_returnZeroForEveryTimeUnit() {

        val duration = ""

        val result = callExtractHoursMinutesSeconds(duration)

        val expected = Triple(0, 0, 0)

        assertThat(result, `is`(expected))
    }
}

