package com.example.termproject.logic

import com.example.termproject.model.*

object GameEngine {

    fun startNewGame(): GameState {
        val deck = CardFactory.createDeck()

        val playerHand = MutableList(10) { deck.removeAt(0) }
        val opponentHand = MutableList(10) { deck.removeAt(0) }
        val field = MutableList(8) { deck.removeAt(0) }

        return GameState(
            playerHand = playerHand,
            opponentHand = opponentHand,
            field = field,
            deck = deck,
            currentTurn = Player.HUMAN
        )
    }

    fun playCard(state: GameState, played: GoStopCard) {
        val matches = state.field.filter { it.month == played.month }

        when (matches.size) {
            0 -> state.field.add(played)

            1 -> {
                state.field.remove(matches[0])
                capture(state, played, matches[0])
            }

            2 -> {
                state.field.remove(matches[0])
                capture(state, played, matches[0])
            }

            3 -> { // Bomb
                matches.forEach { state.field.remove(it) }
                matches.forEach { capture(state, played, it) }
                state.goCount += 1
            }
        }

        flipFromDeck(state)
    }

    private fun flipFromDeck(state: GameState) {
        if (state.deck.isEmpty()) return
        val flip = state.deck.removeAt(0)

        val matches = state.field.filter { it.month == flip.month }

        when (matches.size) {
            0 -> state.field.add(flip)

            1 -> {
                state.field.remove(matches[0])
                capture(state, flip, matches[0])
            }

            2 -> {
                state.field.remove(matches[0])
                capture(state, flip, matches[0])
            }

            3 -> { // Bomb from deck
                matches.forEach { state.field.remove(it) }
                matches.forEach { capture(state, flip, it) }
                state.goCount += 1
            }
        }
    }

    private fun capture(state: GameState, a: GoStopCard, b: GoStopCard) {
        if (state.currentTurn == Player.HUMAN) {
            state.playerCaptured.add(a)
            state.playerCaptured.add(b)
        } else {
            state.cpuCaptured.add(a)
            state.cpuCaptured.add(b)
        }
    }

    fun cpuPlay(state: GameState): GoStopCard? {
        val cpu = state.opponentHand
        val field = state.field

        // Capture if possible
        val captureMove = cpu.firstOrNull { handCard ->
            field.any { it.month == handCard.month }
        }
        if (captureMove != null) {
            cpu.remove(captureMove)
            playCard(state, captureMove)
            return captureMove
        }

        // Otherwise drop weakest
        val chosen = cpu.minByOrNull {
            when (it.type) {
                CardType.BRIGHT -> 5
                CardType.ANIMAL -> 3
                CardType.RIBBON -> 2
                CardType.JUNK -> 1
            }
        } ?: cpu.first()

        cpu.remove(chosen)
        playCard(state, chosen)
        return chosen
    }

    fun isRoundOver(state: GameState): Boolean {
        return state.deck.isEmpty() ||
                state.playerHand.isEmpty() ||
                state.opponentHand.isEmpty()
    }

    fun shouldOfferGoStop(state: GameState): Boolean {
        return state.playerCaptured.size >= 7 && state.currentTurn == Player.HUMAN
    }

    fun calculateFinalScore(state: GameState): Pair<Int, Int> {

        fun score(list: List<GoStopCard>): Int {
            val brights = list.count { it.type == CardType.BRIGHT }
            val animals = list.count { it.type == CardType.ANIMAL }
            val ribbons = list.count { it.type == CardType.RIBBON }
            val junk = list.count { it.type == CardType.JUNK }

            var score = 0
            if (brights >= 3) score += (brights - 2) * 3
            if (animals >= 5) score += (animals - 4)
            if (ribbons >= 5) score += (ribbons - 4)
            if (junk >= 10) score += (junk - 9) / 2

            return score
        }

        return score(state.playerCaptured) to score(state.cpuCaptured)
    }
}
