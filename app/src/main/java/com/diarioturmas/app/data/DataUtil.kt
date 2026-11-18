package com.diarioturmas.app.data

import java.util.Calendar
import java.util.TimeZone

object DataUtil {
    private val meses = listOf(
        "jan", "fev", "mar", "abr", "mai", "jun",
        "jul", "ago", "set", "out", "nov", "dez"
    )

    private val diasLongos = listOf(
        "Domingo", "Segunda-feira", "Terça-feira", "Quarta-feira",
        "Quinta-feira", "Sexta-feira", "Sábado"
    )

    // Rótulo -> constante Calendar.DAY_OF_WEEK
    val diasSemana = listOf(
        "Seg" to Calendar.MONDAY,
        "Ter" to Calendar.TUESDAY,
        "Qua" to Calendar.WEDNESDAY,
        "Qui" to Calendar.THURSDAY,
        "Sex" to Calendar.FRIDAY,
        "Sáb" to Calendar.SATURDAY,
        "Dom" to Calendar.SUNDAY,
    )

    fun iso(y: Int, m: Int, d: Int): String = "%04d-%02d-%02d".format(y, m, d)

    fun hojeISO(): String {
        val c = Calendar.getInstance()
        return iso(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH))
    }

    // '2026-09-11' -> '11/09/2026'
    fun formatar(isoStr: String): String {
        if (isoStr.isBlank()) return ""
        val p = isoStr.split("-")
        if (p.size != 3) return isoStr
        return "${p[2]}/${p[1]}/${p[0]}"
    }

    // '2026-09-11' -> '11 set'
    fun curta(isoStr: String): String {
        if (isoStr.isBlank()) return ""
        val p = isoStr.split("-")
        if (p.size != 3) return isoStr
        return "${p[2].toInt()} ${meses[p[1].toInt() - 1]}"
    }

    fun hora(h: Int, min: Int): String = "%02d:%02d".format(h, min)

    fun cabecalhoHoje(): String {
        val c = Calendar.getInstance()
        return "${diasLongos[c.get(Calendar.DAY_OF_WEEK) - 1]}, " +
            "${c.get(Calendar.DAY_OF_MONTH)} ${meses[c.get(Calendar.MONTH)]}"
    }

    fun saudacao(): String {
        val h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            h < 12 -> "Bom dia"
            h < 20 -> "Boa tarde"
            else -> "Boa noite"
        }
    }

    fun resumoDias(dias: List<Int>): String {
        if (dias.isEmpty() || dias.size >= 7) return "Todos os dias"
        val uteis = listOf(
            Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY,
            Calendar.THURSDAY, Calendar.FRIDAY
        )
        if (dias.size == 5 && uteis.all { dias.contains(it) }) return "Dias úteis"
        return diasSemana.filter { dias.contains(it.second) }.joinToString(", ") { it.first }
    }

    // Conversões para os seletores do Material 3 (que usam millis UTC à meia-noite).
    fun isoParaMillisUtc(isoStr: String): Long {
        val p = if (isoStr.isBlank()) hojeISO().split("-") else isoStr.split("-")
        val c = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(p[0].toInt(), p[1].toInt() - 1, p[2].toInt())
        }
        return c.timeInMillis
    }

    fun millisUtcParaIso(millis: Long): String {
        val c = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = millis }
        return iso(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH))
    }
}
