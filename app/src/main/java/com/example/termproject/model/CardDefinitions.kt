package com.example.termproject.model

import com.example.termproject.R

data class CardDefinition(
    val month: Int,
    val type: CardType,
    val drawable: Int,
    val points: Int
)

object CardDefinitions {

    // One helper to convert month to filename prefix
    private fun prefix(month: Int): String {
        return when (month) {
            1 -> "january"
            2 -> "february"
            3 -> "march"
            4 -> "april"
            5 -> "may"
            6 -> "june"
            7 -> "july"
            8 -> "august"
            9 -> "september"
            10 -> "october"
            11 -> "november"
            12 -> "december"
            else -> throw IllegalArgumentException("Invalid month: $month")
        }
    }

    // Build the entire deck based on the filenames
    val deck: List<CardDefinition> = buildList {

        for (month in 1..12) {
            val p = prefix(month)

            // Bright card?
            val brightId = getDrawableId("${p}_bright")
            if (brightId != 0) {
                add(CardDefinition(month, CardType.BRIGHT, brightId, points = 20))
            }

            // Animal card?
            val animalId = getDrawableId("${p}_animal")
            if (animalId != 0) {
                add(CardDefinition(month, CardType.ANIMAL, animalId, points = 10))
            }

            // Ribbon?
            val ribbonId = getDrawableId("${p}_ribbon")
            if (ribbonId != 0) {
                add(CardDefinition(month, CardType.RIBBON, ribbonId, points = 5))
            }

            // Junk cards (some months have 2 or 3 junk images)
            for (i in 1..3) {
                val junkId = getDrawableId("${p}_junk_${i}")
                if (junkId != 0) {
                    add(CardDefinition(month, CardType.JUNK, junkId, points = 1))
                }
            }
        }
    }

    // Helper to get drawable resource ID by name
    private fun getDrawableId(name: String): Int {
        return try {
            val clazz = R.drawable::class.java
            val field = clazz.getField(name)
            field.getInt(null)
        } catch (e: Exception) {
            0
        }
    }
}

