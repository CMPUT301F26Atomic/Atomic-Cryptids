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
}