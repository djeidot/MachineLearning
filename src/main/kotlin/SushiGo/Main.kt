package SushiGo

import kotlin.math.max

fun main() {
    val players = listOf(
        Player(Position.South, false),
        Player(Position.West, true),
        Player(Position.North, true),
        Player(Position.East, true)
    )

    fun draw() {
        val tableWidth = 1 +
            (players.firstOrNull { it.position == Position.West }?.let { it.getHandWidth() + 1 + it.getTableWidth() } ?: 0) +
            4 + max(
                players.filter { it.position == Position.North }.let { it.sumOf { max(it.getHandWidth(), it.getTableWidth()) } + (it.count() - 1) * 4 },
                players.first { it.position == Position.South }.let { max(it.getHandWidth(), it.getTableWidth()) }
            ) + 4 +
            (players.firstOrNull { it.position == Position.East }?.let { it.getHandWidth() + it.getTableWidth() } ?: 0) +
            1
        
        val tableHeight = 1 +
            max(
                players.filter { it.position == Position.North }.maxOf { it.getHandHeight() + 1 + it.getTableHeight() } +
                    4 +
                    players.first { it.position == Position.South }.let { it.getTableHeight() + 1 + it.getHandHeight() },
                3 + max(
                    (players.firstOrNull { it.position == Position.West }?.let { max(it.getHandHeight(), it.getTableHeight()) } ?: 0),
                    (players.firstOrNull { it.position == Position.East }?.let { max(it.getHandHeight(), it.getTableHeight()) } ?: 0)
                ) + 3
            )

        val bigLines = mutableListOf<String>()
        bigLines.add("+" + "-".repeat(tableWidth - 2) + "+")
        for (i in 1..(tableHeight - 2))
            bigLines.add("|" + " ".repeat(tableWidth - 2) + "|")
        bigLines.add("+" + "-".repeat(tableWidth - 2) + "+")

        fun mapBlock(lines: MutableList<String>, row: Int, col: Int, width: Int, block: List<String>) {
            for (i in block.indices) {
                lines[row + i] = lines[row + i].replaceRange(col, col + width, block[i])
            }
        }
        
        for (player in players) {
            when (player.position) {
                Position.South -> {
                    mapBlock(bigLines, tableHeight - 2 - player.getHandHeight() - 1 - player.getTableHeight(), (tableWidth - player.getTableWidth()) / 2, player.getTableWidth(), player.drawTable())
                    mapBlock(bigLines, tableHeight - 2 - player.getHandHeight(), (tableWidth - player.getHandWidth()) / 2, player.getHandWidth(), player.drawHand())
                }
                Position.West -> {
                    mapBlock(bigLines, (tableHeight - player.getHandHeight()) / 2, 1, player.getHandHeight(), player.drawHand())
                    mapBlock(bigLines, (tableHeight - player.getTableHeight()) / 2, 1 + player.getHandWidth() + 1, player.getTableWidth(), player.drawTable())
                }
                Pos
            }
        }

    }





}