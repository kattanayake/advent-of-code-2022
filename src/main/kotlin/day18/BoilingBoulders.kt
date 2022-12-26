package day18

import PuzzleSolution

import java.io.File

class BoilingBoulders: PuzzleSolution {
    override fun solveFirst() {
        val blocks = parseInput()
        val surfaceArea = blocks.map {
            var freeAdjacentSquares = 0
            if (Triple(it.first-1, it.second, it.third) !in blocks) freeAdjacentSquares++
            if (Triple(it.first+1, it.second, it.third) !in blocks) freeAdjacentSquares++
            if (Triple(it.first, it.second-1, it.third) !in blocks) freeAdjacentSquares++
            if (Triple(it.first, it.second+1, it.third) !in blocks) freeAdjacentSquares++
            if (Triple(it.first, it.second, it.third-1) !in blocks) freeAdjacentSquares++
            if (Triple(it.first, it.second, it.third+1) !in blocks) freeAdjacentSquares++

            freeAdjacentSquares
        }.fold(0) { acc, i ->  acc + i}
        println("surfaceArea: $surfaceArea")
    }

    override fun solveSecond() {
        val blocks = parseInput()

        val minX = blocks.minBy { it.first }.first
        val maxX = blocks.maxBy { it.first }.first
        val minY = blocks.minBy { it.second }.second
        val maxY = blocks.maxBy { it.second }.second
        val minZ = blocks.minBy { it.third }.third
        val maxZ = blocks.maxBy { it.third }.third
        println("Have to evaluate X from $minX to $maxX, y from $minY to $maxY, Z from $minZ to $maxZ")

        val waterBlocks = mutableSetOf<ThreeDPoint>()
        val bottomLevel = minZ - 1
        ((minX-1)..(maxX + 1)).forEach { x ->
            ((minY-1)..(maxY+1)).forEach { y ->
                waterBlocks.add(ThreeDPoint(x, y, bottomLevel))
            }
        }

        // Moving water around
        var waterDidNotMove: Boolean
        val nextWaterBlocks = mutableSetOf<ThreeDPoint>()

        fun addWaterBlock(block: ThreeDPoint) {
            if (block !in waterBlocks) {
                waterDidNotMove = false
                nextWaterBlocks.add(block)
            }
        }
        do {
            waterDidNotMove = true
            waterBlocks.forEach {
                if ((it.first > (minX - 1))  and (Triple(it.first-1, it.second, it.third) !in blocks)) addWaterBlock(Triple(it.first-1, it.second, it.third))
                if ((it.first < (maxX + 1)) and (Triple(it.first+1, it.second, it.third) !in blocks)) addWaterBlock(Triple(it.first+1, it.second, it.third))
                if ((it.second > (minY - 1)) and (Triple(it.first, it.second-1, it.third )!in blocks)) addWaterBlock(Triple(it.first, it.second-1, it.third))
                if ((it.second < (maxY + 1)) and (Triple(it.first, it.second+1, it.third )!in blocks)) addWaterBlock(Triple(it.first, it.second+1, it.third))
                if ((it.third > (minZ - 1)) and (Triple(it.first, it.second, it.third-1) !in blocks)) addWaterBlock(Triple(it.first, it.second, it.third-1))
                if ((it.third < (maxZ + 1)) and (Triple(it.first, it.second, it.third+1) !in blocks)) addWaterBlock(Triple(it.first, it.second, it.third+1))
            }
            waterBlocks.addAll(nextWaterBlocks)
        } while (!waterDidNotMove)
        println("Num water blocks: ${waterBlocks.size}")
        println("Num lava blocks: ${blocks.size}")

        val totalSurfaceArea = blocks.map {
            var freeAdjacentSquares = 0
            if (Triple(it.first-1, it.second, it.third) in waterBlocks) freeAdjacentSquares++
            if (Triple(it.first+1, it.second, it.third) in waterBlocks) freeAdjacentSquares++
            if (Triple(it.first, it.second-1, it.third) in waterBlocks) freeAdjacentSquares++
            if (Triple(it.first, it.second+1, it.third) in waterBlocks) freeAdjacentSquares++
            if (Triple(it.first, it.second, it.third-1) in waterBlocks) freeAdjacentSquares++
            if (Triple(it.first, it.second, it.third+1) in waterBlocks) freeAdjacentSquares++
            freeAdjacentSquares
        }.fold(0) { acc, i ->  acc + i}

        println("Water surface area: $totalSurfaceArea")

    }

    private fun parseInput() = readTextByLine(INPUT).map {
        val (x, y, z) = it.split(",")
        ThreeDPoint(x.toInt(), y.toInt(), z.toInt())
    }.toSet()

    companion object {
        const val INPUT = "day18/input.txt"
    }
}

typealias ThreeDPoint = Triple<Int, Int, Int> // X, Y, Z