import java.io.File
import java.math.BigInteger
import kotlin.system.exitProcess

private const val DAY = 6

fun main() {
    if (!File("./day${DAY}_input").exists()) {
        downloadInput(DAY)
        println("Input downloaded")
        exitProcess(0)
    }
//    val transformer = { x: String -> x }
//    val input = loadInput(DAY, false, transformer)
    val input = listOf(Pair(40, 215), Pair(92, 1064), Pair(97, 1505), Pair(90, 1100))
    println(input)
    println(solvePart1(input))
//    println(solvePart2(Pair(71530.toBigInteger(), 940200.toBigInteger())))
    println(solvePart2(Pair(40929790.toBigInteger(), 215106415051100.toBigInteger())))
}

// Part 1
private fun solvePart1(input: List<Pair<Int, Int>>): Int {
    return input.map { getWinnings(it).count() }.reduce(Int::times)
}

fun getWinnings(input: Pair<Int, Int>): List<Int> {
    return (1..input.first).map { (input.first - it) * it }.filter { it > input.second }
}

// Part 2
private fun solvePart2(input: Pair<BigInteger, BigInteger>): BigInteger {
    var winDist = 0.toBigInteger()
    var seconds = 0.toBigInteger()
    while (winDist < input.second) {
        winDist = seconds * (input.first - seconds)
        seconds++
    }
    seconds--
    val winStart = seconds
    winDist = 0.toBigInteger()
    seconds = input.first
    while (winDist < input.second) {
        winDist = seconds * (input.first - seconds)
        seconds--
    }
    seconds++
    return seconds - winStart + 1.toBigInteger()
}