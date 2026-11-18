package com.diarioturmas.app.notif

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.diarioturmas.app.data.Repositorio

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra("id", -1)
        if (id < 0) return

        Repositorio.initSeNecessario(context)
        val l = Repositorio.lembretes.firstOrNull { it.id == id } ?: return
        if (!l.ativo) return

        Notificacoes.criarCanal(context)
        val texto = l.mensagem.ifBlank { "Não te esqueças de fazer os registos no Inovar." }
        Notificacoes.mostrar(context, id, l.titulo, texto)

        // Reagenda a próxima ocorrência
        AgendadorLembretes.agendar(context, l)
    }
}
