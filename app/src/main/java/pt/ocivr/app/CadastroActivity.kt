package pt.ocivr.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class CadastroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        // 🔹 Rede 54
        findViewById<View>(R.id.rede54).setOnClickListener {
            startActivity(
                Intent(this, Rede54Activity::class.java)
            )
        }

        // 🔹 Rede 59
        findViewById<View>(R.id.rede59).setOnClickListener {
            startActivity(
                Intent(this, Rede59Activity::class.java)
            )
        }

        // 🔹 Rede 76
        findViewById<View>(R.id.rede76).setOnClickListener {
            startActivity(
                Intent(this, Rede76Activity::class.java)
            )
        }

        // 🔹 OLT's
        findViewById<View>(R.id.olts).setOnClickListener {
            startActivity(
                Intent(this, PesquisaOLTActivity::class.java)
            )
        }
    }
}