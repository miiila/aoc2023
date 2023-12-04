import java.io.File
import kotlin.math.pow
import kotlin.system.exitProcess

private const val DAY = 4

fun main() {
    if (!File("./day${DAY}_input").exists()) {
        downloadInput(DAY)
        println("Input downloaded")
        exitProcess(0)
    }
    val transformer = { x: String ->
        x.split(":").let {
            it.last().split("|").let {
                it.map { it.split(" ").filter { it != "" }.map(String::toInt) }
            }
        }
    }
    val input = loadInput(DAY, false, transformer)

//    println(input)
    println(solvePart1(input))
    println(solvePart2(input))
}


// Part 1
private fun solvePart1(input: List<List<List<Int>>>): Double {
    var res = 0.0
    for (l in input) {
        val wins = (l.first().toSet() intersect l.last().toSet()).count()
        res += if (wins > 0) 2.0.pow(wins - 1) else 0.0
    }


    return res
}

// Part 2
private fun solvePart2(input: List<List<List<Int>>>): Int {
    val res = mutableMapOf<Int, Int>()
    for ((i, l) in input.withIndex()) {
        res[i] = res[i]?.plus(1) ?: 1
        val wins = (l.first().toSet() intersect l.last().toSet()).count()
        for (k in i + 1..i + wins) {
            res[k] = res[k]?.plus(res[i]!!) ?: res[i]!!
        }
    }

    return res.values.sum()
}