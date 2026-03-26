package pt.ocivr.app

import android.graphics.Color
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL

class PesquisaOLTActivity : AppCompatActivity() {

    private val sheetUrl = "https://docs.google.com/spreadsheets/d/1lypYvzgl7QElTDGvuXf0W4QN1R4sifce11WXNZGzID8/gviz/tq?tqx=out:json&sheet=EXPORT_OLT"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pesquisa_olt)

        val etPesquisa = findViewById<EditText>(R.id.etPesquisa)
        val btnPesquisar = findViewById<CardView>(R.id.btnPesquisar)
        val container = findViewById<LinearLayout>(R.id.containerResultados)

        etPesquisa.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                btnPesquisar.performClick()
                true
            } else false
        }

        btnPesquisar.setOnClickListener {

            // Esconde teclado
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

            val pesquisa = etPesquisa.text.toString().trim()
            if (pesquisa.isEmpty()) {
                Toast.makeText(this, "Introduz um valor", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            container.removeAllViews()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val resposta = URL(sheetUrl).readText()
                    val json = resposta.substring(resposta.indexOf("{"), resposta.lastIndexOf("}") + 1)
                    val tabela = JSONObject(json).getJSONObject("table")
                    val linhas = tabela.getJSONArray("rows")

                    val resultados = mutableListOf<Map<String, String>>()

                    for (i in 0 until linhas.length()) {
                        val linha = linhas.getJSONObject(i)
                        val celulas = linha.getJSONArray("c")

                        fun celula(index: Int): String {
                            return try {
                                celulas.optJSONObject(index)?.optString("v", "") ?: ""
                            } catch (e: Exception) { "" }
                        }

                        val olt = celula(0)
                        val acl = celula(1)

                        if (olt.contains(pesquisa, ignoreCase = true) ||
                            acl.contains(pesquisa, ignoreCase = true)) {
                            resultados.add(mapOf(
                                "OLT" to olt,
                                "ACL" to acl,
                                "SR" to celula(2),
                                "Débito" to celula(3),
                                "Interface SR" to celula(4),
                                "GECA" to celula(5),
                                "Localidade" to celula(6)
                            ))
                        }
                    }

                    withContext(Dispatchers.Main) {
                        if (resultados.isEmpty()) {
                            Toast.makeText(this@PesquisaOLTActivity, "Sem resultados", Toast.LENGTH_SHORT).show()
                        } else {
                            for (r in resultados) {
                                container.addView(criarCard(r))
                            }
                        }
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@PesquisaOLTActivity, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun criarCard(dados: Map<String, String>): CardView {
        val card = CardView(this).apply {
            radius = 24f
            cardElevation = 12f
            setCardBackgroundColor(Color.parseColor("#C8C8C8"))
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, 40)
        card.layoutParams = params

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
        }

        layout.addView(criarTexto("OLT: ${dados["OLT"]}", titulo = true))
        layout.addView(criarTexto("ACL: ${dados["ACL"]}"))
        layout.addView(criarTexto("SR: ${dados["SR"]}"))
        layout.addView(criarTexto("Débito: ${dados["Débito"]}"))
        layout.addView(criarTexto("Interface SR: ${dados["Interface SR"]}"))
        layout.addView(criarTexto("GECA: ${dados["GECA"]}"))
        layout.addView(criarTexto("Localidade: ${dados["Localidade"]}"))

        card.addView(layout)
        return card
    }

    private fun criarTexto(texto: String, titulo: Boolean = false): TextView {
        val tv = TextView(this)
        tv.text = texto
        tv.setTextColor(Color.BLACK)
        tv.textSize = if (titulo) 18f else 16f
        if (titulo) tv.setPadding(0, 0, 0, 20)
        return tv
    }
}