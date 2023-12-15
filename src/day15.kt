import java.io.File
import kotlin.math.max
import kotlin.system.exitProcess

private const val DAY = 15

fun main() {
    if (!File("./day${DAY}_input").exists()) {
        downloadInput(DAY)
        println("Input downloaded")
        exitProcess(0)
    }
    val transformer = { x: String -> x.split(",") }
    val input = loadInput(DAY, false, transformer).first()

//    println(input)
    println(solvePart1(input))
    println(solvePart2(input))
}

// Part 1
private fun solvePart1(input: List<String>): Int {
    return input.map { it.fold(0) { acc, c -> ((acc + c.code) * 17) % 256 } }.sum()
}

// Part 2
private fun solvePart2(input: List<String>): Int {
    return input.fold(mutableMapOf<Int, MutableMap<String, Int>>())
    { boxes, lens ->
        val s = max(lens.indexOf('-'), lens.indexOf('='))
        val hash = lens.slice(0..<s).fold(0) { acc, c -> ((acc + c.code) * 17) % 256 }
        boxes[hash] = boxes[hash] ?: mutableMapOf()
        if (lens[s] == '-') {
            boxes[hash]!!.remove(lens.slice(0..1))
        }
        if (lens[s] == '=') {
            boxes[hash]!![lens.slice(0..1)] = lens.last().digitToInt()
        }
        boxes
    }
        .filterValues { it.isNotEmpty() }
        .map { (boxOrder, box) ->
            (boxOrder + 1) * box.values.mapIndexed { slot, focalLength -> (slot + 1) * focalLength }.sum()
        }
        .sum()
}