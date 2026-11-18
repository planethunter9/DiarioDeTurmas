package com.diarioturmas.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diarioturmas.app.data.Repositorio
import com.diarioturmas.app.data.Turma
import com.diarioturmas.app.notif.AgendadorLembretes
import com.diarioturmas.app.screens.CalendarioScreen
import com.diarioturmas.app.screens.DefinicoesScreen
import com.diarioturmas.app.screens.FaltasScreen
import com.diarioturmas.app.screens.HorarioScreen
import com.diarioturmas.app.screens.InicioScreen
import com.diarioturmas.app.screens.TurmaDetalheScreen
import com.diarioturmas.app.screens.TurmasScreen
import com.diarioturmas.app.ui.theme.Bg
import com.diarioturmas.app.ui.theme.DiarioTurmasTheme
import com.diarioturmas.app.ui.theme.Primary
import com.diarioturmas.app.ui.theme.PrimarySoft
import com.diarioturmas.app.ui.theme.SurfaceC
import com.diarioturmas.app.ui.theme.TextMuted

private data class Aba(val chave: String, val label: String, val icone: ImageVector)

private val ABAS = listOf(
    Aba("inicio", "Início", Icons.Filled.Home),
    Aba("calendario", "Agenda", Icons.Filled.CalendarMonth),
    Aba("horario", "Horário", Icons.Filled.Schedule),
    Aba("turmas", "Turmas", Icons.Filled.Groups),
    Aba("faltas", "Faltas", Icons.Filled.EventBusy),
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DiarioTurmasTheme {
                App()
            }
        }
    }
}

@Composable
private fun App() {
    val context = LocalContext.current
    var tab by remember { mutableStateOf("inicio") }
    var turmaAberta by remember { mutableStateOf<Turma?>(null) }
    var mostrarDefinicoes by remember { mutableStateOf(false) }

    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    val pedirPermissao: () -> Unit = {
        if (Build.VERSION.SDK_INT >= 33) {
            permLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { it.write(Repositorio.exportarJson().toByteArray()) }
                Toast.makeText(context, "Dados exportados.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Não foi possível exportar.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                val texto = context.contentResolver.openInputStream(uri)?.use { it.readBytes().decodeToString() }
                if (texto != null && Repositorio.importarJson(texto)) {
                    Toast.makeText(context, "Dados importados.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Ficheiro inválido.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Não foi possível importar.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        pedirPermissao()
        AgendadorLembretes.agendarTodos(context)
    }

    BackHandler(enabled = mostrarDefinicoes || tab != "inicio" || turmaAberta != null) {
        when {
            mostrarDefinicoes -> mostrarDefinicoes = false
            turmaAberta != null -> turmaAberta = null
            else -> tab = "inicio"
        }
    }

    Scaffold(
        containerColor = Bg,
        bottomBar = {
            MenuFlutuante(tab) { chave ->
                mostrarDefinicoes = false
                if (chave == "turmas") turmaAberta = null
                tab = chave
            }
        }
    ) { innerPadding ->
        Box(Modifier.fillMaxSize().padding(innerPadding)) {
            if (mostrarDefinicoes) {
                DefinicoesScreen(
                    onVoltar = { mostrarDefinicoes = false },
                    onExportar = { exportLauncher.launch("diario-turmas.json") },
                    onImportar = { importLauncher.launch(arrayOf("*/*")) }
                )
            } else when (tab) {
                "inicio" -> InicioScreen(
                    onVerTurmas = { turmaAberta = null; tab = "turmas" },
                    onVerHorario = { tab = "horario" },
                    onVerCalendario = { tab = "calendario" },
                    onAbrirTurma = { t -> turmaAberta = t; tab = "turmas" },
                    onDefinicoes = { mostrarDefinicoes = true }
                )
                "calendario" -> CalendarioScreen()
                "horario" -> HorarioScreen()
                "turmas" -> {
                    val aberta = turmaAberta
                    if (aberta != null) TurmaDetalheScreen(aberta, onVoltar = { turmaAberta = null }, onLembreteMudou = { pedirPermissao(); AgendadorLembretes.agendarTodos(context) })
                    else TurmasScreen(onAbrir = { turmaAberta = it })
                }
                "faltas" -> FaltasScreen()
            }
        }
    }
}

@Composable
private fun MenuFlutuante(tab: String, onSelect: (String) -> Unit) {
    Box(Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 16.dp, vertical = 10.dp)) {
        Surface(
            color = SurfaceC,
            shape = RoundedCornerShape(24.dp),
            shadowElevation = 10.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(Modifier.fillMaxWidth().padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                ABAS.forEach { aba ->
                    val sel = tab == aba.chave
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 2.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (sel) PrimarySoft else Color.Transparent)
                            .clickable { onSelect(aba.chave) }
                            .padding(vertical = 7.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            aba.icone,
                            contentDescription = aba.label,
                            tint = if (sel) Primary else TextMuted,
                            modifier = Modifier.padding(bottom = 3.dp)
                        )
                        Text(
                            aba.label,
                            fontSize = 10.sp,
                            color = if (sel) Primary else TextMuted,
                            fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
