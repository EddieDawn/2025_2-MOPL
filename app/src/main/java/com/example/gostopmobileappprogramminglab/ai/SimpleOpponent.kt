package com.example.gostopmobileappprogramminglab.ai

import com.example.gostopmobileappprogramminglab.model.*

object SimpleOpponent {

    fun chooseMove(state: GameState): GoStopCard? {
        // 1. Try to find a matching card
        val match = state.opponentHand.firstOrNull { cpuCard ->
            state.field.any { it.month == cpuCard.month }
        }
        if (match != null) return match

        // 2. Otherwise play lowest-value card
        return state.opponentHand.minByOrNull { card ->
            when (card.type) {
                CardType.BRIGHT -> 4
                CardType.ANIMAL -> 3
                CardType.RIBBON -> 2
                CardType.JUNK -> 1
            }
        }
    }

    fun decideGoStop(state: GameState): Boolean {
        val cpuScore = GameEngine.calculateScore(state.cpuCaptured)

        // CPU stops if score >= 7
        if (cpuScore >= 7) return false

        // CPU goes if score >= 3
        if (cpuScore >= 3) return true

        return false // default behavior
    }
}

