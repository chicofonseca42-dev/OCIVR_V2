package pt.ocivr.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ConfiguracoesActivity : AppCompatActivity() {

    private val nomeFicheiroCache = "base_cache.csv"
    private val sheetUrl = "https://docs.google.com/spreadsheets/d/e/2PACX-1vSW12c1Df2sJq9F1vyWfBlnbVzcYhLBuzIG9CruJiTjwCxWaHO8UOYj11KX3hnGRn-yejD-r5dbe_X2/pub?gid=0&single=true&output=csv"
    private val sheetUrlMovel = "https://docs.google.com/spreadsheets/d/e/2PACX-1vTOTDRMPpRIaj-TZCXRtO_2bZhnqdFxyua9ZvKdlG5tZ12PYHKxXfWcxKItuM4HUxJMl4mqjRaol7i_/pub?gid=1608582643&single=true&output=csv"
    private val ficheiroMovel = "rede_movel_cache.csv"
    private val sheetUrlSiresp = "https://docs.google.com/spreadsheets/d/e/2PACX-1vTOTDRMPpRIaj-TZCXRtO_2bZhnqdFxyua9ZvKdlG5tZ12PYHKxXfWcxKItuM4HUxJMl4mqjRaol7i_/pub?gid=293131231&single=true&output=csv"
    private val ficheiroSiresp = "rede_siresp_cache.csv"
    private val sheetUrlFixa = "https://docs.google.com/spreadsheets/d/e/2PACX-1vTOTDRMPpRIaj-TZCXRtO_2bZhnqdFxyua9ZvKdlG5tZ12PYHKxXfWcxKItuM4HUxJMl4mqjRaol7i_/pub?gid=836241093&single=true&output=csv"
    private val ficheiroFixa = "rede_fixa_cache.csv"

    private lateinit var txtUltimaAtualizacao: TextView
    private lateinit var txtEstadoBase: TextView
    private lateinit var txtTotalRegistos: TextView
    private lateinit var txtCircuitos: TextView
    private lateinit var txtMovel: TextView
    private lateinit var txtSiresp: TextView
    private lateinit var txtFixa: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracoes)

        txtUltimaAtualizacao = findViewById(R.id.txtUltimaAtualizacao)
        txtEstadoBase        = findViewById(R.id.txtEstadoBase)
        txtTotalRegistos     = findViewById(R.id.txtTotalRegistos)
        txtCircuitos         = findViewById(R.id.txtCircuitos)
        txtMovel             = findViewById(R.id.txtMovel)
        txtSiresp            = findViewById(R.id.txtSiresp)
        txtFixa              = findViewById(R.id.txtFixa)

        atualizarTextoData()
        verificarEstadoBase()

        click(R.id.btnAtualizarBase) { atualizarBase() }

        click(R.id.btnLimparCache) {
            deleteFile(nomeFicheiroCache)
            deleteFile(ficheiroMovel)
            deleteFile(ficheiroSiresp)
            deleteFile(ficheiroFixa)
            Toast.makeText(this, getString(R.string.todas_bases_apagadas), Toast.LENGTH_SHORT).show()
            verificarEstadoBase()
        }

        // 📊 NETQ (substituiu o SouMEO)
        click(R.id.btnSouMeo) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://iamoss.telecom.pt/idp/login")))
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

    private fun atualizarBase() {
        val client = OkHttpClient()
        val bases = listOf(
            sheetUrl       to nomeFicheiroCache,
            sheetUrlMovel  to ficheiroMovel,
            sheetUrlSiresp to ficheiroSiresp,
            sheetUrlFixa   to ficheiroFixa
        )
        var concluido = 0
        for (base in bases) {
            val request = Request.Builder().url(base.first).build()
            client.newCall(request).enqueue(object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    runOnUiThread {
                        Toast.makeText(this@ConfiguracoesActivity, getString(R.string.erro_atualizar_base), Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    val body = response.body?.string()
                    if (body != null) openFileOutput(base.second, MODE_PRIVATE).use { it.write(body.toByteArray()) }
                    concluido++
                    if (concluido == bases.size) {
                        val data = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                        guardarDataAtualizacao(data)
                        runOnUiThread {
                            atualizarTextoData()
                            verificarEstadoBase()
                            Toast.makeText(this@ConfiguracoesActivity, getString(R.string.bases_atualizadas_sucesso), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
        }
    }

    private fun verificarEstadoBase() {
        val bases = listOf(
            Triple(nomeFicheiroCache, txtCircuitos, "Circuitos"),
            Triple(ficheiroMovel,     txtMovel,     "Rede Móvel"),
            Triple(ficheiroSiresp,    txtSiresp,    "Rede Siresp"),
            Triple(ficheiroFixa,      txtFixa,      "Rede Fixa")
        )
        var basesProntas = 0
        for (base in bases) {
            val existe = try { openFileInput(base.first).close(); true } catch (e: Exception) { false }
            if (existe) {
                base.second.text = "OK"
                base.second.setTextColor(ContextCompat.getColor(this, R.color.verde_sucesso))
                basesProntas++
            } else {
                base.second.text = "--"
                base.second.setTextColor(ContextCompat.getColor(this, R.color.cinza_inativo))
            }
        }
        when {
            basesProntas == bases.size -> {
                txtEstadoBase.text = getString(R.string.estado_prontas, basesProntas)
                txtEstadoBase.setTextColor(ContextCompat.getColor(this, R.color.verde_sucesso))
            }
            basesProntas > 0 -> {
                txtEstadoBase.text = getString(R.string.estado_parciais, basesProntas)
                txtEstadoBase.setTextColor(ContextCompat.getColor(this, R.color.amarelo_aviso))
            }
            else -> {
                txtEstadoBase.text = getString(R.string.estado_nenhuma)
                txtEstadoBase.setTextColor(ContextCompat.getColor(this, R.color.vermelho_erro))
            }
        }
        try {
            val linhas = openFileInput(nomeFicheiroCache).bufferedReader().readLines()
            txtTotalRegistos.text = "Circuitos · ${if (linhas.isNotEmpty()) linhas.size - 1 else 0} registos"
        } catch (e: Exception) {
            txtTotalRegistos.text = "Circuitos · --"
        }
    }

    private fun guardarDataAtualizacao(data: String) {
        getSharedPreferences("config", Context.MODE_PRIVATE).edit { putString("ultima_atualizacao", data) }
    }

    private fun obterDataAtualizacao(): String {
        return getSharedPreferences("config", Context.MODE_PRIVATE).getString("ultima_atualizacao", "--") ?: "--"
    }

    private fun atualizarTextoData() {
        txtUltimaAtualizacao.text = obterDataAtualizacao()
    }
}