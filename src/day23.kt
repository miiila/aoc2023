import java.io.File
import kotlin.math.max
import kotlin.system.exitProcess

private const val DAY = 23

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
    solvePart2()
}

// Part 1
private fun solvePart1(input: List<List<Char>>): Int {
    val start = Pos(0, input[0].indexOf('.'))
    val end = Pos(input.count() - 1, input[input.count() - 1].indexOf('.'))
    walkTheMaze(input, start, end, setOf())
    return res
}

var res = 0


fun walkTheMaze(grid: List<List<Char>>, start: Pos, end: Pos, visited: Set<Pos>) {
    if (start == end) {
        res = max(res, visited.count())
        println(res)
//        exitProcess(12)
        return
    }
    for ((next, dir) in getNextWithDir(start)) {
        if (next.row < 0 || next.row == grid.count() || next.col < 0 || next.col == grid[0].count()) {
            continue
        }
        if (grid[next.row][next.col] != '.' && grid[next.row][next.col] != dir) {
            continue
        }
        if (grid[next.row][next.col] == '#') {
            continue
        }
        if (next in visited) {
            continue
        }
        walkTheMaze(grid, next, end, visited + next)
    }
}

// Part 2
private fun solvePart2() {
    TODO()
}