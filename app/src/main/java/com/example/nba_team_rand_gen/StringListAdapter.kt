package com.example.nba_team_rand_gen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StringListAdapter(
    private val items: List<String>,
    private val favorites: MutableSet<Int>,
    private val onFavoriteClick: (position: Int) -> Unit
) : RecyclerView.Adapter<StringListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvString: TextView       = view.findViewById(R.id.tvString)
        val btnFavorite: ImageButton = view.findViewById(R.id.btnFavorite)
        val divider: View            = view.findViewById(R.id.divider)

        init {
            btnFavorite.setOnClickListener {
                onFavoriteClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_string, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvString.text = items[position]

        // Optionally hide the divider on the last item (or any other logic):
        holder.divider.visibility =
            if (position == items.size - 1) View.GONE else View.VISIBLE
        holder.btnFavorite.isSelected = favorites.contains(position)
    }

    override fun getItemCount() = items.size
}
