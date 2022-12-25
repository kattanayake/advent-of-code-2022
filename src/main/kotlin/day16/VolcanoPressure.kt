package day16

import PuzzleSolution
import ROOT_DIR
import java.io.File
import java.lang.Integer.max

class VolcanoPressure: PuzzleSolution {
    override fun solveFirst() {
        val graph = parseInput()
        generatePaths(graph)
        val max = traverse(graph, graph["AA"]!!, TIME_LIMIT, graph.filter { it.value.flowRate > 0 }.keys)
        println("max : $max")
    }

    private fun traverse(
        graph: Map<String, Valve>,
        node: Valve,
        stepsLeft: Int,
        valvesLeft: Set<String>,
        isElephant: Boolean = false,
        cache: MutableMap<Checkpoint, Int> = mutableMapOf(),
    ): Int {
        if (stepsLeft == 0){
            return 0
        }
        // Valve is already open when we get here, so pressure from this valve is applicable for rest of time lest
        val pressureFromCurrentNode = node.flowRate*stepsLeft
        val currentCheckpoint = Checkpoint(stepsLeft, valvesLeft, pressureFromCurrentNode)
        val bestPressureFromRest = cache.getOrPut(currentCheckpoint) {
            val humanBest = valvesLeft // Only consider unopened valves
                .filter { node.distanceToNeighbours!![it]!! < stepsLeft } // only consider valves that are reachable
                .takeIf { it.isNotEmpty() }
                ?.maxOf {
                    traverse(graph, graph[it]!!, stepsLeft - node.distanceToNeighbours!![it]!! - 1, valvesLeft - it, isElephant)
                } ?: 0
            // What would the elephant get if he started with this starting set? Because we're always start the elephant
            // after the human, it gets to benefit from the valves opened by the human already. So for the elephant,
            // [bestPressureFromRest] will include human values, so pressure totals will be it's open valve +
            // everything the human has done so far.
            val elephantBest = if(isElephant) traverse(graph, graph["AA"]!!, TIME_LIMIT- ELEPHANT_TRAINING_TIME, valvesLeft) else 0
            max(humanBest, elephantBest)
        }
        return bestPressureFromRest + pressureFromCurrentNode
    }

    data class Checkpoint(val timeRemaining: Int, val valvesOpen: Set<String>, val pressure: Int)

    override fun solveSecond() {
        val graph = parseInput()
        generatePaths(graph)
        val max = traverse(graph, graph["AA"]!!, TIME_LIMIT- ELEPHANT_TRAINING_TIME, graph.filter { it.value.flowRate > 0 }.keys, true)
        println("max : $max")
    }

    private fun generatePaths(graph: Map<String, Valve>){
        val workingValves = graph.values.filter { it.flowRate > 0 } + graph["AA"]!!
        println("workingValves: ${workingValves.size}")
        workingValves.forEach { startVal ->
            startVal.distanceToNeighbours = (workingValves - startVal).associate {endVal ->
                val path = dijkstra(graph, startVal, endVal)
                graph.values.forEach {
                    it.cameFrom = null
                    it.score =  Int.MAX_VALUE
                }
                endVal.name to (path.size - 1)
            }
        }
    }

    private fun tracePath(start: Valve, end: Valve): List<Valve> {
        var current = end
        val path = mutableListOf<Valve>()
        while (current != start){
            path.add(current)
            current.cameFrom?.let { current = it } ?: return emptyList()
        }
        path.add(start)
        return path
    }

    private fun dijkstra(grid: Map<String, Valve>, start: Valve, end: Valve): List<Valve> {
        val queue = ArrayList(grid.values)
        start.score = 0
        while (queue.isNotEmpty()){
            queue.sortBy { it.score }
            val current = queue.removeFirst()
            if (current == end) return tracePath(start, end)
            current.connections.filter { grid[it] in queue }.forEach { neighbourString ->
                val neighbour = grid[neighbourString]!!
                val alt = current.score + 1
                if (alt < neighbour.score){
                    neighbour.score = alt
                    neighbour.cameFrom = current
                }
            }
        }
        return emptyList()
    }

    private fun parseInput() = File(INPUT).readText().split("\n").filter { it.isNotEmpty() }.map { row ->
        val stringParts = row.split(" ")
        Valve(
            name = stringParts[1],
            flowRate = stringParts[4].split("=")[1].split(";")[0].toInt(),
            connections = row.split("valves", "valve")[1].split(",").map { it.trim() }
        )
    }.associateBy { it.name }

    private data class Valve(
        val name: String,
        val flowRate: Int,
        val connections: List<String>,

        // Dijkstra stuff
        var score: Int = Int.MAX_VALUE,
        var cameFrom: Valve? = null,
        var distanceToNeighbours : Map<String, Int>? = null
    ) {
        override fun toString() = "$name: $flowRate: $connections"
    }

    companion object {
        const val INPUT = "$ROOT_DIR/day16/input.txt"
        const val TIME_LIMIT = 30
        const val ELEPHANT_TRAINING_TIME = 4
    }
}