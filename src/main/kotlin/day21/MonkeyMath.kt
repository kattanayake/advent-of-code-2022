package day21

import PuzzleSolution

import java.io.File
import java.lang.Exception

class MonkeyMath: PuzzleSolution {

    override fun solveFirst() {
        val monkeys = parseInput()
        val answer = naiveRecursiveSolution(monkeys, monkeys["root"]!!)
        println("answer: $answer")
    }

    override fun solveSecond() {
        val monkeys = parseInput().toMutableMap()
        val rootMonkey = monkeys["root"] as MonkeyProblem.UnknownMonkeyProblem
        val target = naiveRecursiveSolution(monkeys, monkeys[rootMonkey.rightSource]!!)
        var tooHigh = 4000000000000L
        var tooLow = 3000000000000L
        while (true) {
            val newGuess = (tooHigh + tooLow)/2
            monkeys["humn"] = MonkeyProblem.KnownMonkeyProblem("humn", newGuess)
            val evaluatedAnswer = naiveRecursiveSolution(monkeys, monkeys[rootMonkey.leftSource]!!)
            val offBy = target - evaluatedAnswer
            if (offBy == 0L) {
                println("Found answer! $newGuess")
                return
            }
            if (offBy < 0) tooLow = newGuess
            else tooHigh = newGuess
            println("$newGuess is off by $offBy")
        }
    }

    private fun naiveRecursiveSolution(monkeys: Map<String, MonkeyProblem>, problem: MonkeyProblem): Long {
        return when(problem){
            is MonkeyProblem.KnownMonkeyProblem -> problem.value
            is MonkeyProblem.UnknownMonkeyProblem -> {
                val leftVal = naiveRecursiveSolution(monkeys, monkeys[problem.leftSource]!!)
                val rightVal = naiveRecursiveSolution(monkeys, monkeys[problem.rightSource]!!)
                when(problem.operand){
                    Operand.ADD -> leftVal + rightVal
                    Operand.SUBTRACT -> leftVal - rightVal
                    Operand.MULTIPLY -> leftVal * rightVal
                    Operand.DIVIDE -> leftVal / rightVal
                }
            }
        }
    }

    private fun parseInput() = readTextByLine(INPUT).map { line ->
        val (name, problemParts) = line.split(":").let { (first, second) ->
            first to second.trim().split(" ")
        }
        if (problemParts.size == 1) MonkeyProblem.KnownMonkeyProblem(name, problemParts.first().toLong())
        else MonkeyProblem.UnknownMonkeyProblem(
            name = name,
            leftSource = problemParts[0],
            rightSource = problemParts[2],
            operand = Operand.fromString(problemParts[1]))
    }.associateBy { it.name }

    private sealed class MonkeyProblem(val name: String) {
        class UnknownMonkeyProblem(
            name: String,
            var leftSource: String,
            val rightSource: String,
            val operand: Operand
        ) : MonkeyProblem(name)
        class KnownMonkeyProblem(name: String, val value: Long): MonkeyProblem(name)

    }

    private enum class Operand{
        ADD,
        SUBTRACT,
        MULTIPLY,
        DIVIDE;

        companion object {
            fun fromString(string: String) = when(string){
                "+" -> ADD
                "-" -> SUBTRACT
                "/" -> DIVIDE
                "*" -> MULTIPLY
                else -> throw Exception("Unknown Operand")
            }
        }
    }

    companion object {
        const val INPUT = "day21/input.txt"
    }
}