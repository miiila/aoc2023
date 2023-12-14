import java.io.File
import kotlin.system.exitProcess

private const val DAY = 14

fun main() {
    if (!File("./day${DAY}_input").exists()) {
        downloadInput(DAY)
        println("Input downloaded")
        exitProcess(0)
    }
    val transformer = { x: String -> x.toList() }
    val input = loadInput(DAY, false, transformer)

//    println(input)
    println(solvePart1(input))
    println(solvePart2(input))
}

// Part 1
private fun solvePart1(input: List<List<Char>>): Int {
    val new = rollNorth(input)
    return countNorthLoad(new)
}

// Part 2
private fun solvePart2(input: List<List<Char>>): Int {
    var res: List<List<Char>> = input
    val cycles = mutableMapOf<List<List<Char>>, Int>()
    var i = 1
    do {
        cycles[res] = i++
        res = res.run(::rollNorth).run(::rollWest).run(::rollSouth).run(::rollEast)
    } while (res !in cycles)
    val cycleStart = cycles[res]!!
    // Moreless trial and error - I knew what I wanted, but was hard to write it correctly
    val cycleLen = cycles.count() - cycleStart + 1
    val mod = (1000000000 - cycleStart + 1) % (cycleLen)
    return countNorthLoad(cycles.filterValues { it == cycleStart + mod }.keys.first())
}

fun countNorthLoad(input: List<List<Char>>): Int {
    var res = 0
    for ((y, row) in input.withIndex()) {
        for (c in row) {
            if (c == 'O') {
                res += input.count() - y
            }
        }
    }
    return res
}

fun rollNorth(input: List<List<Char>>): List<List<Char>> {
    val input = input.map { it.toMutableList() }
    for (x in 0..<input[0].count()) {
        var empty: Pair<Int, Int>? = null
        for (y in 0..<input.count()) {
            val cell = input[y][x]
            when (cell) {
                '#' -> empty = null
                '.' -> if (empty == null) empty = Pair(x, y)
                'O' -> {
                    if (empty != null) {
                        input[empty.second][empty.first] = 'O'
                        empty = Pair(empty.first, empty.second + 1)
                        input[y][x] = '.'
                    }
                }
            }
        }
    }
    return input
}

fun rollSouth(input: List<List<Char>>): List<List<Char>> {
    val input = input.map { it.toMutableList() }
    for (x in 0..<input.count()) {
        var empty: Pair<Int, Int>? = null
        for (y in input.count() - 1 downTo 0) {
            val cell = input[y][x]
            when (cell) {
                '#' -> empty = null
                '.' -> if (empty == null) empty = Pair(x, y)
                'O' -> {
                    if (empty != null) {
                        input[empty.second][empty.first] = 'O'
                        empty = Pair(empty.first, empty.second - 1)
                        input[y][x] = '.'
                    }
                }
            }
        }
    }
    return input
}

fun rollEast(input: List<List<Char>>): List<List<Char>> {
    val input = input.map { it.toMutableList() }
    for (y in 0..<input.count()) {
        var empty: Pair<Int, Int>? = null
        for (x in input[0].count() - 1 downTo 0) {
            val cell = input[y][x]
            when (cell) {
                '#' -> empty = null
                '.' -> if (empty == null) empty = Pair(x, y)
                'O' -> {
                    if (empty != null) {
                        input[empty.second][empty.first] = 'O'
                        empty = Pair(empty.first - 1, empty.second)
                        input[y][x] = '.'
                    }
                }
            }
        }
    }
    return input
}

fun rollWest(input: List<List<Char>>): List<List<Char>> {
    val input = input.map { it.toMutableList() }
    for (y in 0..<input.count()) {
        var empty: Pair<Int, Int>? = null
        for (x in 0..<input[0].count()) {
            val cell = input[y][x]
            when (cell) {
                '#' -> empty = null
                '.' -> if (empty == null) empty = Pair(x, y)
                'O' -> {
                    if (empty != null) {
                        input[empty.second][empty.first] = 'O'
                        empty = Pair(empty.first + 1, empty.second)
                        input[y][x] = '.'
                    }
                }
            }
        }
    }
    return input
}
