package Snake

import NeuralNet.NeuralNet

interface Player {
    fun getInput(): Direction
}

class Cpu(val snake: Snake, val world: World) : Player {
    var current = Direction.East
    val vision = FloatArray(24)
    var brain = NeuralNet(24, 18, 4, false)

    override fun getInput(): Direction {
        look()
        return setVelocity()
    }

    // Looks in 8 directions to find food, walls, and its tail
    private fun look() {
        vision.fill(0.0F)

        fun getDistances(direction: Direction, index: Int) {
            val (foodDistance, tailDistance, wallDistance) = lookInDirection(direction)
            vision[index] = foodDistance
            vision[index + 1] = tailDistance
            vision[index + 2] = wallDistance
        }

        getDistances(Direction.East, 0)
        getDistances(Direction.NorthEast, 3)
        getDistances(Direction.North, 6)
        getDistances(Direction.NorthWest, 9)
        getDistances(Direction.West, 12)
        getDistances(Direction.SouthWest, 15)
        getDistances(Direction.South, 18)
        getDistances(Direction.SouthEast, 21)
    }

    private fun lookInDirection(direction: Direction): Triple<Float, Float, Float> {
        // set up a temp array to hold the values that are going to be passed to the Snake.main vision array
        var foodDistance = 0.0F
        var tailDistance = 0.0F

        val initialPosition = snake.head
        var distance = 0.0F

        // move once in the desired direction before starting
        var position = initialPosition + direction
        distance += when (direction) {
            Direction.South, Direction.North, Direction.West, Direction.East -> 1.0F
            else -> 2.0F
        }

        // look in the direction until you reach a wall
        while (position.x in 1..world.width && position.y in 2..world.height + 1) {
            // check for food at the position
            if (foodDistance == 0.0F && position == world.fruits[0]) {
                foodDistance = 1.0F / distance
            }

            // check for tail at the position
            if (tailDistance == 0.0F && position in snake.tail) {
                tailDistance = 1.0F / distance
            }

            // look further in the direction
            position += direction
            distance += when (direction) {
                Direction.South, Direction.North, Direction.West, Direction.East -> 1.0F
                else -> 2.0F
            }
        }

        // set the distance to the wall
        val wallDistance = 1.0F / distance

        return Triple(foodDistance, tailDistance, wallDistance)
    }

    private fun setVelocity(): Direction {
        // get the output of the neural network
        val decision = brain.output(vision)

        // get the maximum position in the output array and use this as the decision on which direction to go
        var max = 0.0F
        var maxIndex = 0
        for (i in decision.indices) {
            if (max < decision[i]) {
                max = decision[i]
                maxIndex = i
            }
        }

        // set the velocity based on this decision
        return when (maxIndex) {
            0 -> Direction.East
            1 -> Direction.North
            2 -> Direction.West
            3 -> Direction.South
            else -> throw IllegalArgumentException("Invalid maxIndex value: $maxIndex")
        }
    }
}

//class Human : Snake.Player {
//    override fun getInput(): Snake.Direction {
//        val df = readLine()
//        return Snake.Direction.East
//    }
//}