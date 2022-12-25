package day20

import PuzzleSolution
import ROOT_DIR
import java.io.File
import kotlin.math.absoluteValue

class GrovePositioningSystem: PuzzleSolution {
    override fun solveFirst() {
        val nodes = generateLinkedList()
        println("Node list size: ${nodes.size}")
        mix(nodes)
        printAnswer(nodes)
    }

    override fun solveSecond() {
        val nodes = generateLinkedList()
        println("Node list size: ${nodes.size}")
        repeat(10) {mix(nodes, DECRYPTION_KEY)}
        printAnswer(nodes, DECRYPTION_KEY)
    }

    private fun mix(nodes: List<LinkedNode>, decryptionKey: Long = 1){
        val listSize = nodes.size
        nodes.forEachIndexed { index, node ->
            val simpleDistance = (node.value * decryptionKey) % (listSize-1)
            if (simpleDistance == 0L) return@forEachIndexed
            val isBackwards = simpleDistance < 0
            var destinationNode = node
            repeat(simpleDistance.absoluteValue.toInt()) {
                destinationNode = if (isBackwards) destinationNode.previousNode!! else destinationNode.nextNode!!
            }
            node.nextNode?.previousNode = node.previousNode
            node.previousNode?.nextNode = node.nextNode
            if (isBackwards){
                destinationNode.previousNode?.nextNode = node
                node.previousNode = destinationNode.previousNode
                destinationNode.previousNode = node
                node.nextNode = destinationNode
            } else {
                destinationNode.nextNode?.previousNode = node
                node.nextNode = destinationNode.nextNode
                destinationNode.nextNode = node
                node.previousNode = destinationNode
            }
            if (index % 500 == 0) println("$index/${nodes.size}")
        }
    }

    private fun printAnswer(nodes: List<LinkedNode>, decryptionKey: Long = 1){
        val startNode = nodes.find { it.value == 0 }
        val answer = listOf(1000, 2000, 3000).map {
            var destinationNode = startNode
            repeat(it%nodes.size){
                destinationNode = destinationNode!!.nextNode
            }
            destinationNode!!.value
        }.fold(0L) { acc, i ->  acc + (i * decryptionKey)}
        println("answer: $answer")
    }

    private fun printList(startNode: LinkedNode){
//        println("---- List Start ----")
        var nextnode = startNode
        do {
            print("${nextnode.value},")
            nextnode = nextnode.nextNode!!
        } while (nextnode != startNode)
        println()
//        println("---- List End ----")
    }

    /**
     * 1 moves between 2 and -3:
     * 2, 1, -3, 3, -2, 0, 4
     *
     * 2 moves between -3 and 3:
     * 1, -3, 2, 3, -2, 0, 4
     *
     * -3 moves between -2 and 0:
     * 1, 2, 3, -2, -3, 0, 4
     *
     * 3 moves between 0 and 4:
     * 1, 2, -2, -3, 0, 3, 4
     *
     * -2 moves between 4 and 1:
     * 1, 2, -3, 0, 3, 4, -2
     *
     * 0 does not move:
     * 1, 2, -3, 0, 3, 4, -2
     *
     * 4 moves between -3 and 0:
     * 1, 2, -3, 4, 0, 3, -2
     */

    private fun generateLinkedList(): MutableList<LinkedNode> {
        var previousNode : LinkedNode? = null
        val nodeList = mutableListOf<LinkedNode>()
        File(INPUT).forEachLine {line ->
            val newNode = LinkedNode(line.toInt())
            previousNode?.let {
                it.nextNode = newNode
                newNode.previousNode = it
            }
            previousNode = newNode
            nodeList.add(newNode)
        }
        nodeList.first().previousNode = previousNode
        previousNode?.nextNode = nodeList.first()
        return nodeList
    }

    private fun parseInput() = File(INPUT).readText().split("\n").filter { it.isNotEmpty() }.map {
        it.toInt()
    }

    private data class LinkedNode(
        val value: Int,
        var previousNode: LinkedNode? = null,
        var nextNode: LinkedNode? = null
    ) {
        override fun toString() = value.toString()
    }

    companion object {
        const val INPUT = "$ROOT_DIR/day20/input.txt"
        const val DECRYPTION_KEY = 811589153L
    }
}