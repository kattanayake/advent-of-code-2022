package day17

import PuzzleSolution
import ROOT_DIR
import util.Coordinate
import java.io.File
import java.lang.Integer.max

class Tetris: PuzzleSolution {
    override fun solveFirst() {
        val heightOfBlocks = simulate()
        println("numRocks: $heightOfBlocks")
    }

    override fun solveSecond() {
        //answer = (number of rocks / cycle length) * cycle height + remainder height
        val target = 1000000000000L
        val nearestRoundNumber = 999999998421L
        val remainder = 1579
        // ((nearest number - offset before repeating starts)/repeating cycle)*height per cycle + height before repeating starts + offset from nearest round number to actual numer
        val answer = ((nearestRoundNumber - 291)/1730)*2659 + 459 + 2431
        println(answer)
    }

    private fun simulate(): Int{
        val instructions = parseInput()
        val grid = Array((SIMULATION_ROCK_COUNT * 4) + 5) { BooleanArray(7) { false } }

        var currentStartingHeight = 0 + HEIGHT_OFFSET
        var numRocks = 0
        var instructionIndex = 0
        var blockIndex = 0
        var nextIsDown = false
        var currentCoordinate = Coordinate(rowIndex = currentStartingHeight, columnIndex = 2)

        val repetitionMap = mutableMapOf<Pair<TetrisShape, Int>, MutableList<Int>>()
        val squareZeroValues = mutableListOf<Int>()
        // debug values
        var instructionStart = 0
        val heightNumRocksMap = mutableSetOf<Pair<Int, Int>>()
        while (numRocks < SIMULATION_ROCK_COUNT){
            val currentBlock = BLOCK_ORDER[blockIndex]
            val currentInstruction = instructions[instructionIndex]
//            if (Pair(currentBlock, instructionIndex) in repetitionMap){
//                repetitionMap[currentBlock to instructionIndex]!!.add(grid.firstEmptyRow())
//            } else {
//                repetitionMap[currentBlock to instructionIndex] = mutableListOf(grid.firstEmptyRow())
//            }
            if ((currentBlock == TetrisShape.Plus) and (instructionIndex == 1660) && nextIsDown){
                squareZeroValues.add(grid.firstEmptyRow())
                val height = grid.firstEmptyRow()
                println("Instruction number is $instructionIndex, numberOfRocks is $numRocks, height is ${height}")
                (1 until 4).forEach{lineNum ->
                    println(grid[height-lineNum].map { if (it) '#' else '.' }.joinToString(""))
                }
            }
            if (grid.firstEmptyRow() > 51 && ((grid.firstEmptyRow() - 51))%53 == 0){
                heightNumRocksMap.add(grid.firstEmptyRow() to numRocks)
            }
            if (nextIsDown){
                // If we can move down, move down
                if(currentBlock.canMoveIn(grid, Direction.Down, currentCoordinate)){
                    currentCoordinate = Coordinate(
                        rowIndex = currentCoordinate.rowIndex - 1,
                        columnIndex = currentCoordinate.columnIndex
                    )
                } else { // Otherwise place block and restart
                    currentBlock.placeAt(grid, coordinate = currentCoordinate)
                    currentStartingHeight = grid.firstEmptyRow() + HEIGHT_OFFSET
                    numRocks+=1
                    blockIndex = (blockIndex + 1) % BLOCK_ORDER.size
                    currentCoordinate = Coordinate(rowIndex = currentStartingHeight, columnIndex = 2)
                    instructionStart = instructionIndex
                    if(numRocks == (1579 + 291)) println("Num rows for ${1579 + 291} rocks: ${grid.firstEmptyRow()}")
                    if(numRocks == (1579 + 2021)) println("Num rows for ${1579 + 2021} rocks: ${grid.firstEmptyRow()}")
//                    grid.printTill(currentStartingHeight, BLOCK_ORDER[blockIndex], currentCoordinate)
                }
//                if (currentStartingHeight == 40) grid.printTill(currentStartingHeight, BLOCK_ORDER[blockIndex], currentCoordinate)
                nextIsDown = false
            } else {
                if(currentBlock.canMoveIn(grid, currentInstruction, currentCoordinate)) {
                    currentCoordinate = Coordinate(
                        rowIndex = currentCoordinate.rowIndex,
                        columnIndex = currentCoordinate.columnIndex + if (currentInstruction == Direction.Left) -1 else +1
                    )
                }
//                if (currentStartingHeight == 40) grid.printTill(currentStartingHeight, currentBlock, currentCoordinate)
                instructionIndex = (instructionIndex + 1) % instructions.size
                nextIsDown = true
            }
        }
        val sortedMap = repetitionMap.toList().sortedBy { it.second.size }.reversed()
//        val gapMap = mutableMapOf<Int, Int>()
//        (0 until squareZeroValues.size).forEach{ i ->
//            (i until squareZeroValues.size).forEach { j ->
//                val gap = squareZeroValues[j] - squareZeroValues[i]
//                if (gap in gapMap) gapMap[gap] = gapMap[gap]!! + 1
//                else gapMap[gap] = 1
//            }
//        }
//        val sortedGapMap = gapMap.toList().sortedByDescending { it.second }
        return grid.firstEmptyRow()
    }

    private fun Array<BooleanArray>.printTill(rowNum: Int, shape: TetrisShape, coordinate: Coordinate){
        val copy = this.map { it.copyOf() }.toTypedArray()
        shape.placeAt(copy, coordinate)
        copy.slice(max(rowNum - 10,0)..rowNum).reversed().forEach {row ->
            println("|${ row.map { if (it) '#' else '.' }.joinToString("") }|")
        }
        println("+-------+")
    }

    private fun Array<BooleanArray>.firstEmptyRow(): Int {
        forEachIndexed { index, booleans ->
            if (booleans.all { !it }) return index
        }
        return -1
    }

    private fun parseInput() = File(INPUT).readText().mapNotNull {
        when(it){
            '>' -> Direction.Right
            '<' -> Direction.Left
            else -> null
        }
    }

    sealed class TetrisShape(
        private val blocks: List<List<Boolean>> // Rows first, then columns. true means block, false is air
    ){
        val height = blocks.size
        val width = blocks.first().size
        object FlatBar: TetrisShape( // ----
            listOf(
                listOf(true, true, true, true)
            )
        )
        object Plus : TetrisShape(
            listOf(
                listOf(false, true, false),
                listOf(true, true, true),
                listOf(false, true, false)
            )
        )
        object El : TetrisShape(
            listOf(
                listOf(false, false, true),
                listOf(false, false, true),
                listOf(true, true, true)
            )
        )
        object VerticalBar: TetrisShape(
            listOf(
                listOf(true),
                listOf(true),
                listOf(true),
                listOf(true)
            )
        )

        object Square: TetrisShape(
            listOf(
                listOf(true, true),
                listOf(true, true)
            )
        )


        fun canMoveIn(
            grid: Array<BooleanArray>,
            direction: Direction,
            coordinate: Coordinate // Bottom left of tetris piece
        ):Boolean{
//            when(direction){
//                Direction.Down -> {
//                    if(coordinate.rowIndex == 0) return false // Already at the bottom, can't go any lower
//                    val rowBelow = coordinate.rowIndex - 1
//                    blocks.last().forEachIndexed { index, b ->
//                        if (b and grid[rowBelow][coordinate.columnIndex+index]) return false
//                    }
//                    return true
//                }
//                Direction.Left -> {
//                    if(coordinate.columnIndex == 0) return false // Already at the left edge
//                    val columnLeft = coordinate.columnIndex - 1
//                    blocks.reversed().forEachIndexed { index,  row ->
//                        if(row.first() and grid[coordinate.rowIndex + index][columnLeft]) return false
//                    }
//                    return true
//                }
//                Direction.Right -> {
//                    if ((coordinate.columnIndex + width) == COLUMN_WIDTH) {
//                        return false // Already at right edge
//                    }
//                    val columnRight = coordinate.columnIndex + width
//                    blocks.reversed().forEachIndexed { index, row ->
//                        if (row.last() and grid[coordinate.rowIndex + index][columnRight]) {
//                            return false
//                        }
//                    }
//                    return true
//                }
//            }
            val newCoordinate = when(direction){
                Direction.Down -> {
                    if(coordinate.rowIndex == 0) return false // Already at the bottom, can't go any lower
                    Coordinate(rowIndex = coordinate.rowIndex - 1, columnIndex = coordinate.columnIndex)
                }
                Direction.Left -> {
                    if(coordinate.columnIndex == 0) return false // Already at the left edge
                    Coordinate(rowIndex = coordinate.rowIndex, columnIndex = coordinate.columnIndex-1)
                }
                Direction.Right -> {
                    if ((coordinate.columnIndex + width) == COLUMN_WIDTH) return false // Already at right edge
                    Coordinate(rowIndex = coordinate.rowIndex, columnIndex = coordinate.columnIndex+1)
                }
            }
            blocks.reversed().forEachIndexed { rowIndex, row ->
                row.forEachIndexed { colIndex, b ->
                    if (b) {
                        if(grid[newCoordinate.rowIndex + rowIndex][newCoordinate.columnIndex + colIndex]){
                            return false
                        }
                    }
                }
            }
            return true
        }

        fun placeAt(grid: Array<BooleanArray>, coordinate: Coordinate){
            blocks.reversed().forEachIndexed { rowIndex, row ->
                row.forEachIndexed { colIndex, b ->
                    if (b) {
                        if(grid[coordinate.rowIndex + rowIndex][coordinate.columnIndex + colIndex]){
                            println("We fucked up")
                        }
                        grid[coordinate.rowIndex + rowIndex][coordinate.columnIndex + colIndex] = true
                    }
                }
            }
        }
    }

    enum class Direction{
        Down,
        Left,
        Right
    }

    companion object {
        const val INPUT = "$ROOT_DIR/day17/input.txt"
        const val SIMULATION_ROCK_COUNT = 2022
        const val COLUMN_WIDTH = 7
        const val HEIGHT_OFFSET = 3
        val BLOCK_ORDER = listOf(
            TetrisShape.FlatBar,
            TetrisShape.Plus,
            TetrisShape.El,
            TetrisShape.VerticalBar,
            TetrisShape.Square
        )
    }
}