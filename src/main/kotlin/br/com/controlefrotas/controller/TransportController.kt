package br.com.controlefrotas.controller

import br.com.controlefrotas.dto.PerDayWeek
import br.com.controlefrotas.entities.Transport
import br.com.controlefrotas.service.TransportService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.HashMap

@RestController
@RequestMapping("/transport")
class TransportController(private val serviceTransport: TransportService) {

    @GetMapping("/{identificacao}")
    fun getByIdentificacao(@PathVariable identificacao: String)
            : ResponseEntity<List<Transport>>
            = ResponseEntity.ok(serviceTransport.findByIdentificacao(identificacao));

    @GetMapping("/{identificacao}/listar-por-dia-semana")
    fun listPerWeekDay(
        @PathVariable identificacao: String
    )
            : ResponseEntity<SortedMap<Int, Int>>
            = ResponseEntity.ok(serviceTransport.listPerDayWeek(identificacao));

    @GetMapping("/{identificacao}/listar-por-mes-divido-por-ano")
    fun listPerDayWeekAndGroupedPerMonth(
        @PathVariable identificacao: String
    )
            : ResponseEntity<SortedMap<Int, SortedMap<Int, Int>>>
            = ResponseEntity.ok(serviceTransport.listPerMonthAndGroupedPerYear(identificacao));

    @GetMapping("/{identificacao}/listar-por-dia-semana-divido-ano-mes")
    fun listPerDayWeekAndGroupedPerYeanAndMonth(
        @PathVariable identificacao: String
    )
            : ResponseEntity<SortedMap<Int, SortedMap<Int, SortedMap<Int, Int>>>>
            = ResponseEntity.ok(serviceTransport.listPerDayWeekAndGroupedPerYearAndMonth(identificacao));

    @GetMapping("/{identificacao}/listar-por-dia")
    fun listPerDay(
        @PathVariable identificacao: String,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) dt: LocalDateTime
    )
            : ResponseEntity<Int>
            = ResponseEntity.ok(serviceTransport.listPerDay(identificacao, dt));

    @GetMapping("/{identificacao}/listar-por-dia-no-ano")
    fun listPerDayYear(
        @PathVariable identificacao: String,
        @RequestParam year: Int
    )
            : ResponseEntity<SortedMap<LocalDate, Int>>
            = ResponseEntity.ok(serviceTransport.listPerDayYear(identificacao, year));
}