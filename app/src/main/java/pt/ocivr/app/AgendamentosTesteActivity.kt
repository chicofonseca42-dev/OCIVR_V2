package pt.ocivr.app

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class AgendamentosTesteActivity : AppCompatActivity() {

    private val sheetId = "1t3cZeesYG4JSvl9hoiTvMZWK5lEPpd1IHHKnS0-ZrsQ"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agendamentos_teste)

        val viewPager = findViewById<ViewPager2>(R.id.viewPagerDias)
        val tituloTopo = findViewById<TextView>(R.id.textDiaTopo)
        val dias = listOf("Segunda","Terça","Quarta","Quinta","Sexta","Sábado","Domingo")

        val adapter = DiasPagerAdapter(dias, List(7) { emptyList() })
        viewPager.adapter = adapter
        tituloTopo.text = dias[0]

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) { tituloTopo.text = dias[position] }
        })

        buscarDados()
    }

    private fun buscarDados() {
        val url = "https://docs.google.com/spreadsheets/d/$sheetId/gviz/tq?tqx=out:json&gid=0"
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        Thread {
            try {
                val response = client.newCall(request).execute()
                val body = response.body?.string()
                if (body != null) {
                    val json = body.substring(body.indexOf("{"), body.lastIndexOf("}") + 1)
                    val rows = JSONObject(json).getJSONObject("table").getJSONArray("rows")


                    val dadosPorDia = MutableList(7) { mutableListOf<String>() }
                    for (i in 0 until rows.length()) {
                        val cells = rows.getJSONObject(i).getJSONArray("c")
                        for (col in 0 until minOf(7, cells.length())) {
                            val valor = cells.optJSONObject(col)?.opt("v")?.toString() ?: ""
                            if (valor.isBlank() || valor == "null") continue
                            if (valor.trim() == "M") continue
                            dadosPorDia[col].add(valor)
                        }
                    }
                    runOnUiThread { atualizarAdapter(dadosPorDia) }
                }
            } catch (e: Exception) { Log.e("SHEET", "Erro: ${e.message}") }
        }.start()
    }


    private fun atualizarAdapter(dados: List<List<String>>) {
        val viewPager = findViewById<ViewPager2>(R.id.viewPagerDias)
        val dias = listOf("Segunda","Terça","Quarta","Quinta","Sexta","Sábado","Domingo")
        viewPager.adapter = DiasPagerAdapter(dias, dados)
    }
}