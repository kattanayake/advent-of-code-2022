package day2

import PuzzleSolution

class RPS: PuzzleSolution {

    override fun solveFirst() {
        val score = readTextByLine(INPUT).map(::parseRound).sum()
        println(score)
    }

    override fun solveSecond() {
        val score = readTextByLine(INPUT).map(::parseRoundSpecial).sum()
        println(score)
    }

    private fun parseRoundSpecial(play: String): Int{
        val (firstPlay, outcome) = play.split(" ").let { (first, second) ->
            firstPlayKey[first] to specialSecondPlayKey[second]
        }
        return if(firstPlay != null && outcome != null) {
            val secondPlay = generatePlay(firstPlay, outcome)
            secondPlay.points + outcome.points
        } else 0
    }

    private fun generatePlay(firstPlay: RPSMove, outcome: RPSOutcome): RPSMove {
        return when(outcome) {
            RPSOutcome.WIN -> firstPlay.losesTo()
            RPSOutcome.LOSS -> firstPlay.beats()
            RPSOutcome.DRAW -> firstPlay
        }
    }

    private fun parseRound( play: String): Int {
        val (firstPlay, secondPlay) = play.split(" ").let { (first, second) ->
            firstPlayKey[first] to defaultSecondPlayKey[second]
        }
        return if(firstPlay != null && secondPlay != null){
            evaluatePlay(firstPlay, secondPlay).points + secondPlay.points
        } else 0
    }

    private fun evaluatePlay(firstPlay: RPSMove, secondPlay: RPSMove): RPSOutcome{
        return when (firstPlay) {
            secondPlay -> RPSOutcome.DRAW
            secondPlay.losesTo() -> RPSOutcome.LOSS
            else -> RPSOutcome.WIN
        }
    }

    internal enum class RPSMove(val points: Int) {
        ROCK(1),
        PAPER(2),
        SCISSORS(3);

        fun losesTo(): RPSMove = when(this){
            ROCK -> PAPER
            PAPER -> SCISSORS
            SCISSORS -> ROCK
        }

        fun beats(): RPSMove = when(this){
            ROCK -> SCISSORS
            PAPER -> ROCK
            SCISSORS -> PAPER
        }
    }

    internal enum class RPSOutcome(val points: Int) {
        WIN(6),
        DRAW(3),
        LOSS(0)
    }

    companion object {
        const val INPUT = "day2/input.txt"
        private val firstPlayKey = mapOf("A" to RPSMove.ROCK, "B" to RPSMove.PAPER, "C" to RPSMove.SCISSORS)
        private val defaultSecondPlayKey = mapOf("X" to RPSMove.ROCK, "Y" to RPSMove.PAPER, "Z" to RPSMove.SCISSORS)
        private val specialSecondPlayKey = mapOf("X" to RPSOutcome.LOSS, "Y" to RPSOutcome.DRAW, "Z" to RPSOutcome.WIN)
    }
}