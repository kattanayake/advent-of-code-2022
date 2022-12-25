package day13

import PuzzleSolution

import com.sun.xml.internal.ws.api.message.Packet
import java.io.File
import java.lang.Exception
import java.lang.Integer.max

class DistressSignal:PuzzleSolution {
    override fun solveFirst() {
        val packets = parseInput()
        val otherPackets = parseInputAlternate()

        packets.forEachIndexed { index, packetPair ->
            if(packetPair != otherPackets[index]){
                println("Difference at index $index")
                println("Ours: $packetPair")
                println("Theirs: ${otherPackets[index]}")
            }
        }
        val inOrderPackets = packets.mapIndexed { index, packetPair ->
            val result = validateLists(packetPair.leftPacket.data, packetPair.rightPacket.data)
            index to result
        }.associate {
            it
        }

        val answer = inOrderPackets.entries.filter { it.value == VALIDATION.VALID }.fold(0) { acc, entry ->
            acc + entry.key + 1
        }
        println("Valid: ${inOrderPackets.values.count { it == VALIDATION.VALID }}")
        println("Invalid: ${inOrderPackets.values.count { it == VALIDATION.INVALID }}")
        println("Indeterminate: ${inOrderPackets.values.count { it == VALIDATION.INDETERMINATE }}")
        println("Answer: $answer")
    }

    override fun solveSecond() {
        val dividerPacket0 = Packet(PacketData.ListData(mutableListOf(PacketData.ListData(mutableListOf(PacketData.IntegerData(2))))))
        val dividerPacket1 = Packet(PacketData.ListData(mutableListOf(PacketData.ListData(mutableListOf(PacketData.IntegerData(6))))))
        val packets = parseInput().map { listOf(it.leftPacket, it.rightPacket) }.flatten() + listOf(dividerPacket0, dividerPacket1)
        println(packets.last().data)
        val sorted = packets.sortedWith { p0, p1 -> validateLists(p0.data, p1.data).sortVal }
        val answer = (sorted.indexOf(dividerPacket0) + 1) * (sorted.indexOf(dividerPacket1) + 1)
        println("answer: $answer")
    }

    private fun parseInput(): MutableList<PacketPair> {
        val packetPairs = mutableListOf<PacketPair>()
        var index = 1
        var firstPacket: Packet? = null
        var secondPacket: Packet? = null
        File(INPUT).forEachLine {
            if (it.startsWith("[")){
                if (firstPacket == null) firstPacket = Packet(parseListData(it))
                else secondPacket = Packet(parseListData(it))
            } else {
                packetPairs.add(PacketPair(
                    index = index,
                    leftPacket = firstPacket!!,
                    rightPacket = secondPacket!!
                ))
                index += 1
                firstPacket = null
                secondPacket = null
            }
        }
        packetPairs.add(PacketPair(
            index = index,
            leftPacket = firstPacket!!,
            rightPacket = secondPacket!!
        ))
        return packetPairs
    }

    private fun parseInputAlternate(): List<PacketPair> {
        val input = File(INPUT).readText().split("\n").fold(mutableListOf<MutableList<PacketData>>(mutableListOf())) { acc, line ->
            if (line.isBlank()) {
                acc.add(mutableListOf())
            } else {
                acc.last().add(parsePacketData(line, 1).first)
            }
            acc
        }.mapIndexed { index, packetData ->
            PacketPair(
                index + 1,
                Packet(packetData[0].toListData()),
                Packet(packetData[1].toListData())
            )
        }
        return input
    }

    private fun parsePacketData(line: String, index: Int): Pair<PacketData, Int> {
        var iterIndex = index
        val currentPacketData = PacketData.ListData(mutableListOf())
        var buffer = ""
        while (line[iterIndex] != ']') {
            if (line[iterIndex] == ',') {
                if (buffer.isNotEmpty()) {
                    currentPacketData.data.add(PacketData.IntegerData(buffer.toInt()))
                    buffer = ""
                }
            }
            else if (line[iterIndex] == '[') {
                val (packet, ind) = parsePacketData(line, iterIndex+1)
                currentPacketData.data.add(packet)
                iterIndex = ind
            } else {
                buffer += line[iterIndex]
            }
            iterIndex++
        }

        if (buffer.isNotEmpty()) {
            currentPacketData.data.add(PacketData.IntegerData(buffer.toInt()))
        }

        return currentPacketData to iterIndex
    }

    private fun parseListData(data: String): PacketData.ListData {
        val elements = mutableListOf<PacketData>()
        var index = 1
        var done = false
        while (!done) {
            when (val character = data[index]) {
                '[' -> { // Start of list, must find end of list and recursive call
                    val listEnd = findMatchingClosingIndex(data.substring(index)) + 1
                    elements.add(
                        parseListData(
                            data.substring(index,listEnd + index)
                        )
                    )
                    index += listEnd
                }
                ',', ']' -> {}
                else -> {
                    val intEnd = findIntEndingIndex(data.substring(index))
                    elements.add(PacketData.IntegerData(data.substring(index, intEnd + index).toInt()))
                    index += intEnd
                }
            }
            index += 1
            if (index == data.length) done = true
        }
        return PacketData.ListData(elements)
    }

    private fun findMatchingClosingIndex(data: String): Int{
        var depth = 0
        data.forEachIndexed { index, char ->
            if (char == '[') depth += 1
            if (char == ']') depth -= 1
            if (depth == 0) return index
        }
        return 0
    }

    private fun findIntEndingIndex(data:String): Int {
        data.forEachIndexed { index, c ->
            if (c.digitToIntOrNull() == null) return index
        }
        return 0
    }

    private fun validateLists(first: PacketData.ListData, second: PacketData.ListData): VALIDATION{
        first.data.forEachIndexed { index, leftElement ->
            if (index >= second.data.size) return VALIDATION.INVALID
            val rightElement = second.data[index]

            if (leftElement is PacketData.IntegerData && rightElement is PacketData.IntegerData){
                if (leftElement.data < rightElement.data) return VALIDATION.VALID
                if (leftElement.data > rightElement.data) return VALIDATION.INVALID
            } else{
                val recursiveResult =  validateLists(leftElement.toListData(), rightElement.toListData())
                if (recursiveResult != VALIDATION.INDETERMINATE) return recursiveResult
            }
        }
        return if (first.data.size < second.data.size) VALIDATION.VALID
        else VALIDATION.INDETERMINATE
    }

    private enum class VALIDATION(val sortVal: Int) {
        VALID(-1),
        INVALID(1),
        INDETERMINATE(0);
    }

    private data class PacketPair(
        val index: Int,
        val leftPacket: Packet,
        val rightPacket: Packet
    )

    private data class Packet(
        val data: PacketData.ListData
    )

    private sealed class PacketData {
        fun toListData() = if(this is ListData) this else ListData(mutableListOf(this))
        data class ListData(val data: MutableList<PacketData>): PacketData(){
            override fun toString(): String {
                return data.toString()
            }
        }
        data class IntegerData(val data: Int): PacketData(){
            override fun toString(): String {
                return data.toString()
            }
        }
    }

    companion object {
        const val INPUT = "day13/input.txt"
    }
}