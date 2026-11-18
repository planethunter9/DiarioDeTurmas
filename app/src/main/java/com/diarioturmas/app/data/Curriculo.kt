package com.diarioturmas.app.data

// Disciplinas e anos usados nos formulários. As disciplinas dependem do ciclo:
// 5º/6º (2º ciclo) e 7º/8º (3º ciclo). Para alterar, edita estas listas.
object Curriculo {
    val anos = listOf("5º", "6º", "7º", "8º")

    // 2º ciclo — 5º e 6º ano
    val disciplinas5e6 = listOf(
        "Português",
        "PLNM",
        "Inglês",
        "Matemática",
        "HGP",
        "Ciências Naturais",
        "Educação Visual",
        "Educação Tecnológica",
        "Educação Musical",
        "TIC",
        "Educação Física",
        "Cidadania e Desenvolvimento",
    )

    // 3º ciclo — 7º e 8º ano
    val disciplinas7e8 = listOf(
        "Português",
        "PLNM",
        "Inglês",
        "Francês",
        "Alemão",
        "Matemática",
        "História",
        "Geografia",
        "Ciências Naturais",
        "Física-Química",
        "TIC",
        "Educação Física",
        "Cidadania e Desenvolvimento",
    )

    // Lista completa (união), caso seja precisa nalgum lado
    val disciplinas = (disciplinas5e6 + disciplinas7e8).distinct()

    fun disciplinasPorAno(ano: String?): List<String> = when (ano) {
        "7º", "8º" -> disciplinas7e8
        else -> disciplinas5e6
    }
}
