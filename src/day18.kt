import java.io.File
import java.util.Collections.max
import java.util.Collections.min
import kotlin.math.abs
import kotlin.system.exitProcess

private const val DAY = 18

fun main() {
    if (!File("./day${DAY}_input").exists()) {
        downloadInput(DAY)
        println("Input downloaded")
        exitProcess(0)
    }
    val transformer = { x: String ->
        val x = x.replace("(", "").replace(")", "").split(" ")
        Triple(x[0][0], x[1].toInt(), x[2])
    }
    val input = loadInput(DAY, false, transformer)

//    println(input)
    println(solvePart1(input))
    println(solvePart2(input))
}

// Part 1
private fun solvePart1(input: List<Triple<Char, Int, String>>): Int {
    val res = getEdges(input)
//    printRes(res)
    val rmin = min(res.map { it.first })
    val rmax = max(res.map { it.first })
    val cmin = min(res.map { it.second })
    val cmax = max(res.map { it.second })

    val q = mutableListOf<Pair<Int, Int>>()
    // Find first inside cell
    for (r in (rmin..rmax)) {
        var found = false
        for (c in (cmin..cmax)) {
            if (Pair(r, c) in res && Pair(r, c + 1) in res) {
                break
            }
            if (Pair(r, c) in res) {
                found = true
                q.add(Pair(r, c + 1))
                break
            }
        }
        if (found) break
    }
    // Fill everything
    val filled = res.toMutableSet()
    while (q.isNotEmpty()) {
        val i = q.removeFirst()
        for (n in getNeighbours(i)) {
            if (n !in filled) {
                filled.add(n)
                q.add(n)
            }
        }
    }
//    printRes(filled.toList())
    return filled.count()

}

// Part 2
private fun solvePart2(input: List<Triple<Char, Int, String>>): Long {
    val inputHexa = input.map { parseHexa(it.third) }
    val vertices = getVertices(inputHexa).map { Pair(it.first.toLong(), it.second.toLong()) }
    return getPolygonArea(vertices)
}

fun parseHexa(hexa: String): Triple<Char, Int, String> {
    val dir = when (hexa[6]) {
        '0' -> 'R'
        '1' -> 'D'
        '2' -> 'L'
        '3' -> 'U'
        else -> error("BOOM")
    }
    return Triple(dir, hexa.slice(1..5).toInt(16), "")
}

fun getVertices(input: List<Triple<Char, Int, String>>): List<Pair<Int, Int>> {
    val res = mutableListOf<Pair<Int, Int>>()
    var cur = Pair(0, 1)
    for ((i, inp) in input.withIndex()) {
        if (i == input.count() - 1) {
            break
        }
        val (store, next) = getNextDig(cur, inp.first, inp.second, input[i + 1].first)
        res.add(store)
        cur = next
    }
    res.add(Pair(0, 0))
    return res
}

fun getEdges(input: List<Triple<Char, Int, String>>): List<Pair<Int, Int>> {
    val res = mutableListOf<Pair<Int, Int>>()
    var cur = Pair(0, 0)
    for (inp in input) {
        for (i in (0..<inp.second)) {
            cur = getNextDig(cur, inp.first, 1)
            res.add(cur)
        }
    }

    return res
}

fun getNeighbours(pos: Pair<Int, Int>): List<Pair<Int, Int>> {
    val res = mutableListOf<Pair<Int, Int>>()
    for (i in listOf(-1, 1)) {
        res.add(Pair(pos.first + i, pos.second))
        res.add(Pair(pos.first, pos.second + i))
    }

    return res
}


fun getPolygonArea(vertices: List<Pair<Long, Long>>): Long {
    val pairs = vertices.windowed(2).toMutableList()
    val r = 0.5 * abs(pairs.sumOf { (v1, v2) -> v1.first * v2.second - v2.first * v1.second })
    return r.toLong()
}

fun getNextDig(pos: Pair<Int, Int>, dir: Char, l: Int, nextDir: Char): Pair<Pair<Int, Int>, Pair<Int, Int>> {
    var storePos = when (dir) {
        'U' -> Pair(pos.first - l, pos.second)
        'D' -> Pair(pos.first + l, pos.second)
        'R' -> Pair(pos.first, pos.second + l)
        'L' -> Pair(pos.first, pos.second - l)
        else -> error("BOOOM")
    }
    val nextPos: Pair<Int, Int>
    if (isRightTurn(dir, nextDir)) {
        nextPos = when (dir) {
            'R' -> Pair(storePos.first + 1, storePos.second)
            'D' -> Pair(storePos.first, storePos.second - 1)
            'L' -> Pair(storePos.first - 1, storePos.second)
            'U' -> Pair(storePos.first, storePos.second + 1)
            else -> error("BOOOM")
        }
    } else {
        nextPos = when (dir) {
            'R' -> Pair(storePos.first, storePos.second - 1)
            'D' -> Pair(storePos.first - 1, storePos.second)
            'L' -> Pair(storePos.first, storePos.second + 1)
            'U' -> Pair(storePos.first + 1, storePos.second)
            else -> error("BOOOM")
        }
        storePos = nextPos
    }

    return Pair(storePos, nextPos)
}

fun getNextDig(pos: Pair<Int, Int>, dir: Char, l: Int): Pair<Int, Int> {
    return when (dir) {
        'U' -> Pair(pos.first - l, pos.second)
        'D' -> Pair(pos.first + l, pos.second)
        'R' -> Pair(pos.first, pos.second + l)
        'L' -> Pair(pos.first, pos.second - l)
        else -> error("BOOOM")
    }

}

fun isRightTurn(prevDir: Char, nextDir: Char): Boolean {
    return when (prevDir) {
        'R' -> nextDir == 'D'
        'D' -> nextDir == 'L'
        'L' -> nextDir == 'U'
        'U' -> nextDir == 'R'
        else -> error("BOOM")
    }
}

fun printRes(res: List<Pair<Int, Int>>) {
    val rmin = min(res.map { it.first })
    val rmax = max(res.map { it.first })
    val cmin = min(res.map { it.second })
    val cmax = max(res.map { it.second })

    for (r in (rmin..rmax)) {
        for (c in (cmin..cmax)) {
            if (Pair(r, c) in res) {
                print('#')
            } else {
                print('.')
            }
        }
        println("")
    }
}
