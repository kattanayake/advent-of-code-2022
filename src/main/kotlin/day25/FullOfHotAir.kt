package day25

import PuzzleSolution
import java.io.File
import kotlin.math.absoluteValue
import kotlin.math.floor
import kotlin.math.pow

class FullOfHotAir: PuzzleSolution {
    override fun solveFirst() {
        val sumOfFuel = parseInput()
        println("Sum of fuel in decimal: $sumOfFuel, in snafu:${decimalToSnafu(sumOfFuel)}")
    }

    override fun solveSecond() = Unit

    private fun parseInput() = readTextByLine(INPUT).map { line ->
        snafuToDecimal(line)
    }.fold(0.0) { acc, d -> acc + d  }

    private fun snafuToDecimal(snafu: String) = snafu.reversed().mapIndexed { index, c ->
        5.0.pow(index) * c.fromSnafu()
    }.fold(0.0) { acc, d -> acc + d  }

    private fun decimalToSnafu(inputValue: Double): String{
        val workingValue = inputValue.absoluteValue
        var numDigits = 0
        while (maxSnafuByInt(numDigits) < workingValue) {
            numDigits++
        }
        val placeValue = 5.0.pow(numDigits)
        var currentDigit = 1
        if((placeValue + maxSnafuByInt(numDigits-1)) + 1 <= workingValue) currentDigit++
        var remainder = workingValue - (placeValue * currentDigit)

        if (inputValue < 0) {
            currentDigit*=-1
            remainder *= -1
        }

        return if (remainder == 0.0) currentDigit.toSnafu().padEnd(numDigits + 1, '0')
        else {
            val rest = decimalToSnafu(remainder)
            currentDigit.toSnafu().padEnd(numDigits + 1 - rest.length, '0') + rest
        }
    }

    private fun maxSnafuByInt(numPlaces: Int) = if(numPlaces==-1) 0.0 else floor((5.0.pow(numPlaces+1)/2))

    private fun Char.fromSnafu() = when(this){
        '=' -> -2
        '-' -> -1
        else -> this.digitToInt()
    }

    private fun Int.toSnafu() = when(this){
        -1 -> "-"
        -2 -> "="
        else -> this.toString()
    }

    companion object{
        const val INPUT = "day25/input.txt"
    }
}