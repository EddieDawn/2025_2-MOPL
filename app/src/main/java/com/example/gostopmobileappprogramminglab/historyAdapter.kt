package com.example.gostopmobileappprogramminglab

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.gostopmobileappprogramminglab.model.GoStopCard

class HistoryAdapter(
    private val items: MutableList<GoStopCard>
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.historyCardImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_card, parent, false)
        return HistoryViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.img.setImageResource(items[position].drawableRes)
    }

    fun addCard(card: GoStopCard) {
        items.add(card)

        // Keep last 5 only
        while (items.size > 5) items.removeAt(0)

        notifyDataSetChanged()
    }
}
