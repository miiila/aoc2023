import java.io.File
import java.util.*
import kotlin.system.exitProcess

private const val DAY = 17

fun main() {
    if (!File("./day${DAY}_input").exists()) {
        downloadInput(DAY)
        println("Input downloaded")
        exitProcess(0)
    }
    val transformer = { x: String -> x.toList().map { it.digitToInt() } }
    val input = loadInput(DAY, true, transformer)

    println(input)
    solvePart1(input)
    solvePart2()
}

// Part 1
private fun solvePart1(input: List<List<Int>>): Int {
    val unvisited = PriorityQueue<Node>()
    for (r in (0..<input.count())) {
        for (c in (0..<input.count())) {
            unvisited.add(Node(Pair(r, c), if (r == 12 && c == 12) 0 else Int.MAX_VALUE, "x"))
        }
    }
    val visited = mutableSetOf<Node>()
    dijkstraSearch(input, unvisited, visited)
    return 0
}

data class Node(val pos: Pair<Int, Int>, var heat: Int, var path: String) : Comparable<Node> {
    override fun compareTo(other: Node): Int {
        return this.heat.compareTo(other.heat)
    }
}

fun dijkstraSearch(grid: List<List<Int>>, unvisited: PriorityQueue<Node>, visited: MutableSet<Node>) {
    while (unvisited.isNotEmpty()) {
        val current = unvisited.remove()
        for (neighbor in getNext(current.pos)) {
            // check grid size
            if (neighbor.first.first < 0 || neighbor.first.first == grid.count() || neighbor.first.second < 0 || neighbor.first.second == grid[0].count()) {
                continue
            }
            // check path
            if (current.path.count() >= 3 && current.path.reversed().slice(0..<3).all { it == neighbor.second }) {
                // three consecutive moves, skip
                continue
            }
            if (isOpposite(current.path.last(), neighbor.second)) {
                // going backwards
                continue
            }
            val nextNode = unvisited.find { it.pos == neighbor.first } ?: continue
            unvisited.remove(nextNode)
            if (nextNode.heat > current.heat + grid[neighbor.first.first][neighbor.first.second]) {
                nextNode.heat = current.heat + grid[neighbor.first.first][neighbor.first.second]
                nextNode.path = current.path + neighbor.second
            }
            // trigger queue sort
            unvisited.add(nextNode)
        }
        visited.add(current)
    }
    return
}

fun isOpposite(c1: Char, c2: Char): Boolean {
    return when (c1) {
        'v' -> c2 == '^'
        '^' -> c2 == 'v'
        '>' -> c2 == '<'
        '<' -> c2 == '>'
        'x' -> false
        else -> error("")
    }
}

fun getNext(pos: Pair<Int, Int>): List<Pair<Pair<Int, Int>, Char>> {
    return listOf(
        Pair(Pair(pos.first, pos.second + 1), '>'),
        Pair(Pair(pos.first + 1, pos.second), 'v'),
        Pair(Pair(pos.first - 1, pos.second), '^'),
        Pair(Pair(pos.first, pos.second - 1), '<'),
    )
}

// Part 2
private fun solvePart2() {
    TODO()
}