import java.io.File
import java.math.BigInteger
import kotlin.system.exitProcess

private const val DAY = 5

fun main() {
    if (!File("./day${DAY}_input").exists()) {
        downloadInput(DAY)
        println("Input downloaded")
        exitProcess(0)
    }
    val transformer = { x: String -> x }
    val input = loadInput(DAY, true, transformer)

//    println(input)
    val (seeds, maps) = parseInput(input)
    println(solvePart1(seeds, maps))
    solvePart2(seeds, maps)
}

data class Range(
    val fromStart: BigInteger,
    val fromEnd: BigInteger,
    val toStart: BigInteger = 0.toBigInteger(),
    val toEnd: BigInteger = 0.toBigInteger()
)

fun parseInput(input: List<String>): Pair<List<BigInteger>, Map<String, List<Range>>> {
    var seeds: List<BigInteger> = listOf()
    val maps: MutableMap<String, MutableList<Range>> = mutableMapOf()
    var currentMap: String = ""
    for ((i, line) in input.withIndex()) {
        if (i == 0) {
            seeds = line.trim().split(":").last().trim().split(" ").map(String::toBigInteger)
            continue
        }
        if (line.isEmpty()) {
            continue
        }
        if (line.endsWith(":")) {
            currentMap = line.split(" ").first()
            maps[currentMap] = mutableListOf()
            continue
        }
        val ranges = line.trim().split(" ").map(String::toBigInteger)
        maps[currentMap]!!.add(Range(ranges[1], ranges[1] + ranges[2], ranges[0], ranges[0] + ranges[2]))
    }


    return Pair(seeds, maps)
}

// Part 1
private fun solvePart1(seeds: List<BigInteger>, maps: Map<String, List<Range>>): BigInteger {
    val res = seeds.map { lookupSeedInMaps(it, maps) }
    return res.min()
}

private fun lookupSeedInMaps(seed: BigInteger, maps: Map<String, List<Range>>): BigInteger {
    var current = seed
    for ((key, ranges) in maps) {
        for (range in ranges) {
            if (current >= range.fromStart && current < range.fromEnd) {
                current = range.toStart + (current - range.fromStart)
                break
            }
        }
    }

    return current
}

private fun lookupSeedInMaps(seed: Range, maps: Map<String, List<Range>>): BigInteger {
    var current = seed
    for ((key, ranges) in maps) {
        for (range in ranges) {
            if (current.fromStart >= range.fromStart && current < range.fromEnd) {
                current = range.toStart + (current - range.fromStart)
                break
            }
        }
    }

    return current
    return 0.toBigInteger()
}

// Part 2
private fun solvePart2(seeds: List<BigInteger>, maps: Map<String, List<Range>>) {
    var i = 0
    val seedsRanges = mutableListOf<Range>()
    while (i < seeds.count()) {
        seedsRanges.add(Range(seeds[i], seeds[i] + seeds[++i]))
        i++
    }
    val res = seeds.map { lookupSeedInMaps(it, maps) }
}

fun splitRanges(first: Range, second: Range): List<Range> {
    val res = mutableListOf()
    // Overlap
    if (first.fromStart <= second.fromEnd && second.fromStart <= first.fromEnd) {
        if (first.fromStart < second.fromStart) {
            res.add(Range(first.fromStart, second.fromStart))
        }
        if (first.fromEnd > second.fromEnd) {
            res.add(Range(second.fromEnd, first.fromEnd))
        }
        res.add(Range())

    } else {
        res.add(first)
    }


    return res
}