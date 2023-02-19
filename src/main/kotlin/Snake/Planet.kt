package Snake

import NeuralNet.NeuralNet

class Planet(numberSpecies: Int, populationSize: Int, snakesSize: Int, mutationRate: Float) {
    val species: Array<Population>
    val snakesOfLegend: Array<World>
    val topBrains: Array<NeuralNet?>
    
    var generation = 0
    var bestScore = 0
    var bestScoreFitness = 0L
    
    val snakesStoredPath = "data/Snake/snakesStored.csv"
    
    init {
        // initiate species
        species = Array(numberSpecies) { Population(populationSize, mutationRate, it) }
        
        // initiate snakes of legends
        snakesOfLegend = Array(snakesSize) { World(40, 15) }
        
        // initiate topbrains
        topBrains = Array(snakesSize) { null }
    }

    fun train() {
        species.forEach { it.currentBest = 0 }
        while (!done()) {
            updateAlive()
        }
        
        species.forEach { 
            it.calcFitness()
            it.setBestSnake()
        }
        printData()
        geneticAlgorithm()
    }

    private fun done() = species.all { it.done() }

    private fun updateAlive() {
//        println("Snakes alive: ${species.joinToString { it.countAlive().toString() }}")
        species.forEach { it.updateAlive() }
    }

    private fun geneticAlgorithm() {
        species.forEach { 
            it.naturalSelection()
        }
        generation++
        setTopScore()
        // if any of the top snakes from the species is better than any of the saved snakes then save them
        species.forEach { saveTopSnake(it) }
    }

    private fun setTopScore() {
        val max = species.withIndex().maxBy { it.value.globalBestFitness }
        bestScore = species[max.index].globalBest
        bestScoreFitness = species[max.index].globalBestFitness
    }

    // saves the top snake of the species if it has a better score than any of the legends
    private fun saveTopSnake(species: Population) {
        val snakesStoredTable = Table.load(snakesStoredPath)
        
        val tr = if (snakesStoredTable.isEmpty()) List(snakesOfLegend.size) {"0"} else snakesStoredTable.getAt(0)
        val snakeIndices = tr.toTypedArray()
        val snakeNo = snakeIndices.indexOfFirst { it.toInt() == 0 }

        if (snakeNo >= 0) {
            species.globalBestSnake.clone().saveSnake(snakeNo, species.globalBest, species.globalBestFitness, species.populationID)
            snakeIndices[snakeNo] = "1"

            snakesStoredTable.setAt(0, *snakeIndices)
            snakesStoredTable.save(snakesStoredPath)
        } else {
            // if snake positions are full, check for snakes from this population to stop snakes from the same generation populating the entire legend list
            for (snakeIndex in snakeIndices.indices) {
                val t1 = Table.load("data/Snake/SnakeStats$snakeIndex.csv")
                if (t1.isEmpty()) {
                    species.globalBestSnake.clone().saveSnake(snakeIndex, species.globalBest, species.globalBestFitness, species.populationID)
                    return
                }
                val tr1 = t1.getAt(1)
                if (species.populationID == tr1[2].toInt()) {
                    if (species.globalBest > tr1[0].toInt() || (species.globalBest == tr1[0].toInt() && species.globalBestFitness > tr1[1].toLong())) {
                        species.globalBestSnake.clone().saveSnake(snakeIndex, species.globalBest, species.globalBestFitness, species.populationID)
                    }
                    return
                }
            }
        }
        // if no snakes from this species are stored then overload the legend with the lowest score if its lower than the score of the top snake of this species
        var min = species.globalBest
        var minIndex = -1
        for (snakeIndex in snakeIndices.indices) {
            val t1 = Table.load("data/Snake/SnakeStats$snakeIndex.csv")
            if (!t1.isEmpty()) {
                val tr1 = t1.getAt(1)
                tr1[0].toInt().let {
                    if (it < min) {
                        min = it
                        minIndex = snakeIndex
                    }
                }
            }
        }
        
        // if the snake to be saved isn't better than any of them don't save it
        if (minIndex != -1) {
            species.globalBestSnake.clone().saveSnake(minIndex, species.globalBest, species.globalBestFitness, species.populationID)
        }
    }

    fun playLegend(snakeToShow: Int): Boolean {
        loadBestSnakes()
        val snake = if (snakeToShow == -1) snakesOfLegend.maxBy { it.fitness } else snakesOfLegend[snakeToShow]
        while (snake.cycle(true)) {}
        return false
    }

    private fun loadBestSnakes() {
        for (i in 0 .. snakesOfLegend.lastIndex) {
            snakesOfLegend[i] = snakesOfLegend[i].loadSnake(i)
        }
    }

    fun printData() {
        println("Generation: ${generation}; "
            + "biggest size: ${species.maxOf { it.currentBest }}(${bestScore}); "
            + "average fitness: ${species.sumOf { it.snakes.sumOf { it.fitness } } / (species.size * species[0].populationSize).toDouble() }; "
            + "best fitness: ${species.maxOf { it.snakes.maxOf { it.fitness } }}(${bestScoreFitness})")
    }
}