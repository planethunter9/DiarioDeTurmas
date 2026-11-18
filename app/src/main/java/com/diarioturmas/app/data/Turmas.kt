package com.diarioturmas.app.data

data class Turma(val id: String, val nome: String, val categoria: String)

// As turmas passaram a ser geridas pelo utilizador (guardadas no Repositorio).
// Aqui ficam só as categorias possíveis e alguns atalhos de consulta.
object Turmas {
    val categorias = listOf("Turmas", "Apoios", "Coadjuvação")

    fun porId(id: String): Turma? = Repositorio.turmas.firstOrNull { it.id == id }
    fun porCategoria(cat: String): List<Turma> = Repositorio.turmas.filter { it.categoria == cat }
    fun nomeCompleto(t: Turma?): String = if (t == null) "" else "${t.nome} · ${t.categoria}"
}
