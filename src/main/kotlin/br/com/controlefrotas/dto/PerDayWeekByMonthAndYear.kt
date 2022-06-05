package br.com.controlefrotas.dto

data class PerDayWeekByMonthAndYear(
    var months: ArrayList<PerDayWeekByMonth>,
    var year: String
)