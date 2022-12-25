package util

data class Coordinate(val columnIndex: Int, val rowIndex: Int) {
    fun up() = Coordinate(columnIndex, rowIndex - 1)
    fun upLeft() = Coordinate(columnIndex-1, rowIndex - 1)
    fun upRight() = Coordinate(columnIndex+1, rowIndex - 1)
    fun down() = Coordinate(columnIndex, rowIndex + 1)
    fun downLeft() = Coordinate(columnIndex-1, rowIndex + 1)
    fun downRight() = Coordinate(columnIndex+1, rowIndex + 1)
    fun left() = Coordinate(columnIndex-1, rowIndex)
    fun right() = Coordinate(columnIndex+1, rowIndex)
}