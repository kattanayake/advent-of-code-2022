package day22

import PuzzleSolution

import sun.font.GlyphLayout.LayoutEngineFactory
import java.io.File
import java.lang.Exception
import java.lang.reflect.Executable

class MonkeyMap :PuzzleSolution{
    override fun solveFirst() {
        val (grid, instructions) = parseInput()

        //Padding rows
        val maxRowWidth = grid.maxBy { it.size }.size
        grid.forEach {
            while (it.size < maxRowWidth) it.add(GridContent.EMPTY)
        }

        val player = Player(
            xCoordinate = 0,
            yCoordinate = grid.first().indexOfFirst { it == GridContent.OPEN},
            direction = Direction.RIGHT
        )
        traverse(grid, instructions, player)
        println("Final Row: ${player.xCoordinate+1}, final column: ${player.yCoordinate+1}, direction: ${player.direction}")
        val answer = (1000*(player.xCoordinate + 1)) + (4 * (player.yCoordinate + 1)) + player.direction.value
        println("Answer: $answer")
    }

    override fun solveSecond() {
        val (grid, instructions) = parseInput()

        //Padding rows
        val maxRowWidth = grid.maxBy { it.size }.size
        grid.forEach {
            while (it.size < maxRowWidth) it.add(GridContent.EMPTY)
        }

        val player = Player(
            xCoordinate = 0,
            yCoordinate = grid.first().indexOfFirst { it == GridContent.OPEN},
            direction = Direction.RIGHT
        )
        traverse(grid, instructions, player, true)
        println("Final Row: ${player.xCoordinate+1}, final column: ${player.yCoordinate+1}, direction: ${player.direction}")
        val answer = (1000*(player.xCoordinate + 1)) + (4 * (player.yCoordinate + 1)) + player.direction.value
        println("Answer: $answer")
    }

    private fun traverse(
        grid: MutableList<MutableList<GridContent>>,
        instructions: List<String>,
        player: Player,
        withWrapping: Boolean = false
    ){
        instructions.forEach { instruction ->
            when(instruction){
                "L" -> player.direction = player.direction.turnLeft()
                "R" -> player.direction = player.direction.turnRight()
                else ->
                    if (withWrapping) travelInDirectionWithWrapping(grid, player, instruction)
                    else travelInDirection(grid, player, instruction)
            }
        }

    }

    private fun travelInDirectionWithWrapping(grid: MutableList<MutableList<GridContent>>, player: Player, numSteps: String) {
        var (xOffset, yOffset) = getOffsets(player.direction)
        var nextX = (player.xCoordinate + xOffset).mod(grid.size)
        var nextY = (player.yCoordinate + yOffset).mod(grid.first().size)
        var nextDirection: Direction? = null
        var stepsTaken = 0
        while (stepsTaken != numSteps.toInt()) {
            when (grid[nextX][nextY]) {
                GridContent.WALL -> return
                GridContent.OPEN -> {
                    player.xCoordinate = nextX
                    player.yCoordinate = nextY
                    nextDirection?.let {
                        player.direction = it
                        nextDirection = null
                        val newOffsets = getOffsets(player.direction)
                        xOffset = newOffsets.first
                        yOffset = newOffsets.second
                    }
                    nextX = (nextX + xOffset).mod(grid.size)
                    nextY = (nextY + yOffset).mod(grid.first().size)
                    stepsTaken++
                }
                GridContent.EMPTY -> {
                    val (nextCoordinates, newDirection) = crossFaces(player)
                    nextX = nextCoordinates.first
                    nextY = nextCoordinates.second
                    nextDirection = newDirection
                }
            }
        }
    }

    private fun travelInDirection(grid: MutableList<MutableList<GridContent>>, player: Player, numSteps: String) {
        val (xOffset, yOffset) = getOffsets(player.direction)
        var (nextX, nextY) = (player.xCoordinate + xOffset).mod(grid.size) to (player.yCoordinate + yOffset).mod(grid.first().size)
        var stepsTaken = 0
        while (stepsTaken != numSteps.toInt()) {
            when (grid[nextX][nextY]) {
                GridContent.WALL -> return
                GridContent.OPEN -> {
                    player.xCoordinate = nextX
                    player.yCoordinate = nextY
                    nextX = (nextX + xOffset).mod(grid.size)
                    nextY = (nextY + yOffset).mod(grid.first().size)
                    stepsTaken++
                }
                GridContent.EMPTY -> {
                    when(player.direction){
                        Direction.UP -> nextX = grid.indexOfLast { it[nextY] != GridContent.EMPTY }
                        Direction.RIGHT -> nextY = grid[nextX].indexOfFirst { it != GridContent.EMPTY }
                        Direction.DOWN -> nextX = grid.indexOfFirst { it[nextY] != GridContent.EMPTY }
                        Direction.LEFT -> nextY = grid[nextX].indexOfLast { it != GridContent.EMPTY }
                    }
                }
            }
        }
    }

    private fun getOffsets(direction: Direction) = when(direction){
        Direction.UP -> -1 to 0
        Direction.RIGHT -> 0 to 1
        Direction.DOWN -> 1 to 0
        Direction.LEFT -> 0 to -1
    }

    private fun parseInput(): Pair<MutableList<MutableList<GridContent>>, List<String>> {
        val grid = mutableListOf<MutableList<GridContent>>()
        lateinit var instructions: List<String>
        readTextByLine(INPUT, false).forEach { line ->
            if (line.isNotEmpty()) {
                if (line.contains(".")) grid.add(line.map { GridContent.fromChar(it) }.toMutableList())
                else instructions = line.split(Regex("((?<=[LR])|(?=[LR]))"))
            }
        }
        return grid to instructions
    }

    private fun getDiceFace(xCoordinate: Int, yCoordinate: Int): Int{
        val xZone = xCoordinate/50
        val yZone = yCoordinate/50
        return when (xZone){
            0 -> {
                when(yZone){
                    1 -> 1
                    2 -> 2
                    else -> throw Exception("Unknown yZone for xZone=0")
                }
            }
            1 -> {
                when(yZone){
                    1 -> 3
                    else -> throw Exception("Unknown yZone for xZone=1")
                }
            }
            2 -> {
                when(yZone){
                    0 -> 4
                    1 -> 5
                    else -> throw Exception("Unknown yZone for xZone=0")
                }
            }
            3 -> {
                when(yZone){
                    0 -> 6
                    else -> throw Exception("Unknown yZone for xZone=0")
                }
            }
            else -> throw Exception("Unknown xZone")
        }
    }

    /**
     * Grid layout
     *      1   2
     *      3
     *  4   5
     *  6
     *
     *  1 - Away
     *  3 - Top
     *  6 - Bottom
     *  5 - Towards
     *  4 - Left
     *  2 - Right
     *
     *  1   - Up    = 6 Left facing Right
     *      - Right = 2 Left facing Right
     *      - Left  = 4 Left Facing Right
     *      - Down  = 3 Up Facing Down
     *  2   - Up    = 6 Down facing Up
     *      - Right = 5 Right facing Left
     *      - Left  = 1 Right facing Left
     *      - Down  = 3 Right facing Left
     *  3   - Up    = 1 Down facing Up
     *      - Right = 2 Down Facing Up
     *      - Down  = 5 Up Facing Down
     *      - Left  = 4 Up Facing Down
     *  4   - Up    = 3 Left Facing Right
     *      - Right = 5 Left Facing Right
     *      - Down  = 6 Up facing Down
     *      - Left  = 1 Left facing Right
     *  5   - Up    = 3 Down facing Up
     *      - Right = 2 Right Facing Left
     *      - Down  = 6 Right Facing left
     *      - Left  = 4 Right Facing left
     *  6   - Up    = 4 Down facing Up
     *      - Right = 5 Down facing Up
     *      - Down  = 2 Up facing Down
     *      - Left  = 1 Up facing Down
     */
    private fun crossFaces(player: Player): Pair<Pair<Int, Int>, Direction> {
        val currentDiceFace = getDiceFace(player.xCoordinate, player.yCoordinate)
        val (localXCoord, localYCoord) = player.xCoordinate % GRID_SIZE to player.yCoordinate % GRID_SIZE
        val invertedXCoord = GRID_SIZE - 1 - localXCoord
        val (newCoordinates, newDirection) = when(currentDiceFace){
            1 -> {
                when(player.direction){
                    // Left of 1 to Left of 4
                    Direction.LEFT -> (edgeStart(4,Direction.UP) + invertedXCoord) to edgeStart(4, Direction.LEFT)
                    // Top of 1 to Left of 6
                    Direction.UP -> (edgeStart(6, Direction.UP) + localYCoord) to edgeStart(6, Direction.LEFT)
                    else -> throw Exception("Unexpected dice crossing")
                } to Direction.RIGHT
            }
            2 -> {
                when(player.direction){
                    // Bottom of 2 to Right of 3
                    Direction.DOWN -> ((edgeStart(3, Direction.UP) + localYCoord) to (edgeStart(3, Direction.RIGHT))) to Direction.LEFT
                    // Right of 2 to Right of 5
                    Direction.RIGHT -> ((edgeStart(5, Direction.UP) + invertedXCoord) to (edgeStart(5, Direction.RIGHT))) to Direction.LEFT
                    // Top of 2 to Bottom of 6
                    Direction.UP -> (edgeStart(6, Direction.DOWN) to (edgeStart(6, Direction.LEFT) + localYCoord)) to Direction.UP
                    else -> throw Exception("Unexpected dice crossing")
                }
            }
            3 -> {
                when(player.direction){
                    // Right edge of 3 to Bottom edge of 2
                    Direction.RIGHT -> ((edgeStart(2, Direction.DOWN)) to (edgeStart(2, Direction.LEFT) + localXCoord)) to Direction.UP
                    // Left edge of 3 to Top edge of 4
                    Direction.LEFT -> (edgeStart(4, Direction.UP) to (edgeStart(4, Direction.LEFT) + localXCoord)) to Direction.DOWN
                    else -> throw Exception("Unexpected dice crossing")
                }
            }
            4 -> {
                when(player.direction){
                    // Left edge of 4 to Left edge of 1
                    Direction.LEFT -> ((edgeStart(1, Direction.UP) + invertedXCoord) to edgeStart(1, Direction.LEFT))
                    // Top edge of 4 to Left edge of 3
                    Direction.UP -> ((edgeStart(3, Direction.UP) + localYCoord) to (edgeStart(3, Direction.LEFT)) )
                    else -> throw Exception("Unexpected dice crossing")
                } to Direction.RIGHT
            }
            5 -> {
                when(player.direction){
                    // Right edge of 5 to Right edge of 2
                    Direction.RIGHT -> ((edgeStart(2, Direction.UP) + invertedXCoord) to (edgeStart(2, Direction.RIGHT)))
                    // Bottom edge of 5 to Right edge of 6
                    Direction.DOWN -> ((edgeStart(6, Direction.UP) + localYCoord) to (edgeStart(6, Direction.RIGHT)))
                    else -> throw Exception("Unexpected dice crossing")
                }  to Direction.LEFT
            }
            6 -> {
                when(player.direction){
                    // Right edge of 6 to bottom edge of 5
                    Direction.RIGHT -> (edgeStart(5, Direction.DOWN) to (edgeStart(5, Direction.LEFT) + localXCoord)) to Direction.UP
                    // Bottom edge of 6 to top edge of 2
                    Direction.DOWN -> (edgeStart(2, Direction.UP) to (edgeStart(2, Direction.LEFT) + localYCoord)) to Direction.DOWN
                    // Left edge of 6 to top edge of 1
                    Direction.LEFT -> (edgeStart(1, Direction.UP) to (edgeStart(1, Direction.LEFT) + localXCoord)) to Direction.DOWN
                    else -> throw Exception("Unexpected dice crossing")
                }
            }
            else -> throw Exception("Unexpected dice face")
        }
        return newCoordinates to newDirection
    }

    private fun edgeStart(edge: Int, direction: Direction) = DICE_EDGES[edge]!![direction]!!

    private enum class GridContent {
        OPEN,
        WALL,
        EMPTY;

        companion object {
            fun fromChar(c: Char) = when(c){
                '.' -> OPEN
                '#' -> WALL
                else -> EMPTY
            }
        }
    }

    private enum class Direction(val value: Int) {
        RIGHT(0),
        DOWN(1),
        LEFT(2),
        UP(3);

        fun turnLeft() = directions[(directions.indexOf(this) - 1).mod(directions.size)]
        fun turnRight() = directions[(directions.indexOf(this) + 1) % directions.size]
        fun turnAround() = directions[(directions.indexOf(this) + 2) % directions.size]

        companion object {
            private val directions = listOf(UP, RIGHT, DOWN, LEFT)
        }
    }

    private data class Player(
        var xCoordinate: Int,
        var yCoordinate: Int,
        var direction: Direction
    )

    companion object {
        const val INPUT = "day22/input.txt"
        const val GRID_SIZE = 50

        /**
         * Grid layout
         *      1   2
         *      3
         *  4   5
         *  6
         */
        private val DICE_EDGES = mapOf(
            1 to mapOf(
                Direction.UP to 0,
                Direction.DOWN to (GRID_SIZE - 1),
                Direction.LEFT to GRID_SIZE,
                Direction.RIGHT to ((2 * GRID_SIZE) - 1)
            ),
            2 to mapOf(
                Direction.UP to 0,
                Direction.DOWN to (GRID_SIZE - 1),
                Direction.LEFT to (2 * GRID_SIZE),
                Direction.RIGHT to ((3 * GRID_SIZE) - 1)
            ),
            3 to mapOf(
                Direction.UP to GRID_SIZE,
                Direction.DOWN to ((2 * GRID_SIZE) - 1),
                Direction.LEFT to GRID_SIZE,
                Direction.RIGHT to ((2 * GRID_SIZE) - 1)
            ),
            4 to mapOf(
                Direction.UP to (2 * GRID_SIZE),
                Direction.DOWN to ((3 * GRID_SIZE) - 1),
                Direction.LEFT to 0,
                Direction.RIGHT to (GRID_SIZE - 1)
            ),
            5 to mapOf(
                Direction.UP to (2 * GRID_SIZE),
                Direction.DOWN to ((3 * GRID_SIZE) - 1),
                Direction.LEFT to GRID_SIZE,
                Direction.RIGHT to ((2 * GRID_SIZE) - 1)
            ),
            6 to mapOf(
                Direction.UP to (3 * GRID_SIZE),
                Direction.DOWN to ((4 * GRID_SIZE) - 1),
                Direction.LEFT to 0,
                Direction.RIGHT to (GRID_SIZE - 1)
            )
        )
    }
}