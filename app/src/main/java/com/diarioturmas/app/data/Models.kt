package com.diarioturmas.app.data

// Lembrete: notificação repetida (diária ou por dias da semana).
// dias usa as constantes de Calendar: 1=Domingo ... 7=Sábado. Vazio ou 7 dias = todos os dias.
data class Lembrete(
    val id: Int,
    val titulo: String,
    val mensagem: String = "",
    val hora: Int = 8,
    val minuto: Int = 0,
    val dias: List<Int> = emptyList(),
    val ativo: Boolean = true,
    val turmaId: String = ""
)

data class Teste(
    val id: String,
    val turmaId: String,
    val tipo: String,        // Teste | Trabalho | Ficha
    val titulo: String,
    val data: String,        // ISO yyyy-MM-dd
    val notas: String = ""
)

data class Sumario(
    val id: String,
    val turmaId: String,
    val data: String,
    val licao: String = "",
    val descricao: String,
    val verificado: Boolean = false
)

data class Comportamento(
    val id: String,
    val turmaId: String,
    val numeroAluno: String,
    val tipo: String,        // positivo | negativo | nota
    val data: String,
    val descricao: String
)

data class Falta(
    val id: String,
    val data: String,
    val tempos: Int = 1,
    val tipo: String = "justificada",   // justificada | injustificada
    val motivo: String = ""
)

data class AlunoNEE(
    val id: String,
    val turmaId: String,
    val nome: String,
    val numero: String = "",
    val observacoes: String = ""
)
