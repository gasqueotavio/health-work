package br.com.healthwork.utils

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

object Formatos {

    private val zona: ZoneId get() = ZoneId.systemDefault()
    private val localeBr = Locale("pt", "BR")

    private val fmtData: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private val fmtHora: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val mesAno: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM 'de' yyyy", localeBr)

    fun paraLocalDateTime(millis: Long): LocalDateTime =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), zona)

    fun paraMillis(dataHora: LocalDateTime): Long =
        dataHora.atZone(zona).toInstant().toEpochMilli()

    fun data(millis: Long): String = paraLocalDateTime(millis).format(fmtData)

    fun hora(millis: Long): String = paraLocalDateTime(millis).format(fmtHora)

    fun dataHora(millis: Long): String = "${data(millis)} ${hora(millis)}"

    fun mes(mes: YearMonth): String =
        mes.format(mesAno).replaceFirstChar { it.titlecase(localeBr) }

    /** Intervalo [início, fim) do dia informado, em milissegundos. */
    fun intervaloDoDia(dia: LocalDate): Pair<Long, Long> =
        paraMillis(dia.atStartOfDay()) to paraMillis(dia.plusDays(1).atStartOfDay())

    /** Intervalo [início, fim) do mês informado, em milissegundos. */
    fun intervaloDoMes(mes: YearMonth): Pair<Long, Long> =
        paraMillis(mes.atDay(1).atStartOfDay()) to paraMillis(mes.plusMonths(1).atDay(1).atStartOfDay())

    /** Converte "dd/MM/yyyy" + "HH:mm" em millis, ou null se inválido. */
    fun lerDataHora(dataTexto: String, horaTexto: String): Long? = try {
        val d = LocalDate.parse(dataTexto.trim(), fmtData)
        val h = LocalTime.parse(horaTexto.trim(), fmtHora)
        paraMillis(LocalDateTime.of(d, h))
    } catch (e: DateTimeParseException) {
        null
    }
}
