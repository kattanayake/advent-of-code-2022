package day5

import PuzzleSolution

import java.io.File

class SupplyStacks: PuzzleSolution {
    override fun solveFirst() {
        val (stacks, instructions) = parseInput()
        executeInstructions(stacks, instructions)
        stacks.map { it.removeLast() }.joinToString("").also {
            println(it)
        }
    }

    override fun solveSecond() {
        val (stacks, instructions) = parseInput()
        executeInstructionsAlt(stacks, instructions)
        stacks.map { it.removeLast() }.joinToString("").also {
            println(it)
        }
    }

    private fun parseInput(): Pair<List<MutableList<Char>>, List<Move>>{
        val stacks = (0..8).map { _ -> mutableListOf<Char>() }
        val instructions = mutableListOf<Move>()
        var parsingMode = ParsingMode.STACKS
        readTextByLine(INPUT).forEach {
            when(parsingMode){
                ParsingMode.STACKS -> {
                    if (it.contains('[')) parseStackInput(stacks, it)
                    else parsingMode = ParsingMode.SKIP
                }
                ParsingMode.SKIP -> if (it.isEmpty()) parsingMode = ParsingMode.INSTRUCTIONS
                ParsingMode.INSTRUCTIONS -> instructions.add(parseInstruction(it))
            }
        }
        return stacks.map { it.reversed().toMutableList() } to instructions
    }

    private fun parseStackInput(stacks: List<MutableList<Char>>, input: String){
        input.forEachIndexed { index, c ->  if (!STACK_BLACK_CHARACTERS.contains(c)) stacks[index / 4].add(c)}
    }

    private fun parseInstruction(input: String): Move {
        return input.split(" ").let {
            Move(
                it[1].toInt(),
                it[3].toInt().dec(),
                it[5].toInt().dec()
            )
        }
    }

    private fun executeInstructions(stacks: List<MutableList<Char>>, instructions: List<Move>){
        instructions.forEach { (numCrates, startStack, endStack) ->
            (1..numCrates).forEach { _ ->
                stacks[endStack].add(stacks[startStack].removeLast())
            }
        }
    }

    private fun executeInstructionsAlt(stacks: List<MutableList<Char>>, instructions: List<Move>){
        instructions.forEach { (numCrates, startStack, endStack) ->
            (1..numCrates).map { _ ->
                stacks[startStack].removeLast()
            }.reversed().also {
                stacks[endStack].addAll(it)
            }
        }
    }

    private data class Move(
        val numCrates: Int,
        val startStack: Int,
        val endStack: Int
    )

    private enum class ParsingMode {
        STACKS,
        SKIP,
        INSTRUCTIONS
    }

    companion object {
        const val INPUT = "day5/input.txt"
        val STACK_BLACK_CHARACTERS = listOf('[', ']', ' ')
    }
}