import java.io.File
import kotlin.math.abs
import kotlin.system.exitProcess

private const val DAY = 11

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
private fun solvePart1(input: List<List<Char>>): Long {
    val galaxies = input.mapIndexed { y, row ->
        row.mapIndexed { x, cell -> if (cell == '#') Pair(x, y) else null }
    }.flatten().filterNotNull()
    val emptyCols = (0..input.count()).toSet() - galaxies.map { it.first }.toSet()
    val emptyRows = (0..input[0].count()).toSet() - galaxies.map { it.second }.toSet()

    return solve(galaxies, emptyCols, emptyRows, 1)
}


fun getCombinations(input: List<Pair<Long, Long>>): List<Pair<Pair<Long, Long>, Pair<Long, Long>>> {
    val res = mutableListOf<Pair<Pair<Long, Long>, Pair<Long, Long>>>()
    for (i in (0..<input.count())) {
        for (c in (i + 1..<input.count())) {
            res.add(Pair(input[i], input[c]))
        }
    }

    return res
}

fun solve(input: List<Pair<Int, Int>>, emptyCols: Set<Int>, emptyRows: Set<Int>, expansion: Int): Long {
    val galaxies = input.map {
        val newC = it.first.toLong() + (emptyCols.count { it2 -> it.first > it2 } * expansion)
        val newR = it.second.toLong() + (emptyRows.count { it2 -> it.second > it2 } * expansion)
        Pair(newC, newR)
    }
    val galaxiesCombinations = getCombinations(galaxies)
    return galaxiesCombinations.sumOf { abs(it.first.first - it.second.first) + abs(it.first.second - it.second.second) }
}

// Part 2
private fun solvePart2(input: List<List<Char>>): Long {
    val galaxies = input.mapIndexed { y, row ->
        row.mapIndexed { x, cell -> if (cell == '#') Pair(x, y) else null }
    }.flatten().filterNotNull()
    val emptyCols = (0..input.count()).toSet() - galaxies.map { it.first }.toSet()
    val emptyRows = (0..input[0].count()).toSet() - galaxies.map { it.second }.toSet()

    return solve(galaxies, emptyCols, emptyRows, 999999)
}