import java.io.File
import kotlin.system.exitProcess

private const val DAY = 0

fun main() {
    if (!File("./day${DAY}_input").exists()) {
        downloadInput(DAY)
        println("Input downloaded")
        exitProcess(0)
    }
    val transformer = { x: String -> x }
    val input = loadInput(DAY, false, transformer)

    println(input)
    solvePart1()
    solvePart2()
}

// Part 1
private fun solvePart1() {
    TODO()
}

// Part 2
private fun solvePart2() {
    TODO()
}