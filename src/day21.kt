import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

private const val DAY = 21

fun main() {
    if (!File("./day${DAY}_input").exists()) {
        downloadInput(DAY)
        println("Input downloaded")
        exitProcess(0)
    }
    val transformer = { x: String -> x.toList() }
    val input = loadInput(DAY, false, transformer)

    println(input)
    println(solvePart1(input))
    println(solvePart2(input))
}

fun findStart(grid: List<List<Char>>): Pair<Int, Int> {
    for ((r, row) in grid.withIndex()) {
        for ((c, char) in row.withIndex()) {
            if (char == 'S') {
                return Pair(r, c)
            }
        }
    }
    error("Start not found")
}

// Part 1
private fun solvePart1(input: List<List<Char>>): Int {
    val start = findStart(input)
    var currents = mutableListOf(start)
    val mem = mutableMapOf<Pair<Int, Int>, Set<Pair<Int, Int>>>()
    var res = 0
    for (i in (1..64)) {
        val nexts = mutableSetOf<Pair<Int, Int>>()
        while (currents.isNotEmpty()) {
            val current = currents.removeFirst()
            if (current !in mem) {
                mem[current] = getNextVisits(input, current)
            }
            nexts.addAll(mem[current]!!)
        }
        currents = nexts.toMutableList()
        res = nexts.count()
    }

    return res
}

fun getNextVisits(grid: List<List<Char>>, pos: Pair<Int, Int>): Set<Pair<Int, Int>> {
    val nexts = mutableSetOf<Pair<Int, Int>>()
    for (next in getNext(pos)) {
        val nextPos = Pair(next.first.first `%` grid.count(), next.first.second `%` grid[0].count())
        if (grid[nextPos.first][nextPos.second] == '#') {
            continue
        }
        nexts.add(nextPos)
    }
    return nexts
}

infix fun Int.`%`(divisor: Int): Int {
    return ((this % divisor) + divisor) % divisor
}

// Part 2
private fun solvePart2(input: List<List<Char>>): Int {
    val start = findStart(input)
    var currents = mutableListOf(start)
    val mem = mutableMapOf<Pair<Int, Int>, Set<Pair<Int, Int>>>()
    var res = 0
    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
    val currentDate = sdf.format(Date())
    println("$currentDate: start")
    for (i in (1..26501365)) {
        if (i % 10000 == 0) {
            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val currentDate = sdf.format(Date())
            println("$currentDate: $i")
        }
        val nexts = mutableSetOf<Pair<Int, Int>>()
        while (currents.isNotEmpty()) {
            val current = currents.removeFirst()
            if (current !in mem) {
                mem[current] = getNextVisits(input, current)
            }
            nexts.addAll(mem[current]!!)
        }
        currents = nexts.toMutableList()
        res = nexts.count()
    }

    return res
}