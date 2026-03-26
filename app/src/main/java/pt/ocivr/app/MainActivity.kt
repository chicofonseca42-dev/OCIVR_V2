package pt.ocivr.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val sheetUrl      = "https://docs.google.com/spreadsheets/d/e/2PACX-1vSW12c1Df2sJq9F1vyWfBlnbVzcYhLBuzIG9CruJiTjwCxWaHO8UOYj11KX3hnGRn-yejD-r5dbe_X2/pub?gid=0&single=true&output=csv"
    private val sheetUrlMovel = "https://docs.google.com/spreadsheets/d/e/2PACX-1vTOTDRMPpRIaj-TZCXRtO_2bZhnqdFxyua9ZvKdlG5tZ12PYHKxXfWcxKItuM4HUxJMl4mqjRaol7i_/pub?gid=1608582643&single=true&output=csv"
    private val sheetUrlSiresp = "https://docs.google.com/spreadsheets/d/e/2PACX-1vTOTDRMPpRIaj-TZCXRtO_2bZhnqdFxyua9ZvKdlG5tZ12PYHKxXfWcxKItuM4HUxJMl4mqjRaol7i_/pub?gid=293131231&single=true&output=csv"
    private val sheetUrlFixa  = "https://docs.google.com/spreadsheets/d/e/2PACX-1vTOTDRMPpRIaj-TZCXRtO_2bZhnqdFxyua9ZvKdlG5tZ12PYHKxXfWcxKItuM4HUxJMl4mqjRaol7i_/pub?gid=836241093&single=true&output=csv"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 📅 Calendário dinâmico
        val cal = Calendar.getInstance()
        val mes = SimpleDateFormat("MMM", Locale("pt")).format(cal.time).uppercase()
        val dia = SimpleDateFormat("dd", Locale("pt")).format(cal.time)
        findViewById<TextView>(R.id.txtMesIcone).text = mes
        findViewById<TextView>(R.id.txtDiaIcone).text = dia

        // 🔄 Atualizar bases em background
        atualizarBasesBackground()


        // ⚙️ Configurações
        click(R.id.btnConfiguracoes) {
            startActivity(
                Intent(
                    this,
                    ConfiguracoesActivity::class.java
                )
            )
        }

        // 📅 Semana Atual
        click(R.id.cardSemanaAtual) {
            startActivity(
                Intent(
                    this,
                    AgendamentosTesteActivity::class.java
                )
            )
        }

        // 🌐 Todos os Sites
        click(R.id.cardTodosSites) { startActivity(Intent(this, TodosSitesActivity::class.java)) }

        // 📋 Cadastro
        click(R.id.cardCadastro) { startActivity(Intent(this, CadastroActivity::class.java)) }

        // 🔗 Circuitos
        click(R.id.cardCircuitos) { startActivity(Intent(this, PesquisaActivity::class.java)) }

        // 🛡️ Prevenção
        click(R.id.cardPrevencao) { startActivity(Intent(this, PrevencaoActivity::class.java)) }

        // 💻 CPL Web
        click(R.id.cardCplWeb) { abrirUrl("http://10.18.25.100:91/Index.aspx") }

        // 📡 RETA/SGA
        click(R.id.cardRetaSga) { abrirUrl("http://sga.telecom.pt/cgi-bin/sgaffm.cgi/SDA1?&SZ=&ALM=Lista+de+Alarmes") }

        // 📱 SouMEO
        click(R.id.cardSouMeo) {
            val intent = packageManager.getLaunchIntentForPackage("pt.meo.and.soumeo")
            if (intent != null) startActivity(intent)
            else abrirUrl("https://soumeo.meo.pt")
        }

        // 📈 Agendamentos
        click(R.id.cardAgendamentos) { abrirUrl("https://docs.google.com/spreadsheets/d/1t3cZeesYG4JSvl9hoiTvMZWK5lEPpd1IHHKnS0-ZrsQ/edit#gid=1804031589") }
    }


    // ─────────────────────────────────────────
    // Atualiza as 4 bases em background
    // ─────────────────────────────────────────
    private fun atualizarBasesBackground() {
        val bases = listOf(
            sheetUrl       to "base_cache.csv",
            sheetUrlMovel  to "rede_movel_cache.csv",
            sheetUrlSiresp to "rede_siresp_cache.csv",
            sheetUrlFixa   to "rede_fixa_cache.csv"
        )
        val client = OkHttpClient()
        var concluido = 0
        for (base in bases) {
            val request = Request.Builder().url(base.first).build()
            client.newCall(request).enqueue(object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: java.io.IOException) { }
                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    val body = response.body?.string()
                    if (body != null) openFileOutput(base.second, MODE_PRIVATE).use { it.write(body.toByteArray()) }
                    concluido++
                    if (concluido == bases.size) {
                        val data = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                        getSharedPreferences("config", MODE_PRIVATE).edit().putString("ultima_atualizacao", data).apply()
                    }
                }
            })
        }
    }

    // ─────────────────────────────────────────
    // Clique com vibração
    // ─────────────────────────────────────────
    private fun click(id: Int, acao: () -> Unit) {
        findViewById<View>(id).setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            acao()
        }
    }

    // ─────────────────────────────────────────
    // Abrir URL no browser
    // ─────────────────────────────────────────
    private fun abrirUrl(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}