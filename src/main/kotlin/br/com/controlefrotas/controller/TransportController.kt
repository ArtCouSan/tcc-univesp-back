package br.com.controlefrotas.controller

import br.com.controlefrotas.dto.*
import br.com.controlefrotas.service.TransportService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/transport")
class TransportController(private val serviceTransport: TransportService) {

    @CrossOrigin(origins = ["http://localhost:4200"])
    @GetMapping("/{identificacao}/listar-itinerarios")
    fun getByIdentificacao(@PathVariable identificacao: String)
            : ResponseEntity<ArrayList<Itinerary>>
            = ResponseEntity.ok(serviceTransport.findByIdentificacao(identificacao));

    @CrossOrigin(origins = ["http://localhost:4200"])
    @GetMapping("/{identificacao}/listar-por-dia-semana")
    fun listPerWeekDay(
        @PathVariable identificacao: String,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) dtBegin: LocalDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) dtEnd: LocalDateTime
    )
            : ResponseEntity<ArrayList<PerDayWeek>>
            = ResponseEntity.ok(serviceTransport.listPerDayWeek(identificacao, dtBegin, dtEnd));

    @CrossOrigin(origins = ["http://localhost:4200"])
    @GetMapping("/{identificacao}/listar-por-mes-divido-por-ano")
    fun listPerDayWeekAndGroupedPerMonth(
        @PathVariable identificacao: String
    )
            : ResponseEntity<ArrayList<PerMonthByYear>>
            = ResponseEntity.ok(serviceTransport.listPerMonthAndGroupedPerYear(identificacao));

    @CrossOrigin(origins = ["http://localhost:4200"])
    @GetMapping("/{identificacao}/listar-por-dia-semana-divido-ano-mes")
    fun listPerDayWeekAndGroupedPerYeanAndMonth(
        @PathVariable identificacao: String
    )
            : ResponseEntity<ArrayList<PerDayWeekByMonthAndYear>>
            = ResponseEntity.ok(serviceTransport.listPerDayWeekAndGroupedPerYearAndMonth(identificacao));

    @CrossOrigin(origins = ["http://localhost:4200"])
    @GetMapping("/{identificacao}/listar-por-dia")
    fun listPerDay(
        @PathVariable identificacao: String,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) dt: LocalDateTime
    )
            : ResponseEntity<Int>
            = ResponseEntity.ok(serviceTransport.listPerDay(identificacao, dt));

    @CrossOrigin(origins = ["http://localhost:4200"])
    @GetMapping("/{identificacao}/listar-por-dia-no-ano")
    fun listPerDayYear(
        @PathVariable identificacao: String,
        @RequestParam year: Int
    )
            : ResponseEntity<ArrayList<PerDayInYear>>
            = ResponseEntity.ok(serviceTransport.listPerDayYear(identificacao, year));
}