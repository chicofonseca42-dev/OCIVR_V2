package pt.ocivr.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 📅 Calendário dinâmico
        val cal = Calendar.getInstance()
        val mes = SimpleDateFormat("MMM", Locale("pt")).format(cal.time).uppercase()
        val dia = SimpleDateFormat("dd", Locale("pt")).format(cal.time)
        findViewById<TextView>(R.id.txtMesIcone).text = mes
        findViewById<TextView>(R.id.txtDiaIcone).text = dia

        // ⚙️ Configurações
        click(R.id.btnConfiguracoes) { startActivity(Intent(this, ConfiguracoesActivity::class.java)) }

        // 📅 Semana Atual
        click(R.id.cardSemanaAtual) { startActivity(Intent(this, AgendamentosTesteActivity::class.java)) }

        // 🌐 Todos os Sites
        click(R.id.cardTodosSites) { startActivity(Intent(this, TodosSitesActivity::class.java)) }

        // 📋 Cadastro
        click(R.id.cardCadastro) { startActivity(Intent(this, CadastroActivity::class.java)) }

        // 🔗 Circuitos
        click(R.id.cardCircuitos) { startActivity(Intent(this, PesquisaActivity::class.java)) }

        // 🛡️ Prevenção
        click(R.id.cardPrevencao) { abrirUrl("https://docs.google.com/spreadsheets/d/1iRu6GlZ9GNlipGW2NXt17Qea-TTRKlssql5BOfWd-J4/edit?gid=1672609704#gid=1672609704") }

        // 💻 CPL Web
        click(R.id.cardCplWeb) { abrirUrl("https://cplweb.meo.pt") }

        // 📡 RETA/SGA
        click(R.id.cardRetaSga) { abrirUrl("https://reta.meo.pt") }

        // 📱 SouMEO
        click(R.id.cardSouMeo) {
            val intent = packageManager.getLaunchIntentForPackage("pt.meo.and.soumeo")
            if (intent != null) startActivity(intent)
            else abrirUrl("https://soumeo.meo.pt")
        }

        // 📈 Agendamentos
        click(R.id.cardAgendamentos) { abrirUrl("https://docs.google.com/spreadsheets/d/1t3cZeesYG4JSvl9hoiTvMZWK5lEPpd1IHHKnS0-ZrsQ/edit?gid=1051320260#gid=1051320260") }
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