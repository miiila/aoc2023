import java.io.File
import kotlin.system.exitProcess

private const val DAY = 16

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

data class Beam(val x: Int, val y: Int, val heading: Char)

// Part 1
private fun solvePart1(grid: List<List<Char>>): Int {
    return solve(grid, Beam(0, 0, 'E'))
}

// Part 2
private fun solvePart2(grid: List<List<Char>>): Int {
    val beams = mutableSetOf<Beam>()
    for (x in (0..<grid[0].count())) {
        beams.add(Beam(x, 0, 'S'))
        beams.add(Beam(x, grid.count() - 1, 'N'))
    }
    for (y in (0..<grid.count())) {
        beams.add(Beam(0, y, 'E'))
        beams.add(Beam(grid[0].count() - 1, y, 'W'))
    }


    return beams.maxOf { solve(grid, it) }
}

fun solve(grid: List<List<Char>>, start: Beam): Int {
    val visited = mutableSetOf<Beam>()
    val queue = mutableListOf(start)
    while (queue.isNotEmpty()) {
        val beam = queue.removeFirst()
        visited.add(beam)
        val next = getNextDirection(beam, grid[beam.y][beam.x])
        for (n in next) {
            if (n.x >= 0 && n.x < grid[0].count() && n.y >= 0 && n.y < grid.count() && n !in visited) {
                queue.add(n)
            }
        }
    }

    return visited.map { Pair(it.x, it.y) }.toSet().count()
}

fun getNextDirection(beam: Beam, pos: Char): List<Beam> {
    val newHeading = when (pos) {
        '|' ->
            when (beam.heading) {
                in (listOf('E', 'W')) -> "NS"
                else -> beam.heading.toString()
            }

        '-' ->
            when (beam.heading) {
                in (listOf('N', 'S')) -> "EW"
                else -> beam.heading.toString()
            }

        '/' ->
            when (beam.heading) {
                'N' -> "E"
                'S' -> "W"
                'E' -> "N"
                'W' -> "S"
                else -> error("BOOM")
            }

        '\\' ->
            when (beam.heading) {
                'N' -> "W"
                'S' -> "E"
                'E' -> "S"
                'W' -> "N"
                else -> error("BOOM")
            }

        else -> beam.heading.toString()
    }
    return newHeading.map {
        when (it) {
            'N' -> Beam(beam.x, beam.y - 1, it)
            'S' -> Beam(beam.x, beam.y + 1, it)
            'E' -> Beam(beam.x + 1, beam.y, it)
            'W' -> Beam(beam.x - 1, beam.y, it)
            else -> error("BOOM")
        }
    }
}