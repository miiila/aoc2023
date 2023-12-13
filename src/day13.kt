import java.io.File
import kotlin.system.exitProcess

private const val DAY = 13

val results = mutableMapOf<List<String>, MutableList<Int>>()

fun main() {
    if (!File("./day${DAY}_input").exists()) {
        downloadInput(DAY)
        println("Input downloaded")
        exitProcess(0)
    }
    val transformer = { x: String -> x }
    val input = parseMirrors(loadInput(DAY, false, transformer))

//    println(input)
    println(solvePart1(input))
    println(solvePart2(input))
}

// Part 1
private fun solvePart1(input: List<List<String>>): Int {
    val res = input.sumOf {
        results[it] = mutableListOf(-1, -1)
        val v = hasVerticalLine(it)
        results[it]!![0] = v
        val h = hasHorizontalLine(it)
        results[it]!![1] = h
        if (v >= 0) v + 1 else (h + 1) * 100
    }

    return res
}

// Part 2
private fun solvePart2(input: List<List<String>>): Int {
    val res = input.sumOf {
        val v = hasVerticalLine(it, true)
        val h = hasHorizontalLine(it, true)
        if (v >= 0) v + 1 else (h + 1) * 100
    }

    return res
}

fun hasVerticalLine(grid: List<String>, withFix: Boolean = false): Int {
    val cols = grid[0].count()
    val mid = if (cols % 2 == 1) cols / 2 else cols / 2 - 1
    for (x in (mid downTo 0).zip(mid..<cols).map { it.toList() }.flatten()) {
        val r = verifyVerticalLine(x, grid, withFix)
        if (r >= 0) {
            if (results[grid]!![0] == r) {
                continue
            }
            return r
        }
    }

    return -1
}

fun hasHorizontalLine(grid: List<String>, withFix: Boolean = false): Int {
    val rows = grid.count()
    val mid = if (rows % 2 == 1) rows / 2 else rows / 2 - 1
    for (x in (mid downTo 0).zip(mid..<rows).map { it.toList() }.flatten()) {
        val r = verifyHorizontalLine(x, grid, withFix)
        if (r >= 0) {
            if (results[grid]!![1] == r) {
                continue
            }
            return r
        }
    }

    return -1
}

fun verifyHorizontalLine(line: Int, grid: List<String>, withFix: Boolean = false): Int {
    var fixed = false
    var x = line
    var y = line + 1
    if (y >= grid.count()) {
        return -1
    }
    while (x >= 0 && y < grid.count()) {
        if (grid[x] != grid[y]) {
            if (withFix && !fixed) {
                if (grid[x].zip(grid[y]).count { it.first != it.second } == 1) {
                    fixed = true
                    x--
                    y++
                    continue
                }
            }
            return -1
        }
        x--
        y++
    }
    return line
}

fun verifyVerticalLine(line: Int, grid: List<String>, withFix: Boolean = false): Int {
    var fixed = false
    var x = line
    var y = line + 1
    if (y >= grid[0].count()) {
        return -1
    }
    while (x >= 0 && y < grid[0].count()) {
        if (getColumn(x, grid) != getColumn(y, grid)) {
            if (withFix && !fixed) {
                if (getColumn(x, grid).zip(getColumn(y, grid)).count { it.first != it.second } == 1) {
                    fixed = true
                    x--
                    y++
                    continue
                }
            }
            return -1
        }
        x--
        y++
    }
    return line
}

fun getColumn(i: Int, grid: List<String>): String {
    return grid.joinToString("") { it[i].toString() }
}

fun parseMirrors(input: List<String>): List<List<String>> {
    val res = mutableListOf<List<String>>()
    var i = mutableListOf<String>()
    for (l in input) {
        if (l == "") {
            res.add(i)
            i = mutableListOf<String>()
            continue
        }
        i.add(l)
    }
    res.add(i)

    return res
}
