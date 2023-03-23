package SushiGo

import kotlin.math.max

class Main {
    private val players = mutableListOf<Player>()
    private val playerDistance = 6
    
    fun run(playerSize: Int) {
        players.clear()
        players.addAll(
            when (playerSize) {
                2 -> listOf (
                    Player(Position.South, false),
                    Player(Position.North, true)
                )
                3 -> listOf(
                    Player(Position.South, false),
                    Player(Position.North, true),
                    Player(Position.East, true)
                )
                4 -> listOf(
                    Player(Position.South, false),
                    Player(Position.West, true),
                    Player(Position.North, true),
                    Player(Position.East, true)
                )
                5 -> listOf(
                    Player(Position.South, false),
                    Player(Position.West, true),
                    Player(Position.North, true, Position.West),
                    Player(Position.North, true, Position.East),
                    Player(Position.East, true)
                )
                else -> throw IllegalArgumentException("Player size must be 2-5")
            }
        )
        
        draw()
    }

    private fun draw() {
        val tableWidth = 1 +
            (players.firstOrNull { it.position == Position.West }?.let { it.getHandWidth() + 1 + it.getTableWidth() }
                ?: 0) +
            playerDistance + max(
                players.filter { it.position == Position.North }
                    .let { filtered -> filtered.sumOf { max(it.getHandWidth(), it.getTableWidth()) } + (filtered.size - 1) * playerDistance },
                players.first { it.position == Position.South }.let { max(it.getHandWidth(), it.getTableWidth()) }
            ) + playerDistance +
            (players.firstOrNull { it.position == Position.East }?.let { it.getHandWidth() + it.getTableWidth() }
                ?: 0) +
            1

        val tableHeight = 1 +
            max(
                players.filter { it.position == Position.North }
                    .maxOf { it.getHandHeight() + 1 + it.getTableHeight() } +
                    playerDistance +
                    players.first { it.position == Position.South }
                        .let { it.getTableHeight() + 1 + it.getHandHeight() },
                3 + max(
                    (players.firstOrNull { it.position == Position.West }
                        ?.let { max(it.getHandHeight(), it.getTableHeight()) } ?: 0),
                    (players.firstOrNull { it.position == Position.East }
                        ?.let { max(it.getHandHeight(), it.getTableHeight()) } ?: 0)
                ) + 3
            )

        val bigLines = mutableListOf<String>()
        bigLines.add("+" + "-".repeat(tableWidth - 2) + "+")
        for (i in 1..(tableHeight - 2)) {
            bigLines.add("|" + " ".repeat(tableWidth - 2) + "|")
        }
        bigLines.add("+" + "-".repeat(tableWidth - 2) + "+")

        fun mapBlock(lines: MutableList<String>, row: Int, col: Int, width: Int, block: List<String>) {
            for (i in block.indices) {
                val realCol = if (col < 0) lines[row + i].length + col else col
                lines[row + i] = lines[row + i].replaceRange(realCol, realCol + width, block[i])
            }
        }

        // Need to draw South and North first, then West and East
        players.filter { it.position == Position.South }.forEach{ player ->
            val areaWidth = max(player.getHandWidth(), player.getTableWidth())
            val areaStart = if (players.size == 3) playerDistance else (tableWidth - areaWidth) / 2
            mapBlock(
                bigLines,
                tableHeight - 1 - player.getHandHeight() - 1 - player.getTableHeight(),
                areaStart + (areaWidth - player.getTableWidth()) / 2,
                player.getTableWidth(),
                player.drawTable()
            )
            mapBlock(
                bigLines,
                tableHeight - 1 - player.getHandHeight(),
                areaStart + (areaWidth - player.getHandWidth()) / 2,
                player.getHandWidth(),
                player.drawHand()
            )
        }
        players.filter { it.position == Position.North }.forEach { player ->
            if (player.subPosition == Position.North) {
                val areaWidth = max(player.getHandWidth(), player.getTableWidth())
                val areaStart = if (players.size == 3) playerDistance else (tableWidth - areaWidth) / 2
                mapBlock(
                    bigLines,
                    1,
                    areaStart + (areaWidth - player.getHandWidth()) / 2,
                    player.getHandWidth(),
                    player.drawHand()
                )
                mapBlock(
                    bigLines,
                    1 + player.getHandHeight() + 1,
                    areaStart + (areaWidth - player.getTableWidth()) / 2,
                    player.getTableWidth(),
                    player.drawTable()
                )
            } else {
                val areaWidth = players.filter { it.position == Position.North }.let { filtered ->
                    filtered.sumOf {
                        max(
                            it.getHandWidth(),
                            it.getTableWidth()
                        )
                    } + playerDistance * (filtered.size - 1)
                }
                val handHeight = players.filter { it.position == Position.North }.maxOf { it.getHandHeight() }
                val playerWidth = max(player.getHandWidth(), player.getTableWidth())
                val areaStart = if (player.subPosition == Position.West) (tableWidth - areaWidth) / 2 
                     else -((tableWidth - areaWidth) / 2 + playerWidth + 1)

                mapBlock(
                    bigLines,
                    1,
                    areaStart + (playerWidth - player.getHandWidth()) / 2,
                    player.getHandWidth(),
                    player.drawHand()
                )
                mapBlock(
                    bigLines,
                    1 + handHeight + 1,
                    areaStart + (playerWidth - player.getTableWidth()) / 2,
                    player.getTableWidth(),
                    player.drawTable()
                )
            }
        }
        players.filter { it.position == Position.West }.forEach { player ->
            mapBlock(
                bigLines,
                (tableHeight - player.getTableHeight()) / 2,
                1 + player.getHandWidth() + 1,
                player.getTableWidth(),
                player.drawTable()
            )
            mapBlock(
                bigLines,
                (tableHeight - player.getHandHeight()) / 2,
                1,
                player.getHandWidth(),
                player.drawHand()
            )
        }
        players.filter { it.position == Position.East }.forEach { player ->
            mapBlock(
                bigLines,
                (tableHeight - player.getTableHeight()) / 2,
                -(1 + player.getHandWidth() + 1 + player.getTableWidth()),
                player.getTableWidth(),
                player.drawTable()
            )
            mapBlock(
                bigLines,
                (tableHeight - player.getHandHeight()) / 2,
                -(1 + player.getHandWidth()),
                player.getHandWidth(),
                player.drawHand()
            )
        }

        bigLines.forEach { println(it) }
    }
}

fun main() {
    val main = Main()
    print("Select number of players: ")
    val playerSize = readln().toInt()
    main.run(playerSize)
}