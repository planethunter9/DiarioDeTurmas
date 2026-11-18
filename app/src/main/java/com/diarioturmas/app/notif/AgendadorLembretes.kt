package com.diarioturmas.app.notif

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.diarioturmas.app.data.Lembrete
import com.diarioturmas.app.data.Repositorio
import java.util.Calendar

// Agenda os lembretes. Usa setAndAllowWhileIdle (inexato, mas dispara mesmo em
// modo de poupança) — assim não é preciso a permissão de "alarmes exatos".
// Cada lembrete tem 1 alarme para a próxima ocorrência; ao disparar, reagenda-se.
object AgendadorLembretes {

    fun agendarTodos(context: Context) {
        Repositorio.lembretes.filter { it.ativo }.forEach { agendar(context, it) }
    }

    fun agendar(context: Context, l: Lembrete) {
        val am = context.getSystemService(AlarmManager::class.java) ?: return
        val prox = proximaOcorrencia(l) ?: return
        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, prox, pending(context, l.id))
    }

    fun cancelar(context: Context, id: Int) {
        val am = context.getSystemService(AlarmManager::class.java) ?: return
        am.cancel(pending(context, id))
    }

    private fun pending(context: Context, id: Int): PendingIntent {
        val i = Intent(context, ReminderReceiver::class.java).putExtra("id", id)
        return PendingIntent.getBroadcast(
            context, id, i,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    // Próximo instante futuro cujo dia da semana está nos dias do lembrete.
    fun proximaOcorrencia(l: Lembrete): Long? {
        val agora = System.currentTimeMillis()
        val diasAlvo = if (l.dias.isEmpty() || l.dias.size >= 7) (1..7).toList() else l.dias
        for (offset in 0..7) {
            val c = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, offset)
                set(Calendar.HOUR_OF_DAY, l.hora)
                set(Calendar.MINUTE, l.minuto)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            if (c.timeInMillis <= agora) continue
            if (c.get(Calendar.DAY_OF_WEEK) in diasAlvo) return c.timeInMillis
        }
        return null
    }
}
