package com.example.gostopmobileappprogramminglab.logic

import com.example.gostopmobileappprogramminglab.model.*

object GameEngine {

    // Create a new shuffled deck and deal hands + field.
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

    // Player or CPU plays a card onto the field.
    fun playCard(state: GameState, played: GoStopCard) {

        // find matches on the field with same month
        val matches = state.field.filter { it.month == played.month }

        when (matches.size) {

            0 -> {
                // No match? card goes to field
                state.field.add(played)
            }

            1 -> {
                // Simple capture
                state.field.remove(matches[0])
                capture(state, played, matches[0])
            }

            2 -> {
                // Three-card situation then auto pick first for simplicity
                state.field.remove(matches[0])
                capture(state, played, matches[0])
            }

            3 -> {
                // Bomb rule to capture all 3
                matches.forEach { state.field.remove(it) }
                matches.forEach { capture(state, played, it) }
                state.goCount += 1
            }
        }

        // Now flip top of deck and resolve
        if (state.deck.isNotEmpty()) {
            flipFromDeck(state)
        }
    }

    // Flip the deck's top card and apply matching logic.
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

            3 -> {
                // Bomb on flip
                matches.forEach { state.field.remove(it) }
                matches.forEach { capture(state, flip, it) }
                state.goCount += 1
            }
        }
    }

    // Put captured cards into the correct captured pile.
    private fun capture(state: GameState, a: GoStopCard, b: GoStopCard) {
        if (state.currentTurn == Player.HUMAN) {
            state.playerCaptured.add(a)
            state.playerCaptured.add(b)
        } else {
            state.cpuCaptured.add(a)
            state.cpuCaptured.add(b)
        }
    }

    /**
     * 1. Capture if possible.
     * 2. Avoid helping player by discarding "dangerous" months.
     * 3. Attempt bomb setup.
     * 4. Otherwise discard the weakest card.
     */
    fun cpuPlay(state: GameState) {

        val cpu = state.opponentHand
        val field = state.field

        // 1.capture if possible
        val captureMove = cpu.firstOrNull { handCard ->
            field.any { it.month == handCard.month }
        }
        if (captureMove != null) {
            cpu.remove(captureMove)
            playCard(state, captureMove)
            switchTurn(state)
            return
        }

        // 2. Avoid helping player: detect the months player has 2+ of
        val dangerousMonths = state.playerHand.groupBy { it.month }
            .filter { it.value.size >= 2 }
            .keys

        val safeMoves = cpu.filter { it.month !in dangerousMonths }
        if (safeMoves.isNotEmpty()) {
            val chosen = safeMoves.random()
            cpu.remove(chosen)
            playCard(state, chosen)
            switchTurn(state)
            return
        }

        // 3. Try to set up bomb (if field has exactly 2 duplicates of a month)
        val setupMove = cpu.firstOrNull { handCard ->
            field.count { it.month == handCard.month } == 2
        }
        if (setupMove != null) {
            cpu.remove(setupMove)
            playCard(state, setupMove)
            switchTurn(state)
            return
        }

        // 4. Fallback so it will play lowest-value card first
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
        switchTurn(state)
    }

    //Switch turns //
    fun switchTurn(state: GameState) {
        state.currentTurn = if (state.currentTurn == Player.HUMAN) Player.CPU else Player.HUMAN
    }

    // Round ends when deck or hands are empty. //
    fun isRoundOver(state: GameState): Boolean {
        return state.deck.isEmpty() ||
                state.playerHand.isEmpty() ||
                state.opponentHand.isEmpty()
    }

     //Bright / Ribbon / Animal / Junk scoring.//

    fun calculateFinalScore(state: GameState): Pair<Int, Int> {

        fun scorePile(list: List<GoStopCard>): Int {

            val brights = list.count { it.type == CardType.BRIGHT }
            val animals = list.count { it.type == CardType.ANIMAL }
            val ribbons = list.count { it.type == CardType.RIBBON }
            val junk = list.count { it.type == CardType.JUNK }

            var score = 0

            // Brights (simple)
            if (brights >= 3) score += (brights - 2) * 3

            // Animals
            if (animals >= 5) score += (animals - 4)

            // Ribbons
            if (ribbons >= 5) score += (ribbons - 4)

            // Junk
            if (junk >= 10) score += (junk - 9) / 2

            return score
        }

        val pScore = scorePile(state.playerCaptured)
        val cScore = scorePile(state.cpuCaptured)

        return pScore to cScore
    }
}
