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
    val graph = parseWorkflowsIntoGraph(loadInput(DAY, false, transformer))

//    println(input)
    println(solvePart1(input))
    println(solvePart2(graph))
}

// Part 1
private fun solvePart1(input: Pair<Map<String, List<(part: Part) -> String?>>, List<Part>>): Int {
    val (workflows, parts) = input
    val state = solveState(workflows, parts)
    return state["A"]!!.sumOf { it.sum() }
}


// Part 2
private fun solvePart2(graph: List<Pair<String, List<Pair<String, String?>>>>): Long {
    val graph = graph.toMap()

    val ranges = mutableMapOf(
        'x' to (1..4000).toSet(),
        'm' to (1..4000).toSet(),
        'a' to (1..4000).toSet(),
        's' to (1..4000).toSet()
    )
    val q = mutableListOf(Pair("in", ranges))
    val res = mutableListOf<Map<Char, Set<Int>>>()
    while (q.isNotEmpty()) {
        val (node, ranges) = q.removeFirst()
        val conds = graph[node]!!
        for ((target, cond) in conds) {
            if (target == "A") {
                if (cond.isNullOrBlank()) {
                    res.add(ranges)
                    continue
                }
                val (c, condRange) = parseCondToRange(cond)
                val newRanges = ranges.toMutableMap()
                newRanges[c] = newRanges[c]!!.intersect(condRange)
                res.add(newRanges)
                ranges[c] = ranges[c]!!.minus(condRange)
                continue
            }
            if (target == "R") {
                if (cond.isNullOrBlank()) {
                    continue
                }
                val (c, condRange) = parseCondToRange(cond)
                ranges[c] = ranges[c]!!.minus(condRange)
                continue
            }
            if (cond.isNullOrBlank()) {
                q.add(Pair(target, ranges))
                continue
            }
            val (c, condRange) = parseCondToRange(cond)
            val newRanges = ranges.toMutableMap()
            newRanges[c] = newRanges[c]!!.intersect(condRange)
            q.add(Pair(target, newRanges))
            ranges[c] = ranges[c]!!.minus(condRange)
        }
    }
    return res.sumOf { it.values.fold(1L) { acc, ints -> acc * ints.count().toLong() } }
}

fun parseCondToRange(cond: String): Pair<Char, IntProgression> {
    return when (cond[1]) {
        '>' -> Pair(cond[0], (cond.drop(2).toInt() + 1..4000))
        '<' -> Pair(cond[0], (1..<cond.drop(2).toInt()))
        else -> error("BOOM")
    }
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

fun parseWorkflowsIntoGraph(input: List<String>):
        List<Pair<String, List<Pair<String, String?>>>> {
    val split = input.indexOfFirst { it.isEmpty() }
    val workflows = input.slice(0..<split).map(::parseWorkflowIntoGraph)

    return workflows
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

fun parseWorkflowIntoGraph(
    workflow: String,
): Pair<String, List<Pair<String, String?>>> {
    val workflow = workflow.split('{')
    val source = workflow[0]
    val targets = workflow[1].let { x ->
        x.split(',').map { x ->
            x.split(':').let { x ->
                if (x.count() == 2) {
                    Pair(x[1], x[0])
                } else {
                    Pair(x[0].dropLast(1), null)
                }
            }
        }
    }
    return Pair(source, targets)
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