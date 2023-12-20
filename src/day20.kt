import java.io.File
import kotlin.system.exitProcess

private const val DAY = 20

fun main() {
    if (!File("./day${DAY}_input").exists()) {
        downloadInput(DAY)
        println("Input downloaded")
        exitProcess(0)
    }
    val transformer = { x: String -> x }
    val input = loadInput(DAY, false, transformer)

//    println(input)
    println(solvePart1(parseCables(input)))
    solvePart2(parseCables(input))
}

// Part 1
private fun solvePart1(circuit: Circuit): Int {
    var circuit = circuit
    var pulses = mutableMapOf(0 to 0, 1 to 0)
    for (i in (1..1000)) {
        val (nc, np) = runPulse(circuit)
        circuit = nc
        pulses[0] = pulses[0]!! + np[0]!!
        pulses[1] = pulses[1]!! + np[1]!!
    }

    return pulses[0]!! * pulses[1]!!
}

// Part 2
private fun solvePart2(circuit: Circuit) {
    var circuit = circuit
    var res = 0L
    printGraph(circuit)
    while (true) {
        res++
        val (nc, _) = runPulse(circuit, res)
        circuit = nc
    }
}

fun printGraph(circuit: Circuit) {
    var mermaid = "flowchart TD\n"
    val q = mutableListOf("broadcaster")
    val done = mutableSetOf<String>()
    while (q.isNotEmpty()) {
        val k = q.removeFirst()
        for (vv in circuit.routing[k]!!) {
            if (vv in circuit.ffStates) {
                mermaid += "    $k --> $vv(($vv))\n"
            } else if (vv in circuit.conjStates) {
                mermaid += "    $k --> $vv\n"
            } else {
                mermaid += "    $k --> $vv\n"
            }
            if (vv in circuit.routing && vv !in done && vv !in q) {
                q.add(vv)
            }
        }
        done.add(k)
    }

    print(mermaid)
}

fun runPulse(circuit: Circuit, count: Long = 0): Pair<Circuit, Map<Int, Int>> {
    val pulses = mutableMapOf(0 to 0, 1 to 0)

    val q = mutableListOf(Triple("button", "broadcaster", 0))
    while (q.isNotEmpty()) {
        val (s, t, p) = q.removeFirst()
        if (count > 0) {
            if (s == "sl" && t == "bq" && p == 0) {
                println("bq: $count")
            }
            if (s == "jz" && t == "vz" && p == 0) {
                println("vz: $count")
            }
            if (s == "rr" && t == "lt" && p == 0) {
                println("lt: $count")
            }
            if (s == "pq" && t == "qh" && p == 0) {
                println("qh: $count")
            }
        }
        pulses[p] = pulses[p]!! + 1
        if (t in circuit.ffStates.keys) {
            if (p == 1) {
                continue
            }
            circuit.ffStates[t] = (circuit.ffStates[t]!! + 1) % 2
            q.addAll(circuit.routing[t]!!.map { Triple(t, it, circuit.ffStates[t]!!) })
            continue
        }
        if (t in circuit.conjStates.keys) {
            circuit.conjStates[t]!![s] = p
            val p = if (circuit.conjStates[t]!!.values.all { it == 1 }) 0 else 1
            q.addAll(circuit.routing[t]!!.map { Triple(t, it, p) })
            continue
        }
        if (t in circuit.routing.keys) {
            q.addAll(circuit.routing[t]!!.map { Triple(t, it, p) })
        }
    }

    return Pair(circuit, pulses)
}

data class Circuit(
    val routing: Map<String, List<String>>,
    val ffStates: MutableMap<String, Int>,
    val conjStates: MutableMap<String, MutableMap<String, Int>>
)

fun parseCables(input: List<String>): Circuit {
    val routing = mutableMapOf<String, List<String>>()
    val ffStates = mutableMapOf<String, Int>()
    val conjStates = mutableMapOf<String, MutableMap<String, Int>>()
    for (line in input) {
        val (source, targets) = line.split(" -> ").let { Pair(it[0], it[1]) }
        when {
            source.startsWith('%') -> ffStates[source.drop(1)] = 0
            source.startsWith('&') -> conjStates[source.drop(1)] = mutableMapOf()
        }
        routing[if (source.startsWith('b')) source else source.drop(1)] = targets.split(", ")
    }
    for (conj in conjStates.keys) {
        for ((s, t) in routing) {
            if (conj in t) {
                conjStates[conj]!![s] = 0
            }
        }
    }

    return Circuit(routing, ffStates, conjStates)
}