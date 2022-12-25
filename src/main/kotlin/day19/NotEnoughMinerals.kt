package day19

import PuzzleSolution
import ROOT_DIR
import java.io.File
import java.lang.Integer.min
import java.util.Calendar

class NotEnoughMinerals: PuzzleSolution {
    override fun solveFirst() {
        val blueprints = parseInput()
        var qualityLevel = 0
        blueprints.forEach {
            val timeStart = Calendar.getInstance().timeInMillis
            val maxGeodeCount = simulate(it)
            qualityLevel += ( maxGeodeCount * it.number)
            val timeEnd = Calendar.getInstance().timeInMillis
            println("maxGeodeCount for blueprint #${it.number}: $maxGeodeCount. Took ${timeEnd - timeStart} milliseconds")
        }
        println("Quality level: $qualityLevel")
    }

    override fun solveSecond() {
        val blueprints = parseInput()
        var topThreeMultiple = 1
        blueprints.subList(0, min(blueprints.size, 3)).forEach {
            val timeStart = Calendar.getInstance().timeInMillis
            val maxGeodeCount = simulate(it, 32)
            topThreeMultiple *= maxGeodeCount
            val timeEnd = Calendar.getInstance().timeInMillis
            println("maxGeodeCount for blueprint #${it.number}: $maxGeodeCount. Took ${timeEnd - timeStart} milliseconds")
        }
        println("Top three multiple: $topThreeMultiple")
    }

    private fun simulate(blueprint: Blueprint, numMinutes: Int = MINUTES): Int {
        val checkpoints = mutableSetOf(BlueprintCheckpoint(blueprint))
        val nextCheckpoints = mutableSetOf<BlueprintCheckpoint>()
        repeat(numMinutes){
            checkpoints.forEach { checkpoint ->
                //Buy nothing
                nextCheckpoints.add(checkpoint.nextTick())
                if (checkpoint.canBuyOreBot()) nextCheckpoints.add(checkpoint.nextTick().buyOreBot())
                if (checkpoint.canBuyClayBot()) nextCheckpoints.add(checkpoint.nextTick().buyClayBot())
                if (checkpoint.canBuyObsidianBot()) nextCheckpoints.add(checkpoint.nextTick().buyObsidianBot())
                if (checkpoint.canBuyGeodeBot()) nextCheckpoints.add(checkpoint.nextTick().buyGeodeBot())
            }
            checkpoints.clear();checkpoints.addAll(nextCheckpoints);nextCheckpoints.clear()
            // Culling slower ticks
            if (checkpoints.size > 1000000){
                val trimmedCheckpoints = checkpoints.sortedByDescending { it2 -> it2.getSortPriority() }.subList(0, 1000000)
                checkpoints.clear(); checkpoints.addAll(trimmedCheckpoints)
            }
        }
        return checkpoints.maxBy { it.geodeCount }.geodeCount
    }

    private fun parseInput() = File(INPUT).readText().split("\n").filter { it.isNotEmpty() }.mapIndexed { index, s ->
        val blueprintParts = s.split(":", ".")
        Blueprint(
            number = index + 1,
            oreRobotOreCost = blueprintParts[1].trim().split(" ")[4].toInt(),
            clayRobotOreCost = blueprintParts[2].trim().split(" ")[4].toInt(),
            obsidianRobotOreCost = blueprintParts[3].trim().split(" ")[4].toInt(),
            obsidianRobotClayCost = blueprintParts[3].trim().split(" ")[7].toInt(),
            geodeRobotOreCost = blueprintParts[4].trim().split(" ")[4].toInt(),
            geodeRobotObsidianCost = blueprintParts[4].trim().split(" ")[7].toInt()
        )
    }

    private data class BlueprintCheckpoint(
        val blueprint: Blueprint,
        val oreCount: Int = 0,
        val clayCount: Int = 0,
        val obsidianCount: Int = 0,
        val geodeCount: Int = 0,
        val oreBotCount: Int = 1,
        val clayBotCount: Int = 0,
        val obsidianBotCount: Int = 0,
        val geodeBotCount: Int = 0
    ) {
        fun nextTick() = this.copy(
            oreCount = oreCount + oreBotCount,
            clayCount = clayCount + clayBotCount,
            obsidianCount = obsidianCount + obsidianBotCount,
            geodeCount = geodeCount + geodeBotCount,
        )

        fun canBuyOreBot() = this.oreCount >= blueprint.oreRobotOreCost
        fun canBuyClayBot() = this.oreCount >= blueprint.clayRobotOreCost
        fun canBuyObsidianBot() = (this.oreCount >= blueprint.obsidianRobotOreCost) and (this.clayCount >= blueprint.obsidianRobotClayCost)
        fun canBuyGeodeBot() = (this.oreCount >= blueprint.geodeRobotOreCost) and (this.obsidianCount >= blueprint.geodeRobotObsidianCost)

        fun buyOreBot() = this.copy(oreCount = oreCount - blueprint.oreRobotOreCost,oreBotCount = oreBotCount + 1)
        fun buyClayBot() = this.copy(oreCount = oreCount - blueprint.clayRobotOreCost, clayBotCount = clayBotCount + 1)
        fun buyObsidianBot() = this.copy(
            oreCount = oreCount - blueprint.obsidianRobotOreCost,
            clayCount = clayCount - blueprint.obsidianRobotClayCost,
            obsidianBotCount = obsidianBotCount + 1
        )
        fun buyGeodeBot() = this.copy(
            oreCount = oreCount - blueprint.geodeRobotOreCost,
            obsidianCount = obsidianCount - blueprint.geodeRobotObsidianCost,
            geodeBotCount = geodeBotCount + 1
        )

        fun getSortPriority() = oreBotCount + (2 * clayBotCount) + (3 * obsidianBotCount) + (4 * geodeBotCount)
    }

    private data class Blueprint(
        val number: Int,
        val oreRobotOreCost: Int,
        val clayRobotOreCost: Int,
        val obsidianRobotOreCost: Int,
        val obsidianRobotClayCost: Int,
        val geodeRobotOreCost: Int,
        val geodeRobotObsidianCost: Int
    )

    companion object {
        const val INPUT = "$ROOT_DIR/day19/input.txt"
        const val MINUTES = 24
    }
}