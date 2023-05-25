package SushiGo

import kotlin.random.Random

abstract class Player(val position: Position, val subPosition: Position = position) {
    var hand = mutableListOf<Cards>()
    val table = mutableMapOf<CardGroups, MutableList<Cards>>()
    val colorReset = "\u001b[0m"
    val colorBack = listOf("\u001b[48;5;166m", "\u001b[48;5;202m")
    val space = " "
    val hiddenCard = "X"
    var score = 0

    protected fun addToTable(card: Cards) {
        if (card.group !in table.keys) {
            table[card.group] = mutableListOf(card)
        } else {
            table[card.group]!!.add(card)
        }
    }

    fun addToHand(card: Cards) {
        hand.add(card)
    }

    fun getHandWidth() : Int =
        when (position) {
            Position.West, Position.East -> if (hand.size == 0) 0 else 5
            Position.North, Position.South -> if (hand.size == 0) 0 else hand.size * 3 + 1
        }

    fun getHandHeight() : Int =
        when (position) {
            Position.West, Position.East -> hand.size
            Position.North, Position.South -> if (hand.size == 0) 0 else 2
        }

    fun getTableWidth(): Int =
        when (position) {
            Position.West, Position.East -> if (table.keys.isEmpty()) 0 else 4
            Position.North, Position.South -> if (table.keys.isEmpty()) 0 else table.keys.count() * 5 - 1
        }

    fun getTableHeight(): Int =
        when (position) {
            Position.West, Position.East -> table.values.sumOf { it.size } + table.keys.count() * 2 - 1
            Position.North, Position.South -> (table.values.maxOfOrNull { it.size } ?: -1) + 1
        }
    
    fun drawHand() : List<String> {
        val lines = mutableListOf<String>()
        val hidden = this is CpuPlayer

        if (hand.size > 0) {
            if (position in listOf(Position.West, Position.East)) {
                for (card in hand.withIndex()) {
                    val cardColor = if (hidden) colorBack[card.index % 2] else card.value.group.bgColor
                    val cardFace = if (hidden) "$hiddenCard$hiddenCard" else card.value.symbol
                    lines.add(
                        when (position) {
                            Position.West -> "$cardColor  $cardFace $colorReset"
                            Position.East -> "$cardColor $cardFace  $colorReset"
                            else -> throw IllegalArgumentException()
                        }
                    )
                }
            } else {
                lines.add(hand.withIndex().joinToString("") { "${if (hidden) colorBack[it.index % 2] else it.value.group.bgColor} ${if (hidden) "$hiddenCard$hiddenCard" else it.value.symbol}" }
                        + "${if (hidden) colorBack[hand.lastIndex % 2] else hand.last().group.bgColor} $colorReset")
                lines.add(hand.withIndex().joinToString("") { "${if (hidden) colorBack[it.index % 2] else it.value.group.bgColor}   " }
                        + "${if (hidden) colorBack[hand.lastIndex % 2] else hand.last().group.bgColor} $colorReset")
            }
        }

        return when (position) {
            Position.North -> lines.reversed()
            else -> lines
        }
    }

    fun drawTable(): List<String> {
        val lines = mutableListOf<String>()

        if (position in listOf(Position.East, Position.West)) {
            for (group in table.keys) {
                for (cards in table[group]!!) {
                    lines.add("${group.bgColor} ${cards.symbol} $colorReset")
                }
                lines.add("${group.bgColor}    $colorReset")
                lines.add(space.repeat(4))
            }
        } else {
            for (i in 0 until (table.values.maxOfOrNull { it.size } ?: -1) + 1) {
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
        }
        
        return when (position) {
            Position.South -> lines
            Position.North -> lines.reversed()
            else -> lines.dropLast(1)
        }
    }

    abstract fun playRound()
    
    fun clearTable() {
        for (group in CardGroups.values().filterNot { it == CardGroups.Pudding }) {
            table.remove(group)
        }
    }

    fun getMakiRollCount(): Int {
        var count = 0
        table[CardGroups.Maki]?.forEach {
            count += when (it) {
                Cards.Maki1 -> 1
                Cards.Maki2 -> 2
                Cards.Maki3 -> 3
                else -> throw IllegalArgumentException()
            }
        }
        return count
    }

    fun reset() {
        score = 0
        hand.clear()
        table.clear()
    }
}
