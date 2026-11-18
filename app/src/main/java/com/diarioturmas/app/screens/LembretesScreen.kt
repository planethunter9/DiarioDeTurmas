package com.diarioturmas.app.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diarioturmas.app.components.BotaoAdicionar
import com.diarioturmas.app.components.CabecalhoEcra
import com.diarioturmas.app.components.CampoTexto
import com.diarioturmas.app.components.Cartao
import com.diarioturmas.app.components.ChipsDias
import com.diarioturmas.app.components.DetalheDialog
import com.diarioturmas.app.components.LinhaInfo
import com.diarioturmas.app.components.LinhaSelecao
import com.diarioturmas.app.components.SeletorHora
import com.diarioturmas.app.components.Vazio
import com.diarioturmas.app.data.DataUtil
import com.diarioturmas.app.data.Lembrete
import com.diarioturmas.app.data.Repositorio
import com.diarioturmas.app.notif.AgendadorLembretes
import com.diarioturmas.app.ui.theme.Primary
import com.diarioturmas.app.ui.theme.TextMain
import com.diarioturmas.app.ui.theme.TextMuted

@Composable
fun LembretesScreen(onPedirPermissao: () -> Unit) {
    val context = LocalContext.current

    var mostrarForm by remember { mutableStateOf(false) }
    var editId by remember { mutableStateOf<Int?>(null) }
    var titulo by remember { mutableStateOf("") }
    var mensagem by remember { mutableStateOf("") }
    var hora by remember { mutableIntStateOf(8) }
    var minuto by remember { mutableIntStateOf(0) }
    val dias = remember { mutableListOf<Int>().toMutableStateList() }
    var ativo by remember { mutableStateOf(true) }
    var seletorHora by remember { mutableStateOf(false) }

    fun abrir(l: Lembrete?) {
        editId = l?.id
        titulo = l?.titulo ?: ""
        mensagem = l?.mensagem ?: ""
        hora = l?.hora ?: 8
        minuto = l?.minuto ?: 0
        dias.clear(); dias.addAll(l?.dias ?: emptyList())
        ativo = l?.ativo ?: true
        mostrarForm = true
    }

    fun guardar() {
        if (titulo.isBlank()) return
        val id = editId ?: Repositorio.proximoIdLembrete()
        val novo = Lembrete(id, titulo.trim(), mensagem.trim(), hora, minuto, dias.toList(), ativo)

        AgendadorLembretes.cancelar(context, id)
        if (ativo) {
            onPedirPermissao()
            AgendadorLembretes.agendar(context, novo)
        }
        if (editId == null) Repositorio.addLembrete(novo) else Repositorio.updateLembrete(novo)
        mostrarForm = false
    }

    fun eliminar() {
        editId?.let {
            AgendadorLembretes.cancelar(context, it)
            Repositorio.removeLembrete(it)
        }
        mostrarForm = false
    }

    fun alternar(l: Lembrete) {
        val novo = l.copy(ativo = !l.ativo)
        if (novo.ativo) {
            onPedirPermissao()
            AgendadorLembretes.agendar(context, novo)
        } else {
            AgendadorLembretes.cancelar(context, l.id)
        }
        Repositorio.updateLembrete(novo)
    }

    Column(Modifier.fillMaxWidth()) {
        CabecalhoEcra("Lembretes", "Avisos para os registos no Inovar", acao = { BotaoAdicionar { abrir(null) } })
        Column(
            Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp).padding(bottom = 24.dp)
        ) {
            if (Repositorio.lembretes.isEmpty()) {
                Vazio(Icons.Filled.NotificationsNone, "Ainda não tens lembretes. Toca no + para criares o primeiro.")
            } else {
                Repositorio.lembretes.forEach { l ->
                    Cartao(onClick = { abrir(l) }) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.weight(1f)) {
                                Text(l.titulo, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (l.ativo) TextMain else TextMuted)
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                                    Icon(Icons.Filled.AccessTime, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.size(4.dp))
                                    Text("${DataUtil.hora(l.hora, l.minuto)}  ·  ${DataUtil.resumoDias(l.dias)}", color = TextMuted, fontSize = 13.sp)
                                }
                            }
                            Switch(
                                checked = l.ativo,
                                onCheckedChange = { alternar(l) },
                                colors = SwitchDefaults.colors(checkedTrackColor = Primary, checkedThumbColor = Color.White)
                            )
                        }
                    }
                }
            }
        }
    }

    if (mostrarForm) {
        DetalheDialog(
            titulo = if (editId != null) "Editar lembrete" else "Novo lembrete",
            novo = editId == null,
            onFechar = { mostrarForm = false },
            onGuardar = { guardar() },
            onEliminar = if (editId != null) ({ eliminar() }) else null,
            infos = {
                LinhaInfo("Título", titulo)
                LinhaInfo("Mensagem", mensagem)
                LinhaInfo("Hora", DataUtil.hora(hora, minuto))
                LinhaInfo("Dias", DataUtil.resumoDias(dias.toList()))
                LinhaInfo("Estado", if (ativo) "Ativo" else "Inativo")
            },
            form = {
                CampoTexto("Título", titulo, { titulo = it }, "Ex.: Registar sumários no Inovar")
                CampoTexto("Mensagem (opcional)", mensagem, { mensagem = it }, "Texto da notificação", multiline = true)
                LinhaSelecao("Hora", DataUtil.hora(hora, minuto), Icons.Filled.AccessTime) { seletorHora = true }
                ChipsDias("Dias (nenhum selecionado = todos os dias)", dias) { d ->
                    if (dias.contains(d)) dias.remove(d) else dias.add(d)
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
                    Text("Lembrete ativo", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextMain, modifier = Modifier.weight(1f))
                    Switch(
                        checked = ativo,
                        onCheckedChange = { ativo = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = Primary, checkedThumbColor = Color.White)
                    )
                }
            }
        )
    }

    SeletorHora(seletorHora, hora, minuto, onFechar = { seletorHora = false }) { h, m ->
        hora = h; minuto = m
    }
}
