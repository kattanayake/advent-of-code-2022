package day11

import PuzzleSolution

import java.io.File

class MonkeyInTheMiddle: PuzzleSolution {
    override fun solveFirst() {
        val monkeys = parseInput()
        val inspections = executeRounds(monkeys)
        inspections.sortDescending()
        val monkeyBusiness = inspections[0] * inspections[1]
        println("monkeyBusiness: $monkeyBusiness")
    }

    private fun executeRounds(monkeys: Map<Int, Monkey>, numRounds:Int = 20, isVeryWorried:Boolean = false, divisor: Long? = null): LongArray {
        val inspections = LongArray(monkeys.size)
        (0 until numRounds).forEach {_ ->
            monkeys.forEach { (monkeyNumber, monkey) ->
                (0 until monkey.items.size).forEach { _ ->
                    inspections[monkeyNumber] = (inspections[monkeyNumber] + 1)
                    val item = monkey.items.removeFirst()
                    val newWorry = monkey.operation(item).let {
                        if (isVeryWorried) {
                            it
                        } else {
                            it / 3
                        }
                    }.let { worry -> divisor?.let { worry % divisor } ?: worry }
                    if ((newWorry % monkey.testDivisibleBy).toInt() == 0) {
                        monkeys[monkey.trueDestination]!!.items.add(newWorry)
                    } else {
                        monkeys[monkey.falseDestination]!!.items.add(newWorry)
                    }
                }

            }
        }
        return inspections
    }

    override fun solveSecond() {
        val monkeys = parseInput()
        val bigDivisor = monkeys.values.fold(1L){ acc, monkey -> acc * monkey.testDivisibleBy }
        val inspections = executeRounds(monkeys, 10000, true, bigDivisor)
        inspections.sortDescending()
        val monkeyBusiness = inspections[0] * inspections[1]
        println("monkeyBusiness: $monkeyBusiness")
    }

    private fun parseInput(): Map<Int, Monkey>{
        val monkeys = mutableMapOf<Int, Monkey>()

        var monkeyNumber: Int = -1
        lateinit var items: ArrayList<Long>
        lateinit var operation: (Long) -> Long
        var test: Int = 0
        var trueDestination: Int = 0
        var falseDestination: Int = 0
        readTextByLine(INPUT).forEach { line ->
            val trimmedLine = line.trim()
            if(trimmedLine.startsWith("Monkey")){
                monkeyNumber+=1
            } else if (trimmedLine.startsWith("Starting items")){
                items = trimmedLine.split(":")[1].trim().split(",").map { it.trim().toLong() }.let { ArrayList(it) }
            } else if (trimmedLine.startsWith("Operation")){
                operation = trimmedLine.split("=")[1].trim().let { OPERATION_MAP[it]!! }
            } else if (trimmedLine.startsWith("Test")){
                test = trimmedLine.split("by")[1].trim().toInt()
            } else if (trimmedLine.startsWith("If true")){
                trueDestination = trimmedLine.split("monkey")[1].trim().toInt()
            } else if (trimmedLine.startsWith("If false")){
                falseDestination = trimmedLine.split("monkey")[1].trim().toInt()
            } else if (trimmedLine.isEmpty()){
                monkeys[monkeyNumber] = Monkey(
                    items = items,
                    operation = operation,
                    testDivisibleBy = test,
                    trueDestination = trueDestination,
                    falseDestination = falseDestination
                )
            }
        }
        monkeys[monkeyNumber] = Monkey(
            items = items,
            operation = operation,
            testDivisibleBy = test,
            trueDestination = trueDestination,
            falseDestination = falseDestination
        )
        return monkeys
    }

    private data class Monkey(
        val items: ArrayList<Long>,
        val operation: (Long) -> Long,
        val testDivisibleBy: Int,
        val falseDestination: Int,
        val trueDestination: Int
    )

    companion object {
        const val INPUT = "day11/input.txt"
        val OPERATION_MAP = mapOf<String, (Long) -> Long>(
            "old * 2" to { x-> x*2 },
            "old * old" to { x-> x*x },
            "old + 6" to { x-> x+6 },
            "old + 2" to { x-> x+2 },
            "old * 11" to { x-> x*11 },
            "old + 7" to { x-> x+7 },
            "old + 1" to { x-> x+1 },
            "old + 5" to { x-> x+5 },
        )
    }
}