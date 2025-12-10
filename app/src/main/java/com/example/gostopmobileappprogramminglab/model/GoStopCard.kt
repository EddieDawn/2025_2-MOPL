package com.example.gostopmobileappprogramminglab.model

enum class CardType { BRIGHT, RIBBON, ANIMAL, JUNK }

data class GoStopCard(
    val id: Int,
    val drawableRes: Int,
    val month: Int,
    val type: CardType
)
