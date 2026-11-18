package com.diarioturmas.app.data

// Uma aula do horário (agora gerida pelo utilizador, guardada no Repositorio).
// dia: 1=Segunda ... 5=Sexta | tempo: 1..10 (início) | duracao: nº de tempos seguidos.
data class Aula(
    val id: String,
    val dia: Int,
    val tempo: Int,
    val disciplina: String,
    val turma: String = "",
    val sala: String = "",
    val duracao: Int = 1,
    val ano: String? = null
)

object Horario {
    val diasNomes = listOf("Segunda", "Terça", "Quarta", "Quinta", "Sexta")
    val diasCurtos = listOf("Seg", "Ter", "Qua", "Qui", "Sex")

    // Início e fim de cada tempo letivo (grelha fixa da escola)
    val horas: Map<Int, Pair<String, String>> = mapOf(
        1 to ("08:15" to "09:05"),
        2 to ("09:10" to "10:00"),
        3 to ("10:20" to "11:10"),
        4 to ("11:20" to "12:10"),
        5 to ("12:20" to "13:10"),
        6 to ("13:20" to "14:10"),
        7 to ("14:20" to "15:10"),
        8 to ("15:20" to "16:10"),
        9 to ("16:20" to "17:10"),
        10 to ("17:20" to "18:10"),
    )

    fun porDia(dia: Int): List<Aula> = Repositorio.aulas.filter { it.dia == dia }.sortedBy { it.tempo }

    fun intervaloHoras(a: Aula): String {
        val ini = horas[a.tempo]?.first ?: ""
        val fim = horas[a.tempo + a.duracao - 1]?.second ?: horas[a.tempo]?.second ?: ""
        return "$ini – $fim"
    }

    fun etiquetaTempo(a: Aula): String =
        if (a.duracao > 1) "${a.tempo}–${a.tempo + a.duracao - 1}" else "${a.tempo}"
}
