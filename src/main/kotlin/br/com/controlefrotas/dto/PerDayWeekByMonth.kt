package br.com.controlefrotas.dto

data class PerDayWeekByMonth constructor(
    var nameMonth: String,
    var perDayWeek: ArrayList<PerDayWeek>);
