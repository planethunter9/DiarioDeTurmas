package com.diarioturmas.app.notif

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.diarioturmas.app.R

object Notificacoes {
    const val CANAL = "lembretes"

    fun criarCanal(context: Context) {
        val canal = NotificationChannel(
            CANAL,
            "Lembretes",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Avisos para os registos no Inovar"
            enableVibration(true)
        }
        context.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(canal)
    }

    fun mostrar(context: Context, id: Int, titulo: String, texto: String) {
        val abrir = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pi = PendingIntent.getActivity(
            context, id, abrir,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val n = NotificationCompat.Builder(context, CANAL)
            .setSmallIcon(R.drawable.ic_stat_notify)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setStyle(NotificationCompat.BigTextStyle().bigText(texto))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pi)
            .build()
        try {
            NotificationManagerCompat.from(context).notify(id, n)
        } catch (e: SecurityException) {
            // Sem permissão de notificações — nada a fazer.
        }
    }
}
