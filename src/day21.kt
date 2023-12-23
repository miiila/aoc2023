import java.io.File
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

fun findStart(grid: List<List<Char>>): Pos {
    for ((r, row) in grid.withIndex()) {
        for ((c, char) in row.withIndex()) {
            if (char == 'S') {
                return Pos(r, c)
            }
        }
    }
    error("Start not found")
}

// Part 1
private fun solvePart1(input: List<List<Char>>): Int {
    val start = findStart(input)
    var currents = mutableListOf(start)
    val mem = mutableMapOf<Pos, Set<Pos>>()
    var res = 0
    for (i in (1..64)) {
        val nexts = mutableSetOf<Pos>()
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

fun getNextVisits(grid: List<List<Char>>, pos: Pos): Set<Pos> {
    val nexts = mutableSetOf<Pos>()
    for (next in getNext(pos)) {
        val nextPos = Pos(next.row `%` grid.count(), next.col `%` grid[0].count())
        if (grid[nextPos.row][nextPos.col] == '#') {
            continue
        }
        nexts.add(nextPos)
    }
    return nexts
}

fun getNextVisitsInf(grid: List<List<Char>>, pos: Pos, visited: Set<Pos>?): Set<Pos> {
    val nexts = mutableSetOf<Pos>()
    for (next in getNext(pos)) {
        if (grid[next.row `%` grid.count()][next.col `%` grid[0].count()] == '#') {
            continue
        }
        if (visited != null && next in visited) {
            continue
        }
        nexts.add(next)
    }
    return nexts
}

infix fun Int.`%`(divisor: Int): Int {
    return ((this % divisor) + divisor) % divisor
}

// Part 2
private fun solvePart2Slow(input: List<List<Char>>): Int {
    val start = findStart(input)
    var currents = mutableListOf(start)
    var counts = mutableMapOf<Int, Int>()
    val visited = mutableSetOf<Pos>()
    for (i in (1..1000)) {
        val nexts = mutableSetOf<Pos>()
        while (currents.isNotEmpty()) {
            val current = currents.removeFirst()
            visited.add(current)
            for (next in getNextVisitsInf(input, current, null)) {
                if (next !in visited) {
                    nexts.add(next)
                }
            }
        }
        currents = nexts.toMutableList()
        counts[i] = nexts.count()
    }

    val isEven = counts.keys.last() % 2 == 0
    var res = if (isEven) 1 else 0
    res += if (isEven) {
        counts.filter { (k, v) -> k % 2 == 0 }.values.sum()
    } else {
        counts.filter { (k, v) -> k % 2 == 1 }.values.sum()
    }

    return res
}

private fun solvePart2WIP(input: List<List<Char>>): Int {
    val start = findStart(input)
    var currents = setOf(start)
    val visited = mutableSetOf<Pos>()
    val mem = mutableMapOf<Set<Pos>, Int>()
    val mem2 = mutableMapOf<Set<Pos>, Set<Pos>>()
    var res = 0
    for (i in (1..1000)) {
        val nexts = mutableSetOf<Pos>()
        println("Is seen? ${currents in mem}")
//        if (currents !in mem) {
        val iters = currents.toMutableList()
        while (iters.isNotEmpty()) {
            val current = iters.removeFirst()
            visited.add(current)
            for (next in getNextVisits(input, current)) {
//                val nextMod = Pair(next.first `%` input.count(), next.second `%` input[0].count())
                nexts.add(next)
            }
        }
        mem[currents.toSet()] = nexts.count()
//            mem2[currents.toSet()] = nexts
        res = nexts.count()
//        } else {
        res = nexts.count()
//        }
        currents = nexts
    }

    return res
}

private fun solvePart2(input: List<List<Char>>): Int {
    val start = findStart(input)
    val mem: MutableMap<Set<Pos>, Set<Pos>> = mutableMapOf()
    val visited = mutableSetOf<Pos>()
    var q = mutableListOf<Pos>(start)
    for (i in 0..26501365) {
        if (i % 1000 == 0) {
            println(i)
        }
        val newQ: MutableSet<Pos> = mutableSetOf()
        while (q.isNotEmpty()) {
            val current = q.removeFirst()
            visited.add(current)
            newQ.addAll(getNextVisitsInf(input, current, visited))
        }
        q = newQ.toMutableList()
    }

    return 0
}


fun getNextState(grid: List<List<Char>>, reach: Set<Pos>): Set<Pos> {
    val nexts = mutableSetOf<Pos>()
    for (current in reach) {
        for (next in getNextVisits(grid, current)) {
            nexts.add(next)
        }
    }

    return nexts
}

