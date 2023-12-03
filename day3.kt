import java.io.File
import kotlin.system.exitProcess

private const val DAY = 3

fun main() {
    if (!File("./day${DAY}_input").exists()) {
        downloadInput(DAY)
        println("Input downloaded")
        exitProcess(0)
    }
    val transformer = { x: String -> x }
    val input = loadInput(DAY, false, transformer)

    println(solvePart1(input))
    println(solvePart2(input))
}

// Part 1
private fun solvePart1(input: List<String>): Int {
    var res = 0
    for ((row, line) in input.withIndex()) {
        var col = 0
        while (col < line.length) {
            val c = line[col]
            if (c.isDigit()) {
                val num = line.slice(col..<line.length).takeWhile { it.isDigit() }
                if (isSymbolAround(row, col, num.length, input)) {
                    res += num.toInt()
                }
                col += num.length
            } else {
                col++
            }
        }
    }

    return res
}

fun isSymbolAround(row: Int, col: Int, colOffset: Int, grid: List<String>, symbol: Char? = null): Boolean {
    for (dr in -1..1) {
        if (row + dr < 0 || row + dr >= grid.count()) {
            continue
        }
        for (dc in -1..colOffset) {
            if (col + dc < 0 || col + dc >= grid[row + dr].length) {
                continue
            }
            val s = grid[row + dr][col + dc]
            if ((symbol != null && s == symbol) || s != '.' && !s.isDigit()) {
                return true
            }
        }
    }

    return false
}


// Part 2
private fun solvePart2(input: List<String>): Int {
    val gears = mutableMapOf<Pair<Int, Int>, List<Int>>()
    for ((row, line) in input.withIndex()) {
        var col = 0
        while (col < line.length) {
            val c = line[col]
            if (c.isDigit()) {
                val num = line.slice(col..<line.length).takeWhile { it.isDigit() }
                val gear = getGearAround(row, col, num.length, input)
                if (gear != null) {
                    gears[gear] = gears[gear]?.plus(num.toInt()) ?: listOf(num.toInt())
                }
                col += num.length
            } else {
                col++
            }
        }
    }

    return gears.filter { it.toPair().second.count() > 1 }.map { it.value.reduce(Int::times) }.sum()
}

fun getGearAround(row: Int, col: Int, colOffset: Int, grid: List<String>): Pair<Int, Int>? {
    for (dr in -1..1) {
        if (row + dr < 0 || row + dr >= grid.count()) {
            continue
        }
        for (dc in -1..colOffset) {
            if (col + dc < 0 || col + dc >= grid[row + dr].length) {
                continue
            }
            val s = grid[row + dr][col + dc]
            if (s == '*') {
                return Pair(row + dr, col + dc)
            }
        }
    }

    return null
}
