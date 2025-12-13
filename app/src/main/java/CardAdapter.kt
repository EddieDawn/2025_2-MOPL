package com.example.gostopmobileappprogramminglab

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.gostopmobileappprogramminglab.model.GoStopCard

class CardAdapter(
    private val cards: List<GoStopCard>,
    private val selectable: Boolean = false,
    private val onCardSelected: ((GoStopCard) -> Unit)? = null
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardImage: ImageView = itemView.findViewById(R.id.itemImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cards[position]

        holder.cardImage.setImageResource(card.drawableRes)

        if (selectable) {
            holder.itemView.setOnClickListener {
                onCardSelected?.invoke(card)
            }
        } else {
            holder.itemView.setOnClickListener(null)
        }
    }

    override fun getItemCount(): Int = cards.size
}
