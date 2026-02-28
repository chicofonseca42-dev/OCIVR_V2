package pt.ocivr.app

import androidx.core.net.toUri
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import okhttp3.*
import java.io.IOException

class PesquisaRedeFixaActivity : AppCompatActivity() {

    private val sheetUrl =
        "https://docs.google.com/spreadsheets/d/e/2PACX-1vTOTDRMPpRIaj-TZCXRtO_2bZhnqdFxyua9ZvKdlG5tZ12PYHKxXfWcxKItuM4HUxJMl4mqjRaol7i_/pub?gid=836241093&single=true&output=csv"

    private val nomeFicheiroCache = "rede_fixa_cache.csv"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pesquisa_rede_fixa)

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

            container.removeAllViews()

            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

            val termoDigitado = etPesquisa.text.toString().trim()

            if (termoDigitado.isEmpty()) {
                Toast.makeText(this, "Introduz um ACL", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            buscarRede(termoDigitado) { resultados ->
                runOnUiThread {

                    if (resultados.isEmpty()) {
                        Toast.makeText(this, "Não encontrado", Toast.LENGTH_SHORT).show()
                        return@runOnUiThread
                    }

                    for (r in resultados) {
                        container.addView(
                            criarCard(
                                r["ACL"] ?: "",
                                r["NOME_PI"] ?: "",
                                r["NOME_PA"] ?: "",
                                r["LAT"] ?: "",
                                r["LON"] ?: "",
                                r["MAPS"] ?: ""
                            )
                        )
                    }
                }
            }
        }
    }

    private fun buscarRede(
        termo: String,
        callback: (List<Map<String, String>>) -> Unit
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
                    callback(emptyList())
                }

                override fun onResponse(call: Call, response: Response) {

                    val body = response.body?.string()

                    if (body != null) {
                        openFileOutput(nomeFicheiroCache, MODE_PRIVATE)
                            .use { it.write(body.toByteArray()) }

                        processarCSV(body, termo, callback)
                    } else {
                        callback(emptyList())
                    }
                }
            })
    }

    private fun lerCache(): String? =
        try {
            openFileInput(nomeFicheiroCache)
                .bufferedReader()
                .use { it.readText() }
        } catch (_: Exception) {
            null
        }

    private fun processarCSV(
        body: String,
        termo: String,
        callback: (List<Map<String, String>>) -> Unit
    ) {

        val resultados = mutableListOf<Map<String, String>>()

        for (linha in body.split("\n").drop(1)) {

            val limpa = linha.split(
                Regex(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")
            ).map { it.replace("\"", "").trim() }

            if (limpa.size >= 10 &&
                limpa[0].equals(termo.trim(), ignoreCase = true)
            ) {

                resultados.add(
                    mapOf(
                        "ACL" to limpa[0],
                        "NOME_PI" to limpa[2],
                        "NOME_PA" to limpa[4],
                        "LAT" to limpa[5],
                        "LON" to limpa[6],
                        "MAPS" to limpa[9]
                    )
                )
            }
        }

        callback(resultados)
    }

    private fun criarCard(
        acl: String,
        nomePI: String,
        nomePA: String,
        lat: String,
        lon: String,
        mapsLink: String
    ): CardView {

        val card = CardView(this).apply {
            radius = 24f
            cardElevation = 12f
            setCardBackgroundColor(Color.WHITE)
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

        layout.addView(criarTexto("ACL: $acl", true))
        layout.addView(criarTexto("Nome PI: $nomePI"))
        layout.addView(criarTexto("Nome PA: $nomePA"))
        layout.addView(criarTexto("Latitude: $lat"))
        layout.addView(criarTexto("Longitude: $lon"))

        val btnMapa = Button(this)
        btnMapa.text = "Ver no Maps"
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
        tv.setTextColor(Color.BLACK)
        tv.textSize = if (titulo) 18f else 16f

        if (titulo) {
            tv.setPadding(0, 0, 0, 20)
        }

        return tv
    }
}