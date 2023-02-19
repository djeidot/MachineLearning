package Snake

import kotlin.random.Random

class Population(val populationSize: Int, val globalMutationRate: Float, val populationID: Int) {
    var globalBest = 0 // the best score ever achieved by this population
    var currentBest = 0 // the current best score
    var globalBestFitness = 0L  // the best fitness ever achieved by this population

    var snakes: Array<World>
    var currentBestSnakeIndex = 0   //the position of the current best snake (highest score) in the array
    var globalBestSnake: World   // a clone of the best snake this population has ever seen

//    val populationID = Random.nextInt(10000)    // a random number to identify the population

    init {
        snakes = Array(populationSize) { World(40, 15) }
        globalBestSnake = snakes[0].clone()
    }

    fun updateAlive() {
        for (snake in snakes) {
            if (snake.snakeAlive) {
                snake.cycle(false)
            }
        }
        setCurrentBest()
    }

    private fun setCurrentBest() {
        if (done()) return

        val bestSnake = snakes.withIndex()
            .filter { it.value.snakeAlive }
            .maxBy { it.value.snake.tail.size }
        val maxTailSize = bestSnake.value.snake.tail.size 

        if (maxTailSize > currentBest) {
            currentBest = maxTailSize
        }

        //if the best length is more than 1 greater than the 5 stored in currentBest snake then set it;
        //the + 5 is to stop the current best snake from jumping from snake to snake
        if (!snakes[currentBestSnakeIndex].snakeAlive || maxTailSize > snakes[currentBestSnakeIndex].snake.tail.size) {
            currentBestSnakeIndex = bestSnake.index
        }

        if (currentBest > globalBest) {
            globalBest = currentBest
        }
    }

    fun done() = snakes.none { it.snakeAlive }
    
    fun calcFitness() {
        snakes.forEach { it.calcFitness() }
    }

    fun naturalSelection() {
        val newSnakes = mutableListOf<World>()    // next generation of snakes
        
        // transition the 10% best snakes
        val transitionedSnakesSize = 10 //populationSize / 10
        val sortedSnakes = snakes.sortedByDescending { it.fitness }
        for (i in 0 until transitionedSnakesSize) {
            newSnakes.add(sortedSnakes[i].clone())
        }
        // randomize the next 20%
        val randomizedSize = populationSize / 20
        for (i in transitionedSnakesSize until randomizedSize) {
            newSnakes.add(World(snakes[0].width, snakes[0].height))
        }
        
        // crossover and mutate the rest
        for (i in transitionedSnakesSize until populationSize) {
            // select 2 parents based on fitness
            val parent1 = selectSnake()
            val parent2 = selectSnake()
            
            val child = parent1.crossover(parent2)
            child.mutate(globalMutationRate)
            newSnakes.add(child)
        }
        snakes = newSnakes.toTypedArray()
    }

    fun setBestSnake() {
        // calculate max fitness
        val maxFitnessSnake = snakes.maxBy { it.fitness }
        currentBest = maxFitnessSnake.snake.tail.size
        
        if (maxFitnessSnake.fitness > globalBestFitness) {
            globalBestFitness = maxFitnessSnake.fitness
            globalBestSnake = maxFitnessSnake
            globalBest = currentBest
        }
    }

    fun selectSnake(): World {
        // this function works by randomly choosing a value between 0 and the sum of all the fitnesses
        // then go through all the snakes and add their fitness to a running sum and if that sum is greated than
        // the random value generated that snake is chosen.
        // since snakes with a higher fitness function add more to the running sum they have a higher chance
        // of being chosen.
        
        // choose only from the 10% best snakes
        val sortedSnakes = snakes.sortedByDescending { it.fitness }.take(populationSize / 10)
        
        val fitnessSum = sortedSnakes.sumOf { it.fitness }
        val rand = Random.nextLong(fitnessSum)

        var runningSum = 0L
        for (snake in sortedSnakes) {
            runningSum += snake.fitness
            if (runningSum > rand) {
                return snake
            }
        }
        // unreachable code to make the parser happy
        return sortedSnakes[0]
    }
}
