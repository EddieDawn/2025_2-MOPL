package com.example.gostopmobileappprogramminglab.model

data class GameState(
    val playerHand: MutableList<GoStopCard>,
    val opponentHand: MutableList<GoStopCard>,
    val field: MutableList<GoStopCard>,
    val deck: MutableList<GoStopCard>,
    var currentTurn: Player,
    val playerCaptured: MutableList<GoStopCard> = mutableListOf(),
    val cpuCaptured: MutableList<GoStopCard> = mutableListOf(),

    var goCount: Int = 0,          // how many times the current player has said "Go"
    var multiplier: Int = 1        // score multiplier

)

enum class Player { HUMAN, CPU }

