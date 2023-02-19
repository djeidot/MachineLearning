package Snake

class Program {
    var mutationRate = 0.1f
    val planet = Planet(1, 2000, 1, mutationRate)

    init {
    }
    
    fun cycle(show: Boolean, snakeToShow: Int = -1) {
        if (show) {
            planet.playLegend(snakeToShow)
        } else {
            planet.train()
        }
    }
}


fun main(args: Array<String>) {
//    Run with:
//    kotlinc -include-runtime -d snake.jar src/Snake.main/kotlin/Snake.Program.kt 
//      src/Snake.main/kotlin/Snake.Cpu.kt src/Snake.main/kotlin/NeuralNet.Matrix.kt 
//      src/Snake.main/kotlin/NeuralNet.NeuralNet.kt src/Snake.main/kotlin/Snake.Planet.kt 
//      src/Snake.main/kotlin/Snake.Population.kt src/Snake.main/kotlin/Snake.Position.kt
//      src/Snake.main/kotlin/Snake.Snake.kt src/Snake.main/kotlin/Snake.Table.kt 
//      src/Snake.main/kotlin/Snake.World.kt    
//    java -jar .\snake.jar

//    val world = Snake.World(40, 15)
//    while (world.cycle()) {}

    val program = Program()
    val show = "show" in args
    if (show) {
        for (arg in args) {
            val index = arg.toIntOrNull()
            if (index != null) {
                program.cycle(true, index)
                return
            }
        }
        program.cycle(true)
    } else {
        val maxGenerations = args[0].toInt()
        for (counter in 1..maxGenerations) {
            program.cycle(false)
        }
    }
}