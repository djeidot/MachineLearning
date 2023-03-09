package SushiGo

import java.lang.Integer.max

class Player {
    val hand = mutableListOf<Cards>()
    val table = mutableMapOf<CardGroups, MutableList<Cards>>()
    val colorReset = "\u001b[0m"
    val colorBack = listOf("\u001b[48;5;166m", "\u001b[48;5;202m")
    val space = " "

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
        
        drawNS()
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

    private fun drawWE() {
//        .M2.  
//        .M1.  
//        ....
//
//        .N1. .N1..
//        .... .M3..
//             .Pu..
//        .Te. .Wa..
//        .Te.
//        ....
//
//        .Ch.
//        ....
        var tableWidth = 4
        var tableHeight = table.values.sumOf { it.size + 2 } - 1
        
        var handWidth = 5
        var handHeight = hand.size
        
        var overallWidth = tableWidth + 1 + handWidth
        var overallHeight = max(tableHeight, handHeight)
        
        var lines1 = mutableListOf<String>()
        var lines2 = mutableListOf<String>()
        
        
    }
    private fun drawNS() {
        var bigTableWidth = 50
        var bigTableHeight = 20
        
//        .M2. .N1. .Te. .Ch.
//        .M1. .... .Te. ....
//        ....      ....
        val tableWidth = table.keys.size * 5 - 1
        val tableHeight = table.values.maxOf { it.size } + 1
//        .N1.M3.Pu.Wa.
//        .............        
        val handWidth = hand.size * 3 + 1
        val handHeight = 2
        
        val overallWidth = max(tableWidth, handWidth)
        val overallHeight = tableHeight + 1 + handHeight
        
        val tableStart = (overallWidth - tableWidth) / 2
        val handStart = (overallWidth - handWidth) / 2
        val areaStart = (bigTableWidth - overallWidth) / 2
        
        val lines1 = mutableListOf<String>()
        val lines2 = mutableListOf<String>()
        
        val handN = drawHand("N", true)
        val tableN = drawTable("N")

        val tableS = drawTable("S")
        val handS = drawHand("S", false)

        val bigLines = mutableListOf<String>()
        bigLines.add("+" + "-".repeat(bigTableWidth - 2) + "+")
        for (i in 1..(bigTableHeight - 2))
            bigLines.add("|" + " ".repeat(bigTableWidth - 2) + "|")
        bigLines.add("+" + "-".repeat(bigTableWidth - 2) + "+")

        mapBlock(bigLines, 1, areaStart + handStart, handWidth, handN)
        mapBlock(bigLines, handHeight + 2, areaStart + tableStart, tableWidth, tableN)
        mapBlock(bigLines, bigTableHeight - 1 - handHeight - 1 - tableHeight, areaStart + tableStart, tableWidth, tableS)
        mapBlock(bigLines, bigTableHeight - 1 - handHeight, areaStart + handStart, handWidth, handS)
        
        bigLines.forEach { println(it) }
    }

    private fun mapBlock(lines: MutableList<String>, row: Int, col: Int, width: Int, block: List<String>) {
        for (i in block.indices) {
            lines[row + i] = lines[row + i].replaceRange(col, col + width, block[i])
        }
    }

    private fun drawHand(position: String, hidden: Boolean) : List<String> {
        val lines = mutableListOf<String>()
        lines.add(hand.withIndex().joinToString("") { "${if (hidden) colorBack[it.index % 2] else it.value.group.bgColor} ${if (hidden) "  " else it.value.symbol}" }
            + "${if (hidden) colorBack[hand.lastIndex % 2] else hand.last().group.bgColor} $colorReset")
        lines.add(hand.withIndex().joinToString("") { "${if (hidden) colorBack[it.index % 2] else it.value.group.bgColor}   " }
            + "${if (hidden) colorBack[hand.lastIndex % 2] else hand.last().group.bgColor} $colorReset")

        return when (position) {
            "S" -> lines
            "N" -> lines.reversed()
            else -> lines
        }
    }

    private fun drawTable(position: String): List<String> {
        val lines = mutableListOf<String>()
        for (i in 0 until table.values.maxOf { it.size } + 1) {
            var line = ""
            for ((group, cards) in table) {
                line += if (i < cards.size) {
                    "${group.bgColor} ${cards[i].symbol} $colorReset$space"
                } else if (i == cards.size) {
                    "${group.bgColor}    $colorReset$space"
                } else {
                    space.repeat(5)
                }
            }
            lines.add(line.removeRange(line.lastIndex..line.lastIndex))
        }
        return when (position) {
            "S" -> lines
            "N" -> lines.reversed()
            else -> lines
        }
    }
}
 