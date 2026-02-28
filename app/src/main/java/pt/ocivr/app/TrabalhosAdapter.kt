package pt.ocivr.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.core.graphics.toColorInt

class TrabalhosAdapter(
    private val listaTrabalhos: List<String>
) : RecyclerView.Adapter<TrabalhosAdapter.TrabalhoViewHolder>() {

    class TrabalhoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textTrabalho: TextView = view.findViewById(R.id.textTrabalho)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrabalhoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_trabalho, parent, false)
        return TrabalhoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrabalhoViewHolder, position: Int) {
        val texto = listaTrabalhos[position]
        if (texto.trim() == "T") {
            holder.textTrabalho.text = holder.itemView.context.getString(R.string.turno_tarde)
            holder.textTrabalho.setTypeface(null, android.graphics.Typeface.BOLD)
            holder.textTrabalho.textSize = 16f
            holder.textTrabalho.gravity = android.view.Gravity.CENTER
            holder.itemView.setBackgroundColor("#F2F2F2".toColorInt())
            holder.itemView.setPadding(16, 16, 16, 16)
        } else {
            holder.textTrabalho.text = texto
            holder.textTrabalho.setTypeface(null, android.graphics.Typeface.NORMAL)
            holder.textTrabalho.textSize = 14f
            holder.textTrabalho.gravity = android.view.Gravity.START
            holder.itemView.setBackgroundColor(holder.itemView.context.getColor(android.R.color.white))
            holder.itemView.setPadding(32, 32, 32, 32)
        }
    }

    override fun getItemCount(): Int = listaTrabalhos.size
}
