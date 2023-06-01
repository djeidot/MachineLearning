package SushiGo

import NeuralNet.NeuralNet
import kotlin.math.max
import kotlin.random.Random

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
    private var bestMaxScore = 0
    private var bestTotalScore = 0

    private var skulls = Array(numberBrains) { Skull(NeuralNet(inputSize, 30, outputSize, false)) }

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

            if (skulls[0].totalScore > bestTotalScore) {
                (skulls[0].saveSkull(generation))
            }

            bestTotalScore = max(bestTotalScore, skulls[0].totalScore)
            bestMaxScore = max(bestMaxScore, skulls.maxOf { it.maxScore })
            
            printData()
            reproduce()
        }
    }

    fun printData() {
        println("Generation: ${generation}; " +
                "highest score (10 games): ${skulls[0].totalScore}) (overall: $bestTotalScore); " +
                "highest game score: ${skulls.maxOf { it.maxScore } } (overall: $bestMaxScore)"
        )
    }

    private fun reproduce() {
        naturalSelection()
        generation++
    }

    private fun naturalSelection() {
        val newSkulls = mutableListOf<Skull>()    // next generation

        // transition the 10 best skulls
        val transitionedSkullsSize = 10 //numberBrains / 10
        val sortedSkulls = skulls.sortedByDescending { it.totalScore }
        for (i in 0 until transitionedSkullsSize) {
            newSkulls.add(sortedSkulls[i].clone())
        }
        // randomize the next 10%
        val randomizedSize = numberBrains / 10
        for (i in transitionedSkullsSize until randomizedSize) {
            newSkulls.add(Skull(NeuralNet(skulls[0].brain.inputNodes, skulls[0].brain.hiddenNodes, skulls[0].brain.outputNodes, skulls[0].brain.useSecond)))
        }

        // crossover and mutate the rest
        for (i in randomizedSize until numberBrains) {
            // select 2 parents based on fitness
            val parent1 = selectSkull()
            val parent2 = selectSkull()

            val child = parent1.crossover(parent2)
            child.mutate(mutationRate)
            newSkulls.add(child)
        }
        skulls = newSkulls.toTypedArray()
    }

    private fun selectSkull(): Skull {
        // this function works by randomly choosing a value between 0 and the sum of all the fitnesses
        // then go through all the skulls and add their fitness to a running sum and if that sum is greated than
        // the random value generated that skull is chosen.
        // since skulls with a higher fitness function add more to the running sum they have a higher chance
        // of being chosen.

        val sortedSkulls = skulls.sortedByDescending { it.totalScore }.take(numberBrains / 20)

        val fitnessSum = sortedSkulls.sumOf { (it.totalScore * it.totalScore).toLong() }
        val rand = Random.nextLong(fitnessSum)

        var runningSum = 0L
        for (skull in sortedSkulls) {
            runningSum += (skull.totalScore * skull.totalScore)
            if (runningSum > rand) {
                return skull
            }
        }
        // unreachable code to make the parser happy
        return sortedSkulls[0]
    }

}