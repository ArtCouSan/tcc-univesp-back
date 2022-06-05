package br.com.controlefrotas.service

import br.com.controlefrotas.dto.PerDayInYear
import br.com.controlefrotas.dto.PerDayWeek
import br.com.controlefrotas.dto.PerDayWeekByMonthAndYear
import br.com.controlefrotas.dto.PerMonthByYear
import br.com.controlefrotas.entities.Transport
import java.time.LocalDateTime

interface TransportService {
    fun findByIdentificacao(identificacao: String): List<Transport>;
    fun listPerDay(identificacao: String, dt: LocalDateTime):  Int;
    fun listPerDayYear(identificacao: String, year: Int): ArrayList<PerDayInYear>;
    fun listPerDayWeek(identificacao: String): ArrayList<PerDayWeek>;
    fun listPerMonthAndGroupedPerYear(identificacao: String): ArrayList<PerMonthByYear>;
    fun listPerDayWeekAndGroupedPerYearAndMonth(identificacao: String): ArrayList<PerDayWeekByMonthAndYear>;
}