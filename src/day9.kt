import java.io.File
import kotlin.system.exitProcess
import kotlin.time.measureTime

private const val DAY = 9

fun main() {
    if (!File("./day${DAY}_input").exists()) {
        downloadInput(DAY)
        println("Input downloaded")
        exitProcess(0)
    }
    val transformer = { x: String -> x.split(" ").map(String::toLong) }
    val input = loadInput(DAY, false, transformer)

    println(measureTime { println(solvePart1(input)) })
    println(measureTime { println(solvePart1Memo(input)) })
    println(measureTime { println(solvePart1Rec(input)) })
    println(measureTime { println(solvePart2(input)) })
}

// Part 1
private fun solvePart1(input: List<List<Long>>): Long {
    val res = input.map { solveRow(it) { chunks: List<Long>, res: Long -> res + chunks.last() } }
    return res.sum()
}

// Part 2
private fun solvePart2(input: List<List<Long>>): Long {
    val res = input.map { solveRow(it) { chunks: List<Long>, res: Long -> chunks.first() - res } }
    return res.sum()
}

fun solveRow(input: List<Long>, result: (chunks: List<Long>, current: Long) -> Long): Long {
    var res: Long = 0
    var found = false
    // Search bottom-up
    for (chunkSize in input.count() downTo 1) {
        val chunks =
            input.windowed(chunkSize)
                .map { it.zip(getPascalCoef(it.count() - 1)).sumOf { it.first * it.second } }
        // Find first row, which is not all zeros
        found = found || chunks.any { it != 0.toLong() }
        // Count results based on provided function - adding last values for part one, subtracting firsts for part two
        if (found) {
            res = result(chunks, res)
        }
    }
    return res
}

// Returns coefficient from Pascal's triangle for a particular row, eg. 1-5-10-10-5-1 for 5th row
// Can be memoized
fun getPascalCoef(row: Int): List<Long> {
    // Odd row (zero indexed) needs to repeat middle
    val isEvenRow = row % 2 == 0
    // Odd rows need to repeat middle item
    val range = if (isEvenRow) (0 until row / 2) else (0..row / 2)
    // And because we are substracting, we need to have proper sign
    val sign = if (isEvenRow) 1 else -1

    return (range + (row / 2 downTo 0)).map(Int::toLong).mapIndexed { i, it ->
        val coef = computeCombinationNumber(row.toLong(), it)
        coef * sign * if (i % 2 == 0) 1 else -1
    }
}

// Computes combination number C(n,k) = n! / k!(n-k)! = (n * n-1 * n-2 * .. n-k) / k!
fun computeCombinationNumber(n: Long, k: Long): Long {
    return if (k == 0.toLong())
        1
    else
        (n downTo (n - k + 1)).reduce(Long::times) / (1..k).reduce(Long::times)
}


// EXPERIMENTS

private fun solvePart1Rec(input: List<List<Long>>): Long {
    val res = input.map { solveRowRec(it) }
    return res.sum()
}

private fun solvePart1Memo(input: List<List<Long>>): Long {
    val mem = Memoized()
    val res = input.map { solveRowMemo(it, { chunks: List<Long>, res: Long -> res + chunks.last() }, mem) }
    return res.sum()
}


fun solveRowRec(input: List<Long>): Long {
    var stop = false
    var inp = input
    val lasts = mutableListOf<Long>()
    while (!stop) {
        lasts.add(inp.last())
        inp = inp.windowed(2).map { it[1] - it[0] }
        stop = inp.all { it == 0.toLong() }
    }

    return lasts.sum()
}

fun solveRowMemo(input: List<Long>, result: (chunks: List<Long>, current: Long) -> Long, mem: Memoized): Long {
    var res: Long = 0
    var found = false
    for (chunkSize in input.count() downTo 1) {
        val chunks =
            input.windowed(chunkSize)
                .map { it.zip(mem.getPascalCoef(it.count() - 1)).sumOf { it.first * it.second } }
        found = found || chunks.any { it != 0.toLong() }
        if (found) {
            res = result(chunks, res)
        }
    }
    return res
}


class Memoized {
    private val coefs = mutableMapOf<Int, List<Long>>()
    private val combinationNumbers = mutableMapOf<Pair<Long, Long>, Long>()

    private fun getCombinationNumber(n: Long, k: Long): Long {
        val p = Pair(n, k)
        if (p !in combinationNumbers) {
            combinationNumbers[p] = computeCombinationNumber(n, k)
        }

        return combinationNumbers[p]!!
    }

    fun getPascalCoef(row: Int): List<Long> {
        if (row !in coefs) {
            coefs[row] = getPascalCoefMemo(row)
        }

        return coefs[row]!!
    }

    private fun getPascalCoefMemo(row: Int): List<Long> {
        // Odd row (zero indexed) needs to repeat middle
        val isEvenRow = row % 2 == 0
        // Odd rows need to repeat middle item
        val range = if (isEvenRow) (0 until row / 2) else (0..row / 2)
        // And because we are substracting, we need to have proper sign
        val sign = if (isEvenRow) 1 else -1

        return (range + (row / 2 downTo 0)).map(Int::toLong).mapIndexed { i, it ->
            val coef = getCombinationNumber(row.toLong(), it)
            coef * sign * if (i % 2 == 0) 1 else -1
        }
    }
}