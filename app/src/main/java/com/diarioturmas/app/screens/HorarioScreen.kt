package com.diarioturmas.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diarioturmas.app.components.Badge
import com.diarioturmas.app.components.BotaoAdicionar
import com.diarioturmas.app.components.CabecalhoEcra
import com.diarioturmas.app.components.CampoTexto
import com.diarioturmas.app.components.Cartao
import com.diarioturmas.app.components.ChipsUnico
import com.diarioturmas.app.components.DetalheDialog
import com.diarioturmas.app.components.LinhaInfo
import com.diarioturmas.app.components.OpcaoChip
import com.diarioturmas.app.components.SeletorLista
import com.diarioturmas.app.components.Vazio
import com.diarioturmas.app.data.Aula
import com.diarioturmas.app.data.Curriculo
import com.diarioturmas.app.data.Horario
import com.diarioturmas.app.data.Repositorio
import com.diarioturmas.app.ui.theme.Danger
import com.diarioturmas.app.ui.theme.Neutral
import com.diarioturmas.app.ui.theme.NeutralSoft
import com.diarioturmas.app.ui.theme.Primary
import com.diarioturmas.app.ui.theme.PrimarySoft
import com.diarioturmas.app.ui.theme.Success
import com.diarioturmas.app.ui.theme.SuccessSoft
import com.diarioturmas.app.ui.theme.SurfaceAlt
import com.diarioturmas.app.ui.theme.TextMain
import com.diarioturmas.app.ui.theme.TextMuted
import com.diarioturmas.app.ui.theme.Warning
import com.diarioturmas.app.ui.theme.WarningSoft
import java.util.Calendar

private const val OUTRA = "Outra…"

private fun coresDisciplina(d: String): Pair<Color, Color> {
    val n = d.uppercase()
    return when {
        n.contains("HGP") || n.contains("HISTÓRIA") || n.contains("GEOGRAFIA") -> Primary to PrimarySoft
        n.contains("APOIO") || n.contains("CIDADANIA") -> Success to SuccessSoft
        n.contains("COADJ") -> Warning to WarningSoft
        else -> Neutral to NeutralSoft
    }
}

private fun diaDeHoje(): Int = when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
    Calendar.MONDAY -> 1
    Calendar.TUESDAY -> 2
    Calendar.WEDNESDAY -> 3
    Calendar.THURSDAY -> 4
    Calendar.FRIDAY -> 5
    else -> 1
}

private fun inferAno(turma: String): String? {
    val d = Regex("""\d""").find(turma)?.value ?: return null
    val a = "${d}º"
    return if (a in Curriculo.anos) a else null
}

@Composable
fun HorarioScreen() {
    var dia by remember { mutableIntStateOf(diaDeHoje()) }

    var mostrarForm by remember { mutableStateOf(false) }
    var editId by remember { mutableStateOf<String?>(null) }
    var fDia by remember { mutableIntStateOf(diaDeHoje()) }
    var fTempo by remember { mutableIntStateOf(1) }
    var fDuracao by remember { mutableIntStateOf(1) }
    var fAno by remember { mutableStateOf(Curriculo.anos.first()) }
    var fDisciplina by remember { mutableStateOf("") }
    var fOutra by remember { mutableStateOf(false) }
    var fTurma by remember { mutableStateOf("") }
    var fSala by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf<String?>(null) }

    fun abrirForm(a: Aula?) {
        editId = a?.id
        fDia = a?.dia ?: dia
        fTempo = a?.tempo ?: 1
        fDuracao = a?.duracao ?: 1
        fAno = a?.ano?.takeIf { it.isNotBlank() } ?: a?.turma?.let { inferAno(it) } ?: Curriculo.anos.first()
        fDisciplina = a?.disciplina ?: ""
        fOutra = a != null && a.disciplina.isNotBlank() && a.disciplina !in Curriculo.disciplinasPorAno(fAno)
        fTurma = a?.turma ?: ""
        fSala = a?.sala ?: ""
        erro = null
        mostrarForm = true
    }

    fun guardar() {
        if (fTurma.isBlank() || fDisciplina.isBlank()) {
            erro = "Indica o ano, a turma e a disciplina."
            return
        }
        val a = Aula(editId ?: Repositorio.novoId(), fDia, fTempo, fDisciplina.trim(), fTurma.trim(), fSala.trim(), fDuracao, fAno)
        if (editId == null) Repositorio.addAula(a) else Repositorio.updateAula(a)
        dia = fDia
        mostrarForm = false
    }

    fun eliminar() {
        editId?.let { Repositorio.removeAula(it) }
        mostrarForm = false
    }

    val doDia = Horario.porDia(dia)

    Column(Modifier.fillMaxWidth()) {
        CabecalhoEcra("Horário", "O teu horário semanal", acao = { BotaoAdicionar { abrirForm(null) } })

        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Horario.diasCurtos.forEachIndexed { i, nome ->
                val valor = i + 1
                FilterChip(
                    selected = dia == valor,
                    onClick = { dia = valor },
                    label = { Text(nome) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Primary, selectedLabelColor = Color.White)
                )
            }
        }

        Column(
            Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp).padding(top = 14.dp, bottom = 24.dp)
        ) {
            if (Repositorio.aulas.isEmpty()) {
                Vazio(Icons.Filled.Place, "Ainda não adicionaste o teu horário. Toca no + para adicionares aulas.")
            } else if (doDia.isEmpty()) {
                Vazio(Icons.Filled.Place, "Sem aulas na ${Horario.diasNomes[dia - 1]}.")
            } else {
                doDia.forEach { a -> CartaoAula(a) { abrirForm(a) } }
            }
        }
    }

    if (mostrarForm) {
        val opcoesDia = Horario.diasCurtos.mapIndexed { i, c -> OpcaoChip(c, (i + 1).toString()) }
        val opcoesTempo = (1..10).map { OpcaoChip(it.toString(), it.toString()) }
        val opcoesDur = listOf(OpcaoChip("1 tempo", "1"), OpcaoChip("2 tempos", "2"), OpcaoChip("3 tempos", "3"))
        val opcoesAno = Curriculo.anos.map { OpcaoChip(it, it) }
        val opcoesDisc = Curriculo.disciplinasPorAno(fAno) + OUTRA
        DetalheDialog(
            titulo = if (editId != null) "Editar aula" else "Nova aula",
            novo = editId == null,
            onFechar = { mostrarForm = false },
            onGuardar = { guardar() },
            onEliminar = if (editId != null) ({ eliminar() }) else null,
            infos = {
                LinhaInfo("Dia", Horario.diasNomes[fDia - 1])
                LinhaInfo("Tempo", if (fDuracao > 1) "${fTempo}–${fTempo + fDuracao - 1}" else "$fTempo")
                LinhaInfo("Ano", fAno)
                LinhaInfo("Turma", fTurma)
                LinhaInfo("Disciplina", fDisciplina)
                LinhaInfo("Sala", fSala)
            },
            form = {
                ChipsUnico("Dia", opcoesDia, fDia.toString()) { fDia = it.toInt() }
                ChipsUnico("Tempo", opcoesTempo, fTempo.toString()) { fTempo = it.toInt() }
                ChipsUnico("Duração", opcoesDur, fDuracao.toString()) { fDuracao = it.toInt() }
                ChipsUnico("Ano", opcoesAno, fAno) { novo ->
                    fAno = novo
                    // se a disciplina escolhida não existe neste ano, limpa a seleção
                    if (!fOutra && fDisciplina.isNotBlank() && fDisciplina !in Curriculo.disciplinasPorAno(novo)) {
                        fDisciplina = ""
                    }
                }
                CampoTexto("Turma", fTurma, { fTurma = it }, "Ex.: 5ºD")
                SeletorLista("Disciplina", opcoesDisc, if (fOutra) OUTRA else fDisciplina, placeholder = "Escolhe a disciplina") { escolha ->
                    if (escolha == OUTRA) { fOutra = true; fDisciplina = "" } else { fOutra = false; fDisciplina = escolha }
                }
                if (fOutra) {
                    CampoTexto("Disciplina (outra)", fDisciplina, { fDisciplina = it }, "Escreve a disciplina")
                }
                CampoTexto("Sala (opcional)", fSala, { fSala = it }, "Ex.: FC 9")
                if (erro != null) {
                    Text(erro ?: "", color = Danger, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 2.dp))
                }
            }
        )
    }
}

@Composable
private fun CartaoAula(a: Aula, onClick: () -> Unit) {
    val (cor, soft) = coresDisciplina(a.disciplina)
    Cartao(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(44.dp)) {
                Badge(Horario.etiquetaTempo(a), cor, soft)
            }
            Column(Modifier.weight(1f).padding(start = 10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(a.disciplina, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = TextMain, modifier = Modifier.weight(1f, fill = false))
                    if (a.turma.isNotBlank()) {
                        Spacer(Modifier.width(8.dp))
                        Badge(a.turma, cor, soft)
                    }
                }
                Text(Horario.intervaloHoras(a), color = TextMuted, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                if (a.sala.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                        Icon(Icons.Filled.Place, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(a.sala, color = TextMuted, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
