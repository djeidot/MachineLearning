package SushiGo

import kotlin.math.max

class Main (val playerSize: Int) {
    private val players = mutableListOf<Player>()
    private val playerDistance = 6
    private val deck = mutableListOf<Cards>()
    
    fun run() {
        setupPlayers()
        
        for (round in 1..3) {
            dealCards()

            while (players.first().hand.size > 0) {
                draw()

                players.forEach {
                    it.playRound()
                }
                val handLastPlayer = players.last().hand
                for (i in players.lastIndex downTo 1) {
                    players[i].hand = players[i - 1].hand
                }
                players.first().hand = handLastPlayer
            }
            
            updateScore()
            if (round != 3) players.forEach { it.clearTable() }
        }
    }

    private fun updateScore() {
        // Maki - count maki rolls, most gets 6 points, second gets 3 points
        val makiRolls = players.withIndex().map { Pair(it.index, it.value.getMakiRollCount()) }.sortedByDescending { it.second }
        val makiHighScore = makiRolls[0].second
        
        val makiFirstPlayers = makiRolls.filter { it.second == makiHighScore }
        val makiFirstPlayerScore = makiHighScore / makiFirstPlayers.size
        makiFirstPlayers.forEach { players[it.first].score += makiFirstPlayerScore }

        if (makiFirstPlayers.size == 1) {
            val secondScore = makiRolls.drop(1)[0].second
            val secondPlayers = makiRolls.filter { it.second == secondScore }
            val secondPlayerScore = secondScore / secondPlayers.size
            secondPlayers.forEach { players[it.first].score += secondPlayerScore }
        }
        // Tempura - each 2 tempura cards score 5 points
        for (player in players) {
            val tempuraCount = player.table[CardGroups.Tempura]?.size ?: 0
            val tempuraScore = (tempuraCount / 2) * 5
            player.score += tempuraScore
        }
        // Sashimi - each 3 sashimi cards score 10 points
        for (player in players) {
            val sashimiCount = player.table[CardGroups.Sashimi]?.size ?: 0
            val sashimiScore = (sashimiCount / 3) * 10
            player.score += sashimiScore
        }
        // Dumplings - 1 2 3 4 5+ cards yield 1 3 6 10 15 points
        for (player in players) {
            val dumplingCount = player.table[CardGroups.Dumplings]?.size ?: 0
            val dumplingScore = (0..dumplingCount).reduce { sum, it -> sum + it }
            player.score += dumplingScore
        }
        // Nigiri - value on card, if on top of Wasabi then x3
        for (player in players) {
            var activateWasabi = false
            var nigiriScore = 0
            for (card in player.table[CardGroups.Nigiri] ?: listOf()) {
                when (card) {
                    Cards.Nigiri1 -> { 
                        nigiriScore += if (activateWasabi) 3 else 1
                        activateWasabi = false
                    }
                    Cards.Nigiri2 -> {
                        nigiriScore += if (activateWasabi) 6 else 2
                        activateWasabi = false
                    }
                    Cards.Nigiri3 -> {
                        nigiriScore += if (activateWasabi) 9 else 3
                        activateWasabi = false
                    }
                    Cards.Wasabi -> {
                        activateWasabi = true
                    }
                    else -> throw IllegalArgumentException()
                }
            }
            player.score += nigiriScore
        }
        // Pudding - most puddings gets +6, least puddings gets -6
//        var puddingCount = players.withIndex().map { Pair(it.index, it.value.table[CardGroups.Pudding]?.size ?: 0)}.sortedByDescending { it.second }
//        val puddingHighScore = puddingCount[0].second
//
//        val puddingFirstPlayers = makiRolls.filter { it.second == puddingHighScore }
//        val puddingFirstPlayerScore = makiHighScore / puddingFirstPlayers.size
//        puddingFirstPlayers.forEach { players[it.first].score += puddingFirstPlayerScore }
//
//        val puddingLowScore = puddingCount.last().second
//        if (players.size > 2 && puddingLowScore != puddingHighScore) {
//            val lastPlayers = makiRolls.filter { it.second == puddingLowScore }
//            val lastPlayerScore = puddingLowScore / lastPlayers.size
//            lastPlayers.forEach { players[it.first].score += puddingLowScore }
//        }
    }

    private fun setupPlayers() {
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
    }

    private fun dealCards() {
        Cards.setNewDeck(deck)
        
        val puddingCount = players.sumOf { it.table[CardGroups.Pudding]?.size ?: 0 }
        repeat(puddingCount) { deck.remove(Cards.Pudding) }
        
        for (player in players) {
            val dealtCards = 12 - playerSize
            repeat(dealtCards) { player.addToHand(deck.removeFirst()) }
        }        
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
    print("Select number of players: ")
    val playerSize = readln().toInt()
    val main = Main(playerSize)
    main.run()
}