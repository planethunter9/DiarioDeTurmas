package com.diarioturmas.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diarioturmas.app.components.Badge
import com.diarioturmas.app.components.CabecalhoEcra
import com.diarioturmas.app.components.Cartao
import com.diarioturmas.app.data.DataUtil
import com.diarioturmas.app.data.Repositorio
import com.diarioturmas.app.data.Turmas
import com.diarioturmas.app.ui.theme.Primary
import com.diarioturmas.app.ui.theme.PrimarySoft
import com.diarioturmas.app.ui.theme.TextMain
import com.diarioturmas.app.ui.theme.TextMuted
import com.diarioturmas.app.ui.theme.coresCategoria
import java.util.Calendar

private val MESES = listOf(
    "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
    "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
)

@Composable
fun CalendarioScreen() {
    val agora = Calendar.getInstance()
    var ano by remember { mutableIntStateOf(agora.get(Calendar.YEAR)) }
    var mes0 by remember { mutableIntStateOf(agora.get(Calendar.MONTH)) }
    var selecionado by remember { mutableStateOf(DataUtil.hojeISO()) }

    val cal = Calendar.getInstance().apply { clear(); set(ano, mes0, 1) }
    val diasNoMes = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val offset = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7 // 0 = Segunda
    val prefixo = DataUtil.iso(ano, mes0 + 1, 1).substring(0, 7)
    val diasComEvento = Repositorio.testes
        .filter { it.data.startsWith(prefixo) }
        .mapNotNull { it.data.substring(8).toIntOrNull() }
        .toSet()
    val eventosDoDia = Repositorio.testes.filter { it.data == selecionado }.sortedBy { it.titulo }

    Column(Modifier.fillMaxWidth()) {
        CabecalhoEcra("Calendário", "Testes e trabalhos")
        Column(
            Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp).padding(bottom = 24.dp)
        ) {
            // navegação do mês
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                IconeNav(Icons.Filled.ChevronLeft) { if (mes0 == 0) { mes0 = 11; ano -= 1 } else mes0 -= 1 }
                Text(
                    "${MESES[mes0]} $ano",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextMain
                )
                IconeNav(Icons.Filled.ChevronRight) { if (mes0 == 11) { mes0 = 0; ano += 1 } else mes0 += 1 }
            }

            // dias da semana
            Row(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom").forEach { d ->
                    Text(d, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                }
            }

            // grelha de dias
            val celulas = ArrayList<Int?>()
            repeat(offset) { celulas.add(null) }
            for (d in 1..diasNoMes) celulas.add(d)
            while (celulas.size % 7 != 0) celulas.add(null)
            celulas.chunked(7).forEach { semana ->
                Row(Modifier.fillMaxWidth()) {
                    semana.forEach { dia ->
                        Box(Modifier.weight(1f).height(46.dp).padding(2.dp), contentAlignment = Alignment.Center) {
                            if (dia != null) {
                                val iso = DataUtil.iso(ano, mes0 + 1, dia)
                                val sel = iso == selecionado
                                Box(
                                    Modifier.size(38.dp).clip(RoundedCornerShape(19.dp))
                                        .background(if (sel) Primary else Color.Transparent)
                                        .clickable { selecionado = iso },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            "$dia",
                                            color = if (sel) Color.White else TextMain,
                                            fontSize = 14.sp,
                                            fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal
                                        )
                                        if (dia in diasComEvento) {
                                            Spacer(Modifier.size(2.dp))
                                            Box(Modifier.size(5.dp).clip(RoundedCornerShape(3.dp)).background(if (sel) Color.White else Primary))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // eventos do dia selecionado
            Text(
                "Eventos de ${DataUtil.formatar(selecionado)}",
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextMain,
                modifier = Modifier.padding(top = 18.dp, bottom = 10.dp)
            )
            if (eventosDoDia.isEmpty()) {
                Cartao { Text("Sem testes ou trabalhos neste dia.", color = TextMuted, fontSize = 14.sp) }
            } else {
                eventosDoDia.forEach { t ->
                    val turma = Turmas.porId(t.turmaId)
                    val c = turma?.let { coresCategoria(it.categoria) }
                    Cartao {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.weight(1f)) {
                                Text(t.titulo.ifBlank { t.tipo }, fontWeight = FontWeight.Bold, color = TextMain, fontSize = 15.sp)
                                Text(Turmas.nomeCompleto(turma), color = TextMuted, fontSize = 13.sp)
                            }
                            Badge(t.tipo, c?.cor ?: Primary, c?.soft ?: PrimarySoft)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IconeNav(icone: ImageVector, onClick: () -> Unit) {
    Box(
        Modifier.size(40.dp).clip(RoundedCornerShape(20.dp)).background(PrimarySoft).clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(icone, contentDescription = null, tint = Primary, modifier = Modifier.size(22.dp))
    }
}
