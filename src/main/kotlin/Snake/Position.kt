package Snake

data class Position(val x: Int = 0, val y: Int = 0) {
    operator fun plus(direction: Direction) = when (direction) {
        Direction.North -> Position(x, y - 1)
        Direction.South -> Position(x, y + 1)
        Direction.West -> Position(x - 1, y)
        Direction.East -> Position(x + 1, y)
        Direction.NorthEast -> Position(x + 1, y - 1)
        Direction.NorthWest -> Position(x - 1, y - 1)
        Direction.SouthWest -> Position(x - 1, y + 1)
        Direction.SouthEast -> Position(x + 1, y + 1)
    }
}

enum class Direction {
    North,
    South,
    East,
    West,
    NorthEast,
    NorthWest,
    SouthWest,
    SouthEast
}