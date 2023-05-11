package SushiGo

import NeuralNet.NeuralNet
import kotlin.random.Random

class CpuPlayer(position: Position, subPosition: Position = position) :
    Player(position, subPosition) {
        private val inputSize = 1 + // number of players
                1 + // current game
                1 + // current round 
                1 + // number of cards in hand
                1 + // current score 
                4 + // other players' scores
                15 + // cards in table
                4 * 15 + // other players' cards in table
                12 // cards in hand
        private val outputSize = 12 + 12 * 12   // cards (no chopsticks) plus cards (with chopsticks)
        val brain = NeuralNet(inputSize, 30, outputSize, false)
    
    override fun playRound() {
        val decision = runMachineLearning()

        val canUseChopsticks = (table[CardGroups.Chopsticks]?.size ?: 0) >= 1 && hand.size > 1
        // Play a card into your table
        val card = hand.random()
        hand.remove(card)
        addToTable(card)
        if (canUseChopsticks) {
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

    private fun runMachineLearning(): Int {
        val input = getInput()
        val decisionArray = brain.output(input)
        val filteredDecision = filterDecisions(decisionArray)
        return filteredDecision
    }

    private fun getInput(): FloatArray {
        // Input includes:
        // Number of players, divided by 5
        // Current game, divided by 10
        // Current round (0 -> 1, 0.5 -> 2, 1 -> 3)
        // Number of cards in hand, divided by 10
        // Current score, divided by 100, and with 100 subtracted (so -1.0 -> 0, 0.0 -> 100, 1.0 -> 200) 
        // Scores of remaining players starting with the one on your left (times 4, for non-existing players score remains at -1)
        // Table cards for current player, for each card type we divide the number of cards by 10.
        //   Card types are:
        //   - Nigiri1
        //   - Nigiri2
        //   - Nigiri3
        //   - Nigiri1 after Wasabi
        //   - Nigiri2 after Wasabi
        //   - Nigiri3 after Wasabi
        //   - Wasabi, only unused ones
        //   - Maki1
        //   - Maki2
        //   - Maki3
        //   - Tempura
        //   - Sashimi
        //   - Dumplings
        //   - Pudding
        //   - Chopsticks
        // Table cards for all other players, starting with the one on your left (times 4, for non-existing players all cards have 0).
        // Hand cards for current player, for each card type we calculate value as (1 - 1 / (number of cards + 1))
        //   Card types are:
        //   - Nigiri1
        //   - Nigiri2
        //   - Nigiri3
        //   - Wasabi
        //   - Maki1
        //   - Maki2
        //   - Maki3
        //   - Tempura
        //   - Sashimi
        //   - Dumplings
        //   - Pudding
        //   - Chopsticks        
        val input = FloatArray(inputSize) { 0f }

        var index = 0
        input[index++] = Main.playerSize / 5f
        input[index++] = Main.currentGame / 10f
        input[index++] = (Main.currentRound - 1) / 2f
        input[index++] = hand.size / 10f

        fillAllPlayers(this, input, index, -1f, 1) { player, input, startIndex ->
            var index = startIndex
            input[index++] = (player.score / 100f) - 100f
            index
        }

        fillAllPlayers(this, input, index, 0f, 15) { player, input, startIndex ->
            addTableToInput(player, input, startIndex)
        }

        fillAllPlayers(this, input, index, 0f, 12) { player, input, startIndex ->
            addHandToInput(player, input, startIndex)
        }
        return input
    }

    private fun filterDecisions(decisionArray: FloatArray): Int {
        
    }

    companion object {
        fun fillAllPlayers(
            firstPlayer: Player,
            input: FloatArray,
            startIndex: Int,
            defaultValue: Float,
            defaultSize: Int,
            func: (Player, FloatArray, Int) -> Int
        ): Int {
            var index = startIndex
            index = func(firstPlayer, input, index)
            var nextPlayer = Main.getPlayerToTheLeft(firstPlayer)
            while (nextPlayer != firstPlayer) {
                index = func(nextPlayer, input, index)
                nextPlayer = Main.getPlayerToTheLeft(nextPlayer)
            }
            for (nonPlayer in Main.playerSize..4) {
                repeat(defaultSize) { input[index++] = defaultValue }
            }
            return index
        }

        fun addTableToInput(player: Player, input: FloatArray, startIndex: Int): Int {
            var index = startIndex
            val nigiris = IntArray(7) { 0 }
            player.table[CardGroups.Nigiri]?.forEach {
                when (it) {
                    Cards.Nigiri1 -> if (nigiris[6] > 0) {
                        nigiris[3]++; nigiris[6]--
                    } else nigiris[0]++

                    Cards.Nigiri2 -> if (nigiris[6] > 0) {
                        nigiris[4]++; nigiris[6]--
                    } else nigiris[1]++

                    Cards.Nigiri3 -> if (nigiris[6] > 0) {
                        nigiris[5]++; nigiris[6]--
                    } else nigiris[2]++

                    Cards.Wasabi -> nigiris[6] = 1
                    else -> throw IllegalArgumentException("Non-Nigiri card found in Nigiri group")
                }
            }
            for (nigiri in nigiris) {
                input[index++] = nigiri / 10f
            }
            input[index++] = (player.table[CardGroups.Maki]?.count { it == Cards.Maki1 } ?: 0) / 10f
            input[index++] = (player.table[CardGroups.Maki]?.count { it == Cards.Maki2 } ?: 0) / 10f
            input[index++] = (player.table[CardGroups.Maki]?.count { it == Cards.Maki3 } ?: 0) / 10f
            input[index++] = (player.table[CardGroups.Tempura]?.size ?: 0) / 10f
            input[index++] = (player.table[CardGroups.Sashimi]?.size ?: 0) / 10f
            input[index++] = (player.table[CardGroups.Dumplings]?.size ?: 0) / 10f
            input[index++] = (player.table[CardGroups.Pudding]?.size ?: 0) / 10f
            input[index++] = (player.table[CardGroups.Chopsticks]?.size ?: 0) / 10f
            return index
        }

        fun addHandToInput(player: Player, input: FloatArray, startIndex: Int): Int {
            var index = startIndex
            input[index++] = (1f - 1f / (player.hand.count { it == Cards.Nigiri1 } + 1f))
            input[index++] = (1f - 1f / (player.hand.count { it == Cards.Nigiri2 } + 1f))
            input[index++] = (1f - 1f / (player.hand.count { it == Cards.Nigiri3 } + 1f))
            input[index++] = (1f - 1f / (player.hand.count { it == Cards.Wasabi } + 1f))
            input[index++] = (1f - 1f / (player.hand.count { it == Cards.Maki1 } + 1f))
            input[index++] = (1f - 1f / (player.hand.count { it == Cards.Maki2 } + 1f))
            input[index++] = (1f - 1f / (player.hand.count { it == Cards.Maki3 } + 1f))
            input[index++] = (1f - 1f / (player.hand.count { it == Cards.Tempura } + 1f))
            input[index++] = (1f - 1f / (player.hand.count { it == Cards.Sashimi } + 1f))
            input[index++] = (1f - 1f / (player.hand.count { it == Cards.Dumplings } + 1f))
            input[index++] = (1f - 1f / (player.hand.count { it == Cards.Pudding } + 1f))
            input[index++] = (1f - 1f / (player.hand.count { it == Cards.Chopsticks } + 1f))
            return index
        }

    }
}

