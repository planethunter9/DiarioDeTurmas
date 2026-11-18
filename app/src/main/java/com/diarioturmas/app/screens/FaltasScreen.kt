package com.diarioturmas.app.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import com.diarioturmas.app.components.LinhaSelecao
import com.diarioturmas.app.components.OpcaoChip
import com.diarioturmas.app.components.SeletorData
import com.diarioturmas.app.components.Vazio
import com.diarioturmas.app.data.DataUtil
import com.diarioturmas.app.data.Falta
import com.diarioturmas.app.data.Repositorio
import com.diarioturmas.app.ui.theme.Danger
import com.diarioturmas.app.ui.theme.DangerSoft
import com.diarioturmas.app.ui.theme.Primary
import com.diarioturmas.app.ui.theme.Success
import com.diarioturmas.app.ui.theme.SuccessSoft
import com.diarioturmas.app.ui.theme.TextMain
import com.diarioturmas.app.ui.theme.TextMuted

private val TIPOS = listOf(
    OpcaoChip("Justificada", "justificada", Success),
    OpcaoChip("Injustificada", "injustificada", Danger),
)

@Composable
fun FaltasScreen() {
    var mostrarForm by remember { mutableStateOf(false) }
    var editId by remember { mutableStateOf<String?>(null) }
    var seletorData by remember { mutableStateOf(false) }
    var data by remember { mutableStateOf(DataUtil.hojeISO()) }
    var tempos by remember { mutableStateOf("1") }
    var tipo by remember { mutableStateOf("justificada") }
    var motivo by remember { mutableStateOf("") }

    fun abrir(f: Falta?) {
        editId = f?.id
        data = f?.data ?: DataUtil.hojeISO()
        tempos = f?.tempos?.toString() ?: "1"
        tipo = f?.tipo ?: "justificada"
        motivo = f?.motivo ?: ""
        mostrarForm = true
    }

    fun guardar() {
        if (data.isBlank()) return
        val f = Falta(editId ?: Repositorio.novoId(), data, tempos.toIntOrNull() ?: 0, tipo, motivo.trim())
        if (editId == null) Repositorio.addFalta(f) else Repositorio.updateFalta(f)
        mostrarForm = false
    }

    fun eliminar() {
        editId?.let { Repositorio.removeFalta(it) }
        mostrarForm = false
    }

    val ordenadas = Repositorio.faltas.sortedByDescending { it.data }
    val totalTempos = Repositorio.faltas.sumOf { it.tempos }

    Column(Modifier.fillMaxWidth()) {
        CabecalhoEcra("As minhas faltas", "Faltas do docente · ${Repositorio.faltas.size} · $totalTempos tempo(s)", acao = { BotaoAdicionar { abrir(null) } })
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp).padding(bottom = 24.dp)) {
            if (ordenadas.isEmpty()) {
                Vazio(Icons.Filled.EventBusy, "Sem faltas registadas. Toca no + para adicionares.")
            } else {
                ordenadas.forEach { f ->
                    val just = f.tipo == "justificada"
                    Cartao(onClick = { abrir(f) }) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(64.dp)) {
                                Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                                Text(DataUtil.formatar(f.data), color = Primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(Modifier.weight(1f).padding(start = 12.dp)) {
                                Text("${f.tempos} tempo(s)", fontWeight = FontWeight.Bold, color = TextMain, fontSize = 15.sp)
                            }
                            Badge(if (just) "Justificada" else "Injustificada", if (just) Success else Danger, if (just) SuccessSoft else DangerSoft)
                        }
                    }
                }
            }
        }
    }

    if (mostrarForm) {
        DetalheDialog(
            titulo = if (editId != null) "Editar falta" else "Nova falta",
            novo = editId == null,
            onFechar = { mostrarForm = false },
            onGuardar = { guardar() },
            onEliminar = if (editId != null) ({ eliminar() }) else null,
            infos = {
                LinhaInfo("Data", DataUtil.formatar(data))
                LinhaInfo("Nº de tempos", tempos)
                LinhaInfo("Tipo", if (tipo == "justificada") "Justificada" else "Injustificada")
                LinhaInfo("Motivo", motivo)
            },
            form = {
                LinhaSelecao("Data", DataUtil.formatar(data), Icons.Filled.CalendarMonth) { seletorData = true }
                CampoTexto("Nº de tempos", tempos, { tempos = it }, "Ex.: 2", teclado = KeyboardType.Number)
                ChipsUnico("Tipo", TIPOS, tipo) { tipo = it }
                CampoTexto("Motivo (opcional)", motivo, { motivo = it }, "Ex.: Consulta, formação...", multiline = true)
            }
        )
    }

    SeletorData(seletorData, data, onFechar = { seletorData = false }) { iso -> data = iso }
}
