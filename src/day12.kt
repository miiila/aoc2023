import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.system.exitProcess
import kotlin.time.measureTime

private const val DAY = 12

fun main() {
    if (!File("./day${DAY}_input").exists()) {
        downloadInput(DAY)
        println("Input downloaded")
        exitProcess(0)
    }
    val transformer =
        { x: String -> x.split(" ").let { Pair(it.first().toList(), it.last().split(",").map(String::toInt)) } }
    val input = loadInput(DAY, false, transformer)

    println(measureTime { println(solvePart1(input)) })
    var mem = Memo()
    println(measureTime { println(solvePart2(input, mem)) })
//  Breaks without mutext - concurrent access to mem
    mem = Memo()
    println(measureTime { println(solvePart2Coroutines(input, mem)) })
//  println(measureTime { println(solvePart2Slow(input)) })
}

// Part 1
private fun solvePart1(input: List<Pair<List<Char>, List<Int>>>): Long {
    return input.sumOf(::solveRow)
}

// Part 2
private fun solvePart2(input: List<Pair<List<Char>, List<Int>>>, mem: Memo): Long {
    val input = getPart2input(input)
    return (input.sumOf(mem::solveRow))
}

// Part 2
private fun solvePart2Slow(input: List<Pair<List<Char>, List<Int>>>): Long {
    val input = getPart2input(input)
    return (input.sumOf(::solveRow))
}

private fun solvePart2Coroutines(input: List<Pair<List<Char>, List<Int>>>, mem: Memo): Long {
    val input = getPart2input(input)
    val res = runBlocking {
        input.mapIndexed { i, inp ->
            async(Dispatchers.Default) {
                var r: Long
                // Requires mutex
                // println("${i}: ${measureTime { r = mem.solveRow(inp) }}")
                // Uses new memo for every row => doesn't require mutex
                println("${i}: ${measureTime { r = Memo().solveRow(inp) }}")
                r
            }
        }.awaitAll()
    }

    return res.sum()
}

fun getPart2input(input: List<Pair<List<Char>, List<Int>>>): List<Pair<List<Char>, List<Int>>> {
    return input.map {
        val newFirst = it.first.toMutableList()
        val newSecond = it.second.toMutableList()
        for (i in (0..3)) {
            newFirst.add('?')
            newFirst.addAll(it.first)
            newSecond.addAll(it.second)
        }
        Pair(newFirst, newSecond)
    }
}


class Memo {
    //    private val mutex = Mutex()
    private val memo = mutableMapOf<Pair<List<Char>, List<Int>>, Long>()

    fun solveRow(input: Pair<List<Char>, List<Int>>): Long {
        if (input !in memo) {
            val r = input.first
            val g = input.second
            if (r.count() < g.sum() + g.count() - 1) {
//                mutex.withLock {
                memo[input] = 0
//                }
                return memo[input]!!
            }
            if (g.isEmpty()) {
                if ('#' !in r)
//                    mutex.withLock {
                    memo[input] = 1
//                    }
                else
//                    mutex.withLock {
                    memo[input] = 0
//                    }
                return memo[input]!!
            }
            var res = 0L
            val limit = if (r.indexOf('#') >= 0) r.indexOf('#') else r.count() - (g.sum() + g.count() - 1)
            for (i in (0..limit)) {
                res += this.solveRowRec(Pair(r.slice(i..<r.count()), g))
            }
//            mutex.withLock {
            memo[input] = res
//            }
        }

        return memo[input]!!
    }

    private fun solveRowRec(input: Pair<List<Char>, List<Int>>): Long {
        val r = input.first.toMutableList()
        val g = input.second
        var gg = g.first()
        while (gg > 0) {
            if (r.isEmpty()) {
                return 0
            }
            val rr = r.removeFirst()
            if (rr == '.') {
                break
            }
            gg--
        }
        if (gg > 0) {
            return 0
        }
        if (r.isNotEmpty() && r.first() == '#') {
            return 0
        }


        return this.solveRow(
            Pair(
                r.slice(1..<r.count()),
                g.slice(1..<g.count())
            )
        )
    }

}

fun solveRowRec(input: Pair<List<Char>, List<Int>>, memo: Memo? = null): Long {
    val r = input.first.toMutableList()
    val g = input.second
    var gg = g.first()
    while (gg > 0) {
        if (r.isEmpty()) {
            return 0
        }
        val rr = r.removeFirst()
        if (rr == '.') {
            break
        }
        gg--
    }
    if (gg > 0) {
        return 0
    }
    if (r.isNotEmpty() && r.first() == '#') {
        return 0
    }

    return solveRow(Pair(r.slice(1..<r.count()), g.slice(1..<g.count())))
}

fun solveRow(input: Pair<List<Char>, List<Int>>): Long {
    val r = input.first
    val g = input.second
    if (r.count() < g.sum() + g.count() - 1) {
        return 0
    }
    if (g.isEmpty()) {
        if ('#' !in r)
            return 1
        return 0
    }
    var res = 0L
    val limit = if (r.indexOf('#') >= 0) r.indexOf('#') else r.count() - (g.sum() + g.count() - 1)
    for (i in (0..limit)) {
        res += solveRowRec(Pair(r.slice(i..<r.count()), g))
    }

    return res
}
