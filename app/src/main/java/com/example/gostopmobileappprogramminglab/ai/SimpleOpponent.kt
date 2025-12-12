package com.example.gostopmobileappprogramminglab.ai

import com.example.gostopmobileappprogramminglab.logic.GameEngine
import com.example.gostopmobileappprogramminglab.model.*

object SimpleOpponent {

    fun chooseMove(state: GameState): GoStopCard? {

        //behavior process
        // 1. Play a card that creates a capture
        val captureMove = state.opponentHand.firstOrNull { cpuCard ->
            state.field.any { it.month == cpuCard.month }
        }
        if (captureMove != null) return captureMove

        // 2. cards with duplicate months (this will be the setup for combos)
        val groupedByMonth = state.opponentHand.groupBy { it.month }
        val duplicateMonthGroup = groupedByMonth.entries.firstOrNull { it.value.size >= 2 }
        if (duplicateMonthGroup != null) {
            return duplicateMonthGroup.value.minByOrNull { card ->
                when (card.type) {
                    CardType.BRIGHT -> 4
                    CardType.ANIMAL -> 3
                    CardType.RIBBON -> 2
                    CardType.JUNK -> 1
                }
            }
        }

        //3.play lowest option card
        return state.opponentHand.minByOrNull { card ->
            when (card.type) {
                CardType.BRIGHT -> 5
                CardType.ANIMAL -> 3
                CardType.RIBBON -> 2
                CardType.JUNK -> 1
            }
        }
    }

    //Go stop logic upgrade
    fun decideGoStop(state: GameState): Boolean {

        val cpuScore = GameEngine.calculateScore(state.cpuCaptured)
        val playerScore = GameEngine.calculateScore(state.playerCaptured)

        val cardsLeft = state.deck.size + state.opponentHand.size + state.playerHand.size

        // if main player has a multiplier greater than 1, CPU will stop
        if (state.multiplier >= 2 && cpuScore >= 3) {
            return false // CPU stops to avoid being punished
        }

        // If playerScore > cpuScore AND deck is small, CPU stops immediately
        if (playerScore > cpuScore && cardsLeft < 10) {
            return false
        }

        // CPU deciding to GO
        // CPU goes when leading with 3+ points
        if (cpuScore >= 3 && cpuScore > playerScore) {
            return true
        }

        // CPU goes when holding good types (Brights/Animals)
        val cpuHasStrongCards =
            state.opponentHand.count { it.type == CardType.BRIGHT || it.type == CardType.ANIMAL } >= 2

        if (cpuScore >= 3 && cpuHasStrongCards) {
            return true
        }

        // default
        return false
    }
}



