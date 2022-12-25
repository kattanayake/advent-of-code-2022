package day6

import PuzzleSolution

import java.io.File

class TuningTrouble: PuzzleSolution{
    override fun solveFirst() {
        File(INPUT).forEachLine {
            val end = it.length
            (1..(end-4)).forEach {startIndex ->
                val characters = it.substring(startIndex-1, startIndex+3).toSet()
                if (characters.size == 4) {
                    println(startIndex+3)
                    return@forEachLine
                }
            }
        }
    }

    override fun solveSecond() {
        File(INPUT).forEachLine {
            val end = it.length
            (1..(end-14)).forEach {startIndex ->
                val characters = it.substring(startIndex-1, startIndex+13).toSet()
                if (characters.size == 14) {
                    println(startIndex+13)
                    return@forEachLine
                }
            }
        }
    }

    companion object {
        const val INPUT = "day6/input.txt"
    }

}