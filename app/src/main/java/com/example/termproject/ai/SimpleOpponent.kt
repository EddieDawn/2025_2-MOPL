package com.example.termproject.ai

import com.example.termproject.model.*

object SimpleOpponent {

    fun chooseMove(state: GameState): GoStopCard? {

        val hand = state.opponentHand

        // 1. If any card can capture → choose best value
        val matchCards = hand.filter { cpuCard ->
            state.field.any { it.month == cpuCard.month }
        }

        if (matchCards.isNotEmpty()) {
            return matchCards.maxByOrNull { valueOf(it) }
        }

        // 2. No match → discard lowest-value card
        return hand.minByOrNull { valueOf(it) }
    }

    private fun valueOf(card: GoStopCard): Int {
        return when (card.type) {
            CardType.BRIGHT -> 4
            CardType.ANIMAL -> 3
            CardType.RIBBON -> 2
            CardType.JUNK -> 1
        }
    }
}
