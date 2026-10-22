package pt.ocivr.app

import android.content.Context

object Credenciais {
    private const val PREF_NAME = "credenciais_corporativas"
    private const val KEY_USER = "user_rede"
    private const val KEY_PASS = "pass_rede"

    // Guarda ou atualiza as credenciais únicas
    fun guardar(context: Context, user: String, pass: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_USER, user.trim())
            .putString(KEY_PASS, pass.trim())
            .apply()
    }

    // Obtém o par (utilizador, password) guardado no telemóvel
    fun obter(context: Context): Pair<String, String> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val u = prefs.getString(KEY_USER, "") ?: ""
        val p = prefs.getString(KEY_PASS, "") ?: ""
        return Pair(u, p)
    }

    // Confirma se já existe utilizador e password configurados
    fun temCredenciais(context: Context): Boolean {
        val (u, p) = obter(context)
        return u.isNotEmpty() && p.isNotEmpty()
    }
}