package com.example.gostopmobileappprogramminglab.model

object CardFactory {

    fun createDeck(): MutableList<GoStopCard> {
        // Convert CardDefinition → GoStopCard
        val list = CardDefinitions.deck.mapIndexed { index, def ->
            GoStopCard(
                id = index,
                month = def.month,
                type = def.type,
                drawableRes = def.drawable,
                points = def.points
            )
        }.toMutableList()

        list.shuffle()
        return list
    }
}
