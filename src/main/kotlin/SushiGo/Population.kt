package SushiGo

import NeuralNet.NeuralNet

class Population(val players: List<Player>) {
    private val numberBrains = 1200
    private val mutationRate = 0.1f
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
    private var generation = 1

    class Skull(val brain: NeuralNet) {
        var totalScore = 0
        var maxScore = 0
    }

    private val skulls = Array(numberBrains) { Skull(NeuralNet(inputSize, 30, outputSize, false)) }

    fun train() {
        while (true) {
            skulls.shuffle()

            for (i in skulls.indices step players.size) {
                for (j in players.indices) {
                    val player = players[j] as CpuPlayer
                    player.setSkull(skulls[i + j])
                }

                for (game in 1..10) {
                    Main.playGame(true)
                    players.forEach { (it as CpuPlayer).updateSkullScore() }
                }
            }

            skulls.sortByDescending { it.totalScore }

            printData()
            reproduce()
        }
    }

    fun printData() {
        println("Generation: ${generation}; "
                + "highest score (10 games): ${skulls[0].totalScore}); "
                + "highest game score: ${skulls.maxOf { it.maxScore } }")
    }
}