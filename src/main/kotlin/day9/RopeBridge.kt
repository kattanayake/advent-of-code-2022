package day9

import PuzzleSolution
import ROOT_DIR
import day9.RopeBridge.DIRECTION.Companion.toDirection
import java.io.File
import kotlin.math.absoluteValue

class RopeBridge: PuzzleSolution {

    override fun solveFirst() {
        val visitedByTail = mutableSetOf<Coordinate>()
        val head = RopeEnd.Head()
        val tail = RopeEnd.Tail()
        File(INPUT).forEachLine {move ->
            val (direction, steps) = move.split(" ").let { (first, second)-> first.toDirection() to second.toInt() }
            (1..steps).forEach { _ ->
                head.moveInDirection(direction)
                tail.moveToHead(head.coordinate)
                visitedByTail.add(tail.coordinate)
            }
        }
        println("visitedByTail: ${visitedByTail.size}")
    }

    override fun solveSecond() {
        val visitedByTail = mutableSetOf<Coordinate>()
        val head = RopeEnd.Head()
        val tails = (1..9).map { RopeEnd.Tail() }
        File(INPUT).forEachLine {move ->
            val (direction, steps) = move.split(" ").let { (first, second)-> first.toDirection() to second.toInt() }
            (1..steps).forEach { _ ->
                head.moveInDirection(direction)
                tails.first().moveToHead(head.coordinate)
                (1..8).forEach {
                    val currentHead = tails[it-1]
                    val currentTail = tails[it]
                    currentTail.moveToHead(currentHead.coordinate)
                }
                visitedByTail.add(tails.last().coordinate)
            }
        }
        println("visitedByLastTail: ${visitedByTail.size}")
    }

    companion object {
        const val INPUT = "$ROOT_DIR/day9/input.txt"
    }

    private enum class DIRECTION {
        UP,
        DOWN,
        LEFT,
        RIGHT;

        companion object {
            fun String.toDirection(): DIRECTION {
                return when(this){
                    "U" -> UP
                    "R" -> RIGHT
                    "L" -> LEFT
                    "D" -> DOWN
                    else -> throw Error("Unexpected direction string")
                }
            }
        }
    }

    private sealed class RopeEnd(var coordinate: Coordinate){
        fun moveInDirection(direction: DIRECTION){
            coordinate = when(direction){
                DIRECTION.UP -> Pair(coordinate.first + 1, coordinate.second)
                DIRECTION.DOWN -> Pair(coordinate.first - 1, coordinate.second)
                DIRECTION.RIGHT -> Pair(coordinate.first, coordinate.second + 1)
                DIRECTION.LEFT -> Pair(coordinate.first, coordinate.second - 1)
            }
        }
        class Head(coordinate: Coordinate): RopeEnd(coordinate){
            constructor(): this(Pair(0,0))
        }

        class Tail(coordinate: Coordinate): RopeEnd(coordinate){
            constructor(): this(Pair(0,0))
            fun moveToHead(headCoordinates: Coordinate) {
                val verticalDistance = headCoordinates.first - coordinate.first
                val horizontalDistance = headCoordinates.second - coordinate.second
                val notTouching = (verticalDistance.absoluteValue == 2) or (horizontalDistance.absoluteValue == 2)

                val inSameColumn = headCoordinates.first == coordinate.first
                val inSameRow = headCoordinates.second == coordinate.second
                val needDiagonalStep = (!inSameRow) and (!inSameColumn)

                val moves = mutableListOf<DIRECTION>()

                if (notTouching) {
                    if (needDiagonalStep) {
                        if (verticalDistance <= -1) moves.add(DIRECTION.DOWN)
                        else if (verticalDistance >= 1) moves.add(DIRECTION.UP)

                        if (horizontalDistance <= -1) moves.add(DIRECTION.LEFT)
                        else if (horizontalDistance >= 1) moves.add(DIRECTION.RIGHT)
                    } else {
                        if (verticalDistance == -2) moves.add(DIRECTION.DOWN)
                        else if (verticalDistance == 2) moves.add(DIRECTION.UP)
                        else if (horizontalDistance == -2) moves.add(DIRECTION.LEFT)
                        else if (horizontalDistance == 2) moves.add(DIRECTION.RIGHT)
                    }
                }
                moves.forEach { moveInDirection(it) }
            }
        }
    }
}

/**
 * Bottom left is 0,0. Going up is incrementing X (first), going right is incrementing Y (second)
 * X (first) is vertical, Y (second) is horizontal
 */
typealias Coordinate = Pair<Int, Int>