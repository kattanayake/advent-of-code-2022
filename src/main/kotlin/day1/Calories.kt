package day1

import PuzzleSolution
import ROOT_DIR
import java.io.File
import java.nio.file.Paths

class Calories:PuzzleSolution {

    override fun solveFirst(){
        var maxCalories = 0
        var currentCalories = 0
        println( Paths.get("").toAbsolutePath().toString() )
        File(INPUT_FILE).forEachLine {
            if (it.isEmpty()){
                if (currentCalories > maxCalories) {
                    maxCalories = currentCalories
                }
                currentCalories = 0
            } else {
                currentCalories += it.toInt()
            }
        }
        if (currentCalories > maxCalories) maxCalories = currentCalories
        println("Maximum calories: $maxCalories")
    }

    override fun solveSecond(){
        val elfCalories = mutableListOf<Int>()
        var currentCalories = 0
        File(INPUT_FILE).forEachLine {
            if (it.isEmpty()){
                elfCalories.add(currentCalories)
                currentCalories = 0
            } else {
                currentCalories += it.toInt()
            }
        }
        elfCalories.add(currentCalories)
        elfCalories.sortDescending()
        val topThree = elfCalories.subList(0,3).sum()
        print(topThree)
    }

    companion object {
        const val INPUT_FILE = "$ROOT_DIR/day1/input.txt"
    }
}