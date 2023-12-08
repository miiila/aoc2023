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
//    println(solvePart1(instructions, map))
    println(solvePart2(instructions, map))
}

// Part 1
private fun solvePart1(instructions: String, map: Map<String, Pair<String, String>>): Long {
    return getStepsToTarget("AAA", { it == "ZZZ" }, instructions, map).first
}

// Part 2
private fun solvePart2(instructions: String, map: Map<String, Pair<String, String>>): Long {
    {}//    var steps = 0
    var cur = map.keys.filter { it.endsWith('A') }.map { Pair(0.toLong(), it) }
    val dist =
        map.keys.filter { it.endsWith('A') || it.endsWith('Z') }
            .associateWith { getStepsToTarget(it, { it.endsWith('Z') }, instructions, map) }

    while (true) {
        val newCur = mutableListOf<Pair<Long, String>>()
        while (cur.isNotEmpty()) {
            val c = cur.removeFirst()
            val new = dist[c.second]
            val newDist = c.first + new!!.first
            if (newCur.all { it.first == newDist } and cur.all { it.first == newDist }) {
                return newDist
            }
            newCur.add(Pair(c.first + new.first, new.second))
        }
        cur = newCur
    }
}


fun getStepsToTarget(
    from: String,
    to: Predicate<String>,
    instructions: String,
    map: Map<String, Pair<String, String>>
): Pair<Long, String> {
    var steps = 0.toLong()
    val ins = instructions.toMutableList()
    var cur = from
    while (true) {
        steps++
        val i = ins.removeFirst()
        cur = if (i == 'L') map[cur]!!.first else map[cur]!!.second
        if (to.test(cur)) {
            break
        }
        ins.add(i)
    }

    return Pair(steps, cur)

}

fun parseMap(input: List<String>): Map<String, Pair<String, String>> {
    val regex = "(\\w{3}) = \\((\\w{3}), (\\w{3})\\)".toRegex()
    return input.associate {
        val x = regex.find(it)!!.groupValues
        x[1] to Pair(x[2], x[3])
    }


}