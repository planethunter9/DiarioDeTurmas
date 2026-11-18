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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diarioturmas.app.components.Badge
import com.diarioturmas.app.components.CampoTexto
import com.diarioturmas.app.components.Cartao
import com.diarioturmas.app.components.ChipsDias
import com.diarioturmas.app.components.ChipsUnico
import com.diarioturmas.app.components.DetalheDialog
import com.diarioturmas.app.components.LinhaInfo
import com.diarioturmas.app.components.LinhaSelecao
import com.diarioturmas.app.components.OpcaoChip
import com.diarioturmas.app.components.SeletorData
import com.diarioturmas.app.components.SeletorHora
import com.diarioturmas.app.components.Vazio
import com.diarioturmas.app.data.AlunoNEE
import com.diarioturmas.app.data.Comportamento
import com.diarioturmas.app.data.DataUtil
import com.diarioturmas.app.data.Lembrete
import com.diarioturmas.app.data.Repositorio
import com.diarioturmas.app.data.Sumario
import com.diarioturmas.app.data.Teste
import com.diarioturmas.app.data.Turma
import com.diarioturmas.app.ui.theme.Danger
import com.diarioturmas.app.ui.theme.DangerSoft
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
import com.diarioturmas.app.ui.theme.coresCategoria

private val TIPOS_TESTE = listOf(
    OpcaoChip("Teste", "Teste"),
    OpcaoChip("Trabalho", "Trabalho"),
    OpcaoChip("Ficha", "Ficha"),
)
private val TIPOS_COMP = listOf(
    OpcaoChip("Positivo", "positivo", Success),
    OpcaoChip("A melhorar", "negativo", Danger),
    OpcaoChip("Nota", "nota", Neutral),
)

private fun coresComp(tipo: String): Triple<Color, Color, String> = when (tipo) {
    "positivo" -> Triple(Success, SuccessSoft, "Positivo")
    "negativo" -> Triple(Danger, DangerSoft, "A melhorar")
    else -> Triple(Neutral, NeutralSoft, "Nota")
}

@Composable
fun TurmaDetalheScreen(turma: Turma, onVoltar: () -> Unit, onLembreteMudou: () -> Unit = {}) {
    val cores = coresCategoria(turma.categoria)
    var aba by remember { mutableStateOf("testes") }

    var mostrarForm by remember { mutableStateOf(false) }
    var editId by remember { mutableStateOf<String?>(null) }
    var seletorData by remember { mutableStateOf(false) }
    var dataForm by remember { mutableStateOf(DataUtil.hojeISO()) }
    // teste
    var tTipo by remember { mutableStateOf("Teste") }
    var tTitulo by remember { mutableStateOf("") }
    var tNotas by remember { mutableStateOf("") }
    // sumário
    var sLicao by remember { mutableStateOf("") }
    var sDescricao by remember { mutableStateOf("") }
    var sVerificado by remember { mutableStateOf(false) }
    // comportamento
    var cNumero by remember { mutableStateOf("") }
    var cTipo by remember { mutableStateOf("positivo") }
    var cDescricao by remember { mutableStateOf("") }
    // aluno NEE
    var nNome by remember { mutableStateOf("") }
    var nNumero by remember { mutableStateOf("") }
    var nObs by remember { mutableStateOf("") }
    // lembrete
    var editLembreteId by remember { mutableStateOf<Int?>(null) }
    var lTitulo by remember { mutableStateOf("") }
    var lMensagem by remember { mutableStateOf("") }
    var lHora by remember { mutableIntStateOf(8) }
    var lMinuto by remember { mutableIntStateOf(0) }
    val lDias = remember { mutableListOf<Int>().toMutableStateList() }
    var lAtivo by remember { mutableStateOf(true) }
    var seletorHora by remember { mutableStateOf(false) }

    fun abrirNovo() {
        editId = null
        dataForm = DataUtil.hojeISO()
        tTipo = "Teste"; tTitulo = ""; tNotas = ""
        sLicao = ""; sDescricao = ""; sVerificado = false
        cNumero = ""; cTipo = "positivo"; cDescricao = ""
        nNome = ""; nNumero = ""; nObs = ""
        editLembreteId = null; lTitulo = ""; lMensagem = ""; lHora = 8; lMinuto = 0; lDias.clear(); lAtivo = true
        mostrarForm = true
    }
    fun abrirTeste(t: Teste) { editId = t.id; dataForm = t.data; tTipo = t.tipo; tTitulo = t.titulo; tNotas = t.notas; mostrarForm = true }
    fun abrirSumario(s: Sumario) { editId = s.id; dataForm = s.data; sLicao = s.licao; sDescricao = s.descricao; sVerificado = s.verificado; mostrarForm = true }
    fun abrirComp(c: Comportamento) { editId = c.id; dataForm = c.data; cNumero = c.numeroAluno; cTipo = c.tipo; cDescricao = c.descricao; mostrarForm = true }
    fun abrirNee(a: AlunoNEE) { editId = a.id; nNome = a.nome; nNumero = a.numero; nObs = a.observacoes; mostrarForm = true }
    fun abrirLembrete(l: Lembrete) { editLembreteId = l.id; lTitulo = l.titulo; lMensagem = l.mensagem; lHora = l.hora; lMinuto = l.minuto; lDias.clear(); lDias.addAll(l.dias); lAtivo = l.ativo; mostrarForm = true }

    fun guardar() {
        when (aba) {
            "testes" -> {
                if (tTitulo.isBlank()) return
                val t = Teste(editId ?: Repositorio.novoId(), turma.id, tTipo, tTitulo.trim(), dataForm, tNotas.trim())
                if (editId == null) Repositorio.addTeste(t) else Repositorio.updateTeste(t)
            }
            "sumarios" -> {
                if (sDescricao.isBlank()) return
                val s = Sumario(editId ?: Repositorio.novoId(), turma.id, dataForm, sLicao.trim(), sDescricao.trim(), sVerificado)
                if (editId == null) Repositorio.addSumario(s) else Repositorio.updateSumario(s)
            }
            "comportamento" -> {
                if (cNumero.isBlank() || cDescricao.isBlank()) return
                val c = Comportamento(editId ?: Repositorio.novoId(), turma.id, cNumero.trim(), cTipo, dataForm, cDescricao.trim())
                if (editId == null) Repositorio.addComportamento(c) else Repositorio.updateComportamento(c)
            }
            "nee" -> {
                if (nNome.isBlank()) return
                val a = AlunoNEE(editId ?: Repositorio.novoId(), turma.id, nNome.trim(), nNumero.trim(), nObs.trim())
                if (editId == null) Repositorio.addAlunoNee(a) else Repositorio.updateAlunoNee(a)
            }
            "lembretes" -> {
                if (lTitulo.isBlank()) return
                val id = editLembreteId ?: Repositorio.proximoIdLembrete()
                val l = Lembrete(id, lTitulo.trim(), lMensagem.trim(), lHora, lMinuto, lDias.toList(), lAtivo, turma.id)
                if (editLembreteId == null) Repositorio.addLembrete(l) else Repositorio.updateLembrete(l)
                onLembreteMudou()
            }
        }
        mostrarForm = false
    }

    fun eliminar() {
        when (aba) {
            "testes" -> editId?.let { Repositorio.removeTeste(it) }
            "sumarios" -> editId?.let { Repositorio.removeSumario(it) }
            "comportamento" -> editId?.let { Repositorio.removeComportamento(it) }
            "nee" -> editId?.let { Repositorio.removeAlunoNee(it) }
            "lembretes" -> { editLembreteId?.let { Repositorio.removeLembrete(it) }; onLembreteMudou() }
        }
        mostrarForm = false
    }

    Column(Modifier.fillMaxWidth()) {
        // Cabeçalho da turma
        Surface(color = cores.soft, modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp)) {
                Box(Modifier.size(40.dp).clip(RoundedCornerShape(20.dp)).clickable { onVoltar() }, contentAlignment = Alignment.Center) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = cores.cor)
                }
                Column(Modifier.weight(1f).padding(start = 8.dp)) {
                    Text(turma.nome, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = cores.cor)
                }
                Surface(color = cores.cor, shape = RoundedCornerShape(21.dp), modifier = Modifier.size(42.dp).clickable { abrirNovo() }) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Add, contentDescription = "Adicionar", tint = Color.White)
                    }
                }
            }
        }

        Spacer(Modifier.size(14.dp))

        Segmentos(
            opcoes = listOf(
                "Testes" to "testes",
                "Sumários" to "sumarios",
                "Comportamento" to "comportamento",
                "Alunos NEE" to "nee",
                "Lembretes" to "lembretes"
            ),
            valor = aba,
            onSel = { aba = it }
        )

        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp).padding(top = 16.dp, bottom = 24.dp)) {
            when (aba) {
                "testes" -> {
                    val lista = Repositorio.testes.filter { it.turmaId == turma.id }.sortedBy { it.data }
                    if (lista.isEmpty()) Vazio(Icons.Filled.Description, "Sem testes ou trabalhos. Toca no + para adicionares.")
                    lista.forEach { t ->
                        Cartao(onClick = { abrirTeste(t) }) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                Box(Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(PrimarySoft), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Filled.Description, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
                                }
                                Column(Modifier.weight(1f).padding(start = 12.dp)) {
                                    Text(t.titulo, fontWeight = FontWeight.Bold, color = TextMain, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Badge(t.tipo, Primary, PrimarySoft)
                                    Spacer(Modifier.size(6.dp))
                                    Text(DataUtil.formatar(t.data), color = TextMuted, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                "sumarios" -> {
                    val lista = Repositorio.sumarios.filter { it.turmaId == turma.id }.sortedByDescending { it.data }
                    if (lista.isEmpty()) Vazio(Icons.Filled.CheckCircle, "Sem sumários registados. Toca no + para adicionares.")
                    lista.forEach { s ->
                        Cartao {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                Box(Modifier.size(28.dp).clickable { Repositorio.updateSumario(s.copy(verificado = !s.verificado)) }, contentAlignment = Alignment.Center) {
                                    if (s.verificado) Icon(Icons.Filled.CheckCircle, contentDescription = "Verificado", tint = Success, modifier = Modifier.size(26.dp))
                                    else Icon(Icons.Outlined.Circle, contentDescription = "Por verificar", tint = Neutral, modifier = Modifier.size(26.dp))
                                }
                                Column(Modifier.weight(1f).padding(start = 12.dp).clickable { abrirSumario(s) }) {
                                    Text(
                                        (if (s.licao.isNotBlank()) "Lição ${s.licao} — " else "") + s.descricao,
                                        fontWeight = FontWeight.Bold,
                                        color = if (s.verificado) TextMuted else TextMain,
                                        fontSize = 15.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textDecoration = if (s.verificado) TextDecoration.LineThrough else TextDecoration.None
                                    )
                                    Text(DataUtil.formatar(s.data), color = TextMuted, fontSize = 13.sp)
                                }
                                if (!s.verificado) Badge("Por verificar", Warning, WarningSoft)
                            }
                        }
                    }
                }
                "comportamento" -> {
                    val lista = Repositorio.comportamento.filter { it.turmaId == turma.id }.sortedByDescending { it.data }
                    if (lista.isEmpty()) Vazio(Icons.Filled.Groups, "Sem registos de comportamento. Toca no + para adicionares.")
                    lista.forEach { c ->
                        val (cor, soft, label) = coresComp(c.tipo)
                        Cartao(onClick = { abrirComp(c) }) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                Box(Modifier.heightIn(min = 44.dp).widthIn(min = 44.dp).clip(RoundedCornerShape(12.dp)).background(soft), contentAlignment = Alignment.Center) {
                                    Text(c.numeroAluno, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = cor, maxLines = 1, modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp))
                                }
                                Column(Modifier.weight(1f).padding(start = 12.dp)) {
                                    Text(DataUtil.formatar(c.data), fontWeight = FontWeight.Bold, color = TextMain, fontSize = 15.sp)
                                }
                                Badge(label, cor, soft)
                            }
                        }
                    }
                }
                "nee" -> {
                    val lista = Repositorio.alunosNee.filter { it.turmaId == turma.id }.sortedBy { it.nome.lowercase() }
                    if (lista.isEmpty()) Vazio(Icons.Filled.School, "Sem alunos NEE registados. Toca no + para adicionares.")
                    lista.forEach { a ->
                        Cartao(onClick = { abrirNee(a) }) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                Box(Modifier.heightIn(min = 44.dp).widthIn(min = 44.dp).clip(RoundedCornerShape(12.dp)).background(cores.soft), contentAlignment = Alignment.Center) {
                                    if (a.numero.isNotBlank()) {
                                        Text(a.numero, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = cores.cor, maxLines = 1, modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp))
                                    } else {
                                        Icon(Icons.Filled.School, contentDescription = null, tint = cores.cor, modifier = Modifier.padding(10.dp).size(22.dp))
                                    }
                                }
                                Column(Modifier.weight(1f).padding(start = 12.dp)) {
                                    Text(a.nome, fontWeight = FontWeight.Bold, color = TextMain, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                Badge(turma.nome, cores.cor, cores.soft)
                            }
                        }
                    }
                }
                "lembretes" -> {
                    val lista = Repositorio.lembretes.filter { it.turmaId == turma.id }
                    if (lista.isEmpty()) Vazio(Icons.Filled.Notifications, "Sem lembretes nesta turma. Toca no + para adicionares.")
                    lista.forEach { l ->
                        Cartao(onClick = { abrirLembrete(l) }) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                Box(Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(cores.soft), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Filled.Notifications, contentDescription = null, tint = cores.cor, modifier = Modifier.size(20.dp))
                                }
                                Column(Modifier.weight(1f).padding(start = 12.dp)) {
                                    Text(l.titulo, fontWeight = FontWeight.Bold, color = if (l.ativo) TextMain else TextMuted, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text("${DataUtil.hora(l.hora, l.minuto)} · ${DataUtil.resumoDias(l.dias)}", color = TextMuted, fontSize = 13.sp)
                                }
                                Switch(
                                    checked = l.ativo,
                                    onCheckedChange = { Repositorio.updateLembrete(l.copy(ativo = it)); onLembreteMudou() },
                                    colors = SwitchDefaults.colors(checkedTrackColor = cores.cor, checkedThumbColor = Color.White)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarForm) {
        val tituloModal = when (aba) {
            "testes" -> if (editId != null) "Editar registo" else "Novo teste / trabalho"
            "sumarios" -> if (editId != null) "Editar sumário" else "Novo sumário"
            "comportamento" -> if (editId != null) "Editar comportamento" else "Novo registo de comportamento"
            "lembretes" -> if (editLembreteId != null) "Editar lembrete" else "Novo lembrete"
            else -> if (editId != null) "Editar aluno NEE" else "Novo aluno NEE"
        }
        val eNovo = if (aba == "lembretes") editLembreteId == null else editId == null
        val podeEliminar = if (aba == "lembretes") editLembreteId != null else editId != null
        DetalheDialog(
            titulo = tituloModal,
            novo = eNovo,
            onFechar = { mostrarForm = false },
            onGuardar = { guardar() },
            onEliminar = if (podeEliminar) ({ eliminar() }) else null,
            infos = {
                when (aba) {
                    "testes" -> {
                        LinhaInfo("Tipo", tTipo)
                        LinhaInfo("Descrição", tTitulo)
                        LinhaInfo("Data", DataUtil.formatar(dataForm))
                        LinhaInfo("Notas", tNotas)
                    }
                    "sumarios" -> {
                        LinhaInfo("Data da aula", DataUtil.formatar(dataForm))
                        LinhaInfo("Nº da lição", sLicao)
                        LinhaInfo("Sumário", sDescricao)
                        LinhaInfo("Estado", if (sVerificado) "Verificado" else "Por verificar")
                    }
                    "comportamento" -> {
                        LinhaInfo("Nº do aluno", cNumero)
                        LinhaInfo("Tipo", coresComp(cTipo).third)
                        LinhaInfo("Data", DataUtil.formatar(dataForm))
                        LinhaInfo("Ocorrência", cDescricao)
                    }
                    "nee" -> {
                        LinhaInfo("Nome do aluno", nNome)
                        LinhaInfo("Nº do aluno", nNumero)
                        LinhaInfo("Turma", "${turma.nome} · ${turma.categoria}")
                        LinhaInfo("Medidas / observações", nObs)
                    }
                    "lembretes" -> {
                        LinhaInfo("Título", lTitulo)
                        LinhaInfo("Mensagem", lMensagem)
                        LinhaInfo("Hora", DataUtil.hora(lHora, lMinuto))
                        LinhaInfo("Dias", DataUtil.resumoDias(lDias.toList()))
                        LinhaInfo("Estado", if (lAtivo) "Ativo" else "Inativo")
                    }
                }
            },
            form = {
                when (aba) {
                    "testes" -> {
                        ChipsUnico("Tipo", TIPOS_TESTE, tTipo) { tTipo = it }
                        CampoTexto("Descrição", tTitulo, { tTitulo = it }, "Ex.: Teste — Unidade 2")
                        LinhaSelecao("Data", DataUtil.formatar(dataForm), Icons.Filled.CalendarMonth) { seletorData = true }
                        CampoTexto("Notas (opcional)", tNotas, { tNotas = it }, "Matéria, materiais, etc.", multiline = true)
                    }
                    "sumarios" -> {
                        LinhaSelecao("Data da aula", DataUtil.formatar(dataForm), Icons.Filled.CalendarMonth) { seletorData = true }
                        CampoTexto("Nº da lição (opcional)", sLicao, { sLicao = it }, "Ex.: 45", teclado = KeyboardType.Number)
                        CampoTexto("Sumário", sDescricao, { sDescricao = it }, "O que foi dado na aula", multiline = true)
                    }
                    "comportamento" -> {
                        CampoTexto("Nº do aluno", cNumero, { cNumero = it }, "Ex.: 14", teclado = KeyboardType.Number)
                        ChipsUnico("Tipo", TIPOS_COMP, cTipo) { cTipo = it }
                        LinhaSelecao("Data", DataUtil.formatar(dataForm), Icons.Filled.CalendarMonth) { seletorData = true }
                        CampoTexto("Ocorrência", cDescricao, { cDescricao = it }, "Descreve o comportamento", multiline = true)
                    }
                    "nee" -> {
                        Text(
                            "Fica associado à turma ${turma.nome} · ${turma.categoria}",
                            fontSize = 13.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(bottom = 14.dp)
                        )
                        CampoTexto("Nome do aluno", nNome, { nNome = it }, "Nome completo")
                        CampoTexto("Nº do aluno (opcional)", nNumero, { nNumero = it }, "Ex.: 14", teclado = KeyboardType.Number)
                        CampoTexto("Medidas / observações (opcional)", nObs, { nObs = it }, "Ex.: medidas universais, adaptações...", multiline = true)
                    }
                    "lembretes" -> {
                        CampoTexto("Título", lTitulo, { lTitulo = it }, "Ex.: Lançar sumários no Inovar")
                        CampoTexto("Mensagem (opcional)", lMensagem, { lMensagem = it }, "Texto da notificação", multiline = true)
                        LinhaSelecao("Hora", DataUtil.hora(lHora, lMinuto), Icons.Filled.AccessTime) { seletorHora = true }
                        ChipsDias("Dias (nenhum selecionado = todos os dias)", lDias) { d -> if (lDias.contains(d)) lDias.remove(d) else lDias.add(d) }
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
                            Text("Lembrete ativo", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextMain, modifier = Modifier.weight(1f))
                            Switch(checked = lAtivo, onCheckedChange = { lAtivo = it }, colors = SwitchDefaults.colors(checkedTrackColor = cores.cor, checkedThumbColor = Color.White))
                        }
                    }
                }
            }
        )
    }

    SeletorData(seletorData, dataForm, onFechar = { seletorData = false }) { iso -> dataForm = iso }

    SeletorHora(seletorHora, lHora, lMinuto, onFechar = { seletorHora = false }) { h, m -> lHora = h; lMinuto = m }
}

@Composable
private fun Segmentos(opcoes: List<Pair<String, String>>, valor: String, onSel: (String) -> Unit) {
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        opcoes.forEach { (label, v) ->
            val ativo = valor == v
            Surface(
                color = if (ativo) Primary else SurfaceAlt,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.clickable { onSel(v) }
            ) {
                Text(
                    label,
                    color = if (ativo) Color.White else TextMuted,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
                )
            }
        }
    }
}
