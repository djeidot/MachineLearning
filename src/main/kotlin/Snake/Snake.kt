package Snake

class Snake(initX: Int, initY: Int) {
    var head: Position
        private set

    val tail = mutableListOf<Position>()

    init {
        head = (Position(initX, initY))
        tail.add(Position(initX - 1, initY))
    }

    var lifetime = 0
    var fitness = 0L
    var leftToLive = 250
    var growCount = 0
    val alive = true
    val test = false

    fun canMove(direction: Direction): Boolean = tail.isEmpty() || head + direction != tail[0]

    fun move(direction: Direction, grow: Boolean) {
        tail.add(0, head)
        if (!grow) tail.removeLast()
        head += direction
    }

    fun touchesTail(): Boolean = head in tail

    fun touchesEdge(width: Int, height: Int): Boolean =
        head.x == 0 || head.x == width + 1 || head.y == 1 || head.y == height + 2

}
