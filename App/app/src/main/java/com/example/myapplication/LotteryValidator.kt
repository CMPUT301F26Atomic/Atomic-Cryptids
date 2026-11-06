/**
 * Takes input from user for the amount of winners in a lottery and confirms it is a positive integer
 *
 */

package com.example.myapplication

object LotteryValidator {
    fun isValidWinnerCount(input: String): Boolean {
        val count = input.toIntOrNull()
        return count != null && count > 0
    }

    fun isValidPeriod(startMillis: Long?, endMillis: Long?): Boolean {
        return startMillis != null && endMillis != null && endMillis > startMillis
    }

    fun isRegistrationOpen(nowMillis: Long, registrationStart: Long?, registrationEnd: Long?): Boolean {
        return registrationStart != null && registrationEnd != null &&
            nowMillis >= registrationStart && nowMillis < registrationEnd
    }
}