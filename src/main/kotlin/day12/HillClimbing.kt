package day12

import PuzzleSolution

import java.io.File
import kotlin.math.absoluteValue

class HillClimbing: PuzzleSolution {
    override fun solveFirst() {
        val (grid, start, end) = parseInput()
        generateNeighbours(grid)
//        val path = AStar(grid,start,end) {simpleHeuristic(it, end)}
        val path = dijkstra(grid,start,end)
        println(path.size)
//        visualizePath(grid, path)
    }

    override fun solveSecond() {
        val (grid, _, end) = parseInput()
        generateNeighbours(grid)
        val startPoints = grid.map { row -> row.filter { it.height == 0 } }.flatten()
        val distances = startPoints.mapIndexed { index, currentStart ->
            println("${index.inc()}/${startPoints.size}")
            grid.reset()
            dijkstra(grid, currentStart, end).size
//            AStar(grid,currentStart,end) {simpleHeuristic(it, end)}.size
        }
        println("Shortest distance: " + (distances.filter { it > 0 }.minOf { it } - 1))
    }

    private fun List<List<GridSquare>>.reset(){
        this.forEach { row ->
            row.forEach {
                it.fScore = Int.MAX_VALUE
                it.gScore = Int.MAX_VALUE
                it.cameFrom = null
            }
        }
    }

    private fun parseInput(): Triple<List<List<GridSquare>>, GridSquare, GridSquare> {
        lateinit var start: GridSquare
        lateinit var end: GridSquare
        val grid = File(INPUT).readText().split("\n").filter { it.isNotEmpty() }.mapIndexed { rowIndex, row ->
            row.mapIndexed { columnIndex, c ->
                val square = GridSquare(rowIndex, columnIndex, c.subIfNeeded().code - 'a'.code)
                if (c == START) start = square
                else if (c == END) end = square
                square
            }
        }
        return Triple(grid, start, end)
    }

    private fun simpleHeuristic(gridSquare: GridSquare, end: GridSquare) =
        (end.height - gridSquare.height) + (end.columnIndex - gridSquare.columnIndex).absoluteValue + (end.rowIndex - gridSquare.rowIndex).absoluteValue

    private fun Char.subIfNeeded() = if(this == START) 'a' else if(this == END) 'z' else this

    private fun generateNeighbours(grid: List<List<GridSquare>>){
        grid.forEach { row ->
            row.forEach { square ->
                fun GridSquare.processNeighbour(neighbour: GridSquare){
                    if (neighbour.height <= (this.height+1)) this.neighbours.add(neighbour)
                }
                if (square.columnIndex > 0) { // not the left-most in row
                    square.processNeighbour(grid[square.rowIndex][square.columnIndex-1])
                }
                if (square.columnIndex < (grid.first().size-1)){ // not the right-most in row
                    square.processNeighbour(grid[square.rowIndex][square.columnIndex+1])
                }
                if (square.rowIndex > 0) { // not the top-most in column
                    square.processNeighbour(grid[square.rowIndex-1][square.columnIndex])
                }
                if (square.rowIndex < (grid.size-1)){ // not the bottom-most in column
                    square.processNeighbour(grid[square.rowIndex+1][square.columnIndex])
                }
            }
        }
    }

    /** A* finds a path from start to goal.
        h is the heuristic function. h(n) estimates the cost to reach goal from node n.
    **/
    private fun AStar(grid: List<List<GridSquare>>, start: GridSquare, end: GridSquare, heuristic: (GridSquare) -> Int): List<GridSquare>{
        // The set of discovered nodes that may need to be (re-)expanded.
        // Initially, only the start node is known.
        // This is usually implemented as a min-heap or priority queue rather than a hash-set.
        val openSet = ArrayList<GridSquare>().also { it.add(start)}

        // For node n, fScore[n] := gScore[n] + h(n). fScore[n] represents our current best guess as to
        // how cheap a path could be from start to finish if it goes through n.
        start.gScore = 0
        start.fScore = heuristic(start)
        while (openSet.isNotEmpty()){
            val current = openSet.removeFirst()
            if (current == end) {
                return tracePath(start, end)
            }

            current.neighbours.forEach { neighbor ->
                val tentativeGScore = current.gScore + 5 // Edge cost is always 1
                if (tentativeGScore < neighbor.gScore){
                    // This path to neighbor is better than any previous one. Record it!
                    neighbor.cameFrom = current
                    neighbor.gScore = tentativeGScore
                    neighbor.fScore = tentativeGScore + heuristic(neighbor)
                    if (neighbor !in openSet) {
                        openSet.add(neighbor)
                        openSet.sortBy {it.fScore}
                    }
                }

            }
        }
        return emptyList()
    }

    private fun tracePath(start: GridSquare, end: GridSquare): List<GridSquare> {
        var current = end
        val path = mutableListOf<GridSquare>()
        while (current != start){
            path.add(current)
            current.cameFrom?.let { current = it } ?: return emptyList()
        }
        path.add(start)
        return path
    }

    private fun visualizePath(grid: List<List<GridSquare>>, path: List<GridSquare>){
        grid.forEach { row ->
            row.forEach { gridSquare ->
                val charToPrint = if (gridSquare in path){
                    print(GREEN)
                    val index = path.indexOf(gridSquare)
                    if (index != (path.size - 1)){
                        val nextStep = path[index+1]
                        if (nextStep.rowIndex > gridSquare.rowIndex) '^'
                        else if (nextStep.rowIndex < gridSquare.rowIndex) 'V'
                        else if (nextStep.columnIndex > gridSquare.columnIndex) '<'
                        else '>'
                    } else 'S'
                } else {
                    val char = (gridSquare.height + 'a'.code).toChar()
                    if(char == 'a') print(RED)
                    if(char == 'c') print(YELLOW)
                    char
                }
                print(charToPrint)
                print(RESET)
            }
            println()
        }
    }

    private fun dijkstra(grid: List<List<GridSquare>>, start: GridSquare, end:GridSquare): List<GridSquare> {
        val queue = ArrayList(grid.flatten())
        start.fScore = 0
        while (queue.isNotEmpty()){
            queue.sortBy { it.fScore }
            val current = queue.removeFirst()
            if (current == end) return tracePath(start, end)
            current.neighbours.filter { it in queue }.forEach { neighbour ->
                val alt = current.fScore + 1
                if (alt < neighbour.fScore){
                    neighbour.fScore = alt
                    neighbour.cameFrom = current
                }
            }
        }
        return emptyList()
    }

    private data class GridSquare(
        val rowIndex: Int,
        val columnIndex: Int,
        val height: Int,
        // For node n, gScore[n] is the cost of the cheapest path from start to n currently known.
        var gScore: Int = Int.MAX_VALUE,
        // For node n, fScore[n] := gScore[n] + h(n). fScore[n] represents our current best guess as to
        // how cheap a path could be from start to finish if it goes through n.
        var fScore: Int = Int.MAX_VALUE,
        // For node n, cameFrom[n] is the node immediately preceding it on the cheapest path from start
        var cameFrom: GridSquare? = null,
        var neighbours: MutableList<GridSquare> = mutableListOf(),
    ){
        override fun toString(): String {
            return (rowIndex to columnIndex).toString()
        }
    }

    companion object {
        const val INPUT = "day12/input.txt"
        const val START = 'S'
        const val END = 'E'
        const val RED = "\u001b[31m"
        const val GREEN = "\u001b[32m"
        const val YELLOW = "\u001b[33m"
        const val RESET = "\u001b[0m"
    }
}