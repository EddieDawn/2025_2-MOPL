package com.example.gostopmobileappprogramminglab.model

data class GameState(
    val playerHand: MutableList<GoStopCard>,
    val opponentHand: MutableList<GoStopCard>,
    val field: MutableList<GoStopCard>,
    val deck: MutableList<GoStopCard>,
    var currentTurn: Player,
    val playerCaptured: MutableList<GoStopCard> = mutableListOf(),
    val cpuCaptured: MutableList<GoStopCard> = mutableListOf(),
    var goCount: Int = 0,
    var multiplier: Int = 1,
    var pendingChoiceCard: GoStopCard? = null,
    var pendingChoices: MutableList<GoStopCard>? = null

)


enum class Player { HUMAN, CPU }
