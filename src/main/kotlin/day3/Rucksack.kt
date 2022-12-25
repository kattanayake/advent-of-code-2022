package day3

import PuzzleSolution

import day1.Calories
import java.io.File

class Rucksack: PuzzleSolution {
    override fun solveFirst() {
        var sum = 0
        File(INPUT).forEachLine {
            sum += parseRucksack(it)
        }
        println(sum)
    }

    private fun parseRucksack(rucksack: String): Int {
        val firstCompartment = rucksack.substring(0, rucksack.length/2).toSet()
        val secondCompartment = rucksack.substring(rucksack.length/2).toSet()
        val outlier = firstCompartment intersect secondCompartment
        return priority[outlier.first()] ?: 0
    }

    override fun solveSecond() {
        var sum = 0
        var index = 0
        val rucksacks = mutableListOf<Set<Char>>()
        File(INPUT).forEachLine {
            index += 1
            rucksacks.add(it.toSet())
            if(index % 3 == 0){
                val badge = rucksacks[0] intersect rucksacks[1] intersect rucksacks[2]
                sum += (priority[badge.first()] ?: 0)
                rucksacks.clear()
            }
        }
        println(sum)
    }

    companion object {
        const val INPUT = "day3/input.txt"
        val priority = (('a'..'z') + ('A'..'Z')).mapIndexed { index, value -> value to index + 1}.toMap()
    }
}