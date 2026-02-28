package pt.ocivr.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class PrevencaoActivity : AppCompatActivity() {

    private val csvUrl = "https://docs.google.com/spreadsheets/d/e/2PACX-1vSdNR3aU3EKJ2dc9buUjv0bqTbNiMwQIiXcgeIBsTf3Kfz5u3KJ6pEHlQ_vCpN9yNO4JCELoFIfFVzD/pub?gid=1444605401&single=true&output=csv"
    private val urlSheet = "https://docs.google.com/spreadsheets/d/1iRu6GlZ9GNlipGW2NXt17Qea-TTRKlssql5BOfWd-J4"
    private val PREFS = "prevencao_cache"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prevencao)

        // ── Mostrar cache imediatamente ──
        mostrarCache()

        // ── Atualizar em background ──
        carregarDados()

        findViewById<View>(R.id.btnVerSheet).setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(urlSheet)))
        }

        findViewById<View>(R.id.btnAtualizar).setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            carregarDados()
        }
    }

    // ─────────────────────────────────────────
    // Mostra os dados guardados em cache
    // ─────────────────────────────────────────
    private fun mostrarCache() {
        val prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val nome   = prefs.getString("nome", null)
        val semana = prefs.getString("semana", null)

        if (nome != null && semana != null) {
            findViewById<TextView>(R.id.txtNomePrevencao).text = nome
            findViewById<TextView>(R.id.txtSemana).text = semana
            findViewById<TextView>(R.id.txtEstado).text = "✓ Prevenção encontrada"
        } else {
            findViewById<TextView>(R.id.txtEstado).text = "A carregar..."
        }
    }

    // ─────────────────────────────────────────
    // Guarda os dados em cache
    // ─────────────────────────────────────────
    private fun guardarCache(nome: String, semana: String) {
        getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit {
            putString("nome", nome)
            putString("semana", semana)
        }
    }

    // ─────────────────────────────────────────
    // Carrega dados da Google Sheet
    // ─────────────────────────────────────────
    private fun carregarDados() {
        val txtNome   = findViewById<TextView>(R.id.txtNomePrevencao)
        val txtSemana = findViewById<TextView>(R.id.txtSemana)
        val txtEstado = findViewById<TextView>(R.id.txtEstado)

        txtEstado.text = "A atualizar..."

        val client = OkHttpClient()
        val request = Request.Builder().url(csvUrl).build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                runOnUiThread {
                    // Se falhar mantém a cache e avisa
                    txtEstado.text = "⚠️ Sem ligação — a mostrar dados guardados"
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val body = response.body?.string() ?: return
                val linhas = body.lines()

                val semanaRaw = linhas.getOrNull(0)
                    ?.split(",")?.getOrNull(0)
                    ?.trim()?.removePrefix("\"")?.removeSuffix("\"")
                    ?: "--"

                val semanaFormatada = formatarSemana(semanaRaw)

                var nomePrevencao = "--"
                for (i in 1 until linhas.size) {
                    val cols = parseCsvLine(linhas[i])
                    val colC = cols.getOrNull(2)?.trim() ?: ""
                    if (colC.equals("x", ignoreCase = true)) {
                        nomePrevencao = cols.getOrNull(1)?.trim() ?: "--"
                        break
                    }
                }

                // Guardar em cache
                guardarCache(nomePrevencao, semanaFormatada)

                runOnUiThread {
                    txtSemana.text = semanaFormatada
                    txtNome.text   = nomePrevencao
                    txtEstado.text = if (nomePrevencao != "--") "✓ Prevenção encontrada" else "Sem prevenção definida"
                }
            }
        })
    }

    private fun formatarSemana(raw: String): String {
        return try {
            val partes = raw.removePrefix("Semana:").trim()
            val idx = partes.indexOf(" de ")
            if (idx < 0) return raw
            val numSemana = partes.substring(0, idx).trim()
            val datas = partes.substring(idx + 4).trim()
            val dataPartes = datas.split(" a ")
            val inicio = formatarData(dataPartes.getOrNull(0)?.trim() ?: "")
            val fim    = formatarData(dataPartes.getOrNull(1)?.trim() ?: "")
            "Semana $numSemana\n$inicio  →  $fim"
        } catch (e: Exception) {
            raw
        }
    }

    private fun formatarData(data: String): String {
        return try {
            val partes = data.split("/")
            val dia = partes[0].padStart(2, '0')
            val mes = when (partes[1].trim()) {
                "1",  "01" -> "Jan"
                "2",  "02" -> "Fev"
                "3",  "03" -> "Mar"
                "4",  "04" -> "Abr"
                "5",  "05" -> "Mai"
                "6",  "06" -> "Jun"
                "7",  "07" -> "Jul"
                "8",  "08" -> "Ago"
                "9",  "09" -> "Set"
                "10"       -> "Out"
                "11"       -> "Nov"
                "12"       -> "Dez"
                else -> partes[1]
            }
            "$dia $mes"
        } catch (e: Exception) {
            data
        }
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        var current = StringBuilder()
        var inQuotes = false
        for (ch in line) {
            when {
                ch == '"' -> inQuotes = !inQuotes
                ch == ',' && !inQuotes -> { result.add(current.toString()); current = StringBuilder() }
                else -> current.append(ch)
            }
        }
        result.add(current.toString())
        return result
    }
}