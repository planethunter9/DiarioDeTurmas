package com.diarioturmas.app

import android.app.Application
import com.diarioturmas.app.data.Repositorio
import com.diarioturmas.app.notif.Notificacoes

class DiarioApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Repositorio.init(this)
        Notificacoes.criarCanal(this)
    }
}
