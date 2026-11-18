package com.diarioturmas.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diarioturmas.app.components.BotaoAdicionar
import com.diarioturmas.app.components.CabecalhoEcra
import com.diarioturmas.app.components.CampoTexto
import com.diarioturmas.app.components.ChipsUnico
import com.diarioturmas.app.components.FormDialog
import com.diarioturmas.app.components.OpcaoChip
import com.diarioturmas.app.components.Vazio
import com.diarioturmas.app.data.Curriculo
import com.diarioturmas.app.data.Repositorio
import com.diarioturmas.app.data.Turma
import com.diarioturmas.app.data.Turmas
import com.diarioturmas.app.ui.theme.SurfaceC
import com.diarioturmas.app.ui.theme.TextMain
import com.diarioturmas.app.ui.theme.TextMuted
import com.diarioturmas.app.ui.theme.Warning
import com.diarioturmas.app.ui.theme.coresCategoria

@Composable
fun TurmasScreen(onAbrir: (Turma) -> Unit) {
    var mostrarForm by remember { mutableStateOf(false) }
    var editId by remember { mutableStateOf<String?>(null) }
    var ano by remember { mutableStateOf(Curriculo.anos.first()) }
    var letra by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf(Turmas.categorias.first()) }

    fun abrir(t: Turma?) {
        editId = t?.id
        // separa "5ºA" em ano ("5º") + identificação ("A")
        val m = t?.nome?.let { Regex("""^(\d+\s*[ºo°]?)\s*(.*)$""").find(it) }
        ano = m?.groupValues?.getOrNull(1)?.replace(" ", "")?.let { a ->
            Curriculo.anos.firstOrNull { it == a } ?: Curriculo.anos.first()
        } ?: Curriculo.anos.first()
        letra = m?.groupValues?.getOrNull(2)?.trim() ?: (t?.nome ?: "")
        categoria = t?.categoria ?: Turmas.categorias.first()
        mostrarForm = true
    }

    fun guardar() {
        val nome = (ano + letra.trim()).trim()
        if (nome.isBlank()) return
        val t = Turma(editId ?: Repositorio.novoId(), nome, categoria)
        if (editId == null) Repositorio.addTurma(t) else Repositorio.updateTurma(t)
        mostrarForm = false
    }

    fun eliminar() {
        editId?.let { Repositorio.removeTurma(it) }
        mostrarForm = false
    }

    Column(Modifier.fillMaxWidth()) {
        CabecalhoEcra("Turmas", "Toca numa turma para ver os registos", acao = { BotaoAdicionar { abrir(null) } })
        Column(
            Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp).padding(bottom = 24.dp)
        ) {
            if (Repositorio.turmas.isEmpty()) {
                Vazio(Icons.Filled.Groups, "Ainda não adicionaste nenhuma turma. Clica no botão \"+\" para adicionares.")
            } else {
                Turmas.categorias.forEach { cat ->
                    val turmas = Turmas.porCategoria(cat)
                    if (turmas.isNotEmpty()) {
                        val cores = coresCategoria(cat)
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 12.dp, bottom = 10.dp)) {
                            Box(Modifier.size(10.dp).clip(RoundedCornerShape(5.dp)).background(cores.cor))
                            Spacer(Modifier.size(8.dp))
                            Text(cat, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = TextMain)
                        }
                        turmas.chunked(2).forEach { par ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                par.forEach { turma ->
                                    CartaoTurma(Modifier.weight(1f), turma, cores.cor, cores.soft, onAbrir) { abrir(it) }
                                }
                                if (par.size == 1) Spacer(Modifier.weight(1f))
                            }
                            Spacer(Modifier.size(12.dp))
                        }
                    }
                }
            }
        }
    }

    if (mostrarForm) {
        val opcoesAno = Curriculo.anos.map { OpcaoChip(it, it) }
        val opcoesCat = Turmas.categorias.map { OpcaoChip(it, it, coresCategoria(it).cor) }
        FormDialog(
            titulo = if (editId != null) "Editar turma" else "Nova turma",
            onFechar = { mostrarForm = false },
            onGuardar = { guardar() },
            onEliminar = if (editId != null) ({ eliminar() }) else null,
            confirmacaoEliminar = "Eliminar a turma ${ano}${letra.trim()}? Também vais apagar os testes, sumários, comportamento e alunos NEE ligados a ela. Esta ação não pode ser anulada."
        ) {
            ChipsUnico("Ano", opcoesAno, ano) { ano = it }
            CampoTexto("Turma", letra, { letra = it }, "Ex.: A")
            ChipsUnico("Categoria", opcoesCat, categoria) { categoria = it }
            Text(
                "Nome: " + (ano + letra.trim()),
                fontSize = 13.sp,
                color = TextMuted,
                modifier = Modifier.padding(bottom = 2.dp)
            )
            if (editId != null) {
                Text(
                    "Ao eliminar a turma, apagas também os testes, sumários, comportamento e alunos NEE ligados a ela.",
                    fontSize = 12.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun CartaoTurma(
    modifier: Modifier,
    turma: Turma,
    cor: Color,
    soft: Color,
    onAbrir: (Turma) -> Unit,
    onEditar: (Turma) -> Unit
) {
    val pendentes = Repositorio.sumarios.count { it.turmaId == turma.id && !it.verificado }
    Surface(
        modifier = modifier,
        color = SurfaceC,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp
    ) {
        Box {
            Column {
                Surface(color = soft, modifier = Modifier.fillMaxWidth().clickable { onAbrir(turma) }) {
                    Box(Modifier.fillMaxWidth().padding(vertical = 18.dp), contentAlignment = Alignment.Center) {
                        Text(turma.nome, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = cor)
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().clickable { onAbrir(turma) }.padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    if (pendentes > 0) {
                        Text("$pendentes sumário(s) por verificar", color = Warning, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    } else {
                        Text("Sem pendências", color = TextMuted, fontSize = 12.sp, modifier = Modifier.weight(1f))
                    }
                    Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                }
            }
            Box(
                Modifier.align(Alignment.TopEnd).padding(4.dp).size(30.dp).clip(RoundedCornerShape(15.dp)).clickable { onEditar(turma) },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Edit, contentDescription = "Editar turma", tint = cor, modifier = Modifier.size(16.dp))
            }
        }
    }
}
