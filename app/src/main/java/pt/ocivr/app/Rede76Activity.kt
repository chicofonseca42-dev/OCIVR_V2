package pt.ocivr.app

import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Rede76Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rede76)

        val main = findViewById<View>(R.id.main)
        val grid = findViewById<GridLayout>(R.id.gridRede)


        val botoes = listOf(
            "76CHO1","76ABO1","76SVO1",
            "76CHO2","76PSO1","76CAO1",
            "76CHO3","76CBO1","76VVO2",
            "76CHO4","76ANO1","76VVO1",
            "76CHO5","76COO1","76TVO1",
            "76VGO1","76MGO1","76LCO1",
            "76BCO1","76NMO1","76AFO1",
            "","","76TMO1"
        )

        val links = mapOf(
            "76CHO1" to "https://docs.google.com/spreadsheets/d/1J-OOZSX871pgxyt03r0bFdxHWi_HkKcYs4N7q_0-iRw/edit?gid=588980668#gid=588980668",
            "76ABO1" to "https://docs.google.com/spreadsheets/d/1t5kjU9Se-8MNz6au4bZ2i0iZnZ3T_ZV_Fc_9tqMha_k/edit?gid=837619980#gid=837619980",
            "76SVO1" to "https://docs.google.com/spreadsheets/d/19Y5MVx2g-KsXINxbrbXrd6D-UlekaZL09MaRPnRue7o/edit?gid=636342772#gid=636342772",
            "76CHO2" to "https://docs.google.com/spreadsheets/d/1AWy5AOTLq9T-B3O_hrz7_eJo_fkdM-qtJaaF5ROt63k/edit?gid=255486703#gid=255486703",
            "76PSO1" to "https://docs.google.com/spreadsheets/d/1E8-H9JF6IWg_Ud0_rpSVlTlsx53eak5uNzy_W39PE3A/edit?gid=439394706#gid=439394706",
            "76CAO1" to "https://docs.google.com/spreadsheets/d/1otVKiyQvWsVWCK1RUhoOZ8u3YFm0SwpdCFPLJRSRzWo/edit?gid=1360159074#gid=1360159074",
            "76CHO3" to "https://docs.google.com/spreadsheets/d/1cCUPCsSW3GKTHmpMWr0JlhKj_HaBC4gGvz2ea6wreSY/edit?gid=403432938#gid=403432938",
            "76CBO1" to "https://docs.google.com/spreadsheets/d/1Jm10wYX0C8MVaPjO0ODoPu0yPmellmRPsKOODMRaCNo/edit?gid=696175203#gid=696175203",
            "76VVO2" to "https://docs.google.com/spreadsheets/d/1Dp8QIyZK2ZHU0TgOi5pPUXkRftZr8ruGmUaMjHHu_gY/edit?gid=1932593052#gid=1932593052",
            "76CHO4" to "https://docs.google.com/spreadsheets/d/1ZKZsyJ1mhICzMLmD_AoDPFld03ZFy7ObwuuTEJiHlMY/edit?gid=273217671#gid=273217671",
            "76ANO1" to "https://docs.google.com/spreadsheets/d/1rO7AC4iTjj5FkHi4DzCOFQffBngf7KOizflc5iYvt1I/edit?gid=1157401001#gid=1157401001",
            "76VVO1" to "https://docs.google.com/spreadsheets/d/1ZBg9vjSBuYdBmOgFY9KpHXvx3CXvfNZEPTKFkOC8Ckc/edit?gid=945567783#gid=945567783",
            "76CHO5" to "https://docs.google.com/spreadsheets/d/1Ut0i2Rl4792TdlRpNcFzEiayApMxL_PX1h8j1Xzm1j8/edit?gid=233475826#gid=233475826",
            "76COO1" to "https://docs.google.com/spreadsheets/d/1RtZFeU5S9QIEKbivEk4hRsD_GHPI9it8AERm4dAdeHc/edit?gid=2142283957#gid=2142283957",
            "76TVO1" to "https://docs.google.com/spreadsheets/d/1mbzYxVf1--_MmM_tNSmsQUueLlVlY9wINM8vSOqC4nQ/edit?gid=578892694#gid=578892694",
            "76VGO1" to "https://docs.google.com/spreadsheets/d/1TClMAAWiZcmkObex7a78kWSUomjjAsfi_EV2n3x06qo/edit?gid=181826781#gid=181826781",
            "76MGO1" to "https://docs.google.com/spreadsheets/d/1FFhv7SBjDf89H9q1SwHZzJ82ubEu7_AfQ02RFlCSwvc/edit?gid=593793230#gid=593793230",
            "76LCO1" to "https://docs.google.com/spreadsheets/d/1ugKMiXRGAfx1XF70U-T9bIKpBwG2lpIqb9vbQoo2fcc/edit?gid=947339137#gid=947339137",
            "76BCO1" to "https://docs.google.com/spreadsheets/d/1UQPZtdaeV8xLrFt9ggbzfG39IT5pDTmRY9O8DrLli0w/edit?gid=1355085799#gid=1355085799",
            "76NMO1" to "https://docs.google.com/spreadsheets/d/1RTX4G687VW1FqazioshaJ9bXbjxZRAwqBCaKiCBxDc4/edit?gid=640522571#gid=640522571",
            "76AFO1" to "https://docs.google.com/spreadsheets/d/1u896wk5FfOT1xdLjAA2oPuraMXS4KDJf8o2d4TFb29s/edit?gid=56356397#gid=56356397",
            "76TMO1" to "https://docs.google.com/spreadsheets/d/1FicaS2c0QgCV3bXHErdHfrtarfKwlcLZV0eNir-CkPE/edit?gid=2015230735#gid=2015230735"
        )

        for (nome in botoes) {

            if (nome.isBlank()) {
                val vazio = Button(this)
                vazio.isEnabled = false
                vazio.setBackgroundColor(Color.TRANSPARENT)
                val p = GridLayout.LayoutParams().apply {
                    width = 0
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                }
                vazio.layoutParams = p
                grid.addView(vazio)
                continue
            }

            val btn = Button(this).apply {
                text = nome
                setTextColor("#F5F5F5".toColorInt())
                setBackgroundColor(Color.TRANSPARENT)
                textSize = 16f
                isAllCaps = false
                setShadowLayer(4f, 2f, 2f, Color.BLACK)
            }

            val params = GridLayout.LayoutParams().apply {
                width = 0
                setMargins(6, 4, 6, 4)
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            }
            btn.layoutParams = params

            btn.setOnClickListener {
                feedback(btn)
                links[nome]?.let { url ->
                    // ✅ BUG CORRIGIDO: startActivity adicionado
                    btn.postDelayed({
                        startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
                    }, 120)
                }
            }

            grid.addView(btn)
        }
    }

    private fun feedback(btn: Button) {
        btn.alpha = 0.6f
        btn.animate()
            .scaleX(0.96f)
            .scaleY(0.96f)
            .setDuration(90)
            .withEndAction {
                btn.alpha = 1f
                btn.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(90)
                    .start()
            }
            .start()
    }
}
