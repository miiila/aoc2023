import java.io.File
import kotlin.math.max
import kotlin.system.exitProcess

private const val DAY = 10

fun main() {
    if (!File("./day${DAY}_input").exists()) {
        downloadInput(DAY)
        println("Input downloaded")
        exitProcess(0)
    }
    val transformer = { x: String -> x.toMutableList() }
    val input = loadInput(DAY, false, transformer)

    println(solvePart1(input))
    println(solvePart2(input))
}

// Part 1
private fun solvePart1(maze: List<List<Char>>): Int {
    // S = F
    val start = Pair(8, 42)
//    val start = Pair(4, 0)
//    val start = Pair(12, 4)
    var p1 = start
    var p2 = start
    var p1From = "S"
    var p2From = "E"
    var p1Steps = 0
    var p2Steps = 0
    do {
        val p1Next = getNext(p1From, maze[p1.second][p1.first])
        p1From = p1Next.second
        p1 = Pair(p1.first + p1Next.first.first, p1.second + p1Next.first.second)
        p1Steps++
        val p2Next = getNext(p2From, maze[p2.second][p2.first])
        p2From = p2Next.second
        p2 = Pair(p2.first + p2Next.first.first, p2.second + p2Next.first.second)
        p2Steps++
    } while (p1 != p2)

    return max(p1Steps, p2Steps)
}

// Part 2
private fun solvePart2(maze: List<List<Char>>): Int {
    val start = Pair(8, 42)
//    val start = Pair(12, 4)
    val instructions = mutableListOf<Pair<Pair<Int, Int>, String>>()
    val maze = maze.map { it.toMutableList() }.toMutableList()
    var p1 = start
    var p1From = "S"
    var p1Steps = 0
    do {
        val p1Next = getNext(p1From, maze[p1.second][p1.first])
        instructions.add(p1Next)
        maze[p1.second][p1.first] = '*'
        p1From = p1Next.second
        p1 = Pair(p1.first + p1Next.first.first, p1.second + p1Next.first.second)
        p1Steps++
    } while (p1 != start)

    p1 = start
    var res = 0
    var from = 'S'
    while (instructions.isNotEmpty()) {
        val ins = instructions.removeFirst()
        var rightHandDir = getInsideDirection(from)
        var cPos = Pair(p1.first + rightHandDir.first, p1.second + rightHandDir.second)
        while (maze.getOrNull(cPos.second)?.getOrNull(cPos.first) !in listOf('*', null)) {
            maze[cPos.second][cPos.first] = '$'
            res++
            cPos = Pair(cPos.first + rightHandDir.first, cPos.second + rightHandDir.second)
        }
        from = ins.second.first()
        rightHandDir = getInsideDirection(from)
        cPos = Pair(p1.first + rightHandDir.first, p1.second + rightHandDir.second)
        while (maze.getOrNull(cPos.second)?.getOrNull(cPos.first) !in listOf('*', null)) {
            maze[cPos.second][cPos.first] = '$'
            res++
            cPos = Pair(cPos.first + rightHandDir.first, cPos.second + rightHandDir.second)
        }
        p1 = Pair(p1.first + ins.first.first, p1.second + ins.first.second)
    }

    return maze.sumOf { it.count { it == '$' } }
}

fun getInsideDirection(from: Char): Pair<Int, Int> {
    return when (from) {
        'N' -> Pair(1, 0)
        'S' -> Pair(-1, 0)
        'E' -> Pair(0, 1)
        'W' -> Pair(0, -1)
        else -> throw Exception("BOOM")
    }
}


fun getNext(from: String, pos: Char): Pair<Pair<Int, Int>, String> {
    var dx = 0
    var dy = 0
    var nextFrom = from
    val pos = if (pos == 'S') 'F' else pos
//    val pos = if (pos == 'S') '7' else pos
    when (from) {
        "N" -> when (pos) {
            '|' -> dy = 1
            'J' -> {
                dx = -1
                nextFrom = "E"
            }

            'L' -> {
                dx = 1
                nextFrom = "W"
            }
        }

        "S" -> when (pos) {
            '|' -> dy = -1
            '7' -> {
                dx = -1
                nextFrom = "E"
            }

            'F' -> {
                dx = 1
                nextFrom = "W"
            }
        }

        "W" -> when (pos) {
            '-' -> dx = 1
            '7' -> {
                dy = 1
                nextFrom = "N"
            }

            'J' -> {
                dy = -1
                nextFrom = "S"
            }
        }

        "E" -> when (pos) {
            '-' -> dx = -1
            'F' -> {
                dy = 1
                nextFrom = "N"
            }

            'L' -> {
                dy = -1
                nextFrom = "S"
            }
        }
    }

    return Pair(Pair(dx, dy), nextFrom)
}
