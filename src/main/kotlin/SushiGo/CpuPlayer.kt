package SushiGo

import kotlin.random.Random

class CpuPlayer(position: Position, subPosition: Position = position) :
    Player(position, subPosition) {

    override fun playRound() {
        val hasChopsticksInTable = (table[CardGroups.Chopsticks]?.size ?: 0) >= 1
        // Play a card into your table
        val card = hand.random()
        hand.remove(card)
        addToTable(card)
        if (hasChopsticksInTable) {
            if (Random.nextBoolean()) {
                val cardTwo = hand.random()
                hand.remove(cardTwo)
                addToTable(cardTwo)
                val chop = table[CardGroups.Chopsticks]!!.removeFirst()
                hand.add(chop)
                if (table[CardGroups.Chopsticks]?.isEmpty() == true) {
                    table.remove(CardGroups.Chopsticks)
                }
            }
        }
    }
}