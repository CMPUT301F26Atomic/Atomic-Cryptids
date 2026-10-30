package com.example.myapplication

object LotteryValidator {
    fun isValidWinnerCount(input: String): Boolean {
        val count = input.toIntOrNull()
        return count != null && count > 0
    }
}