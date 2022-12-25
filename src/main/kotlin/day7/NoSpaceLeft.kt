package day7

import PuzzleSolution
import ROOT_DIR
import java.io.File
import java.nio.file.Files
import kotlin.properties.Delegates

class NoSpaceLeft: PuzzleSolution {
    override fun solveFirst() {
        val parsedDirectory = generateDirectoryTree()
        val smolDirectories = mutableSetOf<Directory>()
        dfs(parsedDirectory, smolDirectories) {
            it.size < 100000
        }
        val smolDirectorySize = smolDirectories.sumOf { it.size }
        println(smolDirectorySize)
    }

    private fun generateDirectoryTree(): Directory{
        val root = Directory("/","/")
        var currentDirectory = root
        File(INPUT).forEachLine {
            if(it.startsWith(CD_COMMAND)) { // cd command
                when (val newDir = it.split(CD_COMMAND)[1]) {
                    ROOT -> currentDirectory = root
                    PARENT -> currentDirectory = currentDirectory.parent ?: root
                    !in currentDirectory.directories -> {
                        val nextDir = Directory(
                            name = newDir,
                            absolutePath = "${currentDirectory.absolutePath}/$newDir/",
                            parent = currentDirectory
                        )
                        currentDirectory.directories[newDir] = nextDir
                        currentDirectory = nextDir
                    }
                    else -> currentDirectory = currentDirectory.directories[newDir]!!
                }
            } else if (it != LS_COMMAND) {
                if (it.startsWith(DIR)){
                    val newDirName = it.split(DIR)[1]
                    currentDirectory.directories[newDirName] = Directory(
                        name = newDirName,
                        absolutePath = "${currentDirectory.absolutePath}$newDirName/",
                        parent = currentDirectory
                    )
                } else {
                    val (size, fileName) = it.split(" ")
                    currentDirectory.files[fileName] = size.toInt()
                }
            }
        }
        return root
    }

    private fun initializeSizes(root: Directory) {
        root.directories.forEach { (s, directory) -> initializeSizes(directory) }
        root.initializeSize()
    }

    private fun dfs(root: Directory, result: MutableSet<Directory>, condition: (Directory) -> Boolean) {
        root.directories.forEach { (_, directory) ->
            dfs(directory, result, condition)
            if (condition(directory)) result.add(directory)
        }
    }

    override fun solveSecond() {
        val parsedDirectory = generateDirectoryTree()
        val target = SIZE_REQUIRED - (TOTAL_SIZE - parsedDirectory.size)
        val results = mutableSetOf<Directory>()
        dfs(parsedDirectory, results){
            it.size > target
        }
        results.sortedBy { it.size }
        println("first: ${results.first().name}, ${results.first().size}")
        println("last: ${results.last().name}, ${results.last().size}")
    }

    internal data class Directory(
        val name: String,
        val absolutePath: String,
        val parent: Directory? = null,
        val files: MutableMap<String, Int> = mutableMapOf(), // Name, size
        val directories: MutableMap<String, Directory> = mutableMapOf()
    ) {
        val size: Int by lazy { initializeSize() }

        fun initializeSize() = files.values.sum() + directories.values.sumOf { it.size }

        override fun hashCode(): Int  = absolutePath.hashCode()
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Directory

            if (absolutePath != other.absolutePath) return false

            return true
        }

    }

    companion object {
        private const val COMMAND_PREFIX = "$"
        const val CD_COMMAND = "$COMMAND_PREFIX cd "
        const val LS_COMMAND = "$COMMAND_PREFIX ls"
        const val ROOT = "/"
        const val PARENT = ".."
        const val DIR = "dir "
        const val INPUT = "$ROOT_DIR/day7/input.txt"
        const val TOTAL_SIZE = 70000000
        const val SIZE_REQUIRED = 30000000
    }
}