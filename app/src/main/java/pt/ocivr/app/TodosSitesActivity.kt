package pt.ocivr.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class TodosSitesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todos_sites)

        findViewById<View>(R.id.btnRedeMovel).setOnClickListener {
            startActivity(Intent(this, PesquisaRedeMovelActivity::class.java))
        }

        findViewById<View>(R.id.btnRedeSiresp).setOnClickListener {
            startActivity(Intent(this, PesquisaRedeSirespActivity::class.java))
        }

        findViewById<View>(R.id.btnRedeFixa).setOnClickListener {
            startActivity(Intent(this, PesquisaRedeFixaActivity::class.java))
        }
    }
}