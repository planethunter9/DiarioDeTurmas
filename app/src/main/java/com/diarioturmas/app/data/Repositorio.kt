package com.diarioturmas.app.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateListOf
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Guarda tudo localmente no telemóvel. As listas são observáveis pelo Compose,
// por isso qualquer ecrã que as leia atualiza-se automaticamente.
object Repositorio {

    private lateinit var prefs: SharedPreferences
    private val gson = Gson()
    private var iniciado = false

    val turmas = mutableStateListOf<Turma>()
    val lembretes = mutableStateListOf<Lembrete>()
    val testes = mutableStateListOf<Teste>()
    val sumarios = mutableStateListOf<Sumario>()
    val comportamento = mutableStateListOf<Comportamento>()
    val faltas = mutableStateListOf<Falta>()
    val alunosNee = mutableStateListOf<AlunoNEE>()
    val aulas = mutableStateListOf<Aula>()
    private var temaEscuroPref: Boolean? = null

    fun init(context: Context) {
        if (iniciado) return
        prefs = context.applicationContext.getSharedPreferences("diario", Context.MODE_PRIVATE)
        carregar()
        iniciado = true
    }

    // Usado pelos receivers, que podem correr num processo acabado de arrancar.
    fun initSeNecessario(context: Context) {
        if (!iniciado) init(context)
    }

    private fun carregar() {
        turmas.setAll(ler("turmas", object : TypeToken<List<Turma>>() {}))
        lembretes.setAll(ler("lembretes", object : TypeToken<List<Lembrete>>() {}))
        testes.setAll(ler("testes", object : TypeToken<List<Teste>>() {}))
        sumarios.setAll(ler("sumarios", object : TypeToken<List<Sumario>>() {}))
        comportamento.setAll(ler("comportamento", object : TypeToken<List<Comportamento>>() {}))
        faltas.setAll(ler("faltas", object : TypeToken<List<Falta>>() {}))
        alunosNee.setAll(ler("alunosNee", object : TypeToken<List<AlunoNEE>>() {}))
        aulas.setAll(ler("aulas", object : TypeToken<List<Aula>>() {}))
        temaEscuroPref = prefs.getString("temaEscuro", null)?.toBooleanStrictOrNull()
    }

    private fun <T> ler(chave: String, tipo: TypeToken<List<T>>): List<T> {
        val raw = prefs.getString(chave, null) ?: return emptyList()
        return try {
            gson.fromJson<List<T>>(raw, tipo.type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun guardar(chave: String, lista: List<*>) {
        prefs.edit().putString(chave, gson.toJson(lista)).apply()
    }

    fun novoId(): String =
        System.currentTimeMillis().toString(36) + (0..9999).random().toString(36)

    fun proximoIdLembrete(): Int = (lembretes.maxOfOrNull { it.id } ?: 0) + 1

    // ---- Turmas ----
    fun addTurma(t: Turma) { turmas.add(t); guardar("turmas", turmas) }
    fun updateTurma(t: Turma) {
        val i = turmas.indexOfFirst { it.id == t.id }; if (i >= 0) turmas[i] = t
        guardar("turmas", turmas)
    }
    fun removeTurma(id: String) {
        turmas.removeAll { it.id == id }
        testes.removeAll { it.turmaId == id }
        sumarios.removeAll { it.turmaId == id }
        comportamento.removeAll { it.turmaId == id }
        alunosNee.removeAll { it.turmaId == id }
        guardar("turmas", turmas)
        guardar("testes", testes)
        guardar("sumarios", sumarios)
        guardar("comportamento", comportamento)
        guardar("alunosNee", alunosNee)
    }

    // ---- Lembretes ----
    fun addLembrete(l: Lembrete) { lembretes.add(0, l); guardar("lembretes", lembretes) }
    fun updateLembrete(l: Lembrete) {
        val i = lembretes.indexOfFirst { it.id == l.id }
        if (i >= 0) lembretes[i] = l else lembretes.add(0, l)
        guardar("lembretes", lembretes)
    }
    fun removeLembrete(id: Int) { lembretes.removeAll { it.id == id }; guardar("lembretes", lembretes) }

    // ---- Testes ----
    fun addTeste(t: Teste) { testes.add(0, t); guardar("testes", testes) }
    fun updateTeste(t: Teste) {
        val i = testes.indexOfFirst { it.id == t.id }; if (i >= 0) testes[i] = t
        guardar("testes", testes)
    }
    fun removeTeste(id: String) { testes.removeAll { it.id == id }; guardar("testes", testes) }

    // ---- Sumários ----
    fun addSumario(s: Sumario) { sumarios.add(0, s); guardar("sumarios", sumarios) }
    fun updateSumario(s: Sumario) {
        val i = sumarios.indexOfFirst { it.id == s.id }; if (i >= 0) sumarios[i] = s
        guardar("sumarios", sumarios)
    }
    fun removeSumario(id: String) { sumarios.removeAll { it.id == id }; guardar("sumarios", sumarios) }

    // ---- Comportamento ----
    fun addComportamento(c: Comportamento) { comportamento.add(0, c); guardar("comportamento", comportamento) }
    fun updateComportamento(c: Comportamento) {
        val i = comportamento.indexOfFirst { it.id == c.id }; if (i >= 0) comportamento[i] = c
        guardar("comportamento", comportamento)
    }
    fun removeComportamento(id: String) { comportamento.removeAll { it.id == id }; guardar("comportamento", comportamento) }

    // ---- Faltas ----
    fun addFalta(f: Falta) { faltas.add(0, f); guardar("faltas", faltas) }
    fun updateFalta(f: Falta) {
        val i = faltas.indexOfFirst { it.id == f.id }; if (i >= 0) faltas[i] = f
        guardar("faltas", faltas)
    }
    fun removeFalta(id: String) { faltas.removeAll { it.id == id }; guardar("faltas", faltas) }

    // ---- Alunos NEE ----
    fun addAlunoNee(a: AlunoNEE) { alunosNee.add(0, a); guardar("alunosNee", alunosNee) }
    fun updateAlunoNee(a: AlunoNEE) {
        val i = alunosNee.indexOfFirst { it.id == a.id }; if (i >= 0) alunosNee[i] = a
        guardar("alunosNee", alunosNee)
    }
    fun removeAlunoNee(id: String) { alunosNee.removeAll { it.id == id }; guardar("alunosNee", alunosNee) }

    // ---- Aulas (horário) ----
    fun addAula(a: Aula) { aulas.add(a); guardar("aulas", aulas) }
    fun addAulas(novas: List<Aula>) { aulas.addAll(novas); guardar("aulas", aulas) }
    fun updateAula(a: Aula) {
        val i = aulas.indexOfFirst { it.id == a.id }; if (i >= 0) aulas[i] = a
        guardar("aulas", aulas)
    }
    fun removeAula(id: String) { aulas.removeAll { it.id == id }; guardar("aulas", aulas) }

    // ---- Exportar / Importar (mesmo formato da versão PC) ----
    private data class Dados(
        val turmas: List<Turma> = emptyList(),
        val lembretes: List<Lembrete> = emptyList(),
        val testes: List<Teste> = emptyList(),
        val sumarios: List<Sumario> = emptyList(),
        val comportamento: List<Comportamento> = emptyList(),
        val faltas: List<Falta> = emptyList(),
        val alunosNee: List<AlunoNEE> = emptyList(),
        val aulas: List<Aula> = emptyList(),
        val temaEscuro: Boolean? = null,
    )

    fun exportarJson(): String = gson.toJson(
        Dados(
            turmas.toList(), lembretes.toList(), testes.toList(), sumarios.toList(),
            comportamento.toList(), faltas.toList(), alunosNee.toList(), aulas.toList(),
            temaEscuroPref
        )
    )

    fun importarJson(json: String): Boolean {
        return try {
            val d = gson.fromJson(json, Dados::class.java) ?: return false
            turmas.setAll(d.turmas); lembretes.setAll(d.lembretes); testes.setAll(d.testes)
            sumarios.setAll(d.sumarios); comportamento.setAll(d.comportamento); faltas.setAll(d.faltas)
            alunosNee.setAll(d.alunosNee); aulas.setAll(d.aulas)
            temaEscuroPref = d.temaEscuro
            guardar("turmas", turmas); guardar("lembretes", lembretes); guardar("testes", testes)
            guardar("sumarios", sumarios); guardar("comportamento", comportamento); guardar("faltas", faltas)
            guardar("alunosNee", alunosNee); guardar("aulas", aulas)
            prefs.edit().putString("temaEscuro", temaEscuroPref?.toString()).apply()
            true
        } catch (e: Exception) {
            false
        }
    }
}

private fun <T> androidx.compose.runtime.snapshots.SnapshotStateList<T>.setAll(novos: List<T>) {
    clear(); addAll(novos)
}
