package br.com.controlefrotas.service

import br.com.controlefrotas.entities.Transport
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.HashMap

interface TransportService {
    fun findByIdentificacao(identificacao: String): List<Transport>;
    fun listPerDay(identificacao: String, dt: LocalDateTime):  Int;
    fun listPerDayYear(identificacao: String, year: Int): SortedMap<LocalDate, Int>;
    fun listPerDayWeek(identificacao: String): SortedMap<Int, Int>;
    fun listPerMonthAndGroupedPerYear(identificacao: String): SortedMap<Int, SortedMap<Int, Int>>;
    fun listPerDayWeekAndGroupedPerYearAndMonth(identificacao: String): SortedMap<Int, SortedMap<Int, SortedMap<Int, Int>>>;
}