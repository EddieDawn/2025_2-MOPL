package com.example.termproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.termproject.model.GoStopCard

class CardAdapter(
    private val cards: MutableList<GoStopCard>,
    var selectable: Boolean = false,
    private val onCardSelected: ((GoStopCard) -> Unit)? = null,
    var fieldCards: List<GoStopCard>? = null
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

        if (!selectable) {
            holder.itemView.background = null
            holder.itemView.clearAnimation()
            holder.itemView.setOnClickListener(null)
            return
        }

        // --- LEGAL MOVE FIX (prevents board freeze) ---
        val hasAnyMatch = cards.any { handCard ->
            fieldCards?.any { it.month == handCard.month } == true
        }

        val isPlayable = if (!hasAnyMatch) {
            true
        } else {
            fieldCards?.any { it.month == card.month } == true
        }

        holder.itemView.setOnClickListener {
            if (isPlayable) onCardSelected?.invoke(card)
        }

        if (isPlayable) {
            holder.itemView.background =
                holder.itemView.context.getDrawable(R.drawable.card_glow)

            holder.itemView.startAnimation(
                AnimationUtils.loadAnimation(holder.itemView.context, R.anim.glow_pulse)
            )
        } else {
            holder.itemView.background = null
            holder.itemView.clearAnimation()
        }
    }

    override fun getItemCount(): Int = cards.size
}
