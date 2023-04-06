package SushiGo

import kotlin.random.Random

class HumanPlayer(position: Position, subPosition: Position = position) :
    Player(position, subPosition) {

    override fun playRound() {
        val hasChopsticksInTable = (table[CardGroups.Chopsticks]?.size ?: 0) >= 1
        // Play a card into your table
        validCards@ while (true) {
            print("Pick a card to play" + (if (hasChopsticksInTable) " (or two cards if you want to use the Chopsticks)" else "") + ": ")
            val input = readln()
            val cards = input.trim().split(" ")
            if (cards.size != 1 && (cards.size != 2 || !hasChopsticksInTable)) {
                println("You can only play one" + if (hasChopsticksInTable) " or two cards" else "" + ".")
                continue
            }
            val duplicates = cards.size == 2 && cards[0] == cards[1]
            for (card in cards) {
                val realCard = Cards.getFromSymbol(card)
                if (realCard == null) {
                    println("Unrecognized card $card")
                    continue@validCards
                } else if (!hand.contains(realCard)) {
                    println("Card $card not in hand")
                    continue@validCards
                } else if (duplicates && hand.count { it == realCard } < 2) {
                    println("Your hand does not contain 2 of $card")
                    continue@validCards
                }
            }
            for (card in cards) {
                val realCard = Cards.getFromSymbol(card)!!
                hand.remove(realCard)
                addToTable(realCard)
            }
            if (cards.size == 2) {
                val chop = table[CardGroups.Chopsticks]!!.removeFirst()
                hand.add(chop)
                if (table[CardGroups.Chopsticks]?.isEmpty() == true) {
                    table.remove(CardGroups.Chopsticks)
                }
            }
            break
        }
    }

}