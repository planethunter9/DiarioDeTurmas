package com.diarioturmas.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diarioturmas.app.components.CabecalhoEcra
import com.diarioturmas.app.components.CabecalhoSeccao
import com.diarioturmas.app.components.Cartao
import com.diarioturmas.app.data.Aula
import com.diarioturmas.app.data.DataUtil
import com.diarioturmas.app.data.Horario
import com.diarioturmas.app.data.Repositorio
import com.diarioturmas.app.data.Turma
import com.diarioturmas.app.data.Turmas
import com.diarioturmas.app.ui.theme.OnSoftMuted
import com.diarioturmas.app.ui.theme.Primary
import com.diarioturmas.app.ui.theme.PrimarySoft
import com.diarioturmas.app.ui.theme.Success
import com.diarioturmas.app.ui.theme.SuccessSoft
import com.diarioturmas.app.ui.theme.SurfaceAlt
import com.diarioturmas.app.ui.theme.SurfaceC
import com.diarioturmas.app.ui.theme.TextMain
import com.diarioturmas.app.ui.theme.TextMuted
import com.diarioturmas.app.ui.theme.Violet
import com.diarioturmas.app.ui.theme.VioletSoft
import com.diarioturmas.app.ui.theme.Warning
import com.diarioturmas.app.ui.theme.WarningSoft
import com.diarioturmas.app.ui.theme.coresCategoria
import java.util.Calendar

private fun diaDeSemanaHoje(): Int = when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
    Calendar.MONDAY -> 1
    Calendar.TUESDAY -> 2
    Calendar.WEDNESDAY -> 3
    Calendar.THURSDAY -> 4
    Calendar.FRIDAY -> 5
    else -> 0
}

@Composable
fun InicioScreen(
    onVerTurmas: (() -> Unit)? = null,
    onVerHorario: (() -> Unit)? = null,
    onVerCalendario: (() -> Unit)? = null,
    onAbrirTurma: ((Turma) -> Unit)? = null,
    onDefinicoes: (() -> Unit)? = null
) {
    val hoje = DataUtil.hojeISO()
    val sumariosPendentes = Repositorio.sumarios.filter { !it.verificado }
    val proximosTestes = Repositorio.testes.filter { it.data >= hoje }.sortedBy { it.data }
    val diaHoje = diaDeSemanaHoje()
    val aulasHoje = if (diaHoje in 1..5) Horario.porDia(diaHoje) else emptyList()

    Column(Modifier.fillMaxWidth()) {
        CabecalhoEcra(
            "Diário de Turmas",
            DataUtil.cabecalhoHoje(),
            acao = {
                if (onDefinicoes != null) {
                    Surface(color = SurfaceAlt, shape = RoundedCornerShape(22.dp), modifier = Modifier.size(44.dp).clickable { onDefinicoes() }) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Filled.Settings, contentDescription = "Definições", tint = TextMuted)
                        }
                    }
                }
            }
        )
        Column(
            Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp).padding(bottom = 28.dp)
        ) {
            // ---- Aulas de hoje ----
            CabecalhoSeccao(Icons.Filled.Schedule, Primary, PrimarySoft, "Aulas de hoje", "Ver horário", onVerHorario)
            if (aulasHoje.isEmpty()) {
                Cartao {
                    Text(
                        if (diaHoje == 0) "Fim de semana — sem aulas." else "Sem aulas marcadas para hoje.",
                        color = TextMuted, fontSize = 14.sp
                    )
                }
            } else {
                aulasHoje.forEach { a -> CartaoAulaHoje(a) { onVerHorario?.invoke() } }
            }

            // ---- Próximos eventos ----
            CabecalhoSeccao(Icons.Filled.CalendarMonth, Primary, PrimarySoft, "Próximos eventos", "Ver agenda", onVerCalendario)
            if (proximosTestes.isEmpty()) {
                Cartao { Text("Sem testes ou trabalhos agendados.", color = TextMuted, fontSize = 14.sp) }
            } else {
                Row(
                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    proximosTestes.take(10).forEach { t ->
                        val turma = Turmas.porId(t.turmaId)
                        val cor = turma?.let { coresCategoria(it.categoria).cor } ?: Violet
                        CartaoEvento(t.titulo.ifBlank { t.tipo }, turma?.nome ?: "", t.data, cor) { onVerCalendario?.invoke() }
                    }
                }
            }

            // ---- Minhas turmas ----
            CabecalhoSeccao(Icons.Filled.Groups, Primary, PrimarySoft, "Minhas turmas", "Ver turmas", onVerTurmas)
            if (Repositorio.turmas.isEmpty()) {
                Cartao { Text("Ainda não adicionaste turmas.", color = TextMuted, fontSize = 14.sp) }
            } else {
                Row(
                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Repositorio.turmas.forEach { turma -> ChipTurma(turma) { onAbrirTurma?.invoke(turma) } }
                }
            }

            // ---- Sumários por verificar ----
            CabecalhoSeccao(Icons.Filled.CheckCircle, Primary, PrimarySoft, "Sumários por verificar")
            if (sumariosPendentes.isEmpty()) {
                Cartao { Text("Está tudo verificado. 👍", color = TextMuted, fontSize = 14.sp) }
            } else {
                sumariosPendentes.take(5).forEach { s ->
                    val turma = Turmas.porId(s.turmaId)
                    Cartao(onClick = { turma?.let { onAbrirTurma?.invoke(it) } }) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Box(Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(WarningSoft), contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Warning, modifier = Modifier.size(20.dp))
                            }
                            Column(Modifier.weight(1f).padding(start = 12.dp)) {
                                Text(s.descricao.ifBlank { "Sumário" }, fontWeight = FontWeight.Bold, color = TextMain, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(Turmas.nomeCompleto(turma), color = TextMuted, fontSize = 13.sp)
                            }
                            Text(DataUtil.curta(s.data), color = TextMuted, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CartaoEvento(titulo: String, turma: String, data: String, cor: Color, onClick: () -> Unit) {
    Surface(color = SurfaceC, shape = RoundedCornerShape(18.dp), shadowElevation = 2.dp, modifier = Modifier.width(150.dp).height(112.dp).clickable { onClick() }) {
        Column(Modifier.padding(13.dp)) {
            Text(turma, color = cor, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Spacer(Modifier.size(5.dp))
            Text(titulo, color = TextMain, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.weight(1f))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Schedule, contentDescription = null, tint = cor, modifier = Modifier.size(13.dp))
                Spacer(Modifier.size(4.dp))
                Text(DataUtil.curta(data), color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun ChipTurma(turma: Turma, onClick: () -> Unit) {
    val c = coresCategoria(turma.categoria)
    Surface(color = c.soft, shape = RoundedCornerShape(16.dp), modifier = Modifier.width(134.dp).height(86.dp).clickable { onClick() }) {
        Column(Modifier.padding(14.dp)) {
            Text(turma.nome, color = c.cor, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1)
            Spacer(Modifier.weight(1f))
            Text(turma.categoria, color = OnSoftMuted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun CartaoAulaHoje(a: Aula, onClick: () -> Unit) {
    Cartao(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(58.dp)) {
                Text(Horario.horas[a.tempo]?.first ?: "", fontWeight = FontWeight.ExtraBold, color = Primary, fontSize = 15.sp)
                Text("T${Horario.etiquetaTempo(a)}", color = TextMuted, fontSize = 11.sp)
            }
            Column(Modifier.weight(1f).padding(start = 12.dp)) {
                Text(a.disciplina, fontWeight = FontWeight.Bold, color = TextMain, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    listOfNotNull(a.turma.ifBlank { null }, a.sala.ifBlank { null }).joinToString(" · "),
                    color = TextMuted, fontSize = 13.sp
                )
            }
        }
    }
}
