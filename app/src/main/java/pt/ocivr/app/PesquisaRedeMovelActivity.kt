package pt.ocivr.app

import androidx.core.net.toUri
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import okhttp3.*
import java.io.IOException

class PesquisaRedeMovelActivity : AppCompatActivity() {

    private val sheetUrl =
        "https://docs.google.com/spreadsheets/d/e/2PACX-1vTOTDRMPpRIaj-TZCXRtO_2bZhnqdFxyua9ZvKdlG5tZ12PYHKxXfWcxKItuM4HUxJMl4mqjRaol7i_/pub?gid=1608582643&single=true&output=csv"

    private val nomeFicheiroCache = "rede_movel_cache.csv"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pesquisa_rede_movel)

        val btnPesquisar = findViewById<View>(R.id.btnPesquisar)
        val etPesquisa = findViewById<EditText>(R.id.etPesquisa)
        val container = findViewById<LinearLayout>(R.id.containerResultados)

        etPesquisa.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE) {
                btnPesquisar.performClick()
                true
            } else false
        }

        btnPesquisar.setOnClickListener {

            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

            val termoDigitado = etPesquisa.text.toString().trim()

            if (termoDigitado.isEmpty()) {
                Toast.makeText(
                    this,
                    getString(R.string.introduz_id),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            container.removeAllViews()

            buscarRedeMovel(termoDigitado) { resultado ->
                runOnUiThread {

                    if (resultado.isEmpty()) {
                        Toast.makeText(
                            this,
                            getString(R.string.site_nao_encontrado),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@runOnUiThread
                    }

                    container.addView(
                        criarCard(
                            resultado["ID"] ?: "",
                            resultado["NOME"] ?: "",
                            resultado["LAT"] ?: "",
                            resultado["LON"] ?: "",
                            resultado["MAPS"] ?: ""
                        )
                    )
                }
            }
        }
    }

    private fun buscarRedeMovel(
        termo: String,
        callback: (Map<String, String>) -> Unit
    ) {

        val cacheLocal = lerCache()

        if (cacheLocal != null) {
            processarCSV(cacheLocal, termo, callback)
            return
        }

        val client = OkHttpClient()

        client.newCall(Request.Builder().url(sheetUrl).build())
            .enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    callback(emptyMap())
                }

                override fun onResponse(call: Call, response: Response) {

                    val body = response.body?.string()

                    if (body != null) {
                        guardarCache(body)
                        processarCSV(body, termo, callback)
                    } else {
                        callback(emptyMap())
                    }
                }
            })
    }

    private fun processarCSV(
        body: String,
        termo: String,
        callback: (Map<String, String>) -> Unit
    ) {

        for (linha in body.split("\n").drop(1)) {

            val limpa = linha.split(
                Regex(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")
            ).map { it.replace("\"", "").trim() }

            if (limpa.size >= 5 &&
                limpa[0].equals(termo.trim(), ignoreCase = true)
            ) {

                callback(
                    mapOf(
                        "ID" to limpa[0],
                        "NOME" to limpa[1],
                        "LAT" to limpa[2],
                        "LON" to limpa[3],
                        "MAPS" to limpa[4]
                    )
                )
                return
            }
        }

        callback(emptyMap())
    }

    private fun guardarCache(conteudo: String) {
        openFileOutput(nomeFicheiroCache, MODE_PRIVATE)
            .use { it.write(conteudo.toByteArray()) }
    }

    private fun lerCache(): String? =
        try {
            openFileInput(nomeFicheiroCache)
                .bufferedReader()
                .use { it.readText() }
        } catch (_: Exception) {
            null
        }

    private fun criarCard(
        id: String,
        nome: String,
        lat: String,
        lon: String,
        mapsLink: String
    ): CardView {

        val card = CardView(this).apply {
            radius = 24f
            cardElevation = 12f
            setCardBackgroundColor(android.graphics.Color.WHITE)
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

        layout.addView(criarTexto("ID: $id", true))
        layout.addView(criarTexto("Nome: $nome"))
        layout.addView(criarTexto("Latitude: $lat"))
        layout.addView(criarTexto("Longitude: $lon"))

        val btnMapa = Button(this)
        btnMapa.text = getString(R.string.ver_no_maps)
        btnMapa.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, mapsLink.toUri()))
        }

        layout.addView(btnMapa)

        card.addView(layout)

        return card
    }

    private fun criarTexto(
        texto: String,
        titulo: Boolean = false
    ): TextView {

        val tv = TextView(this)
        tv.text = texto
        tv.setTextColor(android.graphics.Color.BLACK)
        tv.textSize = if (titulo) 18f else 16f

        if (titulo) {
            tv.setPadding(0, 0, 0, 20)
        }

        return tv
    }
}