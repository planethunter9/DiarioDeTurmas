package com.diarioturmas.app.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diarioturmas.app.components.CabecalhoSeccao
import com.diarioturmas.app.components.Cartao
import com.diarioturmas.app.ui.theme.Neutral
import com.diarioturmas.app.ui.theme.NeutralSoft
import com.diarioturmas.app.ui.theme.Primary
import com.diarioturmas.app.ui.theme.PrimarySoft
import com.diarioturmas.app.ui.theme.TextMain
import com.diarioturmas.app.ui.theme.TextMuted

@Composable
fun DefinicoesScreen(onVoltar: () -> Unit, onExportar: () -> Unit, onImportar: () -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(start = 14.dp, end = 18.dp, top = 12.dp, bottom = 10.dp)
        ) {
            Box(Modifier.size(40.dp).clip(RoundedCornerShape(20.dp)).clickable { onVoltar() }, contentAlignment = Alignment.Center) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = TextMain)
            }
            Spacer(Modifier.size(6.dp))
            Text("Definições", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = TextMain)
        }
        Column(
            Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp).padding(bottom = 24.dp)
        ) {
            CabecalhoSeccao(Icons.Filled.FileUpload, Primary, PrimarySoft, "Cópia de segurança")
            Cartao {
                Text(
                    "Guarda ou repõe todos os dados num ficheiro, para passares entre o telemóvel e o PC.",
                    color = TextMuted, fontSize = 13.sp
                )
                Spacer(Modifier.size(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    BotaoDados(Modifier.weight(1f), "Exportar", Icons.Filled.FileUpload, onExportar)
                    BotaoDados(Modifier.weight(1f), "Importar", Icons.Filled.FileDownload, onImportar)
                }
            }
        }
    }
}

@Composable
private fun BotaoDados(modifier: Modifier, texto: String, icone: ImageVector, onClick: () -> Unit) {
    Surface(
        modifier = modifier.clickable { onClick() },
        color = PrimarySoft,
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
        ) {
            Icon(icone, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
            Spacer(Modifier.size(6.dp))
            Text(texto, color = Primary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}
