package com.example.gostopmobileappprogramminglab.logic

import com.example.gostopmobileappprogramminglab.R
import com.example.gostopmobileappprogramminglab.model.CardType
import com.example.gostopmobileappprogramminglab.model.GameState
import com.example.gostopmobileappprogramminglab.model.GoStopCard
import com.example.gostopmobileappprogramminglab.model.Player

object GameEngine {

    fun createDeck(): MutableList<GoStopCard> {
        val deck = mutableListOf<GoStopCard>()

        // Defined cards for each month
        val months = mapOf(
            1 to listOf(R.drawable.january_bright, R.drawable.january_ribbon,
                R.drawable.january_junk_1, R.drawable.january_junk_2),
            2 to listOf(R.drawable.february_ribbon, R.drawable.february_animal,
                R.drawable.february_junk_1, R.drawable.february_junk_2),
            3 to listOf(R.drawable.march_bright, R.drawable.march_ribbon,
                R.drawable.march_junk_1, R.drawable.march_junk_2),
            4 to listOf(R.drawable.april_ribbon, R.drawable.april_animal,
                R.drawable.april_junk_1, R.drawable.april_junk_2),
            5 to listOf(R.drawable.may_ribbon, R.drawable.may_animal,
                R.drawable.may_junk_1, R.drawable.may_junk_2),
            6 to listOf(R.drawable.june_ribbon, R.drawable.june_animal,
                R.drawable.june_junk_1, R.drawable.june_junk_2),
            7 to listOf(R.drawable.july_ribbon, R.drawable.july_animal,
                R.drawable.july_junk_1, R.drawable.july_junk_2),
            8 to listOf(R.drawable.august_bright, R.drawable.august_animal,
                R.drawable.august_junk_1, R.drawable.august_junk_2),
            9 to listOf(R.drawable.september_ribbon, R.drawable.september_animal,
                R.drawable.september_junk_1, R.drawable.september_junk_2),
            10 to listOf(R.drawable.october_ribbon, R.drawable.october_animal,
                R.drawable.october_junk_1, R.drawable.october_junk_2),
            11 to listOf(R.drawable.november_bright,
                R.drawable.november_junk_1, R.drawable.november_junk_2),
            12 to listOf(R.drawable.december_bright, R.drawable.december_ribbon, R.drawable.december_animal,
                R.drawable.december_junk_1)
        )

        var idCounter = 1
        for ((month, resList) in months) {
            for (resId in resList) {
                val type = when {
                    resId.toString().contains("bright") -> CardType.BRIGHT
                    resId.toString().contains("ribbon") -> CardType.RIBBON
                    resId.toString().contains("animal") -> CardType.ANIMAL
                    else -> CardType.JUNK
                }
                deck.add(GoStopCard(idCounter++, resId, month, type))
            }
        }

        deck.shuffle()
        return deck
    }

    fun startNewGame(): GameState {
        val deck = createDeck()
        val playerHand = mutableListOf<GoStopCard>()
        val opponentHand = mutableListOf<GoStopCard>()
        val field = mutableListOf<GoStopCard>()

        // Standard Go-Stop: 7 cards per player, 6 on the field
        repeat(7) { if (deck.isNotEmpty()) playerHand.add(deck.removeAt(0)
        ) }
        repeat(7) { if (deck.isNotEmpty()) opponentHand.add(deck.removeAt(0)
        ) }
        repeat(6) { if (deck.isNotEmpty()) field.add(deck.removeAt(0)
        ) }

        return GameState(playerHand, opponentHand, field, deck, Player.HUMAN)
    }

    fun switchTurn(state: GameState) {
        state.currentTurn = if (state.currentTurn == Player.HUMAN) Player.CPU else Player.HUMAN
    }
    fun playCard(state: GameState, card: GoStopCard) {
        val match = state.field.firstOrNull { it.month == card.month }

        if (match != null) {
            // Capture both cards
            state.field.remove(match)
            when (state.currentTurn) {
                Player.HUMAN -> state.playerCaptured.addAll(listOf(card, match))
                Player.CPU -> state.cpuCaptured.addAll(listOf(card, match))
            }
        } else {
            // No match? just place it on the field
            state.field.add(card)
        }
    }
    fun isRoundOver(state: GameState): Boolean {
        return state.playerHand.isEmpty() &&
                state.opponentHand.isEmpty() &&
                state.deck.isEmpty()
    }

// The Go stop Scoring below is simplified

    fun calculateScore(captured: List<GoStopCard>): Int {
        var score = 0
        var junkCount = 0
        var brightCount = 0
        var ribbonCount = 0
        var animalCount = 0

        for (card in captured) {
            when (card.type) {
                CardType.BRIGHT -> brightCount++
                CardType.RIBBON -> ribbonCount++
                CardType.ANIMAL -> animalCount++
                CardType.JUNK -> junkCount++
            }
        }

        // Bright scoring(simplified)
        // 3 brights = +3, 4 brights = +4, 5 brights = +15
        score += when (brightCount) {
            3 -> 3
            4 -> 4
            5 -> 15
            else -> 0
        }

        // Ribbon scoring
        // every 5 ribbons = +1
        score += ribbonCount / 5

        // Animal scoring
        // every 5 animals = +1
        score += animalCount / 5

        // Junk scoring
        // every 2 junk = +1 point
        score += junkCount / 2

        return score
    }

}
