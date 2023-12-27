import java.io.File
import kotlin.system.exitProcess

private const val DAY = 24

fun main() {
    if (!File("./day${DAY}_input").exists()) {
        downloadInput(DAY)
        println("Input downloaded")
        exitProcess(0)
    }
    val transformer = { x: String ->
        x.split(" @ ").let {
            Pair(
                it[0].split(",").map(String::trim).map(String::toDouble),
                it[1].split(", ").map(String::trim).map(String::toDouble)
            )
        }
    }
    val input = loadInput(DAY, false, transformer)

//    println(input)
    println(solvePart1(input))
    solvePart2(input)
}

data class LineXY(val x: Double, val y: Double, val dx: Double, val dy: Double) {
    val slope = dy / dx
    val b = y - slope * x

    fun getYforX(x: Double): Double {
        return slope * x + b
    }
}

// Part 1
private fun solvePart1(input: List<Pair<List<Double>, List<Double>>>): Int {
    val lines = input.map { it -> LineXY(it.first[0], it.first[1], it.second[0], it.second[1]) }
    val res = mutableListOf<Pair<Pair<Double, Double>?, Boolean>>()
    for ((i, line) in lines.withIndex()) {
        for (line2 in lines.drop(i + 1)) {
            res.add(
                Pair(
                    getIntersection(line, line2),
                    willIntersect(line, line2, 200000000000000.0, 400000000000000.0)
                )
            )
        }
    }
    return res.count { it.second }
}

fun getIntersection(a: LineXY, b: LineXY): Pair<Double, Double>? {
    val x = (b.b - a.b) / (a.slope - b.slope)
    if (x == Double.POSITIVE_INFINITY) {
        return null
    }
    val y = a.getYforX(x)
    return Pair(x, y)
}

fun willIntersect(a: LineXY, b: LineXY, min: Double, max: Double): Boolean {
    val intersection = getIntersection(a, b) ?: return false
    if (intersection.first !in (min..max) || intersection.second !in (min..max)) {
        return false
    }
    if ((a.dx < 0 && intersection.first > a.x) || (a.dx > 0 && intersection.first < a.x)) {
        return false
    }
    if ((a.dy < 0 && intersection.second > a.y) || (a.dy > 0 && intersection.second < a.y)) {
        return false
    }
    if ((b.dx < 0 && intersection.first > b.x) || (b.dx > 0 && intersection.first < b.x)) {
        return false
    }
    if ((b.dy < 0 && intersection.second > b.y) || (b.dy > 0 && intersection.second < b.y)) {
        return false
    }

    return true
}

// Part 2
private fun solvePart2(input: List<Pair<List<Double>, List<Double>>>) {
    val velocities = mutableListOf<List<Long>>()
    val startingPoints = mutableListOf<List<Long>>()
    for (inp in input) {
        velocities.add(inp.first.map(Double::toLong))
        startingPoints.add(inp.second.map(Double::toLong))
    }
    println(velocities)
    println(startingPoints)
}
