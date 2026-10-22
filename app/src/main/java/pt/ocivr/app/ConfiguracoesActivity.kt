package pt.ocivr.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

        // 🔑 Janela para configurar ou atualizar a Password corporativa única
        fun abrirJanelaCredenciais() {
            val (userAtual, passAtual) = Credenciais.obter(this)

            val layout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(60, 40, 60, 20)
            }

            val inputUser = EditText(this).apply {
                hint = "Utilizador de rede (ex: z880914)"
                setText(userAtual)
            }

            val inputPass = EditText(this).apply {
                hint = "Palavra-passe de rede"
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                setText(passAtual)
            }

            // Opção para alternar entre ver/ocultar a password
            val checkMostrarPass = CheckBox(this).apply {
                text = "Mostrar palavra-passe"
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        inputPass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    } else {
                        inputPass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    }
                    inputPass.setSelection(inputPass.text.length)
                }
            }

            layout.addView(inputUser)
            layout.addView(inputPass)
            layout.addView(checkMostrarPass)

            AlertDialog.Builder(this)
                .setTitle("Credenciais de Rede")
                .setMessage("Esta palavra-passe será usada no NETQ, SouMEO e CPL Web:")
                .setView(layout)
                .setPositiveButton("Guardar") { _, _ ->
                    val u = inputUser.text.toString().trim()
                    val p = inputPass.text.toString().trim()
                    if (u.isNotEmpty() && p.isNotEmpty()) {
                        Credenciais.guardar(this, u, p)
                        Toast.makeText(this, "Credenciais guardadas com sucesso!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Preencha utilizador e palavra-passe", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        // 📊 NETQ: se não houver password, pede primeiro; se houver, abre de imediato
        click(R.id.btnSouMeo) {
            if (!Credenciais.temCredenciais(this)) {
                Toast.makeText(this, "Configure primeiro as credenciais!", Toast.LENGTH_SHORT).show()
                abrirJanelaCredenciais()
            } else {
                val intent = Intent(this, WebViewActivity::class.java).apply {
                    putExtra("url", "https://netq.telecom.pt")
                }
                startActivity(intent)
            }
        }

        // ⚙️ Toque longo no botão NETQ (manter premido 1 segundo): abre a janela para alterar a password
        findViewById<View>(R.id.btnSouMeo)?.setOnLongClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            abrirJanelaCredenciais()
            true
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
            val total = if (linhas.isNotEmpty()) linhas.size - 1 else 0
            txtTotalRegistos.text = "Circuitos · $total registos"
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