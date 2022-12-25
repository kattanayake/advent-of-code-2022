package day1

import PuzzleSolution
import java.io.File

class Calories: PuzzleSolution {

    override fun solveFirst(){
        val max = readText(INPUT_FILE).split("\n\n").maxOfOrNull { group ->
            group.split("\n").filter { it.isNotEmpty() }.map { it.toInt() }.fold(0) { acc, i -> acc + i }
        }
        println("Maximum calories: $max")
    }

    override fun solveSecond(){
        val max = readText(INPUT_FILE).split("\n\n").map { group ->
            group.split("\n").filter { it.isNotEmpty() }.map { it.toInt() }.fold(0) { acc, i -> acc + i }
        }.sortedDescending().subList(0, 3).fold(0) { acc, i -> acc + i }
        println("Sum of top three Maximum calories: $max")
    }

    companion object {
        const val INPUT_FILE = "day1/input.txt"
    }
}