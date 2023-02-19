package Snake

import kotlin.random.Random

object CS
{
    const val Green = "\u001b[32m"
    const val Red = "\u001b[31m"
    const val ResetColor = "\u001b[0m"
    
    fun clear() = print("\u001b[2J\u001B[H")
    fun printAt(x: Int, y: Int, prt: String = "") = print("\u001b[${y + 1};${x + 1}H$prt")
    fun printAt(pos: Position, prt: String = "") = printAt(pos.x, pos.y, prt)
}

class World(val width: Int, val height: Int) {
    val snake = Snake(width / 2, height / 2)
    val fruits = Array(1) { getFruit() }
    private var direction = Direction.East
    private var speed = 1
    private var player = Cpu(snake, this)
    private var score = 0
    private var lifeTime = 0
    var fitness = 0L
    var snakeAlive = true
        private set

    private fun getFruit(): Position {
        var newFruit: Position
        do {
            newFruit = Position(Random.nextInt(width) + 1, Random.nextInt(height) + 2)
        } while (newFruit == snake.head || newFruit in snake.tail)
        return newFruit
    }

    fun cycle(show: Boolean): Boolean {
        if (show) {
            calcFitness()
            draw()
        }
        
        snake.leftToLive--

        if (snake.leftToLive == 0 || snake.touchesTail() || snake.touchesEdge(width, height)) {
            snakeAlive = false
            return false
        }

        val grow = snake.head in fruits
        if (grow) {
            fruits[fruits.indexOfFirst { it == snake.head }] = getFruit()
            snake.leftToLive = 250
            score++
        }

        val newDirection = player.getInput()
        if (snake.canMove(newDirection)) {
            direction = newDirection
        }
        
        snake.move(direction, grow)
        lifeTime++
        
        return true
    }
    
    fun draw() {
        CS.clear()
        println("SCORE: $score, FITNESS: $fitness")
        println("+${"-".repeat(width)}+")
        for (row in 1..height) {
            println("|${" ".repeat(width)}|")
        }
        print("+${"-".repeat(width)}+")
        fruits.forEach { CS.printAt(it, CS.Red + "*") }
//        Snake.CS.printAt(fruit, Snake.CS.Red + "*")
        CS.printAt(snake.head, CS.Green + "@")
        snake.tail.forEach { CS.printAt(it, CS.Green + "o") }
        CS.printAt(1, height + 2, CS.ResetColor + "\n")
        Thread.sleep((100 / speed).toLong())
    }

    fun clone(): World {
        val newWorld = World(width, height)
        newWorld.player.brain.copyFrom(player.brain)
        return newWorld
    }

    fun calcFitness(): Long {
        // fitness is based on length and lifetime
//        val length = snake.tail.size
//        fitness = if (length < 10) {
//            lifeTime * lifeTime * 2.0.pow(length).toLong()
//        } else {
//            // grows slower after 10 to stop fitness from getting stupidly big
//            lifeTime * lifeTime * 2.0.pow(10.0).toLong() * (length - 9)
//        }
//        return fitness
        val length = snake.tail.size
        fitness = (Math.log10(lifeTime.toDouble()) * 100.0).toLong() * length
        return fitness
    }

    fun crossover(partner: World): World {
        val child = World(width, height)
        child.player.brain = player.brain.crossover(partner.player.brain)
        return child
    }

    fun mutate(globalMutationRate: Float) {
        player.brain.mutate(globalMutationRate)
    }

    // saves the snake to a file by converting it to a table
    fun saveSnake(snakeNo: Int, score: Int, fitness: Long, populationID: Int) {
        println("Saving snake ${populationID}[$snakeNo] - score: $score, fitness: $fitness")
        
        val snakeStats = Table()
        snakeStats.add("TopScore,Fitness,PopulationID")
        snakeStats.add("$score,$fitness,$populationID")
        snakeStats.save("data/Snake/SnakeStats$snakeNo.csv")
        
        val snakeBrain = player.brain.netToTable()
        snakeBrain.save("data/Snake/Snake.Snake$snakeNo.csv")
    }

    fun loadSnake(snakeNo: Int): World {
        val newSnake = World(width, height)
        val table = Table.load("data/Snake.Snake$snakeNo.csv")
        if (!table.isEmpty()) {
            newSnake.player.brain.tableToNet(table)
        }
        return newSnake
    }
}

//fun Snake.main() {
////    Run with:
////    kotlinc -include-runtime -d snake.jar src/Snake.main/kotlin/Snake.World.kt 
////      src/Snake.main/kotlin/Snake.Position.kt src/Snake.main/kotlin/Snake.Cpu.kt 
////      src/Snake.main/kotlin/NeuralNet.NeuralNet.kt src/Snake.main/kotlin/Snake.Snake.kt 
////      src/Snake.main/kotlin/NeuralNet.Matrix.kt
////    java -jar .\snake.jar
//    
//    val world = Snake.World(40, 15)
//    while (world.cycle()) {}
//}