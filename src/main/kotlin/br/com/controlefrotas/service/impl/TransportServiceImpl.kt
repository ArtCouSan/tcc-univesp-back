package br.com.controlefrotas.service.impl

import br.com.controlefrotas.dto.*
import br.com.controlefrotas.repository.ItineraryRepository
import br.com.controlefrotas.repository.RegistrationDataRepository
import br.com.controlefrotas.repository.TransportRepository
import br.com.controlefrotas.service.TransportService
import org.springframework.stereotype.Service
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Service
class TransportServiceImpl(
    private val repository: TransportRepository,
    private val repositoryRegistration: RegistrationDataRepository,
    private val repositoryItinerary: ItineraryRepository): TransportService {

    val formatters: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    val SEM_VALOR = 0;
    val VALOR_INICIAL = 1;

    override fun findByIdentificacao(identificacao: String): ArrayList<Itinerary> {
        var listItineries: ArrayList<Itinerary> = arrayListOf();
        var hashItinerary: HashMap<Integer, HashMap<String, ArrayList<String>>> = hashMapOf();
        repositoryItinerary.findByIdentificacao(identificacao).itinerario.forEach { i ->
            if(hashItinerary.containsKey(i.ordem)) {
                var tempHashItinerary = hashItinerary.getValue(i.ordem);
                var listRoutes: ArrayList<String> = arrayListOf();
                listRoutes = tempHashItinerary.get(i.rota)!!;
                listRoutes.add(i.horario);
                tempHashItinerary.put(i.rota, listRoutes);
                hashItinerary.put(i.ordem, tempHashItinerary);
            } else {
                var hashRoute: HashMap<String, ArrayList<String>> = hashMapOf();
                hashRoute.put(i.rota, arrayListOf(i.horario));
                hashItinerary.put(i.ordem, hashRoute);
            }
        }
        hashItinerary.forEach { (key, value) ->
            value.forEach { (keyRoute, valueRoute) ->
                var itinerary = Itinerary(key, keyRoute, valueRoute);
                listItineries.add(itinerary);
            }
        }
        return listItineries;
    }

    override fun listPerDay(identificacao: String, dt: LocalDateTime): Int {
        val dthIni = dt.withHour(0).withMinute(0).withSecond(0);
        val dthEnd = dt.withHour(23).withMinute(59).withSecond(59);
        return repository.findByIdentificacaoAndDtAlteracaoBetween(identificacao, dthIni, dthEnd).size;
    }

    override fun listPerDayYear(identificacao: String, year: Int): ArrayList<PerDayInYear> {
        val hashPerDay : HashMap<LocalDate, Int> = hashMapOf();
        val dthIni = LocalDateTime.of(year, 1,1, 0, 0, 1);
        val dthEnd = LocalDateTime.of(year, 12, 31, 23, 59, 59);
        val transport = this.repositoryRegistration.findByIdentificacao(identificacao);
        var beginDayNumber = 1L;
        var endDayNumber = 0L;
        if(year.compareTo(LocalDate.now().year) == 0) {
            var dtBegin: LocalDate;
            if(transport.dtInicioAtuacao.year.compareTo(year) != 0) {
                dtBegin = LocalDate.now().withMonth(1).withDayOfMonth(1);
            } else {
                dtBegin = transport.dtInicioAtuacao.toLocalDate();
            }
            val dtEnd = LocalDate.now();
            beginDayNumber = dtBegin.dayOfYear.toLong();
            endDayNumber = ChronoUnit.DAYS.between(dtBegin, dtEnd);
        } else {
            endDayNumber = Year.of(year).length().toLong();
        }
        for (day: Long in beginDayNumber..endDayNumber) {
            val dt: LocalDate = Year.of(year).atDay(day.toInt());
            hashPerDay.put(dt, 0);
        }
        repository.findByIdentificacaoAndDtAlteracaoBetween(identificacao, dthIni, dthEnd).forEach {
            val dt = it.dtAlteracao.toLocalDate();
            if (hashPerDay.containsKey(dt)) {
                val qtn: Int = hashPerDay.getValue(dt);
                hashPerDay.put(dt, qtn + VALOR_INICIAL);
            } else {
                hashPerDay.put(dt, VALOR_INICIAL);
            }
        };
        var listPerDayInYear: ArrayList<PerDayInYear> = arrayListOf();
        hashPerDay.toSortedMap().forEach { (key, value) ->
            var perDayInYear = PerDayInYear(formatters.format(key), value);
            listPerDayInYear.add(perDayInYear);
        }
        return listPerDayInYear;
    }

    override fun listPerDayWeek(
        identificacao: String,
        dtBegin: LocalDateTime,
        dtEnd: LocalDateTime
    ): ArrayList<PerDayWeek> {
        val hashPerWeek : HashMap<Int, Int> = hashMapOf();
        hashPerWeek.put(DayOfWeek.MONDAY.value, SEM_VALOR);
        hashPerWeek.put(DayOfWeek.TUESDAY.value, SEM_VALOR);
        hashPerWeek.put(DayOfWeek.WEDNESDAY.value, SEM_VALOR);
        hashPerWeek.put(DayOfWeek.THURSDAY.value, SEM_VALOR);
        hashPerWeek.put(DayOfWeek.FRIDAY.value, SEM_VALOR);
        hashPerWeek.put(DayOfWeek.SATURDAY.value, SEM_VALOR);
        hashPerWeek.put(DayOfWeek.SUNDAY.value, SEM_VALOR);
        val dtEndFormatted = dtEnd.withHour(23).withMinute(59).withSecond(59);
        repository.findByIdentificacaoAndDtAlteracaoBetween(identificacao, dtBegin, dtEndFormatted).forEach {
            val nameWeekFormatted = it.dtAlteracao.dayOfWeek.value;
            if (hashPerWeek.containsKey(nameWeekFormatted)) {
                val qtn: Int = hashPerWeek.getValue(nameWeekFormatted);
                hashPerWeek.put(nameWeekFormatted, qtn + VALOR_INICIAL);
            } else {
                hashPerWeek.put(nameWeekFormatted, VALOR_INICIAL);
            }
        };
        var listPerDayWeek: ArrayList<PerDayWeek> = arrayListOf();
        hashPerWeek.toSortedMap().forEach { (key, value) ->
            var perDayInYear = PerDayWeek(key.toString(), value);
            listPerDayWeek.add(perDayInYear);
        }
        return listPerDayWeek;
    }

    override fun listPerMonthAndGroupedPerYear(
        identificacao: String
    ): ArrayList<PerMonthByYear> {
        val hashPerYear : HashMap<Int, HashMap<Int, Int>> = hashMapOf();
        val transport = this.repositoryRegistration.findByIdentificacao(identificacao);
        val dtBegin = transport.dtInicioAtuacao;
        val dtEnd = LocalDateTime.now();
        val qtnMonths = ChronoUnit.MONTHS.between(dtBegin, dtEnd);
        var dtTempEmpty: LocalDateTime;
        for (month: Long in 0..qtnMonths) {
            dtTempEmpty = dtBegin.plusMonths(month);
            val nameMonthFormatted = dtTempEmpty.month.value;
            val year = dtTempEmpty.year;
            if (hashPerYear.containsKey(year)) {
                val hashPerMonth = hashPerYear.getValue(year);
                hashPerMonth.put(nameMonthFormatted, SEM_VALOR);
                hashPerYear.put(year, hashPerMonth);
            } else {
                val hashPerMonth: HashMap<Int, Int> = hashMapOf();
                hashPerMonth.put(nameMonthFormatted, SEM_VALOR);
                hashPerYear.put(year, hashPerMonth);
            }
        }
        repository.findByIdentificacaoAndDtAlteracaoBetween(identificacao, dtBegin, dtEnd).forEach {
            val nameMonthFormatted = it.dtAlteracao.month.value;
            val year = it.dtAlteracao.year;
            if (hashPerYear.containsKey(year)) {
                val hashPerMonth = hashPerYear.getValue(year);
                if(hashPerMonth.containsKey(nameMonthFormatted)) {
                    val qtn: Int = hashPerMonth.getValue(nameMonthFormatted);
                    hashPerMonth.put(nameMonthFormatted, qtn + VALOR_INICIAL);
                    hashPerYear.put(year, hashPerMonth);
                } else {
                    val hashPerMonth: HashMap<Int, Int> = hashMapOf();
                    hashPerMonth.put(nameMonthFormatted, VALOR_INICIAL);
                    hashPerYear.put(year, hashPerMonth);
                }
            } else {
                val hashPerMonth: HashMap<Int, Int> = hashMapOf();
                hashPerMonth.put(nameMonthFormatted, VALOR_INICIAL);
                hashPerYear.put(year, hashPerMonth);
            }
        };
        var sortedMapPerYear: SortedMap<Int, SortedMap<Int, Int>> = sortedMapOf();
        for (yearHash in hashPerYear.toSortedMap()) {
            sortedMapPerYear.put(yearHash.key, yearHash.value.toSortedMap());
        }
        var listPerYear: ArrayList<PerMonthByYear> = arrayListOf();
        sortedMapPerYear.toSortedMap().forEach { (key, sortedMapPerMonth) ->
            var listPerMonth: ArrayList<PerMonth> = arrayListOf();
            sortedMapPerMonth.forEach { (key, qtn) ->
                listPerMonth.add(PerMonth(key.toString(), qtn));
            }
            listPerYear.add(PerMonthByYear(listPerMonth, key.toString()));
        }
        return listPerYear;
    }

    override fun listPerDayWeekAndGroupedPerYearAndMonth(
        identificacao: String
    ): ArrayList<PerDayWeekByMonthAndYear> {
        val hashPerYear : HashMap<Int, HashMap<Int, HashMap<Int, Int>>> = hashMapOf();
        val transport = this.repositoryRegistration.findByIdentificacao(identificacao);
        val dtBegin = transport.dtInicioAtuacao;
        val dtEnd = LocalDateTime.now();
        val qtnDays = ChronoUnit.DAYS.between(dtBegin, dtEnd);
        var dtTempEmpty: LocalDateTime;
        for (day: Long in 0..qtnDays) {
            dtTempEmpty = dtBegin.plusDays(day);
            val nameWeekFormatted = dtTempEmpty.dayOfWeek.value;
            val nameMonthFormatted = dtTempEmpty.month.value;
            val year = dtTempEmpty.year;
            if (hashPerYear.containsKey(year)) {
                val hashPerMonth = hashPerYear.getValue(year);
                if(hashPerMonth.containsKey(nameMonthFormatted)) {
                    val hashPerWeek = hashPerMonth.getValue(nameMonthFormatted);
                    hashPerWeek.put(nameWeekFormatted, SEM_VALOR);
                    hashPerMonth.put(nameMonthFormatted, hashPerWeek);
                    hashPerYear.put(year, hashPerMonth);
                } else {
                    val hashPerWeek: HashMap<Int, Int> = hashMapOf();
                    hashPerWeek.put(nameWeekFormatted, SEM_VALOR);
                    hashPerMonth.put(nameMonthFormatted, hashPerWeek);
                    hashPerYear.put(year, hashPerMonth);
                }
            } else {
                val hashPerMonth: HashMap<Int, HashMap<Int, Int>> = hashMapOf();
                val hashPerWeek: HashMap<Int, Int> = hashMapOf();
                hashPerWeek.put(nameWeekFormatted, SEM_VALOR);
                hashPerMonth.put(nameMonthFormatted, hashPerWeek);
                hashPerYear.put(year, hashPerMonth);
            }
        }
        repository.findByIdentificacaoAndDtAlteracaoBetween(identificacao, dtBegin, dtEnd).forEach {
            val nameWeekFormatted = it.dtAlteracao.dayOfWeek.value;
            val nameMonthFormatted = it.dtAlteracao.month.value;
            val year = it.dtAlteracao.year;
            if (hashPerYear.containsKey(year)) {
                val hashPerMonth = hashPerYear.getValue(year);
                if(hashPerMonth.containsKey(nameMonthFormatted)) {
                    val hashPerWeek = hashPerMonth.getValue(nameMonthFormatted);
                    if(hashPerWeek.containsKey(nameWeekFormatted)) {
                        val qtn: Int = hashPerWeek.getValue(nameWeekFormatted);
                        hashPerWeek.put(nameWeekFormatted, qtn + VALOR_INICIAL);
                        hashPerMonth.put(nameMonthFormatted, hashPerWeek);
                        hashPerYear.put(year, hashPerMonth);
                    } else {
                        hashPerWeek.put(nameWeekFormatted, VALOR_INICIAL);
                        hashPerMonth.put(nameMonthFormatted, hashPerWeek);
                        hashPerYear.put(year, hashPerMonth);
                    }
                } else {
                    val hashPerWeek: HashMap<Int, Int> = hashMapOf();
                    hashPerWeek.put(nameWeekFormatted, VALOR_INICIAL);
                    hashPerMonth.put(nameMonthFormatted, hashPerWeek);
                    hashPerYear.put(year, hashPerMonth);
                }
            } else {
                val hashPerMonth: HashMap<Int, HashMap<Int, Int>> = hashMapOf();
                val hashPerWeek: HashMap<Int, Int> = hashMapOf();
                hashPerWeek.put(nameWeekFormatted, VALOR_INICIAL);
                hashPerMonth.put(nameMonthFormatted, hashPerWeek);
                hashPerYear.put(year, hashPerMonth);
            }
        };
        var sortedMapPerYear: SortedMap<Int, SortedMap<Int, SortedMap<Int, Int>>> = sortedMapOf();
        for (yearHash in hashPerYear.toSortedMap()) {
            var sortedMapPerMonth: SortedMap<Int, SortedMap<Int, Int>> = sortedMapOf();
            for (monthHash in yearHash.value) {
                sortedMapPerMonth.put(monthHash.key, monthHash.value.toSortedMap());
            }
            sortedMapPerYear.put(yearHash.key, sortedMapPerMonth);
        }
        var listPerYear: ArrayList<PerDayWeekByMonthAndYear> = arrayListOf();
        sortedMapPerYear.toSortedMap().forEach { (key, sortedMapPerMonth) ->
            var listPerDayWeekByMonth: ArrayList<PerDayWeekByMonth> = arrayListOf();
            sortedMapPerMonth.forEach { (key, sortedMapPerDayWeek) ->
                var listPerDayWeek: ArrayList<PerDayWeek> = arrayListOf();
                sortedMapPerDayWeek.forEach { (key, dayWeek) ->
                    listPerDayWeek.add(PerDayWeek(key.toString(), dayWeek));
                }
                var month = PerDayWeekByMonth(key.toString(), listPerDayWeek);
                listPerDayWeekByMonth.add(month);
            }
            var perDayWeekByMonthAndYear = PerDayWeekByMonthAndYear(listPerDayWeekByMonth, key.toString());
            listPerYear.add(perDayWeekByMonthAndYear);
        }
        return listPerYear;
    }

//    override fun listPerItinerary(identificacao: String, dtBegin: LocalDateTime, dtEnd: LocalDateTime): ArrayList<PerItinerary> {
//        var listPerItinerary: ArrayList<PerItinerary> = arrayListOf();
//        var hashPerItinerary: HashMap<Integer, HashMap<String, Integer>> = hashMapOf();
//        val dtEndFormatted = dtEnd.withHour(23).withMinute(59).withSecond(59);
//        repository.findByIdentificacaoAndDtAlteracaoBetween(identificacao, dtBegin, dtEndFormatted).forEach {
//            if (hashPerItinerary.containsKey(it.idRota)) {
//                val qtn: Int = hashPerWeek.getValue(nameWeekFormatted);
//                hashPerWeek.put(nameWeekFormatted, qtn + VALOR_INICIAL);
//            } else {
//                hashPerItinerary.put(it, VALOR_INICIAL);
//            }
//        };
//        var listPerDayWeek: ArrayList<PerDayWeek> = arrayListOf();
//        hashPerWeek.toSortedMap().forEach { (key, value) ->
//            var perDayInYear = PerDayWeek(key.toString(), value);
//            listPerDayWeek.add(perDayInYear);
//        }
//    }
}