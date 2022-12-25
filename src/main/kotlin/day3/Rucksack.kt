package day3

import PuzzleSolution

class Rucksack: PuzzleSolution {
    override fun solveFirst() {
        val sum = readTextByLine(INPUT).map(::parseRucksack).sum()
        println(sum)
    }

    private fun parseRucksack(rucksack: String): Int {
        val firstCompartment = rucksack.substring(0, rucksack.length/2).toSet()
        val secondCompartment = rucksack.substring(rucksack.length/2).toSet()
        val outlier = firstCompartment intersect secondCompartment
        return priority[outlier.first()] ?: 0
    }

    override fun solveSecond() {
        val sum = readTextByLine(INPUT).withIndex().groupBy { it.index /3 }.map { ( _ , rucksacks) ->
            val badge = rucksacks[0].value.toSet() intersect rucksacks[1].value.toSet() intersect rucksacks[2].value.toSet()
            (priority[badge.first()] ?: 0)
        }.sum()
        println(sum)
    }

    companion object {
        const val INPUT = "day3/input.txt"
        val priority = (('a'..'z') + ('A'..'Z')).mapIndexed { index, value -> value to index + 1}.toMap()
    }
}