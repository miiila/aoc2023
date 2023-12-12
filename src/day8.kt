import java.io.File
import java.util.function.Predicate
import kotlin.system.exitProcess

private const val DAY = 8

fun main() {
    if (!File("./day${DAY}_input").exists()) {
        downloadInput(DAY)
        println("Input downloaded")
        exitProcess(0)
    }
    val transformer = { x: String -> x }
    val input = loadInput(DAY, test = false, transformer)

//    println(input)
    val instructions = input[0]
    val map = parseMap(input.drop(2))
    println(solvePart1(instructions, map))
    println(solvePart2(instructions, map))
}

// Part 1
private fun solvePart1(instructions: String, map: Map<String, Pair<String, String>>): Long {
    return getStepsToTarget("AAA", { it == "ZZZ" }, instructions, map)
}

// Part 2
private fun solvePart2(instructions: String, map: Map<String, Pair<String, String>>): Long {
    val dist =
        map.keys.filter { it.endsWith('A') }
            .associateWith { getStepsToTarget(it, { it.endsWith('Z') }, instructions, map) }

    return dist.values.reduce(::getLcm)
}

fun getLcm(a: Long, b: Long): Long {
    return (a * b) / getGcd(a, b)
}

fun getGcd(a: Long, b: Long): Long {
    var x = a
    var y = b
    while (y > 0) {
        x = y.also { y = x.mod(y) }
    }

    return x
}

fun getStepsToTarget(
    from: String,
    to: Predicate<String>,
    instructions: String,
    map: Map<String, Pair<String, String>>
): Long {
    var steps = 0.toLong()
    val ins = instructions.toMutableList()
    var cur = from
    while (!to.test(cur)) {
        steps++
        val i = ins.removeFirst()
        cur = if (i == 'L') map[cur]!!.first else map[cur]!!.second
        ins.add(i)
    }

    return steps

}

fun parseMap(input: List<String>): Map<String, Pair<String, String>> {
    val regex = "(\\w{3}) = \\((\\w{3}), (\\w{3})\\)".toRegex()
    return input.associate {
        val x = regex.find(it)!!.groupValues
        x[1] to Pair(x[2], x[3])
    }


}