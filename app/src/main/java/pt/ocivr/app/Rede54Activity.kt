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

class Rede54Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rede54)

        val main = findViewById<View>(R.id.main)
        val grid = findViewById<GridLayout>(R.id.gridRede)


        val botoes = listOf(
            "54RG01","54RG02","54RG03","54AM01","54AS01","54CS01","54GS01","54LG01","54LG02",
            "54MB01","54MB02","54MB03","54MJ01","54MJ02","54PB01","54PN01","54RD01","54RD02",
            "54SL01","54SL02","54SM01","54SM02","54VL01","54PC01","54MZ01","54MZ02","54PJ01",
            "54PS01","54SC01","54SP01","54ST01","54SZ01","54SZ02","54TB01","54TR01","54TR02","54PI01"
        )

        val links = mapOf(
            "54RG01" to "https://docs.google.com/spreadsheets/d/1kCrCKKewhm5fvNXjvtLX5k3mpsSE-_CmA8Yg5VkUVb8/edit?gid=1509660899#gid=1509660899",
            "54RG02" to "https://docs.google.com/spreadsheets/d/1PxBaFOn5dBf-to77i8ZSOZQvPlDyCQy9cF6Q5UMBiDk/edit?gid=572026419#gid=572026419",
            "54RG03" to "https://docs.google.com/spreadsheets/d/1ZVu9RrovsEIPtZ6tUXegJvFHKsXh9LQHzl92KH3Fopo/edit?gid=442194035#gid=442194035",
            "54AM01" to "https://docs.google.com/spreadsheets/d/1CwktpJ3BlxruuCHBxu3VlgaQ-nTu-otohqfYNds9Xxk/edit?gid=324992055#gid=324992055",
            "54AS01" to "https://docs.google.com/spreadsheets/d/12yi9mHMMQlxtQyQAQ094chwS-3B10Wz2bqGpxyZbn_M/edit?gid=342297860#gid=342297860",
            "54CS01" to "https://docs.google.com/spreadsheets/d/1Wp4H8RCaK2pPrAxWxkMz7Ed2B-yNPv3qHh_dR3EK4kQ/edit?gid=1159019424#gid=1159019424",
            "54GS01" to "https://docs.google.com/spreadsheets/d/1yGgmFhCFA39EvNf2PnBDAHxQKSxWH9z_tFYJvl6TYRI/edit?gid=890796295#gid=890796295",
            "54LG01" to "https://docs.google.com/spreadsheets/d/162mvGgU-e9c5ngfSqYzYLE4WNtnumAq-qd7CWfg9-9E/edit?gid=759085587#gid=759085587",
            "54LG02" to "https://docs.google.com/spreadsheets/d/1I9d0KYdU319nLGkpF7G80zeBGwpT7vdF3nQy3SXqSbY/edit?gid=1191003039#gid=1191003039",
            "54MB01" to "https://docs.google.com/spreadsheets/d/1_80q_1jpKpyb3BDhkvnXubndzOkrJDkATe9BKiDHZCY/edit?gid=737970932#gid=737970932",
            "54MB02" to "https://docs.google.com/spreadsheets/d/19WMRrn2740lNer3q6wCR-Yz-qEo0qrDQnm5QQ_Wc0kE/edit?gid=1930139773#gid=1930139773",
            "54MB03" to "https://docs.google.com/spreadsheets/d/1U39OKDXVI8KxfrxBs0EjEOxb5xEkJQH7E8aDPKRK1as/edit?gid=1967909802#gid=1967909802",
            "54MJ01" to "https://docs.google.com/spreadsheets/d/1qkIDpGqI4hhWg5FYa0IHtUWsyQjY5BIDP7YhVnBqoKY/edit?gid=732043607#gid=732043607",
            "54MJ02" to "https://docs.google.com/spreadsheets/d/1KBnpTNP4dtoughGKU4EVPqEKcQivjcOpjsTMy0CCXqU/edit?gid=2128248665#gid=2128248665",
            "54PB01" to "https://docs.google.com/spreadsheets/d/16nagL7klrUu8N_Bu2nvIvSzW8GUJmnwPRjPwZmNeqLE/edit?gid=1861130720#gid=1861130720",
            "54PN01" to "https://docs.google.com/spreadsheets/d/1PrBm2ge-o_FW4M5wDrlFqbDh74m-N2U1a9sxTTesRx8/edit?gid=912400296#gid=912400296",
            "54RD01" to "https://docs.google.com/spreadsheets/d/1OpjAyamfBzwua6SrH9SEwSKh1x_0a0PYZYjhNUSiWeo/edit?gid=1112336056#gid=1112336056",
            "54RD02" to "https://docs.google.com/spreadsheets/d/13bEugI07VDH9_eoQm-bl2AuJn-wR1RUxCLjzWaV_iJY/edit?gid=58946481#gid=58946481",
            "54SL01" to "https://docs.google.com/spreadsheets/d/1XkFdxei1RLEwnAK2rVA28XzUMIkHciDSC4wE1Ea96hY/edit?gid=481029323#gid=481029323",
            "54SL02" to "https://docs.google.com/spreadsheets/d/1QC4e-ZgO9GXFRCbGZ71piwa1x7CjEssKA7gBpOrE9-c/edit?gid=1279931600#gid=1279931600",
            "54SM01" to "https://docs.google.com/spreadsheets/d/1ThoOFZFaaB_dJJPFHpjKzhuPWv4XGM6vlx1qDnFPe7g/edit?gid=1588077753#gid=1588077753",
            "54SM02" to "https://docs.google.com/spreadsheets/d/1FjwiLEn-B0msprgdj6XRKL_L7NlRR2UwcHNvd7wbgBs/edit?gid=62012763#gid=62012763",
            "54VL01" to "https://docs.google.com/spreadsheets/d/1_OwZbQ9INFbX1fgdRUWNbdi6nVhbgL939boTXWwBDlU/edit?gid=1914216836#gid=1914216836",
            "54PC01" to "https://docs.google.com/spreadsheets/d/120QrvrEIxHQgXELZLo4kmaVE5WyasI6gb9Ga61nvfcI/edit?gid=349918661#gid=349918661",
            "54MZ01" to "https://docs.google.com/spreadsheets/d/11_pJ-kTVMmTGqymCgRETUBgI1OuJArM4qbnBqSgLvUU/edit?gid=1613089017#gid=1613089017",
            "54MZ02" to "https://docs.google.com/spreadsheets/d/1a1INCnnyKT00Pe8HXlZVgeChX-zZELinc4yclLLlJ1E/edit?gid=1464942565#gid=1464942565",
            "54PJ01" to "https://docs.google.com/spreadsheets/d/13HbtgjeNf9DZ6Xx21FERzcKBatULdp9ytDMupfzCA3I/edit?gid=283643944#gid=283643944",
            "54PS01" to "https://docs.google.com/spreadsheets/d/1nqLBAaEyntJTn4i79svq4PAojit5XBGkcaLcqzzZehQ/edit?gid=533252018#gid=533252018",
            "54SC01" to "https://docs.google.com/spreadsheets/d/1z-oEFduW5Ik1tGFhSQXxRikfSeaMak4msz5aijz22sg/edit?gid=1278468788#gid=1278468788",
            "54SP01" to "https://docs.google.com/spreadsheets/d/1EqcdiB9igmVUSAPFDZLSLRuYgNKo85HCcNhfEhPUUfg/edit?gid=961176148#gid=961176148",
            "54ST01" to "https://docs.google.com/spreadsheets/d/1dQtBig9oYJoYbeJB-LdpiEjc41Z1YEObE3EolfV1zpk/edit?gid=566798981#gid=566798981",
            "54SZ01" to "https://docs.google.com/spreadsheets/d/1BqHLnqfA5oh-AuJI8ZityNx7LpWJn2SnGHHhj4s7AYg/edit?gid=1931584533#gid=1931584533",
            "54SZ02" to "https://docs.google.com/spreadsheets/d/19toT-3R32t__8s29vBEQ1GuqflyAgMGOQyhDlLdxuT8/edit?gid=510567618#gid=510567618",
            "54TB01" to "https://docs.google.com/spreadsheets/d/1kZ9shW2ealYRRKVMO9TUi1hAMY6eVKhNuUA_G8jPxPU/edit?gid=1807481253#gid=1807481253",
            "54TR01" to "https://docs.google.com/spreadsheets/d/1hBv7F4l_nHlOk1FaQC9NJM9reR_tnK9G5on_9AhkQtU/edit?gid=1735171556#gid=1735171556",
            "54TR02" to "https://docs.google.com/spreadsheets/d/136ytpQnGAZ0NRyKu4_OXCMu6gDExPyLDXlOvrj-0Dek/edit?gid=684668291#gid=684668291",
            "54PI01" to "https://docs.google.com/spreadsheets/d/1FOjheqboeDS6stUAYB_BP7AeZDe3hWhN1-D5E3c8RH8/edit?gid=1446719977#gid=1446719977"
        )

        for (nome in botoes) {
            val btn = Button(this).apply {
                text = nome; setTextColor("#F5F5F5".toColorInt()); setBackgroundColor(Color.TRANSPARENT)
                textSize = 16f; isAllCaps = false; setShadowLayer(4f, 2f, 2f, Color.BLACK)
            }
            val params = GridLayout.LayoutParams().apply {
                width = 0; setMargins(6, 4, 6, 4)
                columnSpec = if (nome == "54PI01") GridLayout.spec(2, 1f) else GridLayout.spec(GridLayout.UNDEFINED, 1f)
            }
            btn.layoutParams = params
            btn.setOnClickListener {
                feedback(btn)
                links[nome]?.let { url -> btn.postDelayed({ startActivity(Intent(Intent.ACTION_VIEW, url.toUri())) }, 120) }
            }
            grid.addView(btn)
        }

        }
    }

    private fun feedback(btn: Button) {
        btn.alpha = 0.6f
        btn.animate().scaleX(0.96f).scaleY(0.96f).setDuration(90).withEndAction {
            btn.alpha = 1f; btn.animate().scaleX(1f).scaleY(1f).setDuration(90).start()
        }.start()
    }

