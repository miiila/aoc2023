import java.io.File
import kotlin.system.exitProcess

private const val DAY = 1

fun main() {
    if (!File("./day${DAY}_input").exists()) {
        downloadInput(DAY)
        println("Input downloaded")
        exitProcess(0)
    }
    val transformer = { x: String -> x }
    val input = loadInput(DAY, false, transformer)

    println(solvePart1(input))
    println(solvePart2(input))
}

// Part 1
private fun solvePart1(input: List<String>): Int {
    val res =
        input.map { x: String ->
            x.map { it.digitToIntOrNull() }.filterNotNull().let {
                it.first() * 10 + it.last()
            }
        }
    return res.sum()
}

// Part 2
private fun solvePart2(input: List<String>): Int {
    val regex = "(?=(one|two|three|four|five|six|seven|eight|nine|\\d))".toRegex()
    val digitsMap = mapOf(
        "one" to 1,
        "two" to 2,
        "three" to 3,
        "four" to 4,
        "five" to 5,
        "six" to 6,
        "seven" to 7,
        "eight" to 8,
        "nine" to 9
    )
    val res =
        input.map { x: String ->
            regex.findAll(x).let { it ->
                val first = it.first().groupValues.last().let { it1 -> digitsMap[it1] ?: it1.toInt() }
                val last = it.last().groupValues.last().let { it2 -> digitsMap[it2] ?: it2.toInt() }
                first * 10 + last
            }
        }
    return res.sum()
}