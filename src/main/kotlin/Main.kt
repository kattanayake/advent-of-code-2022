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

fun main() {
   val currentPuzzleSolution: PuzzleSolution = FullOfHotAir()
   currentPuzzleSolution.solveFirst()
   currentPuzzleSolution.solveSecond()
}

interface PuzzleSolution {
   fun solveFirst()
   fun solveSecond()
}
const val ROOT_DIR = "src/main/kotlin/"