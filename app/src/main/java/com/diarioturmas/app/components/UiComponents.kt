package com.diarioturmas.app.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.diarioturmas.app.data.DataUtil
import com.diarioturmas.app.ui.theme.BorderC
import com.diarioturmas.app.ui.theme.Neutral
import com.diarioturmas.app.ui.theme.Primary
import com.diarioturmas.app.ui.theme.PrimarySoft
import com.diarioturmas.app.ui.theme.SurfaceAlt
import com.diarioturmas.app.ui.theme.SurfaceC
import com.diarioturmas.app.ui.theme.TextMain
import com.diarioturmas.app.ui.theme.TextMuted

// ---------- Cabeçalho de secção (ícone em círculo + título + ação) ----------
@Composable
fun CabecalhoSeccao(
    icone: ImageVector,
    cor: Color,
    soft: Color,
    titulo: String,
    accao: String? = null,
    onAccao: (() -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(top = 22.dp, bottom = 12.dp)
    ) {
        Box(Modifier.size(40.dp).clip(RoundedCornerShape(20.dp)).background(soft), contentAlignment = Alignment.Center) {
            Icon(icone, contentDescription = null, tint = cor, modifier = Modifier.size(21.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(titulo, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, color = TextMain, modifier = Modifier.weight(1f))
        if (accao != null && onAccao != null) {
            Text(
                accao,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = cor,
                modifier = Modifier.clickable { onAccao() }
            )
        }
    }
}

// ---------- Cabeçalho de ecrã ----------
@Composable
fun CabecalhoEcra(titulo: String, subtitulo: String? = null, acao: (@Composable () -> Unit)? = null) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 14.dp)
    ) {
        Column(Modifier.weight(1f)) {
            Text(titulo, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = TextMain, letterSpacing = (-0.8).sp)
            if (subtitulo != null) Text(subtitulo, fontSize = 13.sp, color = TextMuted)
        }
        if (acao != null) acao()
    }
}

@Composable
fun BotaoAdicionar(onClick: () -> Unit) {
    Surface(
        color = Primary,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 4.dp,
        modifier = Modifier.size(48.dp).clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.Add, contentDescription = "Adicionar", tint = Color.White)
        }
    }
}

// ---------- Cartão ----------
@Composable
fun Cartao(
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val base = Modifier
        .fillMaxWidth()
        .padding(bottom = 12.dp)
    val clic = if (onClick != null) base.clickable { onClick() } else base
    Surface(
        modifier = clic,
        color = SurfaceC,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 3.dp
    ) {
        Column(Modifier.padding(18.dp), content = content)
    }
}

// ---------- Badge ----------
@Composable
fun Badge(texto: String, cor: Color, soft: Color) {
    Surface(color = soft, shape = RoundedCornerShape(50)) {
        Text(
            texto,
            color = cor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 5.dp)
        )
    }
}

// ---------- Chips ----------
data class OpcaoChip(val label: String, val valor: String, val cor: Color? = null)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipsUnico(etiqueta: String?, opcoes: List<OpcaoChip>, valor: String, onSelecionar: (String) -> Unit) {
    Column(Modifier.padding(bottom = 14.dp)) {
        if (etiqueta != null) Rotulo(etiqueta)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            opcoes.forEach { o ->
                val cor = o.cor ?: Primary
                FilterChip(
                    selected = valor == o.valor,
                    onClick = { onSelecionar(o.valor) },
                    label = { Text(o.label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = cor,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipsDias(etiqueta: String, selecionados: List<Int>, onToggle: (Int) -> Unit) {
    Column(Modifier.padding(bottom = 14.dp)) {
        Rotulo(etiqueta)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DataUtil.diasSemana.forEach { (label, valor) ->
                FilterChip(
                    selected = selecionados.contains(valor),
                    onClick = { onToggle(valor) },
                    label = { Text(label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Primary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
    }
}

// ---------- Campo de texto ----------
@Composable
fun CampoTexto(
    etiqueta: String,
    valor: String,
    onChange: (String) -> Unit,
    placeholder: String = "",
    multiline: Boolean = false,
    teclado: KeyboardType = KeyboardType.Text
) {
    Column(Modifier.padding(bottom = 14.dp)) {
        Rotulo(etiqueta)
        OutlinedTextField(
            value = valor,
            onValueChange = onChange,
            placeholder = { if (placeholder.isNotBlank()) Text(placeholder, color = TextMuted) },
            singleLine = !multiline,
            minLines = if (multiline) 3 else 1,
            keyboardOptions = KeyboardOptions(keyboardType = teclado),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ---------- Seletor de lista (dropdown) ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeletorLista(etiqueta: String, opcoes: List<String>, valor: String, placeholder: String = "", onSelecionar: (String) -> Unit) {
    var aberto by remember { mutableStateOf(false) }
    Column(Modifier.padding(bottom = 14.dp)) {
        Rotulo(etiqueta)
        ExposedDropdownMenuBox(expanded = aberto, onExpandedChange = { aberto = !aberto }) {
            OutlinedTextField(
                value = valor,
                onValueChange = {},
                readOnly = true,
                placeholder = { if (placeholder.isNotBlank()) Text(placeholder, color = TextMuted) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = aberto) },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = aberto, onDismissRequest = { aberto = false }) {
                opcoes.forEach { op ->
                    DropdownMenuItem(text = { Text(op) }, onClick = { onSelecionar(op); aberto = false })
                }
            }
        }
    }
}

// ---------- Linha "toque para escolher" ----------
@Composable
fun LinhaSelecao(etiqueta: String, valor: String, icone: ImageVector, onClick: () -> Unit) {
    Column(Modifier.padding(bottom = 14.dp)) {
        Rotulo(etiqueta)
        Surface(
            color = SurfaceC,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, BorderC),
            modifier = Modifier.fillMaxWidth().clickable { onClick() }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 14.dp)
            ) {
                Text(valor, fontSize = 16.sp, color = TextMain, modifier = Modifier.weight(1f))
                Icon(icone, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp))
            }
        }
    }
}

// ---------- Estado vazio ----------
@Composable
fun Vazio(icone: ImageVector, texto: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(vertical = 44.dp, horizontal = 30.dp)
    ) {
        Box(
            Modifier.size(72.dp).clip(RoundedCornerShape(36.dp)).background(SurfaceAlt),
            contentAlignment = Alignment.Center
        ) {
            Icon(icone, contentDescription = null, tint = Neutral, modifier = Modifier.size(32.dp))
        }
        Spacer(Modifier.size(14.dp))
        Text(texto, color = TextMuted, fontSize = 15.sp, textAlign = TextAlign.Center)
    }
}

@Composable
private fun Rotulo(texto: String) {
    Text(
        texto,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = TextMuted,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

// ---------- Modal de formulário ----------
@Composable
fun FormDialog(
    titulo: String,
    onFechar: () -> Unit,
    onGuardar: () -> Unit,
    onEliminar: (() -> Unit)? = null,
    guardarLabel: String = "Guardar",
    confirmacaoEliminar: String = "Tens a certeza que queres eliminar? Esta ação não pode ser anulada.",
    content: @Composable ColumnScope.() -> Unit
) {
    var confirmar by remember { mutableStateOf(false) }
    Dialog(onDismissRequest = onFechar, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = SurfaceC,
            modifier = Modifier.fillMaxWidth(0.92f)
        ) {
            Column(Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text(titulo, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, color = TextMain, modifier = Modifier.weight(1f))
                    IconButton(onClick = onFechar) {
                        Icon(Icons.Filled.Close, contentDescription = "Fechar", tint = TextMuted)
                    }
                }
                Spacer(Modifier.size(8.dp))
                Column(Modifier.heightIn(max = 460.dp).verticalScroll(rememberScrollState())) {
                    content()
                }
                Spacer(Modifier.size(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    if (onEliminar != null) {
                        TextButton(onClick = { confirmar = true }) {
                            Text("Eliminar", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(8.dp))
                    }
                    Button(
                        onClick = onGuardar,
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Text(guardarLabel, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }

    if (confirmar && onEliminar != null) {
        AlertDialog(
            onDismissRequest = { confirmar = false },
            confirmButton = {
                TextButton(onClick = { confirmar = false; onEliminar() }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { confirmar = false }) { Text("Cancelar") } },
            title = { Text("Eliminar") },
            text = { Text(confirmacaoEliminar) }
        )
    }
}

// ---------- Modal ver / editar ----------
@Composable
fun DetalheDialog(
    titulo: String,
    novo: Boolean,
    onFechar: () -> Unit,
    onGuardar: () -> Unit,
    onEliminar: (() -> Unit)? = null,
    confirmacaoEliminar: String = "Tens a certeza que queres eliminar? Esta ação não pode ser anulada.",
    infos: @Composable ColumnScope.() -> Unit,
    form: @Composable ColumnScope.() -> Unit
) {
    var editar by remember { mutableStateOf(novo) }
    var confirmar by remember { mutableStateOf(false) }
    Dialog(onDismissRequest = onFechar, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(shape = RoundedCornerShape(28.dp), color = SurfaceC, modifier = Modifier.fillMaxWidth(0.92f)) {
            Column(Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text(titulo, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, color = TextMain, modifier = Modifier.weight(1f))
                    IconButton(onClick = onFechar) {
                        Icon(Icons.Filled.Close, contentDescription = "Fechar", tint = TextMuted)
                    }
                }
                Spacer(Modifier.size(8.dp))
                Column(Modifier.heightIn(max = 460.dp).verticalScroll(rememberScrollState())) {
                    if (editar) form() else infos()
                }
                Spacer(Modifier.size(12.dp))
                if (editar) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        if (onEliminar != null) {
                            TextButton(onClick = { confirmar = true }) {
                                Text("Eliminar", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(Modifier.weight(1f))
                        TextButton(onClick = onFechar) {
                            Text("Cancelar", color = TextMuted, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(4.dp))
                        Button(
                            onClick = onGuardar,
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            modifier = Modifier.heightIn(min = 46.dp)
                        ) {
                            Text("Guardar", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                } else {
                    Button(
                        onClick = { editar = true },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Editar", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }

    if (confirmar && onEliminar != null) {
        AlertDialog(
            onDismissRequest = { confirmar = false },
            confirmButton = {
                TextButton(onClick = { confirmar = false; onEliminar() }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { confirmar = false }) { Text("Cancelar") } },
            title = { Text("Eliminar") },
            text = { Text(confirmacaoEliminar) }
        )
    }
}

// Linha de informação (rótulo + valor) para o modo "ver"
@Composable
fun LinhaInfo(rotulo: String, valor: String) {
    if (valor.isBlank()) return
    Column(Modifier.padding(bottom = 12.dp)) {
        Text(rotulo, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMuted)
        Text(valor, fontSize = 15.sp, color = TextMain)
    }
}

// ---------- Seletor de data ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeletorData(
    aberto: Boolean,
    isoInicial: String,
    onFechar: () -> Unit,
    onEscolher: (String) -> Unit
) {
    if (!aberto) return
    val state = rememberDatePickerState(initialSelectedDateMillis = DataUtil.isoParaMillisUtc(isoInicial))
    DatePickerDialog(
        onDismissRequest = onFechar,
        confirmButton = {
            TextButton(onClick = {
                state.selectedDateMillis?.let { onEscolher(DataUtil.millisUtcParaIso(it)) }
                onFechar()
            }) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onFechar) { Text("Cancelar") } }
    ) {
        DatePicker(state = state)
    }
}

// ---------- Seletor de hora ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeletorHora(
    aberto: Boolean,
    horaInicial: Int,
    minutoInicial: Int,
    onFechar: () -> Unit,
    onEscolher: (Int, Int) -> Unit
) {
    if (!aberto) return
    val state = rememberTimePickerState(initialHour = horaInicial, initialMinute = minutoInicial, is24Hour = true)
    Dialog(onDismissRequest = onFechar) {
        Surface(shape = RoundedCornerShape(28.dp), color = SurfaceC) {
            Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Escolher hora", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextMain, modifier = Modifier.padding(bottom = 12.dp))
                TimePicker(state = state)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onFechar) { Text("Cancelar") }
                    TextButton(onClick = {
                        onEscolher(state.hour, state.minute)
                        onFechar()
                    }) { Text("OK") }
                }
            }
        }
    }
}
