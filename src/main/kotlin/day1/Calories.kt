package day1

import PuzzleSolution

class Calories: PuzzleSolution {

    override fun solveFirst(){
        val max = readText(INPUT_FILE).split("\n\n").maxOfOrNull { group ->
            group.split("\n").filter { it.isNotEmpty() }.sumOf { it.toInt() }
        }
        println("Maximum calories: $max")
    }

    override fun solveSecond(){
        val max = readText(INPUT_FILE).split("\n\n").map { group ->
            group.split("\n").filter { it.isNotEmpty() }.sumOf { it.toInt() }
        }.sortedDescending().subList(0, 3).sum()
        println("Sum of top three Maximum calories: $max")
    }

    companion object {
        const val INPUT_FILE = "day1/input.txt"
    }
}