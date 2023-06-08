package SushiGo

import NeuralNet.NeuralNet
import Snake.Table

class Skull(val brain: NeuralNet) {
    val dataPath = "data/SushiGo"
    var totalScore = 0
    var maxScore = 0

    fun clone(): Skull {
        val newBrain =
            NeuralNet(this.brain.inputNodes, this.brain.hiddenNodes, this.brain.outputNodes, this.brain.useSecond)
        newBrain.copyFrom(this.brain)
        return Skull(newBrain)
    }

    fun crossover(partner: Skull): Skull {
        val childBrain = this.brain.crossover(partner.brain)
        return Skull(childBrain)
    }

    fun mutate(mutationRate: Float) {
        brain.mutate(mutationRate)
    }

    fun saveSkull(generation: Int) {
//        // saves top skull in file. Every 50 generations files are copied to a new index
//        var lastIndex = 0
//        while (File("$dataPath/Skull$lastIndex.csv").exists()) {
//            lastIndex++
//        }
//        if (generation / 50 > lastIndex - 1) {
//            for (i in lastIndex - 1 downTo 0) {
//                File("$dataPath/Skull$i.csv").copyTo(File("$dataPath/Skull${i + 1}.csv"), overwrite = true)
//                File("$dataPath/SkullStats$i.csv").copyTo(File("$dataPath/SkullStats${i + 1}.csv"), overwrite = true)
//            }
//        }

        println("Saving skull - total score: $totalScore, max score: $maxScore")

        val brain = this.clone().brain.netToTable()
        brain.save("$dataPath/Skull${generation.toString().padStart(3, '0')}.csv")

        val skullStats = Table()
        skullStats.add("TotalScore,MaxScore")
        skullStats.add("$totalScore,$maxScore")
        skullStats.save("$dataPath/SkullStats${generation.toString().padStart(3, '0')}.csv")
    }

    fun loadBrain(filePath: String) {
        // loads skull from file
        val table = Table.load(filePath)
        if (!table.isEmpty()) {
            brain.tableToNet(table)
        }
    }
}