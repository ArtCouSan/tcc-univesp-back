package br.com.controlefrotas.service

import br.com.controlefrotas.dto.*
import java.time.LocalDateTime

interface TransportService {
    fun findByIdentificacao(identificacao: String): ArrayList<Itinerary>;
    fun listPerDay(identificacao: String, dt: LocalDateTime):  Int;
    fun listPerDayYear(identificacao: String, year: Int): ArrayList<PerDayInYear>;
    fun listPerDayWeek(identificacao: String, dtBegin: LocalDateTime, dtEnd: LocalDateTime): ArrayList<PerDayWeek>;
    fun listPerMonthAndGroupedPerYear(identificacao: String): ArrayList<PerMonthByYear>;
    fun listPerDayWeekAndGroupedPerYearAndMonth(identificacao: String): ArrayList<PerDayWeekByMonthAndYear>;
//    fun listPerItinerary(identificacao: String, dtBegin: LocalDateTime, dtEnd: LocalDateTime): ArrayList<PerItinerary>;
}