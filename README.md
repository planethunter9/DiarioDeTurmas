# Diário de Turmas

App feita à medida para uma professora do 2.º ciclo organizar num só sítio as suas turmas, apoios e coadjuvações: aulas do dia, horário, testes e trabalhos, sumários, comportamento dos alunos e as suas próprias faltas.

Existe em duas versões, com exportação e importação de dados entre elas:

| Pasta | Plataforma | Tecnologias |
|---|---|---|
| `android/` | Android | Kotlin, Jetpack Compose, Material 3 |
| `desktop/` | Windows (.exe / .msi) | Kotlin, Compose Desktop |

## Funcionalidades

- **Início:** as aulas de hoje, com a hora, o tempo, a turma e a sala.
- **Agenda:** calendário mensal com os testes e trabalhos de cada dia.
- **Horário:** horário semanal, dia a dia.
- **Turmas:** grelha com todas as turmas e as pendências de cada uma. Dentro de cada turma:
  - testes e trabalhos;
  - sumários, com marcação de verificado;
  - comportamento na sala de aula, por número de aluno.
- **Faltas:** registo das faltas da professora, com data, número de tempos e justificação.
- **Lembretes** configuráveis (hora e dias da semana) para os registos no Inovar, que se reagendam mesmo depois de reiniciar o telemóvel.
- **Exportar e importar dados** para usar o mesmo diário no telemóvel e no computador.
- Interface em **português (pt-PT)** e **tema claro**.
- Os dados ficam **guardados no próprio dispositivo**, sem conta nem internet.

## Como correr

### Versão Android
1. Abre a pasta `android/` no **Android Studio** (a que tem o `settings.gradle.kts`).
2. Espera que o *Gradle Sync* termine.
3. Carrega em **Run** com um telemóvel ligado ou um emulador.
4. Para gerar o APK: **Build → Build APK(s)**.

### Versão para PC (Windows)
1. Abre a pasta `desktop/` no **IntelliJ IDEA** ou no **Android Studio**.
2. Para correr a app:
   ```bash
   ./gradlew run
   ```
3. Para gerar o instalador:
   ```bash
   ./gradlew packageExe
   ```
   ou
   ```bash
   ./gradlew packageMsi
   ```
   O ficheiro fica em `desktop/build/compose/binaries/`.
