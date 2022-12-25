import day1.Calories
import day10.CRT
import day11.MonkeyInTheMiddle
import day12.HillClimbing
import day13.DistressSignal
import day14.RegolithReservoir
import day15.BeaconExclusionZone
import day16.VolcanoPressure
import day17.Tetris
import day18.BoilingBoulders
import day19.NotEnoughMinerals
import day2.RPS
import day20.GrovePositioningSystem
import day21.MonkeyMath
import day22.MonkeyMap
import day23.UnstableDiffusion
import day24.BlizzardBasin
import day25.FullOfHotAir
import day3.Rucksack
import day4.CampCleanup
import day5.SupplyStacks
import day6.TuningTrouble
import day7.NoSpaceLeft
import day8.TreeHouse
import day9.RopeBridge
import java.io.File
import kotlin.reflect.full.createInstance

fun main(args: Array<String>) {
   val problemClass = args.firstOrNull()?.let { dayMap[it] } ?: Calories::class
   val currentPuzzleSolution: PuzzleSolution = problemClass.createInstance()
   currentPuzzleSolution.solveFirst()
   currentPuzzleSolution.solveSecond()
}

interface PuzzleSolution {
   fun solveFirst()
   fun solveSecond()

   fun readText(fileName: String) = File(ROOT_DIR + fileName).readText().trim()
   fun readTextByLine(filename: String) = readText(filename).split("\n")
   companion object {
      const val ROOT_DIR = "src/main/kotlin/"
   }
}

private val dayMap = mapOf(
   "day1" to Calories::class,
   "day2" to RPS::class,
   "day3" to Rucksack::class,
)
