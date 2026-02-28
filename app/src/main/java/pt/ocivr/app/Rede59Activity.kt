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

class Rede59Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rede59)

        val main = findViewById<View>(R.id.main)
        val grid = findViewById<GridLayout>(R.id.gridRede)


        val botoes = listOf(
            "59AJ01","59MR01","59VM01",
            "59AJ02","59PG01","59VP01",
            "59CD01","59PG02","59VR01_Se",
            "59CJ01","59PR01","59VR01_Do",
            "59CL01","59RP01","59VR02",
            "59CM01","59SB01","59VR03",
            "59CP01","59SD01","59VR04",
            "59CV01","59ST01","59VR05",
            "59JT01","59TS01","59VR06",
            "59JU01","","59VR07"
        )

        val links = mapOf(
            "59AJ01" to "https://docs.google.com/spreadsheets/d/1xGidkES2GhW3Tp6FShAdp74ulCW__BQobPDI4LtXRKY/edit?gid=1235425075#gid=1235425075",
            "59MR01" to "https://docs.google.com/spreadsheets/d/1mw9fFBVcwgGJ8hdShPkZDzYsq7BJ2OHTCodn2uQWuS4/edit?gid=1057174623#gid=1057174623",
            "59VM01" to "https://docs.google.com/spreadsheets/d/1qY_tNNvsPlmbCrnsQNNmGytcchI_5two8VacAXCtYGI/edit?gid=1975701994#gid=1975701994",
            "59AJ02" to "https://docs.google.com/spreadsheets/d/1B49rBgh374xkLfJ1UUgH2Tsj8cVpE5GnxhCQdPMnQAI/edit?gid=1008076586#gid=1008076586",
            "59PG01" to "https://docs.google.com/spreadsheets/d/1UHybURMGQO2CoCYqlO8TA7LEyduU_z8Rogsa3PGgiKI/edit?gid=1171740721#gid=1171740721",
            "59VP01" to "https://docs.google.com/spreadsheets/d/1SbBCEhBgV2qJsekk-gSR7V1MSMCOWpeXIpg4joz2njc/edit?gid=1065374551#gid=1065374551",
            "59CD01" to "https://docs.google.com/spreadsheets/d/1RzuDOQmJ-rNFGQItNuesMKu_JjiurzGb-VXjzdNfGTc/edit?gid=937755512#gid=937755512",
            "59PG02" to "https://docs.google.com/spreadsheets/d/1C2RlaAAt2yI2aWP5evQZer7zmlYtaVdgX0n3pf8SMlE/edit?gid=1666071963#gid=1666071963",
            "59VR01_Se" to "https://docs.google.com/spreadsheets/d/1A_FECGRyie7L7oiSImvqa3M06NNcrTjmgt-DuHDykX0/edit?gid=1694471561#gid=1694471561",
            "59CJ01" to "https://docs.google.com/spreadsheets/d/1zieaod02DwkhGtB0XUoBoa28cdagQ_ZK2KM8FTORf8Q/edit?gid=1735608677#gid=1735608677",
            "59PR01" to "https://docs.google.com/spreadsheets/d/1DUiaOQUbzatISORSB2HErDuNJGEDTNbjCXrLclkZkA4/edit?gid=872177912#gid=872177912",
            "59VR01_Do" to "https://docs.google.com/spreadsheets/d/1dXy65JCKfG-W94YCgQQ7dnzvbbKmwXXDC3b0w7lkKP8/edit?gid=1339391795#gid=1339391795",
            "59CL01" to "https://docs.google.com/spreadsheets/d/1HqcbXp0KgT6Du4EAylPbxeyNQ6niYV0I8no5blaMWYE/edit?gid=56998952#gid=56998952",
            "59RP01" to "https://docs.google.com/spreadsheets/d/1Qk6CKjXl0Oh_cMt7SYRE3W_BC701_nAkm8tN5lzcOW0/edit?gid=1236142387#gid=1236142387",
            "59VR02" to "https://docs.google.com/spreadsheets/d/1HjTE1uqY0WcNLQsWbbs_WsDoCjgA0Vzzf_XR8-sZcOQ/edit?gid=1573707259#gid=1573707259",
            "59CM01" to "https://docs.google.com/spreadsheets/d/1vEXCpCZxHR_UhGd8wU8Bcs3lUXZRV5Pj8GJ1yaBmFOE/edit?gid=1494899945#gid=1494899945",
            "59SB01" to "https://docs.google.com/spreadsheets/d/19TpxFZB5SBnZVDaKR51m54-YqGQUTEYhIv97AR26Fac/edit?gid=697690586#gid=697690586",
            "59VR03" to "https://docs.google.com/spreadsheets/d/1rTEJ_Xbd2naeeWlD3ZiQxB5V4C1ha9WEN3s48_X1Ls8/edit?gid=1307981599#gid=1307981599",
            "59CP01" to "https://docs.google.com/spreadsheets/d/17vD-t4I4F3x7f9UGnlr4szRNnTt3_qpDIr1IEr-SMHQ/edit?gid=105748878#gid=105748878",
            "59SD01" to "https://docs.google.com/spreadsheets/d/1e7P_shQIrMFX9lH9_cnO0jfd9n6bxc1uwe3thwFbfQ0/edit?gid=1658017213#gid=1658017213",
            "59VR04" to "https://docs.google.com/spreadsheets/d/1qYuc74JFRkeYxIsqQssParTYMHwRPaHQeHA29558Gnk/edit?gid=2060761066#gid=2060761066",
            "59CV01" to "https://docs.google.com/spreadsheets/d/1WIUxq3ZRJKlNppZE8bOQnvEOdhgSGsqCfdcrDanratA/edit?gid=365677335#gid=365677335",
            "59ST01" to "https://docs.google.com/spreadsheets/d/1Zoe7OXmvmDXuAF5we_RqW1NYhGXsDYDnfTXlNuqWVZQ/edit?gid=1995252658#gid=1995252658",
            "59VR05" to "https://docs.google.com/spreadsheets/d/1OUaiMCL2Q9j_NuykdKMg81s9lDFstr-WQK90odebDtc/edit?gid=1878134753#gid=1878134753",
            "59JT01" to "https://docs.google.com/spreadsheets/d/1EK6NZ-dnSqU5y8pyrGB446bvHrUdSdWj9YXMfiw6HvA/edit?gid=2017794267#gid=2017794267",
            "59TS01" to "https://docs.google.com/spreadsheets/d/1k2zm3SdhfjUz9UB6STYUhEPYK1KqRPtYrpR_KwAJelY/edit?gid=990719304#gid=990719304",
            "59VR06" to "https://docs.google.com/spreadsheets/d/16BwTK92cfVMoInerIep-PJkjBSQY9A_pSUyocUZpGrI/edit?gid=346357645#gid=346357645",
            "59JU01" to "https://docs.google.com/spreadsheets/d/1yzSeQLBuzGlStRX4_UgR3P6y8MPWEeHueyjjmPwn9eY/edit?gid=62066123#gid=62066123",
            "59VR07" to "https://docs.google.com/spreadsheets/d/1tzzZnxXcuagn5NIvMUej50_RqGW6rkdFlQYmxaKL0Dg/edit?gid=1541379891#gid=1541379891"
        )

        for (nome in botoes) {

            // Espaço vazio (alinhamento)
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