package day14

import PuzzleSolution

import util.Coordinate
import java.io.File
import java.lang.Integer.min
import kotlin.math.max

class RegolithReservoir: PuzzleSolution {
    override fun solveFirst() {
        val rockLines = parseInput()
        val rowStart = rockLines.flatten().minOf { min(it.start.rowIndex, it.end.rowIndex) }
        val rowEnd = rockLines.flatten().maxOf { max(it.start.rowIndex, it.end.rowIndex) }
        val columnStart = rockLines.flatten().minOf { min(it.start.columnIndex, it.end.columnIndex) }
        val columnEnd = rockLines.flatten().maxOf { max(it.start.columnIndex, it.end.columnIndex) }

        val grid = SandGrid(rowStart, columnStart, rowEnd, columnEnd)
        addRocks(grid, rockLines)
        drawGrid(grid)
        val sandFallen = simulateSandFall(grid)
        drawGrid(grid)
        println("sandFallen: ${sandFallen-1}")
    }

    override fun solveSecond() {
        val rockLines = parseInput()
        val rowEnd = rockLines.flatten().maxOf { max(it.start.rowIndex, it.end.rowIndex) }
        val floor = listOf(listOf(RockSegment(
            start = Coordinate(200, rowEnd + 2),
            end = Coordinate(800, rowEnd + 2)
        )))

        val grid = SandGrid(5, 199, rowEnd, 801)
        addRocks(grid, rockLines + floor )
        drawGrid(grid)
        val sandFallen = simulateSandFall(grid)
        drawGrid(grid)
        println("sandFallen: $sandFallen")
    }

    private fun simulateSandFall(grid: SandGrid): Int {
        var sandFallen = 1
        var currentFallingSand = Coordinate(500, grid.rowStartIndex - grid.rowPadding)
        while (true){
            if (currentFallingSand.rowIndex == (grid.rowEnd + grid.rowPadding)) {
                return sandFallen
            }
            //Can it go straight down?
            if(grid.canGoTo(currentFallingSand.down())){
                currentFallingSand = currentFallingSand.down()
            } else if (grid.canGoTo(currentFallingSand.downLeft())) { // Can it go to the bottom left?
                currentFallingSand = currentFallingSand.downLeft()
            } else if (grid.canGoTo(currentFallingSand.downRight())) {
                currentFallingSand = currentFallingSand.downRight()
            } else {
                grid.set(currentFallingSand.rowIndex, currentFallingSand.columnIndex, GridContent.SAND)
                if (currentFallingSand.rowIndex == 0 && currentFallingSand.columnIndex == 500) return sandFallen
                currentFallingSand = Coordinate(500, grid.rowStartIndex - grid.rowPadding)
                sandFallen +=1
            }
        }
    }

    private fun SandGrid.canGoTo(currentSandLocation: Coordinate) =
        get(currentSandLocation.rowIndex, currentSandLocation.columnIndex) == GridContent.AIR

    private fun parseInput() = File(INPUT).readText().split("\n").filter { it.isNotEmpty() }.map {
        it.split("->").let { lines ->
            (1 until lines.size).map { index ->
                RockSegment(
                    start = lines[index-1].split(",").let { (x, y) -> Coordinate(x.trim().toInt(), y.trim().toInt()) },
                    end = lines[index].split(",").let { (x, y) -> Coordinate(x.trim().toInt(), y.trim().toInt()) },
                )
            }
        }
    }

    private fun addRocks(grid: SandGrid, rockLines: List<List<RockSegment>>){
        rockLines.forEach { rockLine ->
            rockLine.forEach { rockSegment ->
                if (rockSegment.start.columnIndex == rockSegment.end.columnIndex) {// Vertical line
                    val lineStart = min(rockSegment.start.rowIndex, rockSegment.end.rowIndex)
                    val lineEnd = max(rockSegment.start.rowIndex, rockSegment.end.rowIndex)
                    (lineStart..lineEnd).forEach { rowIndex ->
                        grid.set(rowIndex, rockSegment.start.columnIndex, GridContent.ROCK)
                    }
                } else { // Horizontal line
                    val lineStart = min(rockSegment.start.columnIndex,rockSegment.end.columnIndex)
                    val lineEnd = max(rockSegment.start.columnIndex,rockSegment.end.columnIndex)
                    (lineStart..lineEnd).forEach { colIndex ->
                        grid.set(rockSegment.start.rowIndex, colIndex, GridContent.ROCK)
                    }
                }
            }
//            drawGrid(grid)
        }
    }

    private fun drawGrid(grid: SandGrid, until: Int? = null){
        val rowNumOffset = 4
        // Drawing column indices
        val gridColStart = grid.columnStartIndex - 1
        val gridColStartLabel = gridColStart.toString()
        val gridColEnd = grid.columnEnd + 1
        val gridColEndLabel = gridColEnd.toString()
        val sandStartLabel = "500"
        (0 until 3).forEach {
            repeat(rowNumOffset) { print(" ") }
            print(gridColStartLabel[it])
            repeat(500 - gridColStart - 1) { print(" ") }
            print(sandStartLabel[it])
            repeat(gridColEnd - 500 - 1) { print( " ") }
            println(gridColEndLabel[it])
        }

        // Drawing rows
        grid.content.forEachIndexed { index, gridRow ->
            print("%03d ".format(grid.rowStartIndex + index - grid.rowPadding))
            gridRow.forEach {
                print(it.representation)
            }
            println()
            if (index == until) return
        }
    }

    private data class RockSegment(val start: Coordinate, val end: Coordinate)

    private class SandGrid(
        val rowStartIndex: Int,
        val columnStartIndex: Int,
        val rowEnd: Int,
        val columnEnd: Int
    ){
        val rowPadding = 5
        val colPadding = 1
        val content = Array(rowEnd + 1 + (2 * rowPadding) - rowStartIndex) {
            Array(columnEnd + 1 + (2 * colPadding) - columnStartIndex) {
                GridContent.AIR
            }
        }

        fun get(rowIndex: Int, columnIndex: Int) = content[rowIndex - rowStartIndex + rowPadding][columnIndex - columnStartIndex + colPadding]

        fun set(rowIndex: Int, columnIndex: Int, newContent: GridContent) {
            content[rowIndex - rowStartIndex + rowPadding][columnIndex - columnStartIndex + colPadding] = newContent
        }
    }

    enum class GridContent(val representation: Char) {
        AIR('·'),
        SAND('o'),
        ROCK('#')
    }

    companion object {
        const val INPUT = "day14/input.txt"
    }

    /**
     *  0 -> rowStart - rowPadding
     *  rowIndex -> rowStart + rowIndex - rowPadding
     */
}