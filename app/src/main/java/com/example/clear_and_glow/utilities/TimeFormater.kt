package com.example.clear_and_glow.utilities

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TimeFormater {

    fun getFormattedDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        return currentDate.format(formatter)
    }
}