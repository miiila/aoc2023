import java.io.File
import kotlin.system.exitProcess

private const val DAY = 19

fun main() {
    if (!File("./day${DAY}_input").exists()) {
        downloadInput(DAY)
        println("Input downloaded")
        exitProcess(0)
    }
    val transformer = { x: String -> x }
    val input = parseWorkflowsAndParts(loadInput(DAY, false, transformer))


//    println(input)
    println(solvePart1(input))
    solvePart2(input)
}

// Part 1
private fun solvePart1(input: Pair<Map<String, List<(part: Part) -> String?>>, List<Part>>): Int {
    val (workflows, parts) = input
    val state = solveState(workflows, parts)
    return state["A"]!!.sumOf { it.sum() }
}


// Part 2
private fun solvePart2(input: Pair<Map<String, List<(part: Part) -> String?>>, List<Part>>): Long {
    val parts = mutableListOf<Part>()
    val (workflows, _) = input
    return 0L
}

fun solveState(
    workflows: Map<String, List<(part: Part) -> String?>>,
    parts: List<Part>
): Map<String, List<Part>> {
    var state = mutableMapOf(Pair("in", parts.toMutableList()))
    while ((state["A"]?.count() ?: 0) + (state["R"]?.count() ?: 0) != parts.count()) {
        val newState = mutableMapOf<String, MutableList<Part>>()
        for ((wf, ps) in state) {
            if (wf == "A" || wf == "R") {
                newState[wf] = newState[wf] ?: mutableListOf()
                newState[wf]!!.addAll(ps)
                continue
            }
            while (ps.isNotEmpty()) {
                val p = ps.removeFirst()
                val r = workflows[wf]!!.firstNotNullOf { it(p) }
                newState[r] = newState[r] ?: mutableListOf()
                newState[r]!!.add(p)
            }
        }
        state = newState
    }

    return state
}


fun parseWorkflowsAndParts(input: List<String>): Pair<Map<String, List<(part: Part) -> String?>>, List<Part>> {
    val split = input.indexOfFirst { it.isEmpty() }
    val workflows = input.slice(0..<split).map(::parseWorkflow).toMap()
    val parts = input.slice(split + 1..<input.count()).map(::parsePart)

    return Pair(workflows, parts)
}

data class Part(val x: Int, val m: Int, val a: Int, val s: Int) {
    fun sum(): Int {
        return x + m + a + s
    }

    fun get(prop: Char): Int {
        return when (prop) {
            'x' -> this.x
            'm' -> this.m
            'a' -> this.a
            's' -> this.s
            else -> error("BOOM")
        }
    }
}

fun parseWorkflow(workflow: String): Pair<String, List<(part: Part) -> String?>> {
    val workflow = workflow.split('{')
    val name = workflow[0]
    return Pair(name, workflow[1].let { x ->
        x.split(',').map { x ->
            when (x[1]) {
                '>' -> isGreater(
                    x[0],
                    x.drop(2).takeWhile { it != ':' }.toInt(),
                    x.reversed().takeWhile { it != ':' }.reversed()
                )

                '<' -> isLess(
                    x[0],
                    x.drop(2).takeWhile { it != ':' }.run(String::toInt),
                    x.reversed().takeWhile { it != ':' }.reversed()
                )

                else -> send(x.dropLast(1))
            }
        }
    })
}

fun isGreater(prop: Char, limit: Int, ret: String): (part: Part) -> String? {
    return { part: Part -> if (part.get(prop) > limit) ret else null }
}

fun isLess(prop: Char, limit: Int, ret: String): (part: Part) -> String? {
    return { part: Part -> if (part.get(prop) < limit) ret else null }
}

fun send(ret: String): (part: Part) -> String? {
    return { ret }
}


fun parsePart(part: String): Part {
    val part = part.slice(1..<part.count() - 1)
    part.let { x ->
        val p = x.split(",")
        return Part(p[0].drop(2).toInt(), p[1].drop(2).toInt(), p[2].drop(2).toInt(), p[3].drop(2).toInt())
    }
}