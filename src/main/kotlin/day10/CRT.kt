package day10

import PuzzleSolution
import ROOT_DIR
import java.io.File

class CRT :PuzzleSolution {
    override fun solveFirst() {
        var cumulativeSignalStrength = 0
        var tick = 0
        var xRegister = 1

        fun handleClockTick(){
            val importantCycles = listOf(20,60,100,140,180,220)
            if (tick in importantCycles) {
                cumulativeSignalStrength += (tick * xRegister)
            }
        }

        File(INPUT).forEachLine {
            if(it.startsWith("addx")){
                val (_, value) = it.split(" ")
                tick += 1
                handleClockTick()
                tick += 1
                handleClockTick()
                xRegister += value.toInt()
            } else if (it == "noop"){
                tick += 1
                handleClockTick()
            }
        }
        println("cumulativeSignalStrength: $cumulativeSignalStrength")
    }

    override fun solveSecond() {
        var tick = 0
        var xRegister = 1 // Middle of the sprite
        val output = mutableListOf<MutableList<Char>>()
        output.add(mutableListOf())

        fun handleClockTick(){
            val currentRow = output.last()
            val currentPixel = (tick-1) % 40
            if((currentPixel == xRegister) or (currentPixel == (xRegister-1)) or (currentPixel == (xRegister+1))){
                currentRow.add('#')
            } else {
                currentRow.add('.')
            }
            if(currentPixel == 39) output.add(mutableListOf())
        }

        File(INPUT).forEachLine {
            if(it.startsWith("addx")){
                val (_, value) = it.split(" ")
                tick += 1
                handleClockTick()
                tick += 1
                handleClockTick()
                xRegister += value.toInt()
            } else if (it == "noop"){
                tick += 1
                handleClockTick()
            }
        }
        output.forEach {
            println(it.joinToString())
        }
    }

    companion object {
        const val INPUT = "$ROOT_DIR/day10/input.txt"
    }
}