package day24

import PuzzleSolution

import day24.BlizzardBasin.Tornado.Direction.Companion.toDirection
import util.Coordinate
import java.io.File

class BlizzardBasin: PuzzleSolution {
    override fun solveFirst() {
        val grid = parseInput()
        val endCoordinate = Coordinate(rowIndex = grid.gridBottom + 1, columnIndex = grid.gridRight)
        val numSteps = traverse(grid, endCoordinate, START_ORIGINAL)
        println("Shortest path steps: $numSteps")
    }

    /**
     * BFS instead of the usual DFS to save on time?
     */
    private fun traverse(grid: TornadoGrid, goal: Coordinate, start: Coordinate): Int {
        val currentCycleOptions = mutableSetOf(start)
        val nextCycleOptions = mutableSetOf<Coordinate>()
        var timeTick = 0
        val rowMax = grid.gridHeight - 1
        val colMax = grid.gridWidth - 1
        // We keep going until we hit the exit
        while (true){
            grid.updateTornadoes()
            currentCycleOptions.forEach { option ->
                if (option == goal) return timeTick
                val rawOptionList = buildList {
                    // Stay still
                    add(option)
                    // Go down
                    if (option.rowIndex < rowMax) add(option.down())
                    // Go up
                    if (option.rowIndex > 0) add(option.up())
                    // If we're at the start, we can only go up, down or stay here
                    if (option == start) return@buildList

                    // Special leave-the-grid case for the start
                    if (option.up() == start) add(start)
                    if (option.down() == start) add(start)
                    // Special leave-the-grid case for the exit
                    if (option.down() == goal) add(goal)
                    if (option.up() == goal) add(goal)

                    // Go right
                    if (option.columnIndex < colMax) add(option.right())
                    // Go left
                    if (option.columnIndex > 0) add(option.left())
                }
                rawOptionList.forEach { if (it !in grid.gridMap) nextCycleOptions.add(it) }
            }
            currentCycleOptions.clear(); currentCycleOptions.addAll(nextCycleOptions); nextCycleOptions.clear()
            timeTick++
        }
    }


    override fun solveSecond() {
        val grid = parseInput()
        val endCoordinate = Coordinate(rowIndex = grid.gridBottom + 1, columnIndex = grid.gridRight)

        val numStepsForward = traverse(grid, endCoordinate, START_ORIGINAL)
        val numStepsBack = traverse(grid, START_ORIGINAL, endCoordinate) + 1
        val numStepsForwardAgain = traverse(grid, endCoordinate, START_ORIGINAL) + 1
        println("Shortest path there, back, and there again: ${numStepsForward + numStepsBack + numStepsForwardAgain}")
    }

    private fun printGrid(tornadoes: TornadoGrid){
        print("#.");repeat(tornadoes.gridWidth){print("#")};println()
        (tornadoes.gridTop..tornadoes.gridBottom).forEach { row ->
            print("#")
            (tornadoes.gridLeft..tornadoes.gridRight).forEach {col ->
                val coordinate = Coordinate(col, row)
                print(tornadoes.gridMap[coordinate]?.direction?.toChar() ?: '.')
            }
            println("#")
        }
        repeat(tornadoes.gridWidth){print("#")};print(".#");println()
    }

    private fun parseInput() = readTextByLine(INPUT).mapIndexed { rowIndex, s ->
        s.mapIndexed { colIndex, c ->
            if (c in TORNADO_CHARS) Tornado(
                position = Coordinate(rowIndex = rowIndex-1, columnIndex = colIndex-1),
                direction = c.toDirection()
            ) else null
        }.filterNotNull()
    }.flatten().let { TornadoGrid(it) }

    private data class Tornado(
        val position: Coordinate,
        val direction: Direction
    ) {
        enum class Direction {
            UP,
            DOWN,
            LEFT,
            RIGHT;

            fun toChar() = when(this){
                UP -> '^'
                RIGHT -> '>'
                LEFT -> '<'
                DOWN -> 'v'
            }

            companion object {
                fun Char.toDirection(): Tornado.Direction {
                    return when(this){
                        '^' -> Tornado.Direction.UP
                        '>' -> Tornado.Direction.RIGHT
                        '<' -> Tornado.Direction.LEFT
                        'v' -> Tornado.Direction.DOWN
                        else -> throw Error("Unexpected direction string")
                    }
                }
            }
        }
    }

    /**
     * Don't let the character stagnate on the first tile, waiting is the last option
     *
     */



    private data class TornadoGrid(var tornadoes: List<Tornado>){
        val gridTop = tornadoes.minBy { it.position.rowIndex }.position.rowIndex
        val gridBottom = tornadoes. maxBy { it.position.rowIndex }.position.rowIndex
        val gridLeft = tornadoes. minBy { it.position.columnIndex }.position.columnIndex
        val gridRight = tornadoes. maxBy { it.position.columnIndex }.position.columnIndex
        val gridHeight = gridBottom - gridTop  + 1
        val gridWidth = gridRight - gridLeft + 1

        var gridMap = tornadoes.associateBy { it.position }

        fun updateTornadoes() {
            tornadoes = tornadoes.map { tornado ->
                Tornado(
                    direction = tornado.direction,
                    position = when (tornado.direction) {
                        Tornado.Direction.UP -> Coordinate(
                            columnIndex = tornado.position.columnIndex,
                            rowIndex = (tornado.position.rowIndex - 1).mod(gridHeight)
                        )

                        Tornado.Direction.DOWN -> Coordinate(
                            columnIndex = tornado.position.columnIndex,
                            rowIndex = (tornado.position.rowIndex + 1).mod(gridHeight)
                        )

                        Tornado.Direction.LEFT -> Coordinate(
                            columnIndex = (tornado.position.columnIndex - 1).mod(gridWidth),
                            rowIndex = tornado.position.rowIndex
                        )

                        Tornado.Direction.RIGHT -> Coordinate(
                            columnIndex = (tornado.position.columnIndex + 1).mod(gridWidth),
                            rowIndex = tornado.position.rowIndex
                        )
                    }
                )
            }
            gridMap = tornadoes.associateBy { it.position }
        }
    }

    companion object {
        const val INPUT = "day24/input.txt"
        val START_ORIGINAL = Coordinate(columnIndex = 0, rowIndex = -1)
        val TORNADO_CHARS = listOf('<','^','v','>')
    }
}