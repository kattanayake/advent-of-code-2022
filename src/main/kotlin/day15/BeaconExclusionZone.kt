package day15

import PuzzleSolution
import ROOT_DIR
import util.Coordinate
import java.io.File
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

class BeaconExclusionZone: PuzzleSolution {
    override fun solveFirst() {
        val scanResults = parseInput()
        val relevantScanners = scanResults.filter {
            (it.coverage.rowStart <= ROW_NUM) && (it.coverage.rowEnd >= ROW_NUM)
        }
        val itemsInRow = scanResults.map {
            buildList<Coordinate> {
                if(it.sensorLocation.rowIndex == ROW_NUM) add(it.sensorLocation)
                if(it.beaconLocation.rowIndex == ROW_NUM) add(it.beaconLocation)
            }
        }.flatten().distinct()
        println("relevantScanners: ${relevantScanners.size}, itemsInRow: $itemsInRow")
        val coveredSquares = relevantScanners.map {
            val heightFromSensor = (ROW_NUM - it.sensorLocation.rowIndex).absoluteValue
            val columnCoverage = it.coverage.coverageRadius - heightFromSensor
            (it.sensorLocation.columnIndex-columnCoverage)..(it.sensorLocation.columnIndex + columnCoverage)
        }.flatten().distinct()
        val coveredSquaresSansItems = coveredSquares.toMutableList().also { it.removeAll(itemsInRow.map { it.columnIndex }) }
        println("coveredSquares: ${coveredSquares.size}, coveredSquaresSansItems: ${coveredSquaresSansItems.size}")
    }

    override fun solveSecond() {
        val scanResults = parseInput()
        (ROW_RANGE_START..ROW_RANGE_END).forEach { row ->
            val coveredRanges = scanResults.mapNotNull {
                val heightFromSensor = (row - it.sensorLocation.rowIndex).absoluteValue
                val columnCoverage = it.coverage.coverageRadius - heightFromSensor
                if (columnCoverage > 0) {
                    val coverageStart = max(it.sensorLocation.columnIndex - columnCoverage, ROW_RANGE_START)
                    val coverageEnd = min(it.sensorLocation.columnIndex + columnCoverage, ROW_RANGE_END)
                    (coverageStart..coverageEnd)
                } else null
            }
            val sortedCoveredRanges = coveredRanges.sortedWith { p0, p1 -> p0.first - p1.first }
            val rangeStart = sortedCoveredRanges.first().first
            var rangeEnd = sortedCoveredRanges.first().last
            if(rangeStart != ROW_RANGE_START) println("$ROW_RANGE_START unaccounted for row $row")
            sortedCoveredRanges.forEach {
                if (it.first > rangeEnd) {
                    val missingValue = (rangeEnd + it.first) / 2
                    println("found a gap, missing $missingValue for row $row")
                    val answer = (missingValue * 4000000L) + row
                    println("Answer: ($missingValue * 4000000) + $row = $answer")
                }
                if (it.last > rangeEnd) rangeEnd = it.last
            }
            if(rangeEnd != ROW_RANGE_END) println("$ROW_RANGE_END unaccounted for row $row")
        }
    }

    private fun parseInput() = File(INPUT).readText().split("\n").filter { it.isNotEmpty() }.map { pair ->
            val (sensor, beacon) = pair.split(":").map {
                val parts = it.split("=")
                val x = parts[1].split(",").first().toInt()
                val y = parts[2].toInt()
                Coordinate(columnIndex = x, rowIndex = y)
            }
            Sensor(
                sensorLocation = sensor,
                beaconLocation = beacon
            )
        }

    companion object {
        const val INPUT = "$ROOT_DIR/day15/input.txt"
        const val ROW_NUM = 2000000
        const val ROW_RANGE_START = 0
        const val ROW_RANGE_END = 4000000
    }

    private data class Sensor(
        val sensorLocation: Coordinate,
        val beaconLocation: Coordinate,
        val coverage: Coverage = generateCoverage(sensorLocation, beaconLocation)
    ){
        companion object {
            private fun generateCoverage(sensorLocation: Coordinate, beaconLocation: Coordinate): Coverage {
                val distanceToSensor = (sensorLocation.columnIndex - beaconLocation.columnIndex).absoluteValue +
                        (sensorLocation.rowIndex - beaconLocation.rowIndex).absoluteValue
                return Coverage(
                    coverageRadius = distanceToSensor,
                    rowStart = sensorLocation.rowIndex - distanceToSensor,
                    rowEnd = sensorLocation.rowIndex + distanceToSensor,
                    colStart = sensorLocation.columnIndex - distanceToSensor,
                    colEnd = sensorLocation.columnIndex + distanceToSensor
                )
            }
        }
    }

    private data class Coverage(
        val coverageRadius: Int,
        val rowStart: Int,
        val colEnd: Int,
        val colStart: Int,
        val rowEnd: Int
    )
}