package day8

import PuzzleSolution
import ROOT_DIR
import java.io.File

class TreeHouse: PuzzleSolution {
    override fun solveFirst() {
        val treeGrid = parseInput()
        val initialUnknown = getCountMatchingVisibility(treeGrid, VISIBILITY.UNKNOWN)
        println("initialUnknown: $initialUnknown")
        bruteForceUpdateVisibility(treeGrid)
        println("bruteForceUpdateVisibility: ${getCountMatchingVisibility(treeGrid, VISIBILITY.VISIBLE)}")
    }

    override fun solveSecond() {
        val treeGrid = parseInput()
        for (i in (1..97)) { // Rows
            for (j in (1..97)) {
                val tree = treeGrid[i][j]
                listOf(CARDINAL_DIRECTION.NORTH, CARDINAL_DIRECTION.SOUTH, CARDINAL_DIRECTION.EAST, CARDINAL_DIRECTION.WEST ).forEach {
                    tree.directionViewingDistance[it] = viewingDistanceInDirection(treeGrid, it, i, j, tree.height)
                }
                tree.setViewingDistance()
            }
        }
        val maxViewingDistance = treeGrid.map { row -> row.map { it.viewingDistance } }.flatten().max()
        println("maxViewingDistance: $maxViewingDistance")
    }

    private fun parseInput(): List<List<Tree>> {
        val grid = mutableListOf<List<Tree>>()
        var rowNum = 0
        File(INPUT).forEachLine {rowInput ->
            val row = rowInput.mapIndexed { index, c ->
                val northVis = if (rowNum == 0) VISIBILITY.VISIBLE else VISIBILITY.UNKNOWN
                val southVis = if (rowNum == 98) VISIBILITY.VISIBLE else VISIBILITY.UNKNOWN
                val eastVis = if (index == 0) VISIBILITY.VISIBLE else VISIBILITY.UNKNOWN
                val westVis = if (index == 98) VISIBILITY.VISIBLE else VISIBILITY.UNKNOWN
                Tree(
                    height = c.digitToInt(),
                    rowIndex = rowNum,
                    columnIndex = index,
                    visibility = if(listOf(northVis, southVis, eastVis, westVis).any { it == VISIBILITY.VISIBLE }) VISIBILITY.VISIBLE else VISIBILITY.UNKNOWN,
                    directionVisibility = mutableMapOf(
                        CARDINAL_DIRECTION.NORTH to northVis,
                        CARDINAL_DIRECTION.SOUTH to southVis,
                        CARDINAL_DIRECTION.EAST to eastVis,
                        CARDINAL_DIRECTION.WEST to westVis,
                    )
                )
            }
            grid.add(row)
            rowNum += 1
        }
        return grid
    }

    private fun bruteForceUpdateVisibility(treeGrid: List<List<Tree>>){
        // North and East visibility, since we're going top down, left to right
        for (i in (1..97)) { // Rows
            for (j in (1..97)) {
                val tree = treeGrid[i][j]
                listOf(CARDINAL_DIRECTION.NORTH, CARDINAL_DIRECTION.SOUTH, CARDINAL_DIRECTION.EAST, CARDINAL_DIRECTION.WEST ).forEach {
                    tree.directionVisibility[it] = if(visibleInDirection(treeGrid,it,i,j,tree.height)) VISIBILITY.VISIBLE else VISIBILITY.HIDDEN
                }
                tree.setVisibility()
            }
        }
    }

    private fun visibleInDirection(
        treeGrid: List<List<Tree>>,
        direction: CARDINAL_DIRECTION,
        rowIndex: Int,
        columnIndex: Int,
        treeHeight: Int,
    ): Boolean {
        if((rowIndex == 0 && direction == CARDINAL_DIRECTION.NORTH) or
            (rowIndex == 98 && direction == CARDINAL_DIRECTION.SOUTH) or
            (columnIndex == 0 && direction == CARDINAL_DIRECTION.EAST) or
            (columnIndex == 98 && direction == CARDINAL_DIRECTION.WEST)
        ) {
            return true
        }
        val (nextRowIndex, nextColumnIndex) = when(direction){
            CARDINAL_DIRECTION.NORTH -> rowIndex - 1 to columnIndex
            CARDINAL_DIRECTION.SOUTH -> rowIndex + 1 to columnIndex
            CARDINAL_DIRECTION.EAST -> rowIndex to columnIndex - 1
            CARDINAL_DIRECTION.WEST -> rowIndex to columnIndex + 1
        }
        val neighbour = treeGrid[nextRowIndex][nextColumnIndex]
        if(neighbour.height >= treeHeight) return false
        return visibleInDirection(treeGrid, direction, nextRowIndex, nextColumnIndex, treeHeight)
    }

    private fun viewingDistanceInDirection(
        treeGrid: List<List<Tree>>,
        direction: CARDINAL_DIRECTION,
        rowIndex: Int,
        columnIndex: Int,
        treeHeight: Int,
        currentDistance: Int = 0
    ): Int {
        if((rowIndex == 0 && direction == CARDINAL_DIRECTION.NORTH) or
            (rowIndex == 98 && direction == CARDINAL_DIRECTION.SOUTH) or
            (columnIndex == 0 && direction == CARDINAL_DIRECTION.EAST) or
            (columnIndex == 98 && direction == CARDINAL_DIRECTION.WEST)
        ) {
            return currentDistance
        }
        val (nextRowIndex, nextColumnIndex) = when(direction){
            CARDINAL_DIRECTION.NORTH -> rowIndex - 1 to columnIndex
            CARDINAL_DIRECTION.SOUTH -> rowIndex + 1 to columnIndex
            CARDINAL_DIRECTION.EAST -> rowIndex to columnIndex - 1
            CARDINAL_DIRECTION.WEST -> rowIndex to columnIndex + 1
        }
        val neighbour = treeGrid[nextRowIndex][nextColumnIndex]
        if(neighbour.height >= treeHeight) return currentDistance + 1
        return viewingDistanceInDirection(treeGrid, direction, nextRowIndex, nextColumnIndex, treeHeight, currentDistance + 1)
    }

    private fun getCountMatchingVisibility(treeGrid: List<List<Tree>>, visibility: VISIBILITY) =
        treeGrid.map { row -> row.map { it.visibility } }.flatten().count { it == visibility }

    private data class Tree(
        val height: Int,
        val rowIndex: Int,
        val columnIndex: Int,
        var visibility: VISIBILITY,
        var directionVisibility: MutableMap<CARDINAL_DIRECTION, VISIBILITY>,
        var viewingDistance: Int = 0,
        var directionViewingDistance: MutableMap<CARDINAL_DIRECTION, Int> = mutableMapOf()
    ) {
        fun setVisibility(){
            if (directionVisibility.all { it.value == VISIBILITY.HIDDEN }) {
                visibility = VISIBILITY.HIDDEN
            } else if (directionVisibility.any { it.value == VISIBILITY.UNKNOWN }) {
                println("Error with tree")
            } else {
                visibility = VISIBILITY.VISIBLE
            }
        }

        fun setViewingDistance(){
            var multiple = 1
            directionViewingDistance.values.forEach { multiple *= it }
            viewingDistance = multiple
        }
    }

    private enum class VISIBILITY {
        VISIBLE,
        HIDDEN,
        UNKNOWN
    }

    private enum class CARDINAL_DIRECTION {
        NORTH,
        SOUTH,
        EAST,
        WEST
    }

    companion object {
        const val INPUT = "$ROOT_DIR/day8/input.txt"
    }
}