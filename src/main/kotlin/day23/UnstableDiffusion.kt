package day23

import PuzzleSolution

import util.Coordinate
import java.io.File
import java.util.*

class UnstableDiffusion: PuzzleSolution {
    override fun solveFirst() {
        val elves = parseInput()
        val afterSimulation = simulateElves(elves)
        val freeSpots = countFreeSpots(afterSimulation)
        println("freeSpots: $freeSpots")
    }

    override fun solveSecond() {
        simulateElves(parseInput(), numIterations = Int.MAX_VALUE)
    }

    private fun parseInput() = readTextByLine(INPUT).mapIndexed { rowIndex, line ->
        line.mapIndexed { columnIndex, c ->
            if (c == '#') Coordinate(rowIndex = rowIndex, columnIndex = columnIndex) else null
        }.filterNotNull()
    }.flatten()

    private fun printGrid(elves: List<Coordinate>){
        val minX = elves.minBy { it.rowIndex }.rowIndex
        val maxX = elves.maxBy { it.rowIndex }.rowIndex
        val minY = elves.minBy { it.columnIndex }.columnIndex
        val maxY = elves.maxBy { it.columnIndex }.columnIndex
        (minX..maxX).forEach { rowIndex ->
            (minY..maxY).forEach { columnIndex ->
                if (Coordinate(rowIndex = rowIndex, columnIndex = columnIndex) in elves) print('#') else print('.')
            }
            println()
        }
        println()
    }

    private fun countFreeSpots(elves: List<Coordinate>): Int {
        val minX = elves.minBy { it.rowIndex }.rowIndex
        val maxX = elves.maxBy { it.rowIndex }.rowIndex
        val minY = elves.minBy { it.columnIndex }.columnIndex
        val maxY = elves.maxBy { it.columnIndex }.columnIndex
        return (minX..maxX).flatMap { rowIndex ->
            (minY..maxY).map { columnIndex ->
                if (Coordinate(rowIndex = rowIndex, columnIndex = columnIndex) in elves) 0 else 1
            }
        }.fold(0) { acc, i -> acc + i }
    }

    private fun simulateElves(elves: List<Coordinate>, numIterations: Int = NUM_CYCLES): List<Coordinate> {
        val elfList = elves.toMutableList()
        (0 until numIterations).forEach { cycleNum ->
            val moveSuggestions = elfList.mapNotNull { elf ->
                val allOptions = buildList {
                    if (!elf.isAllFree(elfList)) {
                        add((elf to elf.up()).takeIf { elf.isNorthFree(elfList) })
                        add((elf to elf.down()).takeIf { elf.isSouthFree(elfList) })
                        add((elf to elf.left()).takeIf { elf.isWestFree(elfList) })
                        add((elf to elf.right()).takeIf { elf.isEastFree(elfList) })
                    }
                }.toMutableList()
                Collections.rotate(allOptions, -1 * (cycleNum% 4))
                allOptions.firstOrNull { it != null }
            }
            val numMoves = moveSuggestions.map { (old, new) ->
                if (moveSuggestions.count { it.second == new } == 1) {
                    elfList.remove(old)
                    elfList.add(new)
                    1
                } else 0
            }.fold(0) { acc, i ->  acc + i}
            if (numMoves == 0) {
                println("No elves moved after ${cycleNum + 1}")
                return emptyList()
            }
        }
        return elfList
    }

    private fun Coordinate.isNorthFree(elves: List<Coordinate>) = (up() !in elves) and (upLeft() !in elves) and (upRight() !in elves)
    private fun Coordinate.isSouthFree(elves: List<Coordinate>) = (down() !in elves) and (downLeft() !in elves) and (downRight() !in elves)
    private fun Coordinate.isEastFree(elves: List<Coordinate>) = (right() !in elves) and (upRight() !in elves) and (downRight() !in elves)
    private fun Coordinate.isWestFree(elves: List<Coordinate>) = (left() !in elves) and (upLeft() !in elves) and (downLeft() !in elves)
    private fun Coordinate.isAllFree(elves: List<Coordinate>) = isNorthFree(elves) and isSouthFree(elves) and isEastFree(elves) and isWestFree(elves)

    companion object {
        const val INPUT = "day23/input.txt"
        const val NUM_CYCLES = 10
    }
}