package com.example.cafeteriamanagementsystem.utils

import android.content.Context

object ReceiptManager {

    private const val PREF_NAME = "receipt_preferences"
    private const val KEY_RECEIPT_NUMBER = "receipt_number"

    fun getNextReceiptNumber(context: Context): String {

        val preferences = context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

        // Get the previous receipt number.
        // If there has never been a receipt, start at 0.
        val currentNumber = preferences.getInt(
            KEY_RECEIPT_NUMBER,
            0
        )

        // Increase the receipt number by 1
        val nextNumber = currentNumber + 1

        // Save the new number
        preferences.edit()
            .putInt(KEY_RECEIPT_NUMBER, nextNumber)
            .apply()

        // Format:
        // 1  -> 001
        // 2  -> 002
        // 10 -> 010
        // 100 -> 100
        return String.format("%03d", nextNumber)
    }
}