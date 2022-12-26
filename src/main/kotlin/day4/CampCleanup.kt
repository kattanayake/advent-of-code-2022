package day4

import PuzzleSolution

import java.io.File

class CampCleanup: PuzzleSolution {
    override fun solveFirst() {
        val numOverlappingElves = readTextByLine(INPUT).sumOf {
            val (elfOneRange, elfTwoRange) = it.split(",").let { (elfOne, elfTwo) ->
                elfOne.toRangePair() to elfTwo.toRangePair()
            }
            if (areCompletelyOverlappingRanges(elfOneRange, elfTwoRange)) 1L else 0L
        }
        println(numOverlappingElves)
    }

    private fun String.toRangePair() = this.split("-").let { (first, second) -> first.toInt() to second.toInt() }

    override fun solveSecond() {
        val numOverlappingElves = readTextByLine(INPUT).sumOf {
            val (elfOneRange, elfTwoRange) = it.split(",").let { (elfOne, elfTwo) ->
                elfOne.toRangePair() to elfTwo.toRangePair()
            }
            if (areOverlappingRanges(elfOneRange, elfTwoRange)) 1L else 0L
        }
        println(numOverlappingElves)
    }

    private fun areCompletelyOverlappingRanges(first: Pair<Int, Int>, second: Pair<Int, Int>): Boolean {
        val (bigger, smaller) = if((first.second - first.first) > (second.second - second.first)) first to second else second to first
        return (bigger.first <= smaller.first) && (bigger.second >= smaller.second)
    }

    private fun areOverlappingRanges(first: Pair<Int, Int>, second: Pair<Int, Int>): Boolean {
        return ((first.first..first.second) intersect  (second.first..second.second)).isNotEmpty()
    }

    companion object {
        const val INPUT = "day4/input.txt"
    }
}