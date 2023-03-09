package SushiGo

import java.lang.Integer.max

class Player {
    val hand = mutableListOf<Cards>()
    val table = mutableMapOf<CardGroups, MutableList<Cards>>()
    val colorReset = "\u001b[0m"
    
    init {
        addToHand(Cards.Nigiri1)
        addToHand(Cards.Maki3)
        addToHand(Cards.Pudding)
        addToHand(Cards.Wasabi)
        
        addToTable(Cards.Maki2)
        addToTable(Cards.Maki1)
        addToTable(Cards.Nigiri1)
        addToTable(Cards.Tempura)
        addToTable(Cards.Tempura)
        addToTable(Cards.Chopsticks)
        addToTable(Cards.Pudding)
        addToTable(Cards.Sashimi)
        addToTable(Cards.Dumplings)
        addToTable(Cards.Dumplings)
        
        draw()
    }

    private fun addToTable(card: Cards) {
        if (card.group !in table.keys) {
            table[card.group] = mutableListOf(card)
        } else {
            table[card.group]!!.add(card)
        }
    }

    private fun addToHand(card: Cards) {
        hand.add(card)
    }

    private fun draw() {
        var bigTableWidth = 50
        var bigTableHeight = 15
        
//        +----++----++----++----+
//        | M2 || N1 || Te || Ch |
//        | M1 |+----+| Te |+----+
//        +----+      +----+
        val tableWidth = table.keys.size * 6
        val tableHeight = table.values.maxOf { it.size } + 3
//        +-------------+
//        | N1 M3 Pu Wa |
        val handWidth = hand.size * 3 + 3
        val handHeight = 3
        
        val overallWidth = max(tableWidth, handWidth)
        val overallHeight = tableHeight + handHeight
        
        val tableStart = (bigTableWidth - tableWidth) / 2
        val handStart = (bigTableWidth - handWidth) / 2
        
        val lines = mutableListOf<String>()
        lines.add("+" + "-".repeat(bigTableWidth - 2) + "+")
        for (i in 1..(bigTableHeight - overallHeight - 1))
            lines.add("|" + " ".repeat(bigTableWidth - 2) + "|")
        
        lines.add("|" + " ".repeat(tableStart - 1) + "+----+".repeat(table.keys.size) + " ".repeat(tableStart - 1) + "|")
        for (i in 0 until table.values.maxOf { it.size } + 2) {
            var line = "|" + " ".repeat(tableStart - 1)
            for ((group, cards) in table) {
                line += if (i < cards.size) {
                    "|${group.bgColor} ${cards[i].symbol} $colorReset|"
                } else if (i == cards.size) {
                    "|${group.bgColor}    $colorReset|"
                } else if (i == cards.size + 1) {
                    "+----+"
                } else {
                    " ".repeat(6)
                }
            }
            line += " ".repeat(tableStart - 1) + "|"
            lines.add(line)
        }
        
        lines.add("|" + " ".repeat(handStart - 1) + "+" + "-".repeat(handWidth - 2) + "+" + " ".repeat(handStart) + "|")
        lines.add("|" + " ".repeat(handStart - 1) + "|"
            + hand.joinToString("") { "${it.group.bgColor} ${it.symbol}" }
            + "${hand.last().group.bgColor} $colorReset"
            + "|"
            + " ".repeat(handStart) + "|")
        lines.add("|" + " ".repeat(handStart - 1) + "|"
            + hand.joinToString("") { "${it.group.bgColor}   " }
            + "${hand.last().group.bgColor} $colorReset"
            + "|"
            + " ".repeat(handStart) + "|")
        

        lines.add("+" + "-".repeat(bigTableWidth - 2) + "+")
        
        lines.forEach { println(it) }
    }
}
 